package gmail.developer_formal.freeappblocker.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import gmail.developer_formal.freeappblocker.BlockersManager;
import gmail.developer_formal.freeappblocker.R;
import gmail.developer_formal.freeappblocker.fragments.BlockersFragment;
import gmail.developer_formal.freeappblocker.fragments.ExtraFragment;
import gmail.developer_formal.freeappblocker.fragments.StrictFragment;
import gmail.developer_formal.freeappblocker.services.AppBlockerService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import gmail.developer_formal.freeappblocker.services.BlockSitesService;
import gmail.developer_formal.freeappblocker.services.StrictService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onPause(){
        super.onPause();
        if(!PermissionReminderActivity.hasAllPermissions(this))
            return;

        BlockersManager blockersManager = BlockersManager.getInstance(this);
        blockersManager.saveBlockers();
        blockersManager.saveStrictBlocker();
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!PermissionReminderActivity.hasAllPermissions(this)){
            startActivity(new Intent(this, PermissionReminderActivity.class));
            return;
        }

        BlockersManager blockersManager = BlockersManager.getInstance(this);
        blockersManager.startAgain(this);
        blockersManager.refreshApps(this);
        blockersManager.loadBlockers();
        blockersManager.loadStrictBlocker();

        Intent intent = new Intent(this, AppBlockerService.class);
        Intent intent2 = new Intent(this, StrictService.class);
        Intent intent3 = new Intent(this, BlockSitesService.class);
        Intent intent4 = new Intent(this, BlockSitesService.class);

        stopService(intent);
        stopService(intent2);
        stopService(intent3);
        stopService(intent4);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
            startForegroundService(intent2);
            startForegroundService(intent3);
            startForegroundService(intent4);
        }
        else{
            startService(intent);
            startService(intent2);
            startService(intent3);
            startService(intent4);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BlockersManager.getInstance(this);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState != null)
            return;

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new BlockersFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.navigation_blockers);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                int id = item.getItemId();
                selectedFragment = id == R.id.navigation_blockers ? new BlockersFragment() : id == R.id.navigation_strict ? new StrictFragment() : id == R.id.navigation_extra ? new ExtraFragment() : null;

                if(selectedFragment != null)
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                return true;
            };

}
