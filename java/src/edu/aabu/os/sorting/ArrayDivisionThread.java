package edu.aabu.os.sorting;

import java.util.Arrays;

/**
 * Thread 2 - Array Division.
 *
 * <p>Splits the original array at {@code length / 2}. Even lengths produce two
 * equal halves; odd lengths give the second half one extra element. Both parts
 * are independent copies, so the sorting threads never write to the same
 * memory region.</p>
 */
public class ArrayDivisionThread extends Thread {

    private final SortingContext context;
    private final ProgressListener listener;

    public ArrayDivisionThread(SortingContext context, ProgressListener listener) {
        super("T2-ArrayDivision");
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void run() {
        long start = System.nanoTime();
        try {
            if (listener != null) {
                listener.onProgress("T2: dividing the array into two halves...", 2);
            }

            int[] original = context.getOriginalArray();
            int middle = original.length / 2;

            int[] first = Arrays.copyOfRange(original, 0, middle);
            int[] second = Arrays.copyOfRange(original, middle, original.length);

            context.setHalves(first, second, System.nanoTime() - start);

            if (listener != null) {
                listener.onProgress("T2: split into " + first.length + " + " + second.length
                        + " elements.", 2);
            }
        } catch (RuntimeException exception) {
            context.setError("T2 - Array Division failed: " + exception.getMessage());
        }
    }
}
