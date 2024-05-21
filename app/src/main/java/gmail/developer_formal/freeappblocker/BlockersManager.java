package gmail.developer_formal.freeappblocker;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import gmail.developer_formal.freeappblocker.objects.AppInfoCache;
import gmail.developer_formal.freeappblocker.objects.Blocker;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BlockersManager {
    private static volatile BlockersManager instance;
    private final List<Blocker> blockers;
    private SharedPreferences prefs;
    private final List<AppInfoCache> appsInfoCache;
    private boolean strictModeEnabled;
    private long strictDelay, startedAt;
    private Blocker strictBlocker;

    private BlockersManager(Context applicationContext) {
        this.blockers = new ArrayList<>();
        this.prefs = applicationContext.getSharedPreferences("BlockersManager", Context.MODE_PRIVATE);
        this.appsInfoCache = new ArrayList<>();
        this.strictModeEnabled = prefs.getBoolean("strictModeEnabled", false);
        this.strictDelay = prefs.getLong("strictDelay", 0);
        this.startedAt = prefs.getLong("startedAt", 0);
        refreshApps(applicationContext);
        loadBlockers();
        loadStrictBlocker();
    }

    public static BlockersManager getInstance(@NotNull Context context) {
        BlockersManager result = instance;
        if (result == null) {
            synchronized (BlockersManager.class) {
                result = instance;
                if (result == null) {
                    instance = result = new BlockersManager(context.getApplicationContext());
                }
            }
        }
        return result;
    }

    public void refreshApps(Context context) {
        this.appsInfoCache.clear();
        PackageManager manager = context.getPackageManager();
        for (ApplicationInfo app : manager.getInstalledApplications(PackageManager.GET_META_DATA))
            if (manager.getLaunchIntentForPackage(app.packageName) != null)
                this.appsInfoCache.add(new AppInfoCache(app.loadLabel(manager), app.loadIcon(manager), app));
    }

    public void startAgain(Context context) {
        this.prefs = context.getSharedPreferences("BlockersManager", Context.MODE_PRIVATE);
    }

    public List<Blocker> getBlockers() {
        return blockers;
    }

    public List<AppInfoCache> getAppsInfoCache() {
        return appsInfoCache;
    }

    private void loadFromPrefs(String key, Type type, List<Blocker> list) {
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        list.addAll(json == null ? new ArrayList<>() : gson.fromJson(json, type));
    }

    private void saveToPrefs(String key, Object obj) {
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        editor.putString(key, json);
        editor.apply();
    }

    public void loadBlockers() {
        blockers.clear();
        loadFromPrefs("BlockersList", new TypeToken<List<Blocker>>() {}.getType(), blockers);
    }

    public void saveBlockers() {
        saveToPrefs("BlockersList", blockers);
    }

    public void setStrictDelay(long seconds) {
        this.strictDelay = seconds;
        saveToPrefs("strictDelay", seconds);
    }

    public void setStartedAt(long timestamp) {
        this.startedAt = timestamp;
        saveToPrefs("startedAt", timestamp);
    }

    public void changeStrictMode(boolean enabled) {
        this.strictModeEnabled = enabled;
        saveToPrefs("strictModeEnabled", enabled);

        if (!enabled)
            return;

        this.strictBlocker = new Blocker("Strict", true, true);
        this.strictBlocker.getBlockedApps().add("com.android.settings");
        this.strictBlocker.getBlockedApps().add("com.google.android.packageinstaller");
        saveStrictBlocker();
    }

    public void addAppToStrictBlocker(String app) {
        this.strictBlocker.getBlockedApps().add(app);
        saveStrictBlocker();
    }

    public void saveStrictBlocker() {
        saveToPrefs("StrictBlocker", strictBlocker);
    }

    public void loadStrictBlocker() {
        Gson gson = new Gson();
        String json = prefs.getString("StrictBlocker", null);
        Type type = new TypeToken<Blocker>() {}.getType();
        this.strictBlocker = json == null ? new Blocker("Strict", true, true) : gson.fromJson(json, type);
    }

    public Blocker getStrictBlocker() {
        return strictBlocker;
    }

    public boolean isStrictModeEnabled() {
        return strictModeEnabled;
    }

    public long getTotalStrictSecondsRemaining() {
        return ((strictDelay + startedAt) - System.currentTimeMillis()) / 1000;
    }

    public boolean isAtLeastABlockerActive(){
        for(Blocker blocker : blockers)
            if(blocker.isActive() && !blocker.getBlockedApps().isEmpty())
                return true;

        return false;
    }

    public long getStrictDelay() {
        return strictDelay;
    }

    public long getStartedAt() {
        return startedAt;
    }
}
