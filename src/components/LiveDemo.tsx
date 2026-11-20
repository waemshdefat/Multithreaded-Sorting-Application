import { useRef, useState } from 'react';

type Arrays = {
  original: number[];
  first: number[];
  second: number[];
  sortedFirst: number[];
  sortedSecond: number[];
  final: number[];
};

const EMPTY: Arrays = {
  original: [], first: [], second: [], sortedFirst: [], sortedSecond: [], final: [],
};

const PRESETS: Record<string, string> = {
  'Small sample': '38, 12, 91, -4, 27, 63, 5, 50, 27, 0',
  'Already sorted': '-20, -9, -3, 0, 4, 11, 18, 25, 30, 42',
  'Reverse sorted': '42, 30, 25, 18, 11, 4, 0, -3, -9, -20',
  Duplicates: '7, 3, 7, 3, 7, 1, 3, 7, 1, 3',
  'Negative numbers': '-15, -2, -99, 0, -7, -2, -45, 8, -1, -99',
  'Odd-sized (11)': '61, 4, -8, 22, 15, 9, 0, 33, 17, -5, 71',
};

const STAGES = [
  { id: 1, label: 'Initialize', thread: 'T1', color: '#38bdf8' },
  { id: 2, label: 'Divide', thread: 'T2', color: '#22d3ee' },
  { id: 3, label: 'Sort A', thread: 'T3', color: '#a78bfa' },
  { id: 4, label: 'Sort B', thread: 'T4', color: '#c084fc' },
  { id: 5, label: 'Merge', thread: 'T5', color: '#34d399' },
  { id: 6, label: 'Validate', thread: 'T6', color: '#fbbf24' },
];

const sleep = (ms: number) => new Promise<void>((resolve) => window.setTimeout(resolve, ms));

function parseIntegers(text: string): number[] {
  if (!text.trim()) throw new Error('Input is empty. Enter integers or generate random values.');
  const tokens = text.split(',');
  if (tokens.length > 200) throw new Error('The browser demo accepts at most 200 values.');
  return tokens.map((token, index) => {
    const trimmed = token.trim();
    if (!trimmed) throw new Error(`Empty value at position ${index + 1}.`);
    const value = Number(trimmed);
    if (!Number.isInteger(value)) throw new Error(`Invalid integer "${trimmed}" at position ${index + 1}.`);
    return value;
  });
}

function mergeArrays(left: number[], right: number[]): number[] {
  const result: number[] = [];
  let i = 0;
  let j = 0;
  while (i < left.length && j < right.length) {
    if (left[i] <= right[j]) result.push(left[i++]);
    else result.push(right[j++]);
  }
  return result.concat(left.slice(i), right.slice(j));
}

function isSorted(values: number[]): boolean {
  return values.every((value, index) => index === 0 || values[index - 1] <= value);
}

function sameMultiset(a: number[], b: number[]): boolean {
  if (a.length !== b.length) return false;
  const counts = new Map<number, number>();
  a.forEach((value) => counts.set(value, (counts.get(value) ?? 0) + 1));
  b.forEach((value) => {
    const current = counts.get(value) ?? 0;
    if (current === 0) return;
    if (current === 1) counts.delete(value);
    else counts.set(value, current - 1);
  });
  return counts.size === 0;
}

function formatValues(values: number[], limit = 28): string {
  if (!values.length) return '[]';
  const shown = values.slice(0, limit).join(', ');
  return values.length > limit ? `[${shown}, ...]` : `[${shown}]`;
}

function ArrayBars({ values, color }: { values: number[]; color: string }) {
  if (!values.length) {
    return <div className="flex h-14 items-center text-xs text-slate-600">empty</div>;
  }
  const min = Math.min(...values);
  const max = Math.max(...values);
  const range = Math.max(1, max - min);
  const shown = values.slice(0, 80);
  return (
    <div className="flex h-14 items-end gap-[2px] overflow-hidden">
      {shown.map((value, index) => (
        <div
          key={index}
          title={String(value)}
          className="min-w-[2px] flex-1 rounded-t-[2px] opacity-80 transition-all duration-500"
          style={{ height: `${16 + ((value - min) / range) * 84}%`, backgroundColor: color }}
        />
      ))}
    </div>
  );
}

