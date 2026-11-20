package edu.aabu.os.sorting;

/**
 * Classic recursive merge sort used by both sorting worker threads and by the
 * sequential baseline benchmark.
 *
 * <p>Complexity: O(n log n) time, O(n) auxiliary space. The implementation is
 * deliberately stable: equal elements keep their original relative order
 * because the left element is chosen when values are equal.</p>
 */
public final class MergeSort {

    private MergeSort() {
        // Utility class: no instances.
    }

    /** Sorts {@code array} in ascending order in place. */
    public static void sort(int[] array) {
        if (array == null || array.length < 2) {
            return;
        }
        int[] auxiliary = new int[array.length];
        sort(array, auxiliary, 0, array.length - 1);
    }

    private static void sort(int[] array, int[] auxiliary, int low, int high) {
        if (low >= high) {
            return;
        }
        int middle = low + (high - low) / 2;
        sort(array, auxiliary, low, middle);
        sort(array, auxiliary, middle + 1, high);
        merge(array, auxiliary, low, middle, high);
    }

    private static void merge(int[] array, int[] auxiliary, int low, int middle, int high) {
        for (int i = low; i <= high; i++) {
            auxiliary[i] = array[i];
        }

        int left = low;
        int right = middle + 1;

        for (int current = low; current <= high; current++) {
            if (left > middle) {
                array[current] = auxiliary[right++];
            } else if (right > high) {
                array[current] = auxiliary[left++];
            } else if (auxiliary[left] <= auxiliary[right]) {
                array[current] = auxiliary[left++];
            } else {
                array[current] = auxiliary[right++];
            }
        }
    }

    /** Returns true when the array is in non-decreasing order. */
    public static boolean isSorted(int[] array) {
        if (array == null || array.length < 2) {
            return true;
        }
        for (int i = 1; i < array.length; i++) {
            if (array[i - 1] > array[i]) {
                return false;
            }
        }
        return true;
    }
}
