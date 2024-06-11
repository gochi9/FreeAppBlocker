package gmail.developer_formal.freeappblocker.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import gmail.developer_formal.freeappblocker.BlockersManager;
import gmail.developer_formal.freeappblocker.R;
import gmail.developer_formal.freeappblocker.activities.BlockerActivity;
import gmail.developer_formal.freeappblocker.activities.ChangelogActivity;

public class ExtraFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_extras, container, false);

        Button settingsButton = view.findViewById(R.id.settings_button);
        Button changeLogsButton = view.findViewById(R.id.changelog_button);
        Button supportButton = view.findViewById(R.id.support_button);

        settingsButton.setOnClickListener(v -> openSettingsFragment());
        changeLogsButton.setOnClickListener(v -> changeLogActivity());
        supportButton.setOnClickListener(v -> openSupportFragment());

        return view;
    }

    private void openSettingsFragment() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new SettingsFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void changeLogActivity(){
        Intent dialogIntent = new Intent(this.getContext(), ChangelogActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(dialogIntent);
    }

    private void openSupportFragment() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new SupportFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
