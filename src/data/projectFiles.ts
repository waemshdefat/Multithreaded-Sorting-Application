import MainJava from '../../java/src/edu/aabu/os/sorting/Main.java?raw';
import MainFrameJava from '../../java/src/edu/aabu/os/sorting/MainFrame.java?raw';
import SortingControllerJava from '../../java/src/edu/aabu/os/sorting/SortingController.java?raw';
import SortingContextJava from '../../java/src/edu/aabu/os/sorting/SortingContext.java?raw';
import ProgressListenerJava from '../../java/src/edu/aabu/os/sorting/ProgressListener.java?raw';
import ArrayInitializerThreadJava from '../../java/src/edu/aabu/os/sorting/ArrayInitializerThread.java?raw';
import ArrayDivisionThreadJava from '../../java/src/edu/aabu/os/sorting/ArrayDivisionThread.java?raw';
import SortingThreadJava from '../../java/src/edu/aabu/os/sorting/SortingThread.java?raw';
import MergeThreadJava from '../../java/src/edu/aabu/os/sorting/MergeThread.java?raw';
import ValidationThreadJava from '../../java/src/edu/aabu/os/sorting/ValidationThread.java?raw';
import MergeSortJava from '../../java/src/edu/aabu/os/sorting/MergeSort.java?raw';
import SortingApplicationTestJava from '../../java/test/edu/aabu/os/sorting/SortingApplicationTest.java?raw';
import PerformanceBenchmarkJava from '../../java/test/edu/aabu/os/sorting/PerformanceBenchmark.java?raw';
import CompileSh from '../../java/compile.sh?raw';
import RunSh from '../../java/run.sh?raw';
import RunTestsSh from '../../java/run-tests.sh?raw';
import ReadmeMd from '../../README.md?raw';
import ReportMd from '../../docs/REPORT.md?raw';
import PresentationMd from '../../docs/PRESENTATION.md?raw';
import TestCasesMd from '../../docs/TEST_CASES.md?raw';

export type ProjectFile = {
  name: string;
  path: string;
  description: string;
  language: string;
  source: string;
};

export const javaFiles: ProjectFile[] = [
  {
    name: 'Main.java',
    path: 'java/src/edu/aabu/os/sorting/Main.java',
    description: 'Entry point; creates the Swing frame on the Event Dispatch Thread.',
    language: 'java',
    source: MainJava,
  },
  {
    name: 'MainFrame.java',
    path: 'java/src/edu/aabu/os/sorting/MainFrame.java',
    description: 'Dark Swing GUI, controls, array panels and custom visualization.',
    language: 'java',
    source: MainFrameJava,
  },
  {
    name: 'SortingController.java',
    path: 'java/src/edu/aabu/os/sorting/SortingController.java',
    description: 'Coordinator Thread: starts workers and joins them in the required order.',
    language: 'java',
    source: SortingControllerJava,
  },
  {
    name: 'SortingContext.java',
    path: 'java/src/edu/aabu/os/sorting/SortingContext.java',
    description: 'Synchronized shared state with defensive copies and timing fields.',
    language: 'java',
    source: SortingContextJava,
  },
  {
    name: 'ProgressListener.java',
    path: 'java/src/edu/aabu/os/sorting/ProgressListener.java',
    description: 'Callback interface used by the GUI and by headless tests.',
    language: 'java',
    source: ProgressListenerJava,
  },
  {
    name: 'ArrayInitializerThread.java',
    path: 'java/src/edu/aabu/os/sorting/ArrayInitializerThread.java',
    description: 'Thread 1 — parses input or generates random integers.',
    language: 'java',
    source: ArrayInitializerThreadJava,
  },
  {
    name: 'ArrayDivisionThread.java',
    path: 'java/src/edu/aabu/os/sorting/ArrayDivisionThread.java',
    description: 'Thread 2 — splits into two halves at length / 2.',
    language: 'java',
    source: ArrayDivisionThreadJava,
  },
  {
    name: 'SortingThread.java',
    path: 'java/src/edu/aabu/os/sorting/SortingThread.java',
    description: 'Threads 3 and 4 — two instances sort the halves concurrently.',
    language: 'java',
    source: SortingThreadJava,
  },
  {
    name: 'MergeThread.java',
    path: 'java/src/edu/aabu/os/sorting/MergeThread.java',
    description: 'Thread 5 — two-pointer merge of the two sorted halves.',
    language: 'java',
    source: MergeThreadJava,
  },
  {
    name: 'ValidationThread.java',
    path: 'java/src/edu/aabu/os/sorting/ValidationThread.java',
    description: 'Thread 6 — checks sorted order and preservation of all values.',
    language: 'java',
    source: ValidationThreadJava,
  },
  {
    name: 'MergeSort.java',
    path: 'java/src/edu/aabu/os/sorting/MergeSort.java',
    description: 'Stable recursive merge sort used by workers and the baseline.',
    language: 'java',
    source: MergeSortJava,
  },
];

