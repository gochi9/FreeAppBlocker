package gmail.developer_formal.freeappblocker.services;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import gmail.developer_formal.freeappblocker.AppUtils;
import gmail.developer_formal.freeappblocker.BlockersManager;
import gmail.developer_formal.freeappblocker.services.AbstractServices.TickableService;

public class StrictService extends TickableService {

    private final Intent broadcastIntent = new Intent("gmail.developer_formal.freeappblocker.UPDATE_STRICT_UI");
    private PowerManager powerManager;

    public StrictService() {
        super("app_blocker_strict_service_channel", 1000);
    }

    @Override
    public int finishInstructions(Intent intent, int flags, int startId) {
        this.powerManager = ((PowerManager) getSystemService(Context.POWER_SERVICE));
        broadcastIntent.setPackage(this.getPackageName());

        return START_STICKY;
    }

    @Override
    protected void tickService(){
        BlockersManager blockersManager = BlockersManager.getInstance(this);
        if (blockersManager == null || !blockersManager.isStrictModeEnabled() || !powerManager.isInteractive())
            return;

        long seconds = blockersManager.getTotalStrictSecondsRemaining();

        if (seconds < 1) {
            blockersManager.changeStrictMode(false);
            broadcastIntent.putExtra("resetUI", "true");
            sendBroadcast(broadcastIntent);

            return;
        }

        broadcastIntent.putExtra("updateSecondsText", AppUtils.convertToDHMS(seconds));
        broadcastIntent.putExtra("resetUI", "");
        sendBroadcast(broadcastIntent);
    }

}
