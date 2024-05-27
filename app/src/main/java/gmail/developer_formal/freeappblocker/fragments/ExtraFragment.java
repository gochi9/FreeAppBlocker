package gmail.developer_formal.freeappblocker.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import gmail.developer_formal.freeappblocker.R;

public class ExtraFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_extras, container, false);

        Button settingsButton = view.findViewById(R.id.settings_button);
        Button supportButton = view.findViewById(R.id.support_button);

        settingsButton.setOnClickListener(v -> openSettingsFragment());
        supportButton.setOnClickListener(v -> openSupportFragment());

        return view;
    }

    private void openSettingsFragment() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new SettingsFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void openSupportFragment() {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new SupportFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
