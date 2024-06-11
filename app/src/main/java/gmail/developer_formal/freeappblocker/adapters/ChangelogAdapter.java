package gmail.developer_formal.freeappblocker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import gmail.developer_formal.freeappblocker.R;
import gmail.developer_formal.freeappblocker.objects.ChangelogEntry;

public class ChangelogAdapter extends RecyclerView.Adapter<ChangelogAdapter.ViewHolder> {

    private final List<ChangelogEntry> changelogEntries;
    private final Context context;

    public ChangelogAdapter(Context context, List<ChangelogEntry> changelogEntries) {
        this.context = context;
        this.changelogEntries = changelogEntries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_changelog, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChangelogEntry entry = changelogEntries.get(position);
        holder.versionTitle.setText("Version: " + entry.getVersion());
        holder.changeDate.setText("Date: " + entry.getDate());
        StringBuilder changes = new StringBuilder();
        for (String change : entry.getChanges()) {
            changes.append("- ").append(change).append("\n");
        }
        holder.changeList.setText(changes.toString());

        holder.versionTitle.setOnClickListener(v -> {
            boolean isVisible = holder.changesContainer.getVisibility() == View.VISIBLE;
            holder.changesContainer.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        });
    }

    @Override
    public int getItemCount() {
        return changelogEntries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView versionTitle;
        private final TextView changeDate;
        private final TextView changeList;
        private final LinearLayout changesContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            versionTitle = itemView.findViewById(R.id.versionTitle);
            changeDate = itemView.findViewById(R.id.changeDate);
            changeList = itemView.findViewById(R.id.changeList);
            changesContainer = itemView.findViewById(R.id.changesContainer);
        }
    }
}