export const supportFiles: ProjectFile[] = [
  {
    name: 'SortingApplicationTest.java',
    path: 'java/test/edu/aabu/os/sorting/SortingApplicationTest.java',
    description: '12 headless tests that execute the real six-thread controller.',
    language: 'java',
    source: SortingApplicationTestJava,
  },
  {
    name: 'PerformanceBenchmark.java',
    path: 'java/test/edu/aabu/os/sorting/PerformanceBenchmark.java',
    description: 'Measures sequential versus threaded times across several sizes.',
    language: 'java',
    source: PerformanceBenchmarkJava,
  },
  {
    name: 'compile.sh',
    path: 'java/compile.sh',
    description: 'Compiles all sources into java/out.',
    language: 'bash',
    source: CompileSh,
  },
  {
    name: 'run.sh',
    path: 'java/run.sh',
    description: 'Launches the Swing desktop application.',
    language: 'bash',
    source: RunSh,
  },
  {
    name: 'run-tests.sh',
    path: 'java/run-tests.sh',
    description: 'Compiles and runs the headless test suite.',
    language: 'bash',
    source: RunTestsSh,
  },
  {
    name: 'README.md',
    path: 'README.md',
    description: 'Setup, execution, structure and verification notes.',
    language: 'markdown',
    source: ReadmeMd,
  },
];

export const documents = {
  report: ReportMd,
  presentation: PresentationMd,
  testCases: TestCasesMd,
  readme: ReadmeMd,
};

export const benchmarkRows = [
  { n: '100', parallel: '1.380', sequential: '0.019', ratio: '0.01x' },
  { n: '1,000', parallel: '1.886', sequential: '0.137', ratio: '0.07x' },
  { n: '10,000', parallel: '7.107', sequential: '1.140', ratio: '0.16x' },
  { n: '50,000', parallel: '12.965', sequential: '6.031', ratio: '0.47x' },
];

export const threadRows = [
  { id: 'T1', name: 'Initialization', cls: 'ArrayInitializerThread', detail: 'Parses input or generates random integers', depends: '—' },
  { id: 'T2', name: 'Division', cls: 'ArrayDivisionThread', detail: 'Splits at length / 2; odd length gives second half one extra', depends: 'T1' },
  { id: 'T3', name: 'Sort first half', cls: 'SortingThread(true)', detail: 'Merge sort on a private copy of the first half', depends: 'T2' },
  { id: 'T4', name: 'Sort second half', cls: 'SortingThread(false)', detail: 'Runs at the same time as T3', depends: 'T2' },
  { id: 'T5', name: 'Merge', cls: 'MergeThread', detail: 'Two-pointer merge; starts only after T3 and T4 are joined', depends: 'T3 + T4' },
  { id: 'T6', name: 'Validation', cls: 'ValidationThread', detail: 'Checks order and value multiset, then reports statistics', depends: 'T5' },
];
