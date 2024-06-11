package gmail.developer_formal.freeappblocker.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import gmail.developer_formal.freeappblocker.important.BlockersManager;
import gmail.developer_formal.freeappblocker.R;
import gmail.developer_formal.freeappblocker.adapters.BlockerAdapter;
import gmail.developer_formal.freeappblocker.objects.Blocker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BlockersFragment extends Fragment {
    private BlockerAdapter blockerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blockers, container, false);
        Activity activity = getActivity();

        if(activity == null)
            return null;

        BlockersManager blockersManager = BlockersManager.getInstance(activity);
        blockersManager.loadBlockers();

        RecyclerView blockersRecyclerView = view.findViewById(R.id.blockers_list);
        List<Blocker> blockers = BlockersManager.getInstance(getActivity()).getBlockers();
        blockerAdapter = new BlockerAdapter(getContext());
        Button addBlockerButton = view.findViewById(R.id.add_blocker_button);

        blockersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        blockersRecyclerView.setAdapter(blockerAdapter);

        addBlockerButton.setOnClickListener(v -> {
            Blocker newBlocker = new Blocker("", false, false);
            blockers.add(newBlocker);
            blockerAdapter.notifyItemInserted(blockers.size() - 1);
            BlockersManager.getInstance(getActivity()).saveBlockers();
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

        Activity activity = getActivity();

        if(activity != null)
            BlockersManager.getInstance(activity).saveBlockers();
    }
}
