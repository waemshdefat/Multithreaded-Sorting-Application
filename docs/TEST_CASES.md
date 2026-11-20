# Test Cases and Expected Outputs

All cases below were executed against the real Java controller with
`edu.aabu.os.sorting.SortingApplicationTest` on Temurin OpenJDK 17.0.21.1.
"Pass" means the coordinator completed, `T6` reported `VALID`, and the final
array equalled the expected sorted array.

## Command

```bash
cd java
./run-tests.sh
```

## Executed results

| # | Test case | Input (or generation) | Expected behaviour | Actual result |
|---:|---|---|---|---|
| 1 | Small array | `38, 12, 91, -4, 27, 63, 5, 50, 27, 0` | `-4, 0, 5, 12, 27, 27, 38, 50, 63, 91` | PASS |
| 2 | Already sorted | `-20, -9, -3, 0, 4, 11, 18, 25, 30, 42` | Unchanged order | PASS |
| 3 | Reverse sorted | `42, 30, 25, 18, 11, 4, 0, -3, -9, -20` | Fully ascending | PASS |
| 4 | Duplicates | `7, 3, 7, 3, 7, 1, 3, 7, 1, 3` | `1, 1, 3, 3, 3, 3, 7, 7, 7, 7` | PASS |
| 5 | Negative numbers | `-15, -2, -99, 0, -7, -2, -45, 8, -1, -99` | `-99, -99, -45, -15, -7, -2, -2, -1, 0, 8` | PASS |
| 6 | Odd-sized array | `61, 4, -8, 22, 15, 9, 0, 33, 17, -5, 71` | 11 values sorted; second half receives 6 elements | PASS |
| 7 | Single element | `42` | `42`; first half empty, second half one element | PASS |
| 8 | Large generated case | 500 deterministic values | Sorted; all values preserved | PASS |
| 9 | Large random array | 1,000 random values in `[-10000, 10000]` | Sorted; length 1,000 | PASS |
| 10 | Empty input | `` (empty string) | T1 error: input is empty; no dependent threads start | PASS |
| 11 | Invalid integer | `4, abc, 9` | T1 error naming `abc` at position 2 | PASS |
| 12 | Empty token | `4, , 9` | T1 error naming position 2 | PASS |

Observed console summary:

```text
RESULT: 12/12 tests passed.
```

## Important note about odd lengths

The division rule is `middle = original.length / 2`:

| Length | First half | Second half |
|---:|---:|---:|
| 10 | 5 | 5 |
| 11 | 5 | 6 |
| 1 | 0 | 1 |

The merge loop handles empty halves naturally, so no special case is needed later in the pipeline.

## Performance benchmark (actually executed)

Command:

```bash
cd java
./compile.sh
java -cp out edu.aabu.os.sorting.PerformanceBenchmark
```

Environment printed by the JVM: `OpenJDK 64-Bit Server VM | 17.0.21.1 | processors=2`.
One warm-up run per size was excluded; five measured runs were averaged.

| Elements | Parallel pipeline (ms) | Sequential merge sort (ms) | seq/par |
|---------:|-----------------------:|---------------------------:|--------:|
| 100 | 1.380 | 0.019 | 0.01x |
| 1,000 | 1.886 | 0.137 | 0.07x |
| 10,000 | 7.107 | 1.140 | 0.16x |
| 50,000 | 12.965 | 6.031 | 0.47x |

Interpretation: `seq/par < 1` means the sequential version was faster. On a
2-processor sandbox with small-to-medium arrays, thread creation and `join()`
overhead dominate. The application is correct; the result demonstrates that
parallelism is not automatically faster.

## GUI verification status

The GUI source compiles cleanly, but the build sandbox was headless, so the
window was not launched and no screenshot was captured. After running `./run.sh`
on a desktop, manually repeat cases 1–6 and confirm the six display panels,
status indicator, thread count, execution time, and validation message.
