package gmail.developer_formal.freeappblocker.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import gmail.developer_formal.freeappblocker.AppBlockServiceHelper;
import gmail.developer_formal.freeappblocker.AppUtils;
import gmail.developer_formal.freeappblocker.R;
import gmail.developer_formal.freeappblocker.activities.PermissionReminderActivity;

public class BlockSitesService extends AccessibilityService {

    private static final String CHANNEL_ID = "app_blocker_site_block_service_channel";
    private AppBlockServiceHelper helper;

    @Override
    public void onCreate() {
        super.onCreate();
        this.helper = new AppBlockServiceHelper(this);
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

        return START_STICKY;
    }

    private long cooldown = System.currentTimeMillis() + 250;

    @Override
    public void onServiceConnected() {
        for(int i = 0; i < 4; i++)
            goBack();

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.notificationTimeout = 250;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        this.setServiceInfo(info);

        if(PermissionReminderActivity.hasAccessibilityPermission(this))
            return;

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
        if (System.currentTimeMillis() < cooldown)
            return;

        helper.handleAppBlocking(this, event);
        cooldown = System.currentTimeMillis() + 250;
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
