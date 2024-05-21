package gmail.developer_formal.freeappblocker.services;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import gmail.developer_formal.freeappblocker.AppUtils;
import gmail.developer_formal.freeappblocker.R;
import gmail.developer_formal.freeappblocker.activities.PermissionReminderActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BlockSitesService extends AccessibilityService {

    private static final String CHANNEL_ID = "app_blocker_service_channel_site_block";
    private List<String> browsers = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        AppUtils.createNotificationChannel(this, CHANNEL_ID, getString(R.string.channel_name), getString(R.string.channel_description));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppUtils.removeNotificationChannel(this, CHANNEL_ID);
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
              //  "com.duckduckgo.mobile.android",
                "com.UCMobile.intl",
                "com.android.browser",
                "com.sec.android.app.sbrowser",
                "com.htc.sense.browser",
                "com.google.android.apps.chrome",
                "com.google.android.apps.chrome_dev",
                "com.google.android.apps.chrome_canary"
            //    "org.torproject.torbrowser"
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

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int evType = event.getEventType();

        if(evType != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && evType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
            return;

        String packageName = event.getPackageName().toString();

        if(!browsers.contains(packageName))
            return;

        String url = getUrlFromEvent(event);
        if (!isBlockedUrl(url))
            return;

        performGlobalAction(GLOBAL_ACTION_BACK);

        try {
            Thread.sleep(500);
            AppUtils.notifyUser(this);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String getUrlFromEvent(AccessibilityEvent event) {
        CharSequence eventText = event.getText().toString();
        String extractedUrl = extractUrlFromText(eventText);

        AccessibilityNodeInfo source = event.getSource();

        if(extractedUrl != null || source == null)
            return extractedUrl != null ? extractedUrl : "unknown";

        List<AccessibilityNodeInfo> nodes = source.findAccessibilityNodeInfosByViewId("android:id/url");

        if (!nodes.isEmpty())
            extractedUrl = nodes.get(0).getText().toString();

        else {
            nodes = source.findAccessibilityNodeInfosByViewId("com.android.chrome:id/url_bar");
            if (!nodes.isEmpty())
                extractedUrl = nodes.get(0).getText().toString();
        }

        return extractedUrl != null ? extractedUrl : "unknown";
    }

    private String extractUrlFromText(CharSequence text) {
        if(text == null)
            return null;

        String textStr = text.toString();
        Pattern urlPattern = Pattern.compile(
                "((http|https)://)?[a-zA-Z0-9\\-\\._]+(\\.[a-zA-Z]{2,})+(/[a-zA-Z0-9\\-\\._?,'+/&%$#=~]*)?",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = urlPattern.matcher(textStr);

        if (matcher.find())
            return matcher.group();

        return null;
    }

    private boolean isBlockedUrl(String url) {
        return url.contains("google");
    }
}
