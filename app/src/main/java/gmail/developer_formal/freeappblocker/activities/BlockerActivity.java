package gmail.developer_formal.freeappblocker.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import gmail.developer_formal.freeappblocker.AdsManager;
import gmail.developer_formal.freeappblocker.BlockersManager;
import gmail.developer_formal.freeappblocker.R;

public class BlockerActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitle("Blocked App Notification");
        setContentView(R.layout.activity_blocker);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        BlockersManager blockersManager = BlockersManager.getInstance(this);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        AdsManager adsManager = new AdsManager();

        adsManager.loadInterstitialAd(this);
        adsManager.loadRewardedAd(this);
        if(blockersManager.isWatchLongAdOnTrigger())
            adsManager.showRewardedAd(this);

        else if(blockersManager.isWatchShortAdOnTrigger())
            adsManager.showInterstitialAd(this);
    }

    @Override
    protected void onResume(){
        super.onResume();

        if(!BlockersManager.getInstance(this).isEnableAdBanner())
            return;

        AdView adView = findViewById(R.id.adView2);

        if(adView == null)
            return;

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    @Override
    protected void onPause() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startActivity(startMain);
        super.onPause();
        this.finish();
    }
}
