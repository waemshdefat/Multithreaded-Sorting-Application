package edu.aabu.os.sorting;

import java.util.Arrays;

/**
 * Shared, synchronized container for every intermediate result produced by the
 * six worker threads.
 *
 * <p>Design note for the OS course: the worker threads do communicate through
 * shared data, but they never read a partially written array. Each worker writes
 * its result into one synchronized setter, and the controller calls
 * {@code join()} before the next dependent thread is started. {@code join()}
 * creates a happens-before relationship, so all writes made by a finished
 * thread are visible to the thread that called {@code join()} and to every
 * thread started afterwards.</p>
 */
public class SortingContext {

    private int[] originalArray = new int[0];
    private int[] firstHalf = new int[0];
    private int[] secondHalf = new int[0];
    private int[] sortedFirstHalf = new int[0];
    private int[] sortedSecondHalf = new int[0];
    private int[] finalArray = new int[0];

    private String error = null;
    private boolean valid = false;
    private String validationMessage = "Not validated yet.";

    private long initNanos;
    private long divisionNanos;
    private long firstSortNanos;
    private long secondSortNanos;
    private long mergeNanos;
    private long validationNanos;
    private long sequentialNanos;
    private long totalNanos;

    /* ------------------------------------------------------------------ */
    /* Array setters (defensive copies keep the shared state encapsulated) */
    /* ------------------------------------------------------------------ */

    public synchronized void setOriginalArray(int[] values, long elapsedNanos) {
        this.originalArray = copyOf(values);
        this.initNanos = elapsedNanos;
    }

    public synchronized void setHalves(int[] first, int[] second, long elapsedNanos) {
        this.firstHalf = copyOf(first);
        this.secondHalf = copyOf(second);
        this.divisionNanos = elapsedNanos;
    }

    public synchronized void setSortedFirstHalf(int[] values, long elapsedNanos) {
        this.sortedFirstHalf = copyOf(values);
        this.firstSortNanos = elapsedNanos;
    }

    public synchronized void setSortedSecondHalf(int[] values, long elapsedNanos) {
        this.sortedSecondHalf = copyOf(values);
        this.secondSortNanos = elapsedNanos;
    }

    public synchronized void setFinalArray(int[] values, long elapsedNanos) {
        this.finalArray = copyOf(values);
        this.mergeNanos = elapsedNanos;
    }

    public synchronized void setValidation(boolean valid, String message, long elapsedNanos) {
        this.valid = valid;
        this.validationMessage = message == null ? "" : message;
        this.validationNanos = elapsedNanos;
    }

    /* ------------------------------------------------------------------ */
    /* Getters return copies so a consumer cannot mutate worker results.   */
    /* ------------------------------------------------------------------ */

    public synchronized int[] getOriginalArray() {
        return copyOf(originalArray);
    }

    public synchronized int[] getFirstHalf() {
        return copyOf(firstHalf);
    }

    public synchronized int[] getSecondHalf() {
        return copyOf(secondHalf);
    }

    public synchronized int[] getSortedFirstHalf() {
        return copyOf(sortedFirstHalf);
    }

    public synchronized int[] getSortedSecondHalf() {
        return copyOf(sortedSecondHalf);
    }

    public synchronized int[] getFinalArray() {
        return copyOf(finalArray);
    }

    /* ------------------------------------------------------------------ */
    /* Error and timing state                                              */
    /* ------------------------------------------------------------------ */

    public synchronized void setError(String message) {
        if (this.error == null) {
            this.error = message;
        }
    }

    public synchronized boolean hasError() {
        return error != null;
    }

    public synchronized String getError() {
        return error;
    }

    public synchronized boolean isValid() {
        return valid;
    }

    public synchronized String getValidationMessage() {
        return validationMessage;
    }

    public synchronized void setSequentialNanos(long sequentialNanos) {
        this.sequentialNanos = sequentialNanos;
    }

    public synchronized void setTotalNanos(long totalNanos) {
        this.totalNanos = totalNanos;
    }

    public synchronized long getInitNanos() {
        return initNanos;
    }

    public synchronized long getDivisionNanos() {
        return divisionNanos;
    }

    public synchronized long getFirstSortNanos() {
        return firstSortNanos;
    }

    public synchronized long getSecondSortNanos() {
        return secondSortNanos;
    }

    public synchronized long getMergeNanos() {
        return mergeNanos;
    }

    public synchronized long getValidationNanos() {
        return validationNanos;
    }

    public synchronized long getSequentialNanos() {
        return sequentialNanos;
    }

    public synchronized long getTotalNanos() {
        return totalNanos;
    }

    /** Six named workers plus the coordinator Thread that starts and joins them. */
    public int getWorkerThreadCount() {
        return 6;
    }

    public int getTotalThreadCount() {
        return 7;
    }

    private static int[] copyOf(int[] source) {
        return source == null ? new int[0] : Arrays.copyOf(source, source.length);
    }
}
