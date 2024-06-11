package gmail.developer_formal.freeappblocker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import gmail.developer_formal.freeappblocker.important.BlockersManager;
import gmail.developer_formal.freeappblocker.R;
import gmail.developer_formal.freeappblocker.objects.SettingItem;
import gmail.developer_formal.freeappblocker.objects.SettingsViewHolder;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsViewHolder> {

    private final List<SettingItem> settingItems;
    private final BlockersManager blockersManager;
    private final Context context;

    public SettingsAdapter(Context context, List<SettingItem> settingItems, BlockersManager blockersManager) {
        this.context = context;
        this.settingItems = settingItems;
        this.blockersManager = blockersManager;
    }

    @NonNull
    @Override
    public SettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting, parent, false);
        return new SettingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsViewHolder holder, int position) {
        SettingItem settingItem = settingItems.get(position);
        holder.nameTextView.setText(context.getString(settingItem.getNameResId()));
        holder.descriptionTextView.setText(context.getString(settingItem.getDescriptionResId()));
        holder.checkBox.setChecked(settingItem.isChecked());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            settingItem.setChecked(isChecked);
            int id = settingItem.getNameResId();

            if(id == R.string.enable_ad_banner)
                blockersManager.setEnableAdBanner(isChecked);

            else if(id == R.string.watch_short_ad_on_trigger)
                blockersManager.setWatchShortAdOnTrigger(isChecked);

            else if(id == R.string.watch_long_ad_on_trigger)
                blockersManager.setWatchLongAdOnTrigger(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return settingItems.size();
    }
}
