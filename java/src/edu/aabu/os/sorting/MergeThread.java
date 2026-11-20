package edu.aabu.os.sorting;

/**
 * Thread 5 - Merging.
 *
 * <p>This thread is started only after the controller has joined both sorting
 * threads. It performs the classic two-pointer merge of two sorted runs into
 * one sorted array.</p>
 */
public class MergeThread extends Thread {

    private final SortingContext context;
    private final ProgressListener listener;

    public MergeThread(SortingContext context, ProgressListener listener) {
        super("T5-Merge");
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void run() {
        long start = System.nanoTime();
        try {
            if (listener != null) {
                listener.onProgress("T5: merging the two sorted halves...", 5);
            }

            int[] left = context.getSortedFirstHalf();
            int[] right = context.getSortedSecondHalf();
            int[] merged = new int[left.length + right.length];

            int i = 0;
            int j = 0;
            int k = 0;

            while (i < left.length && j < right.length) {
                if (left[i] <= right[j]) {
                    merged[k++] = left[i++];
                } else {
                    merged[k++] = right[j++];
                }
            }
            while (i < left.length) {
                merged[k++] = left[i++];
            }
            while (j < right.length) {
                merged[k++] = right[j++];
            }

            context.setFinalArray(merged, System.nanoTime() - start);

            if (listener != null) {
                listener.onProgress("T5: merged " + merged.length + " integers.", 5);
            }
        } catch (RuntimeException exception) {
            context.setError("T5 - Merging failed: " + exception.getMessage());
        }
    }
}
