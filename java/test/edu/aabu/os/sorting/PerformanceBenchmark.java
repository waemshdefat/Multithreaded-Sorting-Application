package edu.aabu.os.sorting;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Optional benchmark that runs the real six-thread controller and the
 * sequential baseline for several input sizes.
 *
 * <p>Results depend on CPU, JVM warm-up and scheduler load, so treat the
 * numbers as an illustration, not a universal proof that threads are faster.</p>
 */
public final class PerformanceBenchmark {

    private PerformanceBenchmark() {
    }

    public static void main(String[] args) throws InterruptedException {
        int[] sizes = {100, 1_000, 10_000, 50_000};
        int repeats = 5;

        System.out.println("Environment: " + System.getProperty("java.vm.name")
                + " | " + System.getProperty("java.version")
                + " | processors=" + Runtime.getRuntime().availableProcessors());
        System.out.printf("%10s %14s %14s %10s%n",
                "elements", "parallel(ms)", "sequential(ms)", "seq/par");

        for (int size : sizes) {
            execute(size); // warm-up, not measured

            double parallelTotal = 0;
            double sequentialTotal = 0;
            for (int i = 0; i < repeats; i++) {
                SortingContext context = execute(size);
                parallelTotal += context.getTotalNanos() / 1_000_000.0;
                sequentialTotal += context.getSequentialNanos() / 1_000_000.0;
            }

            double parallel = parallelTotal / repeats;
            double sequential = sequentialTotal / repeats;
            System.out.printf("%10d %14.3f %14.3f %10.2fx%n",
                    size, parallel, sequential, parallel == 0 ? 0 : sequential / parallel);
        }
    }

    private static SortingContext execute(int count) throws InterruptedException {
        AtomicReference<SortingContext> result = new AtomicReference<>();
        AtomicReference<String> error = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);

        ProgressListener quietListener = new ProgressListener() {
            @Override
            public void onProgress(String message, int phase) {
                // Intentionally silent during benchmarking.
            }

            @Override
            public void onComplete(SortingContext context) {
                result.set(context);
                latch.countDown();
            }

            @Override
            public void onError(String message) {
                error.set(message);
                latch.countDown();
            }
        };

        SortingController controller = new SortingController("", count, true, quietListener);
        controller.start();
        if (!latch.await(60, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Benchmark run timed out for n=" + count);
        }
        controller.join(1000);
        if (error.get() != null) {
            throw new IllegalStateException(error.get());
        }
        return result.get();
    }
}
