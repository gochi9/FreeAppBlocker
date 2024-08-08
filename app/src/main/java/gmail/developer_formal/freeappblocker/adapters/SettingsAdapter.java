package gmail.developer_formal.freeappblocker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
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

            if(id == R.string.block_installed_apps){
                holder.checkBox.setChecked(false);
                Toast.makeText(context, "Not yet implemented!", Toast.LENGTH_SHORT).show();

                //blockersManager.setBlockInstalledApps(holder.checkBox.isChecked());
            }

            if(id == R.string.turn_off_blockers_when_finished)
                blockersManager.setTurnOffBlockersWhenFinished(isChecked);

            else if(id == R.string.enable_ad_banner){
                blockersManager.setEnableAdBanner(isChecked);
                Toast.makeText(context, "This setting requires you to restart the app to take effect", Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "Thank you!", Toast.LENGTH_SHORT).show();
            }

            else if(id == R.string.watch_short_ad_on_trigger){
//                holder.checkBox.setChecked(false);
//                Toast.makeText(context, "Not yet implemented!", Toast.LENGTH_SHORT).show();

                blockersManager.setWatchShortAdOnTrigger(isChecked);
                Toast.makeText(context, "Thank you!", Toast.LENGTH_SHORT).show();
            }

            else if(id == R.string.watch_long_ad_on_trigger){
//                holder.checkBox.setChecked(false);
//                Toast.makeText(context, "Not yet implemented!", Toast.LENGTH_SHORT).show();

                blockersManager.setWatchLongAdOnTrigger(isChecked);
                Toast.makeText(context, "Thank you!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return settingItems.size();
    }
}
