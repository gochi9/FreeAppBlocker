package gmail.developer_formal.freeappblocker.objects;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import gmail.developer_formal.freeappblocker.R;

public class BlockerViewHolder extends RecyclerView.ViewHolder{

    private final TextView name;
    private final SwitchCompat toggle;
    private final Button delete, manageApps;
    private final ImageView centerImage;

    public BlockerViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.blocker_name);
        toggle = itemView.findViewById(R.id.blocker_toggle);
        delete = itemView.findViewById(R.id.delete_button);
        manageApps = itemView.findViewById(R.id.manage_apps_button);
        centerImage = itemView.findViewById(R.id.center_image);
    }

    public TextView getName() {
        return name;
    }

    public SwitchCompat getToggle() {
        return toggle;
    }

    public Button getDelete() {
        return delete;
    }

    public Button getManageApps() {
        return manageApps;
    }

    public ImageView getCenterImage() {
        return centerImage;
    }

}
