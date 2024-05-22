package gmail.developer_formal.freeappblocker.objects;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import gmail.developer_formal.freeappblocker.R;

public class KeywordViewHolder extends RecyclerView.ViewHolder {
    private final TextView keywordText;
    private final CheckBox keywordCheckBox;
    private final ImageButton deleteButton;

    public KeywordViewHolder(@NonNull View itemView) {
        super(itemView);
        keywordText = itemView.findViewById(R.id.keyword_text);
        keywordCheckBox = itemView.findViewById(R.id.keyword_checkbox);
        deleteButton = itemView.findViewById(R.id.keyword_delete_button);
    }

    public TextView getKeywordText() {
        return keywordText;
    }

    public CheckBox getKeywordCheckBox() {
        return keywordCheckBox;
    }

    public ImageButton getDeleteButton() {
        return deleteButton;
    }
}
