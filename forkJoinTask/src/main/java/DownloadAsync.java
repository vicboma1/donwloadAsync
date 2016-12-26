import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;


/**
 * Created by vicboma on 26/12/16.
 */
public class DownloadAsync<T extends Pair<String,String>> {

    private static final int BUFFER = 1024;

    public static DownloadAsync create(){
        return new DownloadAsync();
    }

    DownloadAsync() {

    }

    public ForkJoinTask<T> submit(T elem) {
        return (ForkJoinTask<T>) new ForkJoinPool().submit(() -> {
            try {
                processSingleAttachment(elem).call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ForkJoinTask<T> submit(List<T> _list) {
        return (ForkJoinTask<T>) new ForkJoinPool(_list.size())
                .submit(() -> { processData(_list); });
    }

    private List<Boolean> processData(List<T> _list) {
        return _list
                .stream()
                .parallel()
                .map(it -> {
                    Boolean res = Boolean.FALSE;
                    try {
                        res = processSingleAttachment(it).call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return res;
                })
                .collect(Collectors.toList());
    }

    private Callable<Boolean> processSingleAttachment(T it) {
        return () -> {
            Boolean res = Boolean.TRUE;
            try {
                System.out.println("Processing task: "+it.value());
                execute(it);
                System.out.println("Process completed: "+it.value());
            } catch (IOException e) {
                res = Boolean.FALSE;
                e.printStackTrace();
            } finally {
                return res;
            }
        };
    }

    private void execute(T pair) throws IOException {
        final BufferedInputStream in = new java.io.BufferedInputStream(new URL(pair.key()).openStream());
        final FileOutputStream fos = new FileOutputStream(pair.value());
        final BufferedOutputStream bout = new BufferedOutputStream(fos);
        byte data[] = new byte[BUFFER];
        int read;
        while ((read = in.read(data, 0, BUFFER)) >= 0) {
            bout.write(data, 0, read);
        }
        bout.close();
        in.close();

    }
}