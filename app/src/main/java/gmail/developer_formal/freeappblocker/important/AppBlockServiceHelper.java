package gmail.developer_formal.freeappblocker.important;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import gmail.developer_formal.freeappblocker.activities.AccessibilityDisableActivity;
import gmail.developer_formal.freeappblocker.activities.PermissionReminderActivity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class AppBlockServiceHelper {

    private final Context context;
    private final PowerManager powerManager;
    private final BlockersManager blockersManager;
    private final HashSet<String> browsers;
    private boolean warn = true;

    public AppBlockServiceHelper(Context context) {
        this.context = context;
        this.powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.blockersManager = BlockersManager.getInstance(context);
        this.browsers = new HashSet<>(Arrays.asList(
                "com.android.chrome",
                "org.mozilla.firefox",
                "com.opera.browser",
                "com.opera.mini.native",
                "com.microsoft.emmx",
                "com.brave.browser",
                "com.duckduckgo.mobile.android",
                "com.UCMobile.intl",
                "com.android.browser",
                "com.sec.android.app.sbrowser",
                "com.htc.sense.browser",
                "com.google.android.apps.chrome",
                "com.google.android.apps.chrome_dev",
                "com.google.android.apps.chrome_canary",
                "org.torproject.torbrowser"
        ));
    }

    public boolean shouldBlockApp(String currentApp, boolean isAppBlockService) {
        if (blockersManager == null || !powerManager.isInteractive() || currentApp.isEmpty() ||
                (!blockersManager.getIsAtLeastAppBlockerActive() && !blockersManager.isStrictModeEnabled()) ||
                (isAppBlockService && PermissionReminderActivity.hasAccessibilityPermission(context)))
            return false;

        if(currentApp.equals("android"))
            return false;

        if(isAppBlockService && warn){
            warn = false;
            Intent dialogIntent = new Intent(context, AccessibilityDisableActivity.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(dialogIntent);
            return false;
        }

        if (blockersManager.isStrictModeEnabled() &&
                (blockersManager.getStrictBlocker().getBlockedApps().contains(currentApp) ||
                        (isAppBlockService && (currentApp.equals("com.android.settings") || browsers.contains(currentApp))))) {
            AppUtils.notifyUser(context, currentApp);
            return true;
        }

        for (String blockedApp : blockersManager.getCachedActiveBlocker())
            if (blockedApp.contains(currentApp)) {
                AppUtils.notifyUser(context, currentApp);
                return true;
            }

        return false;
    }

    public void handleAppBlocking(AccessibilityService service, AccessibilityEvent event) {
        if (event == null || !powerManager.isInteractive() || blockersManager == null)
            return;

        CharSequence packageSequence = event.getPackageName();
        if (packageSequence == null)
            return;

        String packageName = packageSequence.toString();

        if (blockersManager.isStrictModeEnabled() && packageName.equals("com.android.settings"))
                if (shouldBlockSettings(service)) {
                    performSequentialActions(service);
                    return;
                }

        if (shouldBlockApp(packageName, false)) {
            performSequentialActions(service);
            return;
        }

        if (blockersManager.getIsisAtLeastSiteBlockerActive() && browsers.contains(packageName))
            getAllTexts(service.getRootInActiveWindow(), false, service);
    }

    private boolean shouldBlockSettings(AccessibilityService service) {
        AccessibilityNodeInfo nodeInfo = service.getRootInActiveWindow();
        return nodeInfo == null || searchNodeText(nodeInfo, "freeappblocker");
    }

    private boolean searchNodeText(AccessibilityNodeInfo node, String keyword) {
        if (node == null)
            return false;

        CharSequence text = node.getText();
        if (text != null && text.toString().toLowerCase().contains(keyword))
            return true;

        for (int i = 0; i < node.getChildCount(); i++)
            if (searchNodeText(node.getChild(i), keyword))
                return true;

        return false;
    }

    private void getAllTexts(AccessibilityNodeInfo node, boolean checkInSettings, AccessibilityService service) {
        if (node == null || !node.isVisibleToUser())
            return;

        CharSequence text = node.getText();
        if (text != null) {
            String id = text.toString();
            if (!id.isEmpty() && !id.equals("android") && (checkInSettings && id.toLowerCase().contains("freeappblocker") || isSiteBlocked(id))) {
                performSequentialActions(service);
               // AppUtils.notifyUser(service, "URL: " + id);
                return;
            }
        }

        for (int i = 0; i < node.getChildCount(); i++)
            getAllTexts(node.getChild(i), checkInSettings, service);

    }

    private boolean isSiteBlocked(String url) {
        for (String block : blockersManager.getCachedBlockedSites())
            if (url.contains(block))
                return true;


        return false;
    }

    public void performSequentialActions(AccessibilityService service) {
        try {
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            Thread.sleep(350);
            service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
            Thread.sleep(900);
            AppUtils.notifyUser(service, "");
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public String getForegroundApp() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 ? getForegroundApp22Above() : getForegroundAppForApi21();
    }

    private String getForegroundApp22Above() {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, time - 1000, time);
        if (appList == null || appList.isEmpty()) return "";

        UsageStats recentUsageStats = null;
        for (UsageStats usageStats : appList)
            if (recentUsageStats == null || usageStats.getLastTimeUsed() > recentUsageStats.getLastTimeUsed())
                recentUsageStats = usageStats;

        return recentUsageStats != null ? recentUsageStats.getPackageName() : "";
    }

    private String getForegroundAppForApi21() {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        if (runningAppProcesses == null || runningAppProcesses.isEmpty()) return "";

        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses)
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
                return processInfo.processName;

        return "";
    }
}
