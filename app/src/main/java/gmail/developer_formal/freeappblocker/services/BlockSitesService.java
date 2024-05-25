package gmail.developer_formal.freeappblocker.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import gmail.developer_formal.freeappblocker.AppUtils;
import gmail.developer_formal.freeappblocker.BlockersManager;
import gmail.developer_formal.freeappblocker.R;
import gmail.developer_formal.freeappblocker.activities.PermissionReminderActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockSitesService extends AccessibilityService {

    private static final String CHANNEL_ID = "app_blocker_site_block_service_channel";
    private List<String> browsers = new ArrayList<>();
    private PowerManager powerManager;

    @Override
    public void onCreate() {
        super.onCreate();
        AppUtils.createNotificationChannel(this, CHANNEL_ID, getString(R.string.channel_name), getString(R.string.channel_description));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onInterrupt() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = AppUtils.createNotification(this, CHANNEL_ID, "Site Blocker Service", "Running...");
        startForeground(1, notification);

        this.browsers = Arrays.asList(
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
        );

        this.powerManager = ((PowerManager) getSystemService(Context.POWER_SERVICE));

        return START_STICKY;
    }

    private long cooldown = System.currentTimeMillis() + 500;

    @Override
    public void onServiceConnected() {
        for(int i = 0; i < 4; i++)
            goBack();


        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.notificationTimeout = 500;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        this.setServiceInfo(info);

        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startActivity(startMain);

        Intent intent = new Intent(this, PermissionReminderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //I might be able to ditch AppBlockerService and just use this to find open apps instead of having a timer
    //Maybe
    //Update for the above note
    //Users can activate an accessibility shortcut, and they can use it to disable this service, anytime, and I can't disable it
    //FUCK THIS SHIT
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if(cooldown - System.currentTimeMillis() > 0)
            return;

        if(event == null)
            return;

//        if(!getEvents.contains(event.getEventType()))
//            return;

        if(powerManager != null && !powerManager.isInteractive())
            return;

        CharSequence packageSequence = event.getPackageName();

        if(packageSequence == null)
            return;

        if(!browsers.contains(packageSequence.toString()))
            return;

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();

        if(rootNode == null)
            return;

        BlockersManager blockersManager = BlockersManager.getInstance(this);

        if (blockersManager == null)
            return;

        if(!blockersManager.getIsisAtLeastSiteBlockerActive())
            return;

        cooldown = System.currentTimeMillis() + 500;
        getAllTexts(rootNode, blockersManager);
    }

    private void getAllTexts(AccessibilityNodeInfo node, BlockersManager blockersManager) {
        if (node == null || !node.isVisibleToUser())
            return;

        CharSequence text = node.getText();
        String id = null;
        if (text != null && !(id = text.toString()).isEmpty()){
            if(isSiteBlocked(id, blockersManager)){
                goBack();
                AppUtils.notifyUser(this, "URL: " + id);
                return;
            }
        }

        for (int i = 0; i < node.getChildCount(); i++)
            getAllTexts(node.getChild(i), blockersManager);
    }

    private boolean isSiteBlocked(String url, BlockersManager blockersManager) {
        for(String block : blockersManager.getCachedBlockedSites())
            if(url.contains(block))
                return true;

        return false;
    }

    private void goBack(){
        try{
            performGlobalAction(GLOBAL_ACTION_BACK);
            Thread.sleep(100);
        }
        catch(InterruptedException e){
            throw new RuntimeException(e);
        }
    }
}
