package gmail.developer_formal.freeappblocker.important;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import gmail.developer_formal.freeappblocker.R;
import gmail.developer_formal.freeappblocker.activities.BlockerActivity;
import gmail.developer_formal.freeappblocker.activities.MainActivity;

import java.util.List;
import java.util.Locale;

public class AppUtils {

    public static void notifyUser(Context context, String packageName) {
        try {
            Intent dialogIntent = new Intent(context, BlockerActivity.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(dialogIntent);
        }
        catch (Exception e) {
            Log.e("AppBlockerService", "Failed to start BlockerActivity", e);
        }
    }

    public static void killAppProcess(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();

        if (runningAppProcesses == null || runningAppProcesses.isEmpty())
            return;

        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcesses){
            Log.d("DSFDSADSFDDSDSF", processInfo.processName);
        }
    }

    public static void createNotificationChannel(Context context, String channelId, CharSequence name, String description) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            return;

        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(channelId, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null)
            notificationManager.createNotificationChannel(channel);
    }

//    public static void removeNotificationChannel(Context context, String channelId){
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
//            return;
//
//        context.getSystemService(NotificationManager.class).deleteNotificationChannel(channelId);
//    }

    public static Notification createNotification(Context context, String channelId, String title, String text) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent;

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            flags |= PendingIntent.FLAG_IMMUTABLE;

        pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, flags);

        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            builder = new Notification.Builder(context, channelId);
        else
            builder = new Notification.Builder(context);

        return builder
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.mipmap.app_logo)
                .setContentIntent(pendingIntent)
                .build();
    }

    public static String convertToDHMS(long seconds) {
        long days = seconds / (24 * 3600);
        seconds %= (24 * 3600);

        long hours = seconds / 3600;
        seconds %= 3600;

        long minutes = seconds / 60;
        seconds %= 60;

        return String.format(Locale.US, "%d:%02d:%02d:%02d", days, hours, minutes, seconds);
    }

    public static int getInt(String s, int def){
        try{
            return Integer.parseInt(s);
        }
        catch(Throwable e){
            return def;
        }
    }

    public static int getHighestInt(String s, int minOrDefault){
        try{
            int i = Integer.parseInt(s);
            return Math.max(i, minOrDefault);
        }
        catch(Throwable e){
            return minOrDefault;
        }
    }

    public static Integer getInteger(String s){
        try{
            return Integer.parseInt(s);
        }
        catch(Throwable e){
            return null;
        }
    }

    public static long getLong(String s){
        try{
            return Long.parseLong(s);
        }
        catch (Throwable e){
            return 0l;
        }
    }

}
