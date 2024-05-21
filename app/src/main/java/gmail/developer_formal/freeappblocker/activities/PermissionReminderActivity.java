package gmail.developer_formal.freeappblocker.activities;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import gmail.developer_formal.freeappblocker.R;

import java.util.List;

public class PermissionReminderActivity extends Activity {
    private static final int REQUEST_CODE_OVERLAY_PERMISSION = 11340401;
    private static final int REQUEST_CODE_BATTERY_OPTIMIZATIONS = 10234272;
    private static final int MILLIS_IN_SECOND = 1000;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_permission_reminder);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        showUsageStatsPermissionDialog();
        updatePermissionStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePermissionStatus();

        if(!hasAllPermissions(this))
            return;

        finish();
        Toast.makeText(this, "Permissions granted! Starting app...", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(alertDialog != null)
            alertDialog.dismiss();

        if(hasAllPermissions(this))
            this.finish();
    }

    private void updatePermissionStatus() {
        updatePermission(findViewById(R.id.usage_permission_text), findViewById(R.id.usage_permission_button), hasUsageStatsPermission(this));
        updatePermission(findViewById(R.id.overlay_permission_text), findViewById(R.id.overlay_permission_button), hasOverlayPermission(this));
        updatePermission(findViewById(R.id.notification_permission_text), findViewById(R.id.notification_permission_button), hasNotificationListenerPermission(this));
        updatePermission(findViewById(R.id.accessibility_permission_text), findViewById(R.id.accessibility_permission_button), hasAccessibilityPermission(this));
        updatePermission(findViewById(R.id.battery_permission_text), findViewById(R.id.battery_permission_button), hasBatteryOptimizationsIgnored(this));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            findViewById(R.id.battery_permission_button).setOnClickListener(v -> requestIgnoreBatteryOptimization());

        findViewById(R.id.usage_permission_button).setOnClickListener(v -> redirectToUsageAccessSettings());
        findViewById(R.id.overlay_permission_button).setOnClickListener(v -> requestOverlayPermission());
        findViewById(R.id.notification_permission_button).setOnClickListener(v -> redirectToNotificationListenerSettings());
        findViewById(R.id.accessibility_permission_button).setOnClickListener(v -> redirectToAccessibilitySettings());
    }

    private void updatePermission(TextView textView, Button button, boolean hasPermission) {
        if (hasPermission) {
            textView.setTextColor(Color.GREEN);
            button.setEnabled(false);
            return;
        }

        textView.setTextColor(Color.RED);
        button.setEnabled(true);
    }

    private void redirectToUsageAccessSettings() {
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }

    private void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION);
    }

    private void requestIgnoreBatteryOptimization() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        String packageName = getPackageName();
        Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + packageName));
        try {
            startActivityForResult(intent, REQUEST_CODE_BATTERY_OPTIMIZATIONS);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void redirectToNotificationListenerSettings() {
        startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
    }

    private void redirectToAccessibilitySettings() {
        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
    }

    public static boolean hasAllPermissions(Context context) {
        return hasUsageStatsPermission(context) && hasOverlayPermission(context) && hasBatteryOptimizationsIgnored(context) && hasNotificationListenerPermission(context) && hasAccessibilityPermission(context);
    }

    public static boolean hasUsageStatsPermission(Context context) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> appList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                time - 110 * MILLIS_IN_SECOND, time);
        return appList != null && !appList.isEmpty();
    }

    public static boolean hasOverlayPermission(Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
    }

    public static boolean hasBatteryOptimizationsIgnored(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return true;

        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        String packageName = context.getPackageName();
        return powerManager.isIgnoringBatteryOptimizations(packageName);
    }

    public static boolean hasNotificationListenerPermission(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = context.getPackageName();

        return enabledNotificationListeners != null && enabledNotificationListeners.contains(packageName);
    }

    public static boolean hasAccessibilityPermission(Context context) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo serviceInfo : enabledServices)
            if (serviceInfo.getResolveInfo().serviceInfo.packageName.equals(context.getPackageName()))
                return true;

        return false;
    }

    private void showUsageStatsPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_text, null);
        TextView text = view.findViewById(R.id.dialog_text);
        text.setMovementMethod(new ScrollingMovementMethod());
        text.setText("Hey there! We want to be transparent about the permissions the app uses. This app requires the permissions PACKAGE_USAGE_STATS, QUERY_ALL_PACKAGES, and NOTIFICATION_LISTENER. Here's what they do:\n\n" +
                "1. **PACKAGE_USAGE_STATS**: This permission allows the app to view your app usage statistics, which helps identify and block distracting apps to improve your productivity.\n\n" +
                "2. **QUERY_ALL_PACKAGES**: This permission lets the app query and get information about all the apps installed on your device. This is necessary to identify and manage the apps you want to block.\n\n" +
                "3. **NOTIFICATION_LISTENER**: This permission allows the app to listen to and interact with your notifications. It is necessary for blocking notifications from distracting apps.\n\n" +
                "4. **ACCESSIBILITY**: This permission allows the app to monitor and block websites you mark as distracting in your blockers.\n\n" +
                "We understand that some users might find this level of access invasive. However, the app only uses these permissions to help you stay productive by blocking apps and notifications you mark as distractions. The app does not access, share, or save any sensitive information.\n\n" +
                "We understand if you're uncomfortable granting these permissions. However, the app cannot work without them. If you prefer not to use these features, you can close the app and uninstall it. We appreciate your understanding!");

        Button continueButton = view.findViewById(R.id.dialogTextContinueButton);
        Button cancelButton = view.findViewById(R.id.dialogTextCancelButton);
        cancelButton.setText("Exit app");
        builder.setView(view);
        alertDialog = builder.create();

        cancelButton.setOnClickListener(v -> {
            alertDialog.dismiss();
            this.finish();
        });

        continueButton.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.show();
    }
}