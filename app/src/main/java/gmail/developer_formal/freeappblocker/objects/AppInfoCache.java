package gmail.developer_formal.freeappblocker.objects;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;

public class AppInfoCache {
    private final CharSequence appName;
    private final Drawable appIcon;
    private final ApplicationInfo applicationInfo;

    public AppInfoCache(CharSequence appName, Drawable appIcon, ApplicationInfo applicationInfo) {
        this.appName = appName;
        this.appIcon = appIcon;
        this.applicationInfo = applicationInfo;
    }

    public CharSequence getAppName() {
        return appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }
}
