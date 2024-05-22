package gmail.developer_formal.freeappblocker.services.AbstractServices;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import gmail.developer_formal.freeappblocker.AppUtils;
import gmail.developer_formal.freeappblocker.R;

public abstract class NotificationService extends Service {

    private final String CHANNEL_ID;

    public NotificationService(String CHANNEL_ID) {
        this.CHANNEL_ID = CHANNEL_ID;
    }

    protected abstract int startInstructions(Intent intent, int flags, int startId);

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return startInstructions(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppUtils.createNotificationChannel(this, CHANNEL_ID, getString(R.string.channel_name), getString(R.string.channel_description));
        Notification notification = AppUtils.createNotification(this, CHANNEL_ID, "App Blocker Service", "Running...");
        startForeground(1, notification);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
       // AppUtils.removeNotificationChannel(this, CHANNEL_ID);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