function ArrayCard({ title, values, color, tag }: {
  title: string; values: number[]; color: string; tag: string;
}) {
  return (
    <div className="rounded-xl border border-slate-700/60 bg-slate-900/60 p-3">
      <div className="mb-2 flex items-center justify-between gap-2">
        <p className="text-xs font-semibold" style={{ color }}>{title}</p>
        <span className="rounded-full bg-slate-800 px-2 py-0.5 font-mono text-[10px] text-slate-400">
          {tag} · {values.length}
        </span>
      </div>
      <ArrayBars values={values} color={color} />
      <p className="mt-2 truncate font-mono text-[10px] text-slate-500" title={formatValues(values, 200)}>
        {formatValues(values)}
      </p>
    </div>
  );
}

export default function LiveDemo() {
  const [input, setInput] = useState(PRESETS['Small sample']);
  const [count, setCount] = useState('20');
  const [preset, setPreset] = useState('Small sample');
  const [arrays, setArrays] = useState<Arrays>(EMPTY);
  const [phase, setPhase] = useState(0);
  const [running, setRunning] = useState(false);
  const [error, setError] = useState('');
  const [logs, setLogs] = useState<string[]>([]);
  const [elapsed, setElapsed] = useState<number | null>(null);
  const [validated, setValidated] = useState(false);
  const tokenRef = useRef(0);

  function log(message: string) {
    setLogs((previous) => [...previous.slice(-7), message]);
  }

  function reset() {
    tokenRef.current += 1;
    setArrays(EMPTY);
    setPhase(0);
    setRunning(false);
    setError('');
    setLogs([]);
    setElapsed(null);
    setValidated(false);
  }

  function loadPreset(name: string) {
    setPreset(name);
    setInput(PRESETS[name]);
    reset();
  }

  function generate() {
    const n = Number(count);
    if (!Number.isInteger(n) || n < 1 || n > 200) {
      setError('Random count must be an integer between 1 and 200.');
      return;
    }
    const values = Array.from({ length: n }, () => Math.floor(Math.random() * 20001) - 10000);
    setInput(values.join(', '));
    setPreset('Custom');
    setError('');
    log(`Generated ${n} random integers.`);
  }

  async function run() {
    const token = ++tokenRef.current;
    setError('');
    setLogs([]);
    setElapsed(null);
    setValidated(false);
    setArrays(EMPTY);

    let values: number[];
    try {
      values = parseIntegers(input);
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : 'Invalid input.');
      return;
    }

    const alive = () => tokenRef.current === token;
    const started = performance.now();
    setRunning(true);

    try {
      setPhase(1);
      log('T1: initializing the original array.');
      await sleep(480);
      if (!alive()) return;
      setArrays({ ...EMPTY, original: [...values] });
      log(`T1: initialized ${values.length} integers.`);

      setPhase(2);
      log('T2: dividing at length / 2.');
      await sleep(480);
      if (!alive()) return;
      const middle = Math.floor(values.length / 2);
      const first = values.slice(0, middle);
      const second = values.slice(middle);
      setArrays((previous) => ({ ...previous, first, second }));
      log(`T2: split into ${first.length} + ${second.length} elements.`);

      setPhase(3.5);
      log('T3 and T4: sorting both halves in parallel.');
      await sleep(720);
      if (!alive()) return;
      const sortedFirst = [...first].sort((a, b) => a - b);
      const sortedSecond = [...second].sort((a, b) => a - b);
      setArrays((previous) => ({ ...previous, sortedFirst, sortedSecond }));
      log('T3/T4: both sorting threads finished; coordinator joins them.');

      setPhase(5);
      log('T5: merging the two sorted halves.');
      await sleep(520);
      if (!alive()) return;
      const final = mergeArrays(sortedFirst, sortedSecond);
      setArrays((previous) => ({ ...previous, final }));
      log(`T5: merged ${final.length} integers.`);

      setPhase(6);
      log('T6: validating order and value preservation.');
      await sleep(420);
      if (!alive()) return;
      const ok = isSorted(final) && sameMultiset(values, final);
      setValidated(ok);
      log(ok ? 'T6: VALID — sorted order and values confirmed.' : 'T6: INVALID — check failed.');

      setElapsed(performance.now() - started);
      setPhase(7);
      setRunning(false);
    } catch (exception) {
      if (!alive()) return;
      setError(exception instanceof Error ? exception.message : 'Unexpected demo error.');
      setRunning(false);
    }
  }

  const activeStage = (id: number) =>
    phase === 3.5 ? id === 3 || id === 4 : phase > 0 && phase < 7 && Math.floor(phase) === id;
  const doneStage = (id: number) =>
    phase === 7 || (phase === 3.5 ? id < 3 : phase > id);

  return (
    <div className="space-y-5">
      <div className="rounded-2xl border border-sky-400/20 bg-sky-400/5 px-5 py-4 text-sm text-sky-100/80">
        <strong className="text-sky-300">Browser visualization:</strong> this panel illustrates the
        exact workflow of the Java application. The real Java Threads, merge sort and Swing GUI
        are in the downloadable project; this animation does not execute Java.
      </div>

      <div className="grid gap-5 lg:grid-cols-[360px_1fr]">
        <div className="space-y-4 rounded-2xl border border-slate-700/70 bg-slate-900/60 p-5">
          <div>
            <label className="mb-2 block text-xs font-semibold uppercase tracking-wider text-slate-400">
              Integers (comma separated)
            </label>
            <textarea
              value={input}
              onChange={(event) => { setInput(event.target.value); setPreset('Custom'); }}
              rows={6}
              className="w-full resize-none rounded-xl border border-slate-700 bg-slate-950/70 p-3 font-mono text-xs text-slate-200 outline-none transition focus:border-sky-400"
            />
          </div>

          <div>
            <label className="mb-2 block text-xs font-semibold uppercase tracking-wider text-slate-400">
              Test preset
            </label>
            <select
              value={preset}
              onChange={(event) => loadPreset(event.target.value)}
              className="w-full rounded-xl border border-slate-700 bg-slate-950/70 px-3 py-2.5 text-sm text-slate-200 outline-none focus:border-sky-400"
            >
              {Object.keys(PRESETS).map((name) => <option key={name}>{name}</option>)}
              {preset === 'Custom' && <option>Custom</option>}
            </select>
          </div>

          <div>
            <label className="mb-2 block text-xs font-semibold uppercase tracking-wider text-slate-400">
              Random element count
            </label>
            <div className="flex gap-2">
              <input
                value={count}
                onChange={(event) => setCount(event.target.value)}
                inputMode="numeric"
                className="min-w-0 flex-1 rounded-xl border border-slate-700 bg-slate-950/70 px-3 py-2.5 font-mono text-sm text-slate-200 outline-none focus:border-sky-400"
              />
              <button
                onClick={generate}
                disabled={running}
                className="rounded-xl border border-sky-400/40 bg-sky-400/10 px-3 py-2.5 text-xs font-semibold text-sky-300 transition hover:bg-sky-400/20 disabled:opacity-40"
              >
                Generate
              </button>
            </div>
          </div>

          <div className="flex gap-2 pt-1">
            <button
              onClick={run}
              disabled={running}
              className="flex-1 rounded-xl bg-gradient-to-r from-sky-500 to-cyan-400 px-4 py-3 text-sm font-bold text-slate-950 shadow-lg shadow-sky-500/20 transition hover:brightness-110 disabled:cursor-not-allowed disabled:opacity-50"
            >
              {running ? 'Running...' : 'Start Sorting'}
            </button>
            <button
              onClick={reset}
              className="rounded-xl border border-slate-600 px-4 py-3 text-sm font-semibold text-slate-300 transition hover:border-slate-400"
            >
              Reset
            </button>
          </div>

          {error && (
            <div className="rounded-xl border border-rose-400/40 bg-rose-500/10 px-3 py-2 text-xs text-rose-200">
              {error}
            </div>
          )}

          <div className="grid grid-cols-2 gap-2 text-xs">
            <div className="rounded-xl bg-slate-950/60 p-3">
              <p className="text-slate-500">Threads</p>
              <p className="mt-1 font-semibold text-slate-200">6 workers + 1</p>
            </div>
            <div className="rounded-xl bg-slate-950/60 p-3">
              <p className="text-slate-500">Demo time</p>
              <p className="mt-1 font-mono font-semibold text-slate-200">
                {elapsed === null ? '—' : `${elapsed.toFixed(0)} ms`}
              </p>
            </div>
          </div>
        </div>

        <div className="space-y-5">
          <div className="rounded-2xl border border-slate-700/70 bg-slate-900/60 p-5">
            <div className="mb-4 flex items-center justify-between">
              <div>
                <p className="text-xs font-semibold uppercase tracking-wider text-slate-400">
                  Six-thread pipeline
                </p>
                <p className="mt-1 text-sm font-semibold text-slate-100">
                  {phase === 0 ? 'Waiting to start' : phase === 7 ? 'All threads completed' :
                    phase === 3.5 ? 'T3 and T4 executing concurrently' : `Stage ${Math.floor(phase)} of 6`}
                </p>
              </div>
              <span className={`rounded-full px-3 py-1 text-xs font-semibold ${validated ? 'bg-emerald-400/15 text-emerald-300' : 'bg-slate-800 text-slate-400'}`}>
                {validated ? 'VALID' : phase ? 'IN PROGRESS' : 'IDLE'}
              </span>
            </div>

            <div className="flex items-center gap-1">
              {STAGES.map((stage, index) => (
                <div key={stage.id} className="flex flex-1 items-center gap-1">
                  <div
                    className={`flex-1 rounded-xl border px-2 py-3 text-center transition-all duration-300 ${
                      activeStage(stage.id)
                        ? 'border-sky-300 bg-sky-400/20 shadow-lg shadow-sky-500/10'
                        : doneStage(stage.id)
                          ? 'border-emerald-400/40 bg-emerald-400/10'
                          : 'border-slate-700 bg-slate-950/50'
                    }`}
                  >
                    <p className="font-mono text-[10px] font-bold" style={{ color: stage.color }}>
                      {stage.thread}
                    </p>
                    <p className="mt-0.5 text-[11px] font-medium text-slate-300">{stage.label}</p>
                  </div>
                  {index < STAGES.length - 1 && <span className="text-xs text-slate-600">›</span>}
                </div>
              ))}
            </div>
          </div>

          <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-3">
            <ArrayCard title="Original Array" values={arrays.original} color="#38bdf8" tag="T1" />
            <ArrayCard title="First Half" values={arrays.first} color="#22d3ee" tag="T2" />
            <ArrayCard title="Second Half" values={arrays.second} color="#c084fc" tag="T2" />
            <ArrayCard title="Sorted First Half" values={arrays.sortedFirst} color="#38bdf8" tag="T3" />
            <ArrayCard title="Sorted Second Half" values={arrays.sortedSecond} color="#c084fc" tag="T4" />
            <ArrayCard title="Final Sorted Array" values={arrays.final} color="#34d399" tag="T5" />
          </div>

          <div className="rounded-2xl border border-slate-700/70 bg-slate-950/60 p-4">
            <p className="mb-2 text-xs font-semibold uppercase tracking-wider text-slate-400">
              Execution log
            </p>
            <div className="space-y-1 font-mono text-[11px] text-slate-400">
              {logs.length === 0 ? (
                <p className="text-slate-600">Press Start Sorting to see thread activity.</p>
              ) : logs.map((entry, index) => (
                <p key={index} className="flex gap-2">
                  <span className="text-slate-600">{String(index + 1).padStart(2, '0')}</span>
                  <span>{entry}</span>
                </p>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
