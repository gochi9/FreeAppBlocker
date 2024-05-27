package gmail.developer_formal.freeappblocker.receivers;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import gmail.developer_formal.freeappblocker.activities.MainActivity;
import gmail.developer_formal.freeappblocker.activities.PermissionReminderActivity;

import static androidx.core.content.ContextCompat.startActivity;

public class MyAdminReceiver extends DeviceAdminReceiver {
    @Override
    public void onEnabled(Context context, Intent intent) {
        Toast.makeText(context, "Device Admin Enabled!", Toast.LENGTH_SHORT).show();
//        Intent newIntent = new Intent(context, MainActivity.class);
//        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(context, newIntent, null);
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Toast.makeText(context, "Device Admin Disabled", Toast.LENGTH_SHORT).show();
    }
}
