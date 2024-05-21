package gmail.developer_formal.freeappblocker.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import gmail.developer_formal.freeappblocker.activities.MainActivity;

public class BootCompletedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(!intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
            return;

        Intent activityIntent = new Intent(context, MainActivity.class);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activityIntent);
    }
}
