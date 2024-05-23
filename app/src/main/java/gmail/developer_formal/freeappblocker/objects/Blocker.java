package gmail.developer_formal.freeappblocker.objects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Blocker {
    private String name;
    private boolean isActive, blockedNotification;
    private Set<String> blockedApps;
    private HashMap<String, Boolean> blockedSites;

    public Blocker(String name, boolean isActive, boolean blockedNotification) {
        this.name = name;
        this.isActive = isActive;
        this.blockedNotification = blockedNotification;
        this.blockedApps = new HashSet<>();
        this.blockedSites = new HashMap<>();
        this.blockedSites.put("google", false);
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

    public boolean isKeywordBlocked(String keyword){
        if(keyword == null || blockedSites.isEmpty())
            return false;

        for(Map.Entry<String, Boolean> entry : blockedSites.entrySet())
            if(entry != null && entry.getValue() && keyword.contains(entry.getKey()))
                return true;

        return false;
    }

    public HashMap<String, Boolean> getBlockedSites(){
        return blockedSites != null ? blockedSites : (blockedSites = new HashMap<>());
    }

    public Set<String> getBlockedSitesActive(){
        if(blockedSites == null || blockedSites.isEmpty())
            return new HashSet<>();

        Set<String> sites = new HashSet<>();

        for(Map.Entry<String, Boolean> entry : blockedSites.entrySet())
            if(entry.getValue())
                sites.add(entry.getKey());

        return sites;
    }

    public void setBlockedSites(HashMap<String, Boolean> blockedSites){
        this.blockedSites = blockedSites;
    }
}
