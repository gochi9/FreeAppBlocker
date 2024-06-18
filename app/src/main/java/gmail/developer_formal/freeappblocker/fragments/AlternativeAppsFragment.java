package gmail.developer_formal.freeappblocker.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import gmail.developer_formal.freeappblocker.R;
import gmail.developer_formal.freeappblocker.important.BlockersManager;

public class AlternativeAppsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_alternative_apps, container, false);

        Button appBlockOpenButton = view.findViewById(R.id.app_block_link_button);
        Button coldTurkeyOpenButton = view.findViewById(R.id.cold_turkey_link_button);

        appBlockOpenButton.setOnClickListener(v -> setLink("https://play.google.com/store/apps/details?id=cz.mobilesoft.appblock&hl=en"));
        coldTurkeyOpenButton.setOnClickListener(v -> setLink("https://getcoldturkey.com/"));

        return view;
    }

    private void setLink(String url){
        BlockersManager.getInstance(this.getContext()).setPause(true);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}