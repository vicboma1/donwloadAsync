import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by vicboma on 26/12/16.
 */
public class ThreadFactoryAsync implements ThreadFactory {

    private AtomicInteger uid = new AtomicInteger(0);

    public static ThreadFactoryAsync create(){
        return new ThreadFactoryAsync();
    }

    public ThreadFactoryAsync() {
    }

    @Override
    public Thread newThread(Runnable runnable) {
        return new Thread(runnable, "Async thread: "+uid.getAndIncrement());
    }
}