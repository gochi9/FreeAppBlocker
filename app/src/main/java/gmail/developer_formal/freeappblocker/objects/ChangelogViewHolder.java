package gmail.developer_formal.freeappblocker.objects;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import gmail.developer_formal.freeappblocker.R;

public class ChangelogViewHolder extends RecyclerView.ViewHolder{

    public final TextView versionTitle;
    public final TextView changeDate;
    public final TextView changeList;
    public final LinearLayout changesContainer;

    public ChangelogViewHolder(@NonNull View itemView) {
        super(itemView);
        versionTitle = itemView.findViewById(R.id.versionTitle);
        changeDate = itemView.findViewById(R.id.changeDate);
        changeList = itemView.findViewById(R.id.changeList);
        changesContainer = itemView.findViewById(R.id.changesContainer);
    }

}
