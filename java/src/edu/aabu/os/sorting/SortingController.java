package edu.aabu.os.sorting;

/**
 * Coordinator thread that implements the complete six-thread workflow.
 *
 * <p>Timeline:</p>
 * <ol>
 *   <li>T1 initializes the array, then {@code join()}.</li>
 *   <li>T2 divides it, then {@code join()}.</li>
 *   <li>T3 and T4 are started together and both are joined before T5.</li>
 *   <li>T5 merges, then {@code join()}.</li>
 *   <li>T6 validates, then {@code join()}.</li>
 *   <li>The coordinator measures a sequential baseline for comparison.</li>
 * </ol>
 *
 * <p>The controller itself is also a real Java Thread, so the application
 * uses seven actual threads: six named workers plus one coordinator. Keeping
 * the workflow off the Swing Event Dispatch Thread prevents the GUI from
 * freezing.</p>
 */
public class SortingController extends Thread {

    private final String input;
    private final int randomCount;
    private final boolean useRandom;
    private final ProgressListener listener;

    public SortingController(String input,
                             int randomCount,
                             boolean useRandom,
                             ProgressListener listener) {
        super("Coordinator");
        this.input = input;
        this.randomCount = randomCount;
        this.useRandom = useRandom;
        this.listener = listener;
    }

    @Override
    public void run() {
        SortingContext context = new SortingContext();
        long pipelineStart = System.nanoTime();

        try {
            publish("Coordinator: starting the six-thread sorting pipeline...", 0);

            /* ---- Thread 1: initialization -------------------------------- */
            Thread initializer = new ArrayInitializerThread(
                    input, randomCount, useRandom, context, listener);
            initializer.start();
            initializer.join();
            abortIfFailed(context);

            /* ---- Thread 2: division -------------------------------------- */
            Thread divider = new ArrayDivisionThread(context, listener);
            divider.start();
            divider.join();
            abortIfFailed(context);

            /* ---- Threads 3 & 4: true parallel sorting --------------------- */
            Thread firstSorter = new SortingThread(true, context, listener);
            Thread secondSorter = new SortingThread(false, context, listener);
            publish("Coordinator: T3 and T4 are running in parallel...", 3);
            firstSorter.start();
            secondSorter.start();
            firstSorter.join();
            secondSorter.join();
            abortIfFailed(context);

            /* ---- Thread 5: merge (only after both sorts finished) --------- */
            Thread merger = new MergeThread(context, listener);
            merger.start();
            merger.join();
            abortIfFailed(context);

            /* ---- Thread 6: validation ------------------------------------ */
            Thread validator = new ValidationThread(context, listener);
            validator.start();
            validator.join();
            abortIfFailed(context);

            if (!context.isValid()) {
                throw new IllegalStateException(context.getValidationMessage());
            }

            /* Execution time covers the six-thread pipeline only. */
            context.setTotalNanos(System.nanoTime() - pipelineStart);

            /* Sequential baseline runs on the coordinator, not a new worker. */
            publish("Coordinator: measuring the sequential baseline...", 6);
            long sequentialStart = System.nanoTime();
            int[] sequentialCopy = context.getOriginalArray();
            MergeSort.sort(sequentialCopy);
            context.setSequentialNanos(System.nanoTime() - sequentialStart);

            publish("Coordinator: all operations completed successfully.", 6);
            if (listener != null) {
                listener.onComplete(context);
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            String message = "Execution was interrupted while waiting for a worker thread.";
            context.setError(message);
            if (listener != null) {
                listener.onError(message);
            }
        } catch (RuntimeException exception) {
            String message = exception.getMessage() == null
                    ? "Unexpected error during sorting."
                    : exception.getMessage();
            context.setError(message);
            if (listener != null) {
                listener.onError(message);
            }
        }
    }

    private void abortIfFailed(SortingContext context) {
        if (context.hasError()) {
            throw new IllegalStateException(context.getError());
        }
    }

    private void publish(String message, int phase) {
        if (listener != null) {
            listener.onProgress(message, phase);
        }
    }
}
