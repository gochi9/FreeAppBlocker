package gmail.developer_formal.freeappblocker.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import gmail.developer_formal.freeappblocker.BlockersManager;
import gmail.developer_formal.freeappblocker.R;
import gmail.developer_formal.freeappblocker.objects.Blocker;
import gmail.developer_formal.freeappblocker.objects.BlockerViewHolder;

import java.util.*;

public class BlockerAdapter extends RecyclerView.Adapter<BlockerViewHolder> {

    private final Context context;

    public BlockerAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public BlockerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.blocker_list_item, parent, false);
        return new BlockerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockerViewHolder holder, int position) {
        Blocker blocker = BlockersManager.getInstance(context).getBlockers().get(position);
        TextView name = holder.getName();
        SwitchCompat toggle = holder.getToggle();
        ImageButton manageApps = holder.getManageApps();
        ImageButton addKeyword = holder.getAddKeywordButton();
        ImageButton delete = holder.getDelete();
        boolean isActive = blocker.isActive();

        name.setText(blocker.getName());
        name.setOnClickListener(v -> showEditNameDialog(blocker, name));
        toggle.setChecked(isActive);
        setBorderAndImage(holder, isActive);

        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isPressed())
                return;

            if (!isChecked && BlockersManager.getInstance(context).isStrictModeEnabled()) {
                buttonView.setChecked(true);
                Toast.makeText(context, "Cannot do that while strict mode is enabled", Toast.LENGTH_SHORT).show();
                return;
            }

            blocker.setActive(isChecked);
            BlockersManager.getInstance(context).saveBlockers();
            setBorderAndImage(holder, isChecked);
        });

        manageApps.setOnClickListener(v -> showManageAppsDialog(blocker));

        addKeyword.setOnClickListener(v -> showAddKeywordDialog(blocker));

        delete.setOnClickListener(v -> {
            if (toggle.isChecked() && BlockersManager.getInstance(context).isStrictModeEnabled()) {
                Toast.makeText(context, "Cannot do that while strict mode is enabled", Toast.LENGTH_SHORT).show();
                return;
            }

            delete.setClickable(false);
            BlockersManager.getInstance(context).getBlockers().remove(position);
            notifyItemRemoved(position);
            BlockersManager.getInstance(context).saveBlockers();
        });
    }

    private void showAddKeywordDialog(Blocker blocker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View customLayout = LayoutInflater.from(context).inflate(R.layout.dialog_add_keyword, null);
        EditText keywordEditText = customLayout.findViewById(R.id.keywordEditText);
        Button addButton = customLayout.findViewById(R.id.addButton);
        RecyclerView keywordRecyclerView = customLayout.findViewById(R.id.keywordRecyclerView);
        Button saveButton = customLayout.findViewById(R.id.saveButton);
        Button cancelButton = customLayout.findViewById(R.id.cancelButton);

        HashMap<String, Boolean> originalKeywords = new HashMap<>(blocker.getBlockedSites());
        HashMap<String, Boolean> currentKeywords = new HashMap<>(originalKeywords);
        boolean isStrictModeEnabled = BlockersManager.getInstance(context).isStrictModeEnabled();
        KeywordAdapter adapter = new KeywordAdapter(context, currentKeywords, originalKeywords, isStrictModeEnabled);
        keywordRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        keywordRecyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(keywordRecyclerView.getContext(),
                ((LinearLayoutManager) keywordRecyclerView.getLayoutManager()).getOrientation());
        keywordRecyclerView.addItemDecoration(dividerItemDecoration);

        builder.setView(customLayout);
        AlertDialog dialog = builder.create();

        addButton.setOnClickListener(v -> {
            String keyword = keywordEditText.getText().toString();
            if (!keyword.isEmpty() && !currentKeywords.containsKey(keyword)) {
                adapter.addKeyword(keyword, false);
                keywordEditText.setText("");
            }
        });

        saveButton.setOnClickListener(v -> {
            blocker.setBlockedSites(currentKeywords);
            BlockersManager.getInstance(context).saveBlockers();
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }



    private void setBorderAndImage(BlockerViewHolder holder, boolean isActive) {
        int borderRes = isActive ? R.drawable.red_border_background : R.drawable.blue_border_background;
        int imageRes = isActive ? R.drawable.blocker_lock_img : R.drawable.blocker_unlock_img;
        int iconTint = isActive ? context.getResources().getColor(android.R.color.holo_red_dark) : context.getResources().getColor(android.R.color.holo_blue_dark);
        ImageView centerImage = holder.getCenterImage();

        holder.itemView.setBackgroundResource(borderRes);
        centerImage.setImageResource(imageRes);
        centerImage.setColorFilter(iconTint, android.graphics.PorterDuff.Mode.SRC_IN);
    }

    private void showEditNameDialog(Blocker blocker, TextView nameView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View customLayout = LayoutInflater.from(context).inflate(R.layout.edit_text_layout, null);
        EditText editText = customLayout.findViewById(R.id.editText);
        editText.setText(blocker.getName());

        builder.setView(customLayout);
        AlertDialog dialog = builder.create();

        Button okButton = customLayout.findViewById(R.id.okButton);
        Button cancelButton = customLayout.findViewById(R.id.cancelButton);

        okButton.setOnClickListener(v -> {
            dialog.dismiss();
            String newName = editText.getText().toString();

            if (newName.equals(blocker.getName()))
                return;

            blocker.setName(newName);
            nameView.setText(newName);
            notifyItemChanged(BlockersManager.getInstance(context).getBlockers().indexOf(blocker));
            BlockersManager.getInstance(context).saveBlockers();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showManageAppsDialog(Blocker blocker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_apps_list, null);
        EditText searchBar = dialogView.findViewById(R.id.search_bar);
        ListView listView = dialogView.findViewById(R.id.list_apps);
        Button btnClearRefresh = dialogView.findViewById(R.id.btnClearRefresh);
        Button btnShowBlocked = dialogView.findViewById(R.id.btnShowBlocked);
        CheckBox blockNotification = dialogView.findViewById(R.id.disableNotifications);

        AppAdapter adapter = new AppAdapter(context, blocker.getBlockedApps());
        listView.setAdapter(adapter);
        blockNotification.setChecked(blocker.isBlockedNotification());
        blockNotification.setBackgroundResource(blockNotification.isChecked() ? R.drawable.red_border_background : R.drawable.blue_border_background);

        blockNotification.setOnCheckedChangeListener((k, v) -> {
            if(BlockersManager.getInstance(context).isStrictModeEnabled() && !k.isChecked()) {
                k.setChecked(true);
                Toast.makeText(context, "Cannot do that while strict mode is enabled", Toast.LENGTH_SHORT).show();
                return;
            }

            k.setBackgroundResource(blockNotification.isChecked() ? R.drawable.red_border_background : R.drawable.blue_border_background);
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClearRefresh.setOnClickListener(v -> {
            searchBar.setText("");
            adapter.getFilter().filter("");
            btnShowBlocked.setText(R.string.show_blocked_only_button);
        });

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        btnShowBlocked.setOnClickListener(v -> {
            if (btnShowBlocked.getText().equals("Show Blocked Only")) {
                adapter.getFilter().filter("blocked");
                btnShowBlocked.setText(R.string.show_all_apps_button);
                return;
            }

            adapter.getFilter().filter("");
            btnShowBlocked.setText(R.string.show_blocked_only_button);
        });

        dialogView.findViewById(R.id.saveButton).setOnClickListener(v -> {
            blocker.setBlockedApps(new HashSet<>(adapter.getSelectedApps()));
            blocker.setBlockedNotification(blockNotification.isChecked());
            BlockersManager.getInstance(context).saveBlockers();
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.cancelButton).setOnClickListener(v -> dialog.dismiss());;
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return BlockersManager.getInstance(context).getBlockers().size();
    }
}
