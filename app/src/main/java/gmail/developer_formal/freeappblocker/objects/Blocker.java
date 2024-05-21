package gmail.developer_formal.freeappblocker.objects;

import java.util.HashSet;
import java.util.Set;

public class Blocker {
    private String name;
    private boolean isActive, blockedNotification;
    private Set<String> blockedApps;

    public Blocker(String name, boolean isActive, boolean blockedNotification) {
        this.name = name;
        this.isActive = isActive;
        this.blockedNotification = blockedNotification;
        this.blockedApps = new HashSet<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isBlockedNotification() {
        return blockedNotification;
    }

    public void setBlockedNotification(boolean blockedNotification) {
        this.blockedNotification = blockedNotification;
    }

    public Set<String> getBlockedApps() {
        return blockedApps;
    }

    public void setBlockedApps(Set<String> blockedApps) {
        this.blockedApps = blockedApps;
    }
}
