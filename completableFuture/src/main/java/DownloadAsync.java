import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * Created by vicboma on 26/12/16.
 */
public class DownloadAsync<T extends CompletableFuture<File>, H extends Pair<String,String>> {

    private ConcurrentLinkedQueue<Pair<T,H>> queue;

    private static final int BUFFER = 1024;
    private static final int MILLISECONDS_THREAD_SLEEP_ = 100;
    private static final int MILLISECOND_THREAD_SLEEP_TASK_RESOLVED = 25;

    public static DownloadAsync create(){
        return new DownloadAsync();
    }

    DownloadAsync() {
        queue = new ConcurrentLinkedQueue();
        processData();
    }

    private void processData() {
       final Thread thread = new Thread(() -> {

           while (!Thread.currentThread().isInterrupted()) {

            final ConcurrentLinkedQueue<Pair<T,H>> _priorityQueue = queue;

            if (_priorityQueue.isEmpty()) {
                try {
                    Thread.sleep(MILLISECONDS_THREAD_SLEEP_);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
               else {
                final int size = _priorityQueue.size();
                for (int i = 0; i < size; i++) {
                    final Pair<T,H> poll = _priorityQueue.poll();
                    try {
                        poll.key().complete(processSingleAttachment(poll).get());
                        //System.out.println("Taks Resolved ******************** " + poll.toString());

                    } catch (Exception e) {
                        System.err.println("Taks Rejected ******************** " + poll.toString());
                        _priorityQueue.add(poll);
                    } finally {

                    }
                }

                try {
                    Thread.sleep(MILLISECOND_THREAD_SLEEP_TASK_RESOLVED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
           }
      });

        thread.setDaemon(true);
        thread.start();

    }


    public T submit(H elem) {
        final CompletableFuture<File> completableFuture = new CompletableFuture<File>();
        final Pair<T,H> pair = Pair.create(completableFuture,elem);
        queue.add(pair);
        return (T)completableFuture;
    }

    public List<T> submit(List<H> list) {
        final List<T> listCompletable = new ArrayList<T>();
        list.stream()
            .filter(it -> it != null)
            .forEach(it -> {
                T res = submit(it);
                listCompletable.add(res);
        });

        return listCompletable;
    }


    private CompletableFuture<File> processSingleAttachment(Pair<T,H> it) {
        return CompletableFuture.supplyAsync(() -> {
            File fileOS = null;
                    try {
                        Pair<String,String> pair = it.value();
                        System.out.println("Processing task: " +pair.value());
                        fileOS = execute(pair);
                        System.out.println("Process completed: " + pair.value());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }finally {
                        return fileOS;
                    }
                }
        );
    }

    private File execute(Pair<String,String> pair) throws IOException {
        final BufferedInputStream in = new BufferedInputStream(new URL(pair.key()).openStream());
        final FileOutputStream fos = new FileOutputStream(pair.value());
        final BufferedOutputStream bout = new BufferedOutputStream(fos);

        byte data[] = new byte[BUFFER];
        int read;
        while ((read = in.read(data, 0, BUFFER)) >= 0) {
            bout.write(data, 0, read);
        }
        bout.close();
        in.close();

        return new File(pair.value());
    }
}