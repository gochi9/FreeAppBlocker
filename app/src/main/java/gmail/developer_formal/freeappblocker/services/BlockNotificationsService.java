package gmail.developer_formal.freeappblocker.services;

import android.app.Notification;
import android.content.Intent;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import gmail.developer_formal.freeappblocker.AppUtils;
import gmail.developer_formal.freeappblocker.BlockersManager;
import gmail.developer_formal.freeappblocker.R;
import gmail.developer_formal.freeappblocker.objects.Blocker;

import java.util.List;

public class BlockNotificationsService extends NotificationListenerService {

    private static final String CHANNEL_ID = "app_blocker_notifications_block_channel_service";

    @Override
    public void onCreate() {
        super.onCreate();
        AppUtils.createNotificationChannel(this, CHANNEL_ID, getString(R.string.channel_name), getString(R.string.channel_description));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
       // AppUtils.removeNotificationChannel(this, CHANNEL_ID);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = AppUtils.createNotification(this, CHANNEL_ID, "Site Blocker Service", "Running...");
        startForeground(1, notification);

        return START_STICKY;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        List<Blocker> blockerList = BlockersManager.getInstance(this).getBlockers();

        for(Blocker blocker : blockerList)
            if(blocker.isActive() && blocker.isBlockedNotification() && blocker.getBlockedApps().contains(sbn.getPackageName()))
                cancelNotification(sbn.getKey());
    }


}
