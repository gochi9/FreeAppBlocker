package gmail.developer_formal.freeappblocker.services.AbstractServices;

import android.content.Intent;

public abstract class TickableService extends NotificationService {

    private boolean running = false;
    private final int TICK_DELAY;

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

    private void startTimer(){
        this.running = true;

        new Thread(() -> {
            while (running && !Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(TICK_DELAY);
                    tickService();
                }
                catch (Throwable e) {
                    if (e instanceof InterruptedException){
                        stopTimer();
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }).start();
    }

    public void stopTimer(){
        this.running = false;
    }
}
