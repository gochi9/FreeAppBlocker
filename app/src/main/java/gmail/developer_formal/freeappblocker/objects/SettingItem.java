package gmail.developer_formal.freeappblocker.objects;

public class SettingItem {
    private int nameResId;
    private int descriptionResId;
    private boolean isChecked;

    public SettingItem(int nameResId, int descriptionResId, boolean isChecked) {
        this.nameResId = nameResId;
        this.descriptionResId = descriptionResId;
        this.isChecked = isChecked;
    }

    public int getNameResId() {
        return nameResId;
    }

    public int getDescriptionResId() {
        return descriptionResId;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
