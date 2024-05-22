package gmail.developer_formal.freeappblocker.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import gmail.developer_formal.freeappblocker.AppUtils;
import gmail.developer_formal.freeappblocker.BlockersManager;
import gmail.developer_formal.freeappblocker.R;
import gmail.developer_formal.freeappblocker.activities.PermissionReminderActivity;
import gmail.developer_formal.freeappblocker.objects.Blocker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlockSitesService extends AccessibilityService {

    private static final String CHANNEL_ID = "app_blocker_site_block_service_channel";
    private List<String> browsers = new ArrayList<>();

//    private final static HashSet<Integer> getEvents = new HashSet<>();{
//        getEvents.add(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED);
//        getEvents.add(AccessibilityEvent.TYPE_WINDOWS_CHANGED);
//        getEvents.add(AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT);
//        getEvents.add(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
//        getEvents.add(AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED);
//        getEvents.add(AccessibilityEvent.TYPE_VIEW_FOCUSED);
//        getEvents.add(AccessibilityEvent.TYPE_VIEW_CLICKED);
//        getEvents.add(AccessibilityEvent.TYPE_TOUCH_INTERACTION_START);
//    };

    @Override
    public void onCreate() {
        super.onCreate();
        AppUtils.createNotificationChannel(this, CHANNEL_ID, getString(R.string.channel_name), getString(R.string.channel_description));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
   //     AppUtils.removeNotificationChannel(this, CHANNEL_ID);
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

        return START_STICKY;
    }

    @Override
    public void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.notificationTimeout = 100;
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
        if(event == null)
            return;

//        if(!getEvents.contains(event.getEventType()))
//            return;

        CharSequence packageSequence = event.getPackageName();

        if(packageSequence == null)
            return;

        if(!browsers.contains(packageSequence.toString()))
            return;

        BlockersManager blockersManager = BlockersManager.getInstance(this);

        if (blockersManager == null)
            return;

        if(!blockersManager.isAtLeastABlockerActive(false))
            return;

        if (!isBlockedUrl(blockersManager, event.getText().toString()))
            return;

        AppUtils.notifyUser(this);
    }

//    private String getUrlFromEvent(AccessibilityEvent event) {
//        CharSequence eventText = event.getText().toString();
//        String extractedUrl = extractUrlFromText(eventText);
//
//        AccessibilityNodeInfo source = event.getSource();
//
//        for(CharSequence s : event.getText())
//            Log.d("Access testt", s.toString());
//
//        if(extractedUrl != null || source == null)
//            return extractedUrl != null ? extractedUrl : "unknown";
//
//        List<AccessibilityNodeInfo> nodes = source.findAccessibilityNodeInfosByViewId("android:id/url");
//
//        if (!nodes.isEmpty())
//            extractedUrl = nodes.get(0).getText().toString();
//
//        else {
//            nodes = source.findAccessibilityNodeInfosByViewId("com.android.chrome:id/url_bar");
//            if (!nodes.isEmpty())
//                extractedUrl = nodes.get(0).getText().toString();
//        }
//
//        return extractedUrl != null ? extractedUrl : "unknown";
//    }
//
//    private String extractUrlFromText(CharSequence text) {
//        if(text == null)
//            return null;
//
//        String textStr = text.toString();
//        Pattern urlPattern = Pattern.compile(
//                "((http|https)://)?[a-zA-Z0-9\\-\\._]+(\\.[a-zA-Z]{2,})+(/[a-zA-Z0-9\\-\\._?,'+/&%$#=~]*)?",
//                Pattern.CASE_INSENSITIVE
//        );
//        Matcher matcher = urlPattern.matcher(textStr);
//
//        if (matcher.find())
//            return matcher.group();
//
//        return null;
//    }

    private boolean isBlockedUrl(BlockersManager blockersManager, String url) {
        for (Blocker blocker : blockersManager.getBlockers())
            if (blocker.isActive() && blocker.isKeywordBlocked(url))
               return true;

        return false;
    }
}
