package gmail.developer_formal.freeappblocker.services;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import gmail.developer_formal.freeappblocker.BlockersManager;
import gmail.developer_formal.freeappblocker.objects.Blocker;

import java.util.List;

public class BlockNotificationsService extends NotificationListenerService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        List<Blocker> blockerList = BlockersManager.getInstance(this).getBlockers();

        for(Blocker blocker : blockerList)
            if(blocker.isActive() && blocker.isBlockedNotification() && blocker.getBlockedApps().contains(sbn.getPackageName()))
                cancelNotification(sbn.getKey());
    }


}
