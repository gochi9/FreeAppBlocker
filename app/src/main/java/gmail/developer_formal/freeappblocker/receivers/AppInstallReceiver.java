package gmail.developer_formal.freeappblocker.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import gmail.developer_formal.freeappblocker.BlockersManager;

public class AppInstallReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Log.d("ReceiverTest", intent.getAction());
        if (!intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED))
            return;

        BlockersManager blockersManager = BlockersManager.getInstance(context);

        if(blockersManager.isStrictModeEnabled())
            blockersManager.addAppToStrictBlocker(intent.getData().getEncodedSchemeSpecificPart());
    }

}
