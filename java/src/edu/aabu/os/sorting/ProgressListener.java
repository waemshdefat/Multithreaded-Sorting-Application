package edu.aabu.os.sorting;

/**
 * Callback interface used by {@link SortingController} to report progress.
 *
 * <p>The controller runs on a background Java Thread. GUI implementations of this
 * interface must marshal updates to the Event Dispatch Thread with
 * {@code SwingUtilities.invokeLater()}. Headless code (for example, unit tests)
 * can implement the same interface without any Swing dependency.</p>
 */
public interface ProgressListener {

    /** Called before a worker thread starts and after important milestones. */
    void onProgress(String message, int phase);

    /** Called once, after all six worker threads and the baseline benchmark finished. */
    void onComplete(SortingContext context);

    /** Called when input parsing or thread execution fails. */
    void onError(String message);
}
