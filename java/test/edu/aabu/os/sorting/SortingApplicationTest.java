package edu.aabu.os.sorting;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Headless test harness for the six-thread sorting pipeline.
 *
 * <p>Run from the project root after compiling:</p>
 * <pre>
 *   javac -d out $(find java/src java/test -name '*.java')
 *   java -cp out edu.aabu.os.sorting.SortingApplicationTest
 * </pre>
 */
public final class SortingApplicationTest {

    private static int passed;
    private static int total;

    private SortingApplicationTest() {
    }

    public static void main(String[] args) throws InterruptedException {
        runValid("Small array", "38, 12, 91, -4, 27, 63, 5, 50, 27, 0");
        runValid("Already sorted", "-20, -9, -3, 0, 4, 11, 18, 25, 30, 42");
        runValid("Reverse sorted", "42, 30, 25, 18, 11, 4, 0, -3, -9, -20");
        runValid("Duplicates", "7, 3, 7, 3, 7, 1, 3, 7, 1, 3");
        runValid("Negative numbers", "-15, -2, -99, 0, -7, -2, -45, 8, -1, -99");
        runValid("Odd-sized array", "61, 4, -8, 22, 15, 9, 0, 33, 17, -5, 71");
        runValid("Single element", "42");
        runValid("Large generated case", buildLargeInput(500));
        runRandom("Large random array", 1000);
        runInvalid("Empty input", "");
        runInvalid("Invalid integer", "4, abc, 9");
        runInvalid("Empty token", "4, , 9");

        System.out.println();
        System.out.printf("RESULT: %d/%d tests passed.%n", passed, total);
        if (passed != total) {
            System.exit(1);
        }
    }

    private static void runValid(String name, String input) throws InterruptedException {
        total++;
        Execution execution = execute(input, 0, false);
        if (execution.error != null) {
            fail(name, execution.error);
            return;
        }
        SortingContext context = execution.context;
        int[] expected = Arrays.stream(input.split(","))
                .map(String::trim)
                .mapToInt(Integer::parseInt)
                .sorted()
                .toArray();

        if (!context.isValid()) {
            fail(name, context.getValidationMessage());
        } else if (!Arrays.equals(expected, context.getFinalArray())) {
            fail(name, "expected " + Arrays.toString(expected) + " but got "
                    + Arrays.toString(context.getFinalArray()));
        } else if (!allThreadsReported(execution)) {
            fail(name, "not all six worker threads reported progress: " + execution.messages);
        } else {
            pass(name);
        }
    }

    private static void runRandom(String name, int count) throws InterruptedException {
        total++;
        Execution execution = execute("", count, true);
        if (execution.error != null) {
            fail(name, execution.error);
            return;
        }
        SortingContext context = execution.context;
        if (!context.isValid()) {
            fail(name, context.getValidationMessage());
        } else if (context.getFinalArray().length != count) {
            fail(name, "expected " + count + " elements but got " + context.getFinalArray().length);
        } else if (!allThreadsReported(execution)) {
            fail(name, "not all six worker threads reported progress.");
        } else {
            pass(name);
        }
    }

    private static void runInvalid(String name, String input) throws InterruptedException {
        total++;
        Execution execution = execute(input, 0, false);
        if (execution.error != null && execution.error.contains("Array Initialization")) {
            pass(name + " (rejected with clear message)");
        } else {
            fail(name, "expected an input error, but the pipeline reported: "
                    + (execution.error == null ? "success" : execution.error));
        }
    }

    private static Execution execute(String input, int count, boolean random)
            throws InterruptedException {
        AtomicReference<SortingContext> result = new AtomicReference<>();
        AtomicReference<String> error = new AtomicReference<>();
        StringBuilder messages = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);

        ProgressListener listener = new ProgressListener() {
            @Override
            public void onProgress(String message, int phase) {
                messages.append(message).append('\n');
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

        SortingController controller = new SortingController(input, count, random, listener);
        controller.start();
        if (!latch.await(20, TimeUnit.SECONDS)) {
            error.set("TIMEOUT: controller did not finish within 20 seconds.");
        }
        controller.join(1000);

        Execution execution = new Execution();
        execution.context = result.get();
        execution.error = error.get();
        execution.messages = messages.toString();
        return execution;
    }

    private static boolean allThreadsReported(Execution execution) {
        String text = execution.messages;
        return text.contains("T1") && text.contains("T2") && text.contains("T3")
                && text.contains("T4") && text.contains("T5") && text.contains("T6");
    }

    private static String buildLargeInput(int size) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append((i * 73) % 211 - 105);
        }
        return builder.toString();
    }

    private static void pass(String name) {
        passed++;
        System.out.printf("[PASS] %s%n", name);
    }

    private static void fail(String name, String reason) {
        System.out.printf("[FAIL] %s -> %s%n", name, reason);
    }

    private static final class Execution {
        private SortingContext context;
        private String error;
        private String messages = "";
    }
}
