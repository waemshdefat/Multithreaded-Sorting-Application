# Multithreaded Sorting Application — Presentation

**Course:** Operating Systems (901332) · Al al-Bayt University  
**Semester:** First Semester 2026 · Instructor: Dr. Ashraf Al-Aoun  
**Format:** 9 slides with speaker notes

---

## Slide 1 — Title

### Multithreaded Sorting Application

* Operating Systems (901332)
* Al al-Bayt University — First Semester 2026
* Dr. Ashraf Al-Aoun
* Java 17 · Swing GUI · Six actual worker threads

**Speaker notes:**  
"Good morning. Today I will present a Java desktop application that sorts integers using six real threads. The goal is not only to sort numbers, but to demonstrate thread creation, synchronization with join(), and safe communication through shared data."

---

## Slide 2 — Problem and Objectives

### Why this project?

A sequential sort cannot show how an OS coordinates concurrent activities.

**Objectives**

1. Divide an array into two nearly equal parts.
2. Sort both parts concurrently with merge sort.
3. Merge only after both sorting threads finish.
4. Validate the final result.
5. Show every stage in a GUI.
6. Compare with a sequential baseline honestly.

**Speaker notes:**  
"The main problem is educational: we need visible, real threads rather than a simulated animation. The key constraint is precedence — the merge thread must not start before both sort threads finish. We also validate that no values were lost or duplicated."

---

## Slide 3 — Merge Sort Recap

### Divide → Conquer → Combine

```text
[38, 12, 91, -4, 27, 63, 5, 50, 27, 0]
        split at length / 2
[38, 12, 91, -4, 27]   [63, 5, 50, 27, 0]
        sort each part
[-4, 12, 27, 38, 91]   [0, 5, 27, 50, 63]
              merge
[-4, 0, 5, 12, 27, 27, 38, 50, 63, 91]
```

* Time: `O(n log n)` · Space: `O(n)`
* Stable because equal values take from the left side first.

**Speaker notes:**  
"Merge sort is ideal because its top-level structure maps naturally onto threads. In our project the first split is done by Thread 2, and the two halves are sorted by Threads 3 and 4. The recursive calls inside each half are ordinary method calls, not new threads."

---

## Slide 4 — Multithreading Concepts

### What the code demonstrates

* `new Thread(...)` creates a thread object.
* `start()` creates a new execution flow and calls `run()`.
* `join()` makes one thread wait for another.
* `synchronized` protects shared state.
* Defensive copies prevent stale or partially written data.
* Swing updates must run on the Event Dispatch Thread.

**Speaker notes:**  
"Calling run() directly would not create a new thread — that is a common mistake. We call start() for every worker. join() establishes a happens-before relationship, so writes made by a finished thread are visible afterwards. The controller never touches Swing components; it reports progress through a listener that uses invokeLater()."

---

## Slide 5 — Thread Architecture

```text
Swing MainFrame  ──start──▶  SortingController (Coordinator Thread)
                                  │
     T1 Init ─▶ T2 Divide ─▶ (T3 Sort A ‖ T4 Sort B) ─▶ T5 Merge ─▶ T6 Validate
                                  │
                          SortingContext (synchronized)
```

**Total: 7 actual Java Threads** = 6 workers + 1 coordinator.

**Speaker notes:**  
"The coordinator is itself a real Thread. It starts workers with start() and waits with join(). The six named workers are T1 initialization, T2 division, T3 and T4 sorting, T5 merging, and T6 validation. The GUI's EDT is separate and is not counted in the pipeline."

---

## Slide 6 — Six-Thread Workflow

| Thread | Responsibility | Must finish before |
|---|---|---|
| T1 | Initialize from input / random | T2 |
| T2 | Divide into two halves | T3, T4 |
| T3 | Sort first half | T5 |
| T4 | Sort second half | T5 |
| T5 | Merge both sorted halves | T6 |
| T6 | Validate order and values | Results |

**Key code:** `firstSorter.join(); secondSorter.join();` then start T5.

**Speaker notes:**  
"This table is the heart of the defense. T3 and T4 are started consecutively and then both joined. Only after both joins does the coordinator create and start T5. That is how we guarantee the merge operation starts with complete sorted halves."

---

## Slide 7 — GUI Demonstration

### Live walkthrough

1. Load a test case or enter comma-separated integers.
2. Optionally generate random values.
3. Press **Start Sorting**.
4. Watch the six pipeline stages light up.
5. Inspect original, halves, sorted halves, and final array.
6. Read validation, thread count, and execution time.

**Speaker notes / live demo script:**  
"For the demo I will use the small array with duplicates and negative numbers: 38, 12, 91, -4, 27, 63, 5, 50, 27, 0. Notice the status message changes as each thread reports progress. The first and second halves are shown separately, then the sorted halves, then the final merged array. The bar chart turns cyan for first-half elements and violet for second-half elements."

*If presenting offline: insert real application screenshots here after running `./java/run.sh`.*

---

## Slide 8 — Testing and Results

### Executed headless test suite

* 12 / 12 tests passed with Temurin JDK 17.0.21.1.
* Covered: small, large, sorted, reverse, duplicates, negatives, odd length, single element, random, empty, invalid integer, empty token.
* Every successful test confirmed all T1–T6 messages and `VALID` status.

```text
[PASS] Small array
[PASS] Duplicates
[PASS] Odd-sized array
[PASS] Empty input (rejected with clear message)
RESULT: 12/12 tests passed.
```

**Speaker notes:**  
"The tests run the real SortingController, not a JavaScript or pseudo animation. Invalid input is tested too: T1 rejects it before T2 starts. Validation uses a frequency map, so duplicates and negative values are checked correctly."

---

## Slide 9 — Performance and Conclusion

### Measured on a 2-processor environment

| n | Parallel (ms) | Sequential (ms) |
|---:|---:|---:|
| 100 | 1.380 | 0.019 |
| 1,000 | 1.886 | 0.137 |
| 10,000 | 7.107 | 1.140 |
| 50,000 | 12.965 | 6.031 |

**Conclusion**

* All mandatory OS requirements are satisfied.
* `start()` + `join()` + synchronized shared state prevent incorrect execution.
* Multithreading is **not always faster**; overhead matters.

**Speaker notes:**  
"These are the actual benchmark numbers from our build environment. The sequential version was faster because we created seven threads, performed several joins, copied arrays, and had only two processors. I will not claim speedup. The important learning outcome is that concurrency is a tool with costs, and correctness and synchronization come first. Thank you — I am ready for questions."

---

## Defense tip

If asked "Why seven threads when the requirement says six?":  
The specification requires six worker threads for the six stages. The coordinator
that starts and joins them is also implemented as a real Java Thread, so the
honest total is seven. The GUI reports "6 workers + 1 coordinator = 7 actual
Threads".
