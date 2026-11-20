package edu.aabu.os.sorting;

import java.util.HashMap;
import java.util.Map;

/**
 * Thread 6 - Validation and Results.
 *
 * <p>Checks two independent properties: (1) the final array is sorted in
 * non-decreasing order, and (2) sorting preserved every original value,
 * including duplicates and negative numbers.</p>
 */
public class ValidationThread extends Thread {

    private final SortingContext context;
    private final ProgressListener listener;

    public ValidationThread(SortingContext context, ProgressListener listener) {
        super("T6-Validation");
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void run() {
        long start = System.nanoTime();
        try {
            if (listener != null) {
                listener.onProgress("T6: validating sorted order and element preservation...", 6);
            }

            int[] original = context.getOriginalArray();
            int[] result = context.getFinalArray();

            boolean lengthMatches = original.length == result.length;
            boolean orderIsCorrect = MergeSort.isSorted(result);
            boolean valuesMatch = lengthMatches && sameMultiset(original, result);
            boolean valid = lengthMatches && orderIsCorrect && valuesMatch;

            String message;
            if (valid) {
                message = "VALID - sorted order confirmed and all " + result.length
                        + " original values preserved.";
            } else {
                message = "INVALID - length match: " + lengthMatches
                        + ", sorted order: " + orderIsCorrect
                        + ", values preserved: " + valuesMatch + ".";
            }

            context.setValidation(valid, message, System.nanoTime() - start);

            if (listener != null) {
                listener.onProgress("T6: " + message, 6);
            }
        } catch (RuntimeException exception) {
            context.setError("T6 - Validation failed: " + exception.getMessage());
        }
    }

    /** Compares frequency maps so duplicates and negative values are handled correctly. */
    private boolean sameMultiset(int[] left, int[] right) {
        Map<Integer, Integer> frequencies = new HashMap<>();
        for (int value : left) {
            frequencies.merge(value, 1, Integer::sum);
        }
        for (int value : right) {
            Integer count = frequencies.get(value);
            if (count == null) {
                return false;
            }
            if (count == 1) {
                frequencies.remove(value);
            } else {
                frequencies.put(value, count - 1);
            }
        }
        return frequencies.isEmpty();
    }
}
