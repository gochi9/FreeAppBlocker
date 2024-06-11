package gmail.developer_formal.freeappblocker.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import gmail.developer_formal.freeappblocker.important.AdsManager;
import gmail.developer_formal.freeappblocker.important.BlockersManager;
import gmail.developer_formal.freeappblocker.R;
import gmail.developer_formal.freeappblocker.adapters.SettingsAdapter;
import gmail.developer_formal.freeappblocker.objects.SettingItem;

import java.util.ArrayList;
import java.util.List;

public class SupportFragment extends Fragment {

    private static final String PAYPAL_URL = "https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=vladimirpirscoveanu@gmail.com&item_name=Support+Me&currency_code=USD";
    private static final String PAYPAL_PACKAGE = "com.paypal.android.p2pmobile";

    private RecyclerView recyclerView;
    private SettingsAdapter settingsAdapter;
    private List<SettingItem> settingItems;
    private BlockersManager blockersManager;
    private Button playShortAdButton, playLongAdButton, donateButton;
    private AdsManager adsManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_support, container, false);

        blockersManager = BlockersManager.getInstance(getContext());
        this.adsManager = new AdsManager();

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        settingItems = new ArrayList<>();
        settingItems.add(new SettingItem(R.string.enable_ad_banner, R.string.enable_ad_banner_description, blockersManager.isEnableAdBanner()));
        settingItems.add(new SettingItem(R.string.watch_short_ad_on_trigger, R.string.watch_short_ad_on_trigger_description, blockersManager.isWatchShortAdOnTrigger()));
        settingItems.add(new SettingItem(R.string.watch_long_ad_on_trigger, R.string.watch_long_ad_on_trigger_description, blockersManager.isWatchLongAdOnTrigger()));

        settingsAdapter = new SettingsAdapter(getContext(), settingItems, blockersManager);
        recyclerView.setAdapter(settingsAdapter);

        playShortAdButton = view.findViewById(R.id.play_short_ad_button);
        playLongAdButton = view.findViewById(R.id.play_long_ad_button);
        donateButton = view.findViewById(R.id.donate_button);

        playShortAdButton.setOnClickListener(v -> adsManager.showInterstitialAd(this.getActivity()));

        playLongAdButton.setOnClickListener(v -> adsManager.showRewardedAd(this.getActivity()));

        donateButton.setOnClickListener(v -> openPayPal(this.getContext()));

        return view;
    }

    private void openPayPal(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(PAYPAL_URL));
        intent.setPackage(PAYPAL_PACKAGE);
        if (isIntentSafe(context, intent))
            context.startActivity(intent);

        else {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(PAYPAL_URL));
            context.startActivity(browserIntent);
        }
    }

    private boolean isIntentSafe(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        return intent.resolveActivity(packageManager) != null;
    }

}
