package gmail.developer_formal.freeappblocker.services;

import android.app.*;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.*;
import android.os.Build;
import android.os.PowerManager;
import gmail.developer_formal.freeappblocker.AppUtils;
import gmail.developer_formal.freeappblocker.BlockersManager;
import gmail.developer_formal.freeappblocker.objects.Blocker;

import gmail.developer_formal.freeappblocker.services.AbstractServices.TickableService;

import java.util.List;

public class AppBlockerService extends TickableService {

    private PowerManager powerManager;

    public AppBlockerService() {
        super("app_blocker_service_channel", 250);
    }

    @Override
    protected int runInstructions(Intent intent, int flags, int startId) {
        this.powerManager = ((PowerManager) getSystemService(Context.POWER_SERVICE));
        return START_STICKY;
    }

    @Override
    protected void tickService(){
        BlockersManager blockersManager = BlockersManager.getInstance(this);

        if (blockersManager == null)
            return;

        boolean isStrictMode = blockersManager.isStrictModeEnabled();

        if((!blockersManager.isAtLeastABlockerActive(true) && !isStrictMode) || !powerManager.isInteractive())
            return;

        String currentApp = getForegroundApp();

        if(currentApp.isEmpty() || currentApp.equals(getPackageName()))
            return;


        if(isStrictMode && blockersManager.getStrictBlocker().getBlockedApps().contains(currentApp)){
            AppUtils.notifyUser(this);
            return;
        }

        for (Blocker blocker : blockersManager.getBlockers()) {
            if (blocker.isActive() && blocker.getBlockedApps().contains(currentApp)) {
                AppUtils.notifyUser(this);
                break;
            }
        }
    }

    public String getForegroundApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
            return getForegroundApp22Above();
         else
            return getForegroundAppForApi21();
    }

    private String getForegroundApp22Above() {
        UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();

        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, time - 1000, time);

        if (appList == null || appList.isEmpty())
            return "";

        UsageStats recentUsageStats = null;
        for (UsageStats usageStats : appList)
            if (recentUsageStats == null || usageStats.getLastTimeUsed() > recentUsageStats.getLastTimeUsed())
                recentUsageStats = usageStats;

        if (recentUsageStats != null)
            return recentUsageStats.getPackageName();

        return "";
    }

    private String getForegroundAppForApi21() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

        if (runningAppProcesses == null || runningAppProcesses.isEmpty())
            return "";

        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses)
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
                return processInfo.processName;

        return "";
    }
}
