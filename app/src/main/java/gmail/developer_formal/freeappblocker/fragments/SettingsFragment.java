package gmail.developer_formal.freeappblocker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import gmail.developer_formal.freeappblocker.important.BlockersManager;
import gmail.developer_formal.freeappblocker.R;
import gmail.developer_formal.freeappblocker.adapters.SettingsAdapter;
import gmail.developer_formal.freeappblocker.objects.SettingItem;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private RecyclerView recyclerView;
    private SettingsAdapter settingsAdapter;
    private List<SettingItem> settingItems;
    private BlockersManager blockersManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        blockersManager = BlockersManager.getInstance(getContext());

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        settingItems = new ArrayList<>();
        settingItems.add(new SettingItem(R.string.block_installed_apps, R.string.block_installed_apps_description, blockersManager.isBlockInstalledApps()));
        settingItems.add(new SettingItem(R.string.turn_off_blockers_when_finished, R.string.turn_off_blockers_when_finished_description, blockersManager.isTurnOffBlockersWhenFinished()));

        settingsAdapter = new SettingsAdapter(getContext(), settingItems, blockersManager);
        recyclerView.setAdapter(settingsAdapter);

        return view;
    }
}
