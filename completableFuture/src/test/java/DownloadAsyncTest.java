import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by vicboma on 26/12/16.
 */
public class DownloadAsyncTest {

    @org.junit.Before
    public void setUp() throws Exception {

    }

    @org.junit.After
    public void tearDown() throws Exception {

    }

    @org.junit.Test
    public void testSubmit() throws Exception {
        final CompletableFuture<FileOutputStream> completableFuture = DownloadAsync.create().submit(Pair.create("https://github.com/vicboma1/donwloadAsync/blob/master/README.md","README.MD"));
        completableFuture.thenAcceptAsync((fos) ->{
            try {
                System.out.println("Assert TRUE");
                assertTrue(fos.getFD().valid());
            } catch (IOException e) {
                e.printStackTrace();
                fail();
            }finally {
          }
        });

        System.out.println("End Test");

        completableFuture.get();
    }

    @org.junit.Test
    public void testSubmit1() throws Exception {
        final List<CompletableFuture<FileOutputStream>> listCompletable = DownloadAsync.create().submit(
                Arrays.asList(
                        Pair.create("https://github.com/vicboma1/donwloadAsync/blob/master/README.md", "README.MD"),
                        Pair.create("https://github.com/vicboma1/donwloadAsync/blob/master/LICENSE", "LICENSE"),
                        Pair.create("https://github.com/vicboma1/donwloadAsync/blob/master/README.md", "README_2.MD"),
                        Pair.create("https://github.com/vicboma1/donwloadAsync/blob/master/LICENSE", "LICENSE_2")
                )
        );


        final CompletableFuture<?> all = CompletableFuture.allOf(listCompletable.toArray(new CompletableFuture<?>[listCompletable.size()]));
        all.thenAcceptAsync(it -> {
            listCompletable
                    .stream()
                    .parallel()
                    .forEach(completable -> {
                        FileOutputStream fos = null;
                        try {
                            fos = completable.get();
                            System.out.println("Assert TRUE");
                        } catch (Exception e) {
                            e.printStackTrace();
                            fail();
                        }
                    });
        });

        System.out.println("End Test");


        all.get();
    }
}