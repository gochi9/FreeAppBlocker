package gmail.developer_formal.freeappblocker.important;

import android.app.Activity;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class AdsManager {

    protected AdsManager(){}

    private InterstitialAd mInterstitialAd;
    private RewardedAd mRewardedAd;

    public void loadInterstitialAd(Activity activity) {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(activity, "ca-app-pub-1957142949765830/9500515993", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mInterstitialAd = null;
            }
        });
    }

    public void showInterstitialAd(Activity activity) {
        if (mInterstitialAd != null){
            BlockersManager.getInstance(activity).setPause(true);
            mInterstitialAd.show(activity);
        }
        else
            loadInterstitialAd(activity);
    }


    public void loadRewardedAd(Activity activity) {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(activity, "ca-app-pub-1957142949765830/1622025971", adRequest, new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                mRewardedAd = rewardedAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mRewardedAd = null;
            }
        });
    }

    public void showRewardedAd(Activity activity) {
        if (mRewardedAd != null) {
            BlockersManager.getInstance(activity).setPause(true);
            mRewardedAd.show(activity, rewardItem -> Toast.makeText(activity, "Thank you!", Toast.LENGTH_SHORT).show());
        }
        else {
            loadRewardedAd(activity);
        }
    }

}
