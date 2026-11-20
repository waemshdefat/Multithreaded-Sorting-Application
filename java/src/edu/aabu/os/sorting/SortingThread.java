package edu.aabu.os.sorting;

import java.util.Arrays;

/**
 * Threads 3 and 4 - First Half Sorting and Second Half Sorting.
 *
 * <p>Two instances of this class are started at the same time by the
 * controller. Both run merge sort on their own private copy of a half, so no
 * lock is needed during the actual sorting work.</p>
 */
public class SortingThread extends Thread {

    private final boolean firstHalf;
    private final SortingContext context;
    private final ProgressListener listener;
    private final String threadLabel;

    public SortingThread(boolean firstHalf, SortingContext context, ProgressListener listener) {
        super(firstHalf ? "T3-FirstHalfSort" : "T4-SecondHalfSort");
        this.firstHalf = firstHalf;
        this.context = context;
        this.listener = listener;
        this.threadLabel = firstHalf ? "T3 - First Half Sorting" : "T4 - Second Half Sorting";
    }

    @Override
    public void run() {
        long start = System.nanoTime();
        try {
            if (listener != null) {
                listener.onProgress(threadLabel + ": sorting with merge sort...", firstHalf ? 3 : 4);
            }

            int[] source = firstHalf ? context.getFirstHalf() : context.getSecondHalf();
            int[] sorted = Arrays.copyOf(source, source.length);
            MergeSort.sort(sorted);

            long elapsed = System.nanoTime() - start;
            if (firstHalf) {
                context.setSortedFirstHalf(sorted, elapsed);
            } else {
                context.setSortedSecondHalf(sorted, elapsed);
            }

            if (listener != null) {
                listener.onProgress(threadLabel + ": finished " + sorted.length + " elements.",
                        firstHalf ? 3 : 4);
            }
        } catch (RuntimeException exception) {
            context.setError(threadLabel + " failed: " + exception.getMessage());
        }
    }
}
