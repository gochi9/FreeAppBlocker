package gmail.developer_formal.freeappblocker.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentActivity;
import gmail.developer_formal.freeappblocker.R;

public class AccessibilityDisableActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitle("Accessibility Disabled");
        setContentView(R.layout.activity_accessibility_disable);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.finish();
        Intent dialogIntent = new Intent(this, MainActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);
    }

}
