package edu.aabu.os.sorting;

import java.util.Random;

/**
 * Thread 1 - Array Initialization.
 *
 * <p>Reads the comma-separated text typed in the GUI, or generates random
 * integers, and publishes the completed original array into the shared
 * {@link SortingContext}. Invalid input is reported through the context so the
 * controller can stop before starting any dependent thread.</p>
 */
public class ArrayInitializerThread extends Thread {

    private static final int MAX_ELEMENTS = 100_000;
    private static final int RANDOM_BOUND = 10_000;

    private final String input;
    private final int randomCount;
    private final boolean useRandom;
    private final SortingContext context;
    private final ProgressListener listener;

    public ArrayInitializerThread(String input,
                                  int randomCount,
                                  boolean useRandom,
                                  SortingContext context,
                                  ProgressListener listener) {
        super("T1-ArrayInitializer");
        this.input = input;
        this.randomCount = randomCount;
        this.useRandom = useRandom;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public void run() {
        long start = System.nanoTime();
        try {
            notifyProgress("T1: initializing the original array...", 1);

            int[] values;
            if (useRandom) {
                values = generateRandomValues(randomCount);
            } else {
                values = parseInput(input);
            }

            context.setOriginalArray(values, System.nanoTime() - start);
            notifyProgress("T1: initialized " + values.length + " integers.", 1);
        } catch (IllegalArgumentException exception) {
            context.setError("T1 - Array Initialization: " + exception.getMessage());
        } catch (RuntimeException exception) {
            context.setError("T1 - Array Initialization failed: " + exception.getMessage());
        }
    }

    private int[] generateRandomValues(int count) {
        if (count <= 0 || count > MAX_ELEMENTS) {
            throw new IllegalArgumentException(
                    "Random element count must be between 1 and " + MAX_ELEMENTS + ".");
        }
        Random random = new Random();
        int[] values = new int[count];
        for (int i = 0; i < count; i++) {
            values[i] = random.nextInt(RANDOM_BOUND * 2 + 1) - RANDOM_BOUND;
        }
        return values;
    }

    /** Parses values such as {@code 5, -2, 8, 8, 0}. */
    static int[] parseInput(String rawInput) {
        if (rawInput == null || rawInput.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Input is empty. Enter integers separated by commas, or generate random numbers.");
        }

        String[] tokens = rawInput.split(",", -1);
        if (tokens.length > MAX_ELEMENTS) {
            throw new IllegalArgumentException("At most " + MAX_ELEMENTS + " values are supported.");
        }

        int[] values = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();
            if (token.isEmpty()) {
                throw new IllegalArgumentException("Empty value at position " + (i + 1) + ".");
            }
            try {
                values[i] = Integer.parseInt(token);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException(
                        "Invalid integer \"" + token + "\" at position " + (i + 1) + ".");
            }
        }
        return values;
    }

    private void notifyProgress(String message, int phase) {
        if (listener != null) {
            listener.onProgress(message, phase);
        }
    }
}
