package gmail.developer_formal.freeappblocker.services.AbstractServices;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

public abstract class TickableService extends NotificationService {

    private boolean running = false;
    private final int TICK_DELAY;
    private Handler handler;

    public TickableService(String CHANNEL_ID, int TICK_DELAY) {
        super(CHANNEL_ID);
        this.TICK_DELAY = TICK_DELAY;
    }

    protected abstract void tickService() throws InterruptedException;
    protected abstract int finishInstructions(Intent intent, int flags, int startId);

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopTimer();
    }

    @Override
    protected int startInstructions(Intent intent, int flags, int startId){
        startTimer();
        return finishInstructions(intent, flags, startId);
    }

    public void startTimer() {
        handler = new Handler(Looper.getMainLooper());
        running = true;

        Runnable tickRunnable = new Runnable() {
            @Override
            public void run() {
                if (!running)
                    return;

                try {
                    tickService();
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                handler.postDelayed(this, TICK_DELAY);
            }
        };

        handler.post(tickRunnable);
    }

    public void stopTimer(){
        this.running = false;
    }
}
