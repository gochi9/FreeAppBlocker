package gmail.developer_formal.freeappblocker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import gmail.developer_formal.freeappblocker.R;
import gmail.developer_formal.freeappblocker.objects.KeywordViewHolder;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KeywordAdapter extends RecyclerView.Adapter<KeywordViewHolder> {

    private final Context context;
    private final List<Map.Entry<String, Boolean>> keywords;
    private final Map<String, Boolean> activeKeywords, originalKeywords;
    private final boolean isStrictModeEnabled;

    public KeywordAdapter(Context context, Map<String, Boolean> keywords, Map<String,Boolean> originalKeywords, boolean isStrictModeEnabled) {
        this.context = context;
        this.keywords = new ArrayList<>(keywords.entrySet());
        this.activeKeywords = keywords;
        this.originalKeywords = originalKeywords;
        this.isStrictModeEnabled = isStrictModeEnabled;
    }

    @NonNull
    @Override
    public KeywordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_keyword, parent, false);
        return new KeywordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeywordViewHolder holder, int position) {
        Map.Entry<String, Boolean> keywordEntry = keywords.get(position);
        String keyword = keywordEntry.getKey();
        boolean isActive = keywordEntry.getValue();

        holder.getKeywordText().setText(keyword);
        holder.getKeywordCheckBox().setChecked(isActive);

        holder.getKeywordCheckBox().setOnCheckedChangeListener((buttonView, isChecked) -> {
            Boolean value = originalKeywords.get(keyword);
            if(!(!isStrictModeEnabled || (value != null && !value))){
                buttonView.setChecked(true);
                Toast.makeText(context, "Cannot do that while strict mode is enabled", Toast.LENGTH_SHORT).show();
                return;
            }

            activeKeywords.put(keyword, isChecked);
        });

        holder.getDeleteButton().setOnClickListener(v -> {
            Boolean value = originalKeywords.get(keyword);

            if(!(!isStrictModeEnabled || (value != null && !value))) {
                Toast.makeText(context, "Cannot do that while strict mode is enabled", Toast.LENGTH_SHORT).show();
                return;
            }

            int actualPosition = holder.getAdapterPosition();

            if(actualPosition == RecyclerView.NO_POSITION)
                return;

            keywords.remove(actualPosition);
            activeKeywords.remove(keyword);
            notifyItemRemoved(actualPosition);
        });
    }

    @Override
    public int getItemCount() {
        return keywords.size();
    }

    public void addKeyword(String keyword, boolean isActive) {
        activeKeywords.put(keyword, isActive);
        keywords.add(new AbstractMap.SimpleEntry<>(keyword, isActive));
        notifyItemInserted(keywords.size() - 1);
    }
}