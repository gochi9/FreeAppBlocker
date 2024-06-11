package gmail.developer_formal.freeappblocker.objects;

import java.util.List;

public class ChangelogEntry {
    private String version;
    private String date;
    private List<String> changes;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDate() {
        return date;
    }

    public List<String> getChanges() {
        return changes;
    }

    public void setChanges(List<String> changes) {
        this.changes = changes;
    }
}
