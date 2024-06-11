package gmail.developer_formal.freeappblocker.objects;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import gmail.developer_formal.freeappblocker.R;

public class SettingsViewHolder extends RecyclerView.ViewHolder {
    public TextView nameTextView;
    public TextView descriptionTextView;
    public CheckBox checkBox;

    public SettingsViewHolder(@NonNull View itemView) {
        super(itemView);
        nameTextView = itemView.findViewById(R.id.setting_name);
        descriptionTextView = itemView.findViewById(R.id.setting_description);
        checkBox = itemView.findViewById(R.id.setting_checkbox);
    }
}
