package com.mpakhomov.jcip.nonstandard.cancellation;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.concurrent.*;

/**
 * @author mpakhomov
 * @since 8/13/2015
 */
public class DecoratedCancellableTaskTest {

    @Test
    public void testCancel() {
        final int NTHREADS = Runtime.getRuntime().availableProcessors();
        ThreadPoolExecutor exec = new CancellingExecutor(NTHREADS);

        DecoratedCancellableTask<Integer> task = new DecoratedCancellableTask<Integer>() {
            @Override
            public Integer call() throws Exception {
                System.out.println("inside DecoratedCancellableTask");
                try {
                    TimeUnit.SECONDS.sleep(6);
                } catch (InterruptedException e) {
                    System.out.println("Caught InterruptedException. Exiting.");
                }
                return Integer.valueOf(42);
            }
        };

        try {
            Future<Integer> result = exec.submit(task);
            TimeUnit.SECONDS.sleep(3);
            result.cancel(true);
//            int universalAnswer = result.get();
//            assertThat(universalAnswer, equalTo(42));
        } catch (InterruptedException ignored) {

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
