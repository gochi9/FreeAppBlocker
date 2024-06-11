package gmail.developer_formal.freeappblocker.services;

import android.content.*;
import gmail.developer_formal.freeappblocker.important.AppBlockServiceHelper;
import gmail.developer_formal.freeappblocker.important.AppUtils;
import gmail.developer_formal.freeappblocker.services.AbstractServices.TickableService;

public class AppBlockerService extends TickableService {

    private AppBlockServiceHelper helper;

    public AppBlockerService() {
        super("app_blocker_service_channel", 250);
    }

    @Override
    protected int finishInstructions(Intent intent, int flags, int startId) {
        this.helper = new AppBlockServiceHelper(this);
        return START_STICKY;
    }

    @Override
    protected void tickService() {
        String currentApp = helper.getForegroundApp();

        if (helper.shouldBlockApp(currentApp, true))
            AppUtils.notifyUser(this, currentApp);
    }
}
