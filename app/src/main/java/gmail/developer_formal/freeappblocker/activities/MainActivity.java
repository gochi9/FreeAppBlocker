package gmail.developer_formal.freeappblocker.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import gmail.developer_formal.freeappblocker.BlockersManager;
import gmail.developer_formal.freeappblocker.R;
import gmail.developer_formal.freeappblocker.fragments.BlockersFragment;
import gmail.developer_formal.freeappblocker.fragments.ExtraFragment;
import gmail.developer_formal.freeappblocker.fragments.StrictFragment;
import gmail.developer_formal.freeappblocker.services.AppBlockerService;
import gmail.developer_formal.freeappblocker.services.BlockNotificationsService;
import gmail.developer_formal.freeappblocker.services.BlockSitesService;
import gmail.developer_formal.freeappblocker.services.StrictService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onPause() {
        super.onPause();
        if (!PermissionReminderActivity.hasAllPermissions(this))
            return;

        BlockersManager blockersManager = BlockersManager.getInstance(this);
        blockersManager.saveBlockers();
        blockersManager.saveStrictBlocker();
        blockersManager.cacheInformation();

        if(blockersManager.isPause()){
            blockersManager.setPause(false);
            return;
        }

        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!PermissionReminderActivity.hasAllPermissions(this)) {
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
        Intent intent3 = new Intent(this, BlockNotificationsService.class);
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
        } else {
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

        MobileAds.initialize(this, initializationStatus -> {});

        if(BlockersManager.getInstance(this).isEnableAdBanner()) {
            AdView adView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StrictFragment()).commit();
            bottomNavigationView.setSelectedItemId(R.id.navigation_strict);
        }
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;

                int id = item.getItemId();
                if (id == R.id.navigation_blockers) {
                    selectedFragment = new BlockersFragment();
                } else if (id == R.id.navigation_strict) {
                    selectedFragment = new StrictFragment();
                } else if (id == R.id.navigation_extra) {
                    selectedFragment = new ExtraFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }

                return true;
            };
}