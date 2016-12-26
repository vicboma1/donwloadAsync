import java.util.Arrays;

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
        DownloadAsync.create().submit(Pair.create("https://github.com/vicboma1/donwloadAsync/blob/master/README.md","README.MD")).get();
        System.out.println("End Test");
    }

    @org.junit.Test
    public void testSubmit1() throws Exception {
        DownloadAsync.create().submit(
                Arrays.asList(
                    Pair.create("https://github.com/vicboma1/donwloadAsync/blob/master/README.md", "README.MD"),
                    Pair.create("https://github.com/vicboma1/donwloadAsync/blob/master/LICENSE", "LICENSE"),
                    Pair.create("https://github.com/vicboma1/donwloadAsync/blob/master/README.md", "README_2.MD"),
                    Pair.create("https://github.com/vicboma1/donwloadAsync/blob/master/LICENSE", "LICENSE_2")
                )
        ).get();

        System.out.println("End Test");
    }
}