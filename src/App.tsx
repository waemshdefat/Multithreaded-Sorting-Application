import { useState } from 'react';
import LiveDemo from './components/LiveDemo';
import CodeBlock from './components/CodeBlock';
import MarkdownView from './components/MarkdownView';
import { benchmarkRows, documents, javaFiles, supportFiles, threadRows, type ProjectFile } from './data/projectFiles';

type Tab = 'overview' | 'demo' | 'source' | 'docs' | 'slides' | 'testing';

const TABS: { id: Tab; label: string }[] = [
  { id: 'overview', label: 'Overview' },
  { id: 'demo', label: 'Live Demo' },
  { id: 'source', label: 'Source Code' },
  { id: 'docs', label: 'Documentation' },
  { id: 'slides', label: 'Presentation' },
  { id: 'testing', label: 'Testing' },
];

const REQUIREMENTS = [
  { title: 'Real Java threads', body: 'Six named worker threads plus a coordinator, each started with start() and synchronized with join().', color: '#38bdf8' },
  { title: 'Precedence guaranteed', body: 'The merge thread starts only after both sorting threads have been joined.', color: '#a78bfa' },
  { title: 'Safe shared state', body: 'Synchronized accessors and defensive copies prevent race conditions and stale reads.', color: '#34d399' },
  { title: 'Functional Swing GUI', body: 'Dark theme, six array displays, status, statistics, presets, and a custom visualization.', color: '#fbbf24' },
];

function SectionHeading({ eyebrow, title, body }: { eyebrow: string; title: string; body?: string }) {
  return (
    <div className="mb-7">
      <p className="mb-2 text-xs font-bold uppercase tracking-[0.2em] text-sky-400">{eyebrow}</p>
      <h2 className="text-2xl font-bold tracking-tight text-white sm:text-3xl">{title}</h2>
      {body && <p className="mt-2 max-w-3xl text-sm leading-relaxed text-slate-400">{body}</p>}
    </div>
  );
}

function StatCard({ value, label, accent }: { value: string; label: string; accent: string }) {
  return (
    <div className="rounded-2xl border border-slate-700/60 bg-slate-900/60 px-5 py-4">
      <p className="text-2xl font-bold" style={{ color: accent }}>{value}</p>
      <p className="mt-1 text-xs text-slate-400">{label}</p>
    </div>
  );
}

export default function App() {
  const [tab, setTab] = useState<Tab>('overview');
  const [selectedFile, setSelectedFile] = useState<ProjectFile>(javaFiles[2]);
  const [docTab, setDocTab] = useState<'report' | 'readme' | 'cases'>('report');

  return (
    <div className="min-h-screen text-slate-200">
      <div className="ambient fixed inset-0 -z-10" />

      <header className="sticky top-0 z-40 border-b border-slate-800/80 bg-slate-950/80 backdrop-blur-xl">
        <div className="mx-auto flex max-w-7xl items-center justify-between gap-4 px-5 py-3">
          <button onClick={() => setTab('overview')} className="flex items-center gap-3 text-left">
            <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-br from-sky-400 to-cyan-500 font-bold text-slate-950 shadow-lg shadow-sky-500/20">
              M
            </div>
            <div className="hidden sm:block">
              <p className="text-sm font-bold leading-tight text-white">Multithreaded Sorting</p>
              <p className="text-[11px] text-slate-500">Al al-Bayt University · OS 901332</p>
            </div>
          </button>

          <nav className="flex max-w-full gap-1 overflow-x-auto rounded-xl border border-slate-800 bg-slate-900/60 p-1">
            {TABS.map((item) => (
              <button
                key={item.id}
                onClick={() => setTab(item.id)}
                className={`whitespace-nowrap rounded-lg px-3 py-2 text-xs font-semibold transition ${
                  tab === item.id
                    ? 'bg-sky-500/20 text-sky-300 shadow-sm'
                    : 'text-slate-400 hover:bg-slate-800/60 hover:text-slate-200'
                }`}
              >
                {item.label}
              </button>
            ))}
          </nav>
        </div>
      </header>

      <main className="mx-auto max-w-7xl px-5 py-10">
        {tab === 'overview' && (
          <div className="space-y-12">
            <section className="relative overflow-hidden rounded-3xl border border-slate-700/60 bg-slate-900/50 px-6 py-12 sm:px-12">
              <div className="hero-glow absolute -right-20 -top-24 h-72 w-72 rounded-full blur-3xl" />
              <div className="relative max-w-3xl">
                <div className="mb-5 flex flex-wrap gap-2">
                  {['Java 17', 'Swing GUI', 'Merge Sort', '7 Actual Threads'].map((badge) => (
                    <span key={badge} className="rounded-full border border-sky-400/30 bg-sky-400/10 px-3 py-1 text-[11px] font-semibold text-sky-300">
                      {badge}
                    </span>
                  ))}
                </div>
                <h1 className="text-4xl font-black leading-tight tracking-tight text-white sm:text-6xl">
                  Multithreaded<br />
                  <span className="bg-gradient-to-r from-sky-400 via-cyan-300 to-violet-400 bg-clip-text text-transparent">
                    Sorting Application
                  </span>
                </h1>
                <p className="mt-5 max-w-2xl text-base leading-relaxed text-slate-300 sm:text-lg">
                  A complete Operating Systems project: six worker threads initialize, divide,
                  sort, merge and validate an integer array — with a functional Swing interface,
                  real <code className="rounded bg-slate-800 px-1.5 py-0.5 font-mono text-sm text-sky-300">start()</code>/
                  <code className="rounded bg-slate-800 px-1.5 py-0.5 font-mono text-sm text-sky-300">join()</code>
                  synchronization, tests, report and slides.
                </p>
                <div className="mt-7 flex flex-wrap gap-3">
                  <button
                    onClick={() => setTab('demo')}
                    className="rounded-xl bg-gradient-to-r from-sky-500 to-cyan-400 px-5 py-3 text-sm font-bold text-slate-950 shadow-lg shadow-sky-500/20 transition hover:brightness-110"
                  >
                    Try the live workflow →
                  </button>
                  <a
                    href="/downloads/multithreaded-sorting-java.zip"
                    className="rounded-xl border border-slate-600 px-5 py-3 text-sm font-semibold text-slate-200 transition hover:border-sky-400 hover:text-sky-300"
                  >
                    Download Java project
                  </a>
                </div>
                <p className="mt-6 text-xs text-slate-500">
                  Course 901332 · First Semester 2026 · Instructor: Dr. Ashraf Al-Aoun
                </p>
              </div>
            </section>

            <section className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
              <StatCard value="7" label="Actual Java threads (6 workers + coordinator)" accent="#38bdf8" />
              <StatCard value="12/12" label="Headless tests executed successfully" accent="#34d399" />
              <StatCard value="0" label="javac -Xlint warnings on final build" accent="#a78bfa" />
              <StatCard value="17" label="Minimum JDK version" accent="#fbbf24" />
            </section>

            <section>
              <SectionHeading
                eyebrow="Requirements met"
                title="Built for the OS specification"
                body="Every mandatory concurrency requirement is implemented in Java source — not simulated in the browser."
              />
              <div className="grid gap-4 md:grid-cols-2">
                {REQUIREMENTS.map((item) => (
                  <div key={item.title} className="rounded-2xl border border-slate-700/60 bg-slate-900/50 p-5">
                    <div className="mb-3 h-1.5 w-10 rounded-full" style={{ backgroundColor: item.color }} />
                    <h3 className="font-semibold text-white">{item.title}</h3>
                    <p className="mt-2 text-sm leading-relaxed text-slate-400">{item.body}</p>
                  </div>
                ))}
              </div>
            </section>

            <section>
              <SectionHeading
                eyebrow="Thread architecture"
                title="Six workers, strict synchronization"
                body="T3 and T4 overlap; T5 waits for both. The coordinator joins every dependent stage."
              />
              <div className="overflow-x-auto rounded-2xl border border-slate-700/60">
                <table className="w-full min-w-[760px] text-left text-sm">
                  <thead className="bg-slate-800/70 text-xs uppercase tracking-wider text-slate-400">
                    <tr>
                      <th className="px-4 py-3">Thread</th>
                      <th className="px-4 py-3">Responsibility</th>
                      <th className="px-4 py-3">Class</th>
                      <th className="px-4 py-3">Waits for</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-slate-800 bg-slate-900/40">
                    {threadRows.map((row) => (
                      <tr key={row.id} className="hover:bg-slate-800/40">
                        <td className="px-4 py-3 font-mono font-bold text-sky-300">{row.id}</td>
                        <td className="px-4 py-3 text-slate-200">{row.name}<span className="block text-xs text-slate-500">{row.detail}</span></td>
                        <td className="px-4 py-3 font-mono text-xs text-violet-300">{row.cls}</td>
                        <td className="px-4 py-3 text-xs text-slate-400">{row.depends}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </section>

            <section>
              <SectionHeading
                eyebrow="Measured comparison"
                title="Parallelism is not automatically faster"
                body="Actual benchmark on a 2-processor environment with JDK 17.0.21.1; five measured runs per size after warm-up."
              />
              <div className="rounded-2xl border border-slate-700/60 bg-slate-900/50 p-6">
                <div className="space-y-4">
                  {benchmarkRows.map((row) => {
                    const max = 12.965;
                    return (
                      <div key={row.n} className="grid grid-cols-[90px_1fr] items-center gap-4">
                        <span className="font-mono text-xs text-slate-400">{row.n}</span>
                        <div className="space-y-1.5">
                          <div className="flex items-center gap-2">
                            <div className="h-3 rounded bg-sky-400" style={{ width: `${(Number(row.parallel) / max) * 100}%` }} />
                            <span className="whitespace-nowrap font-mono text-[11px] text-sky-300">{row.parallel} ms</span>
                          </div>
                          <div className="flex items-center gap-2">
                            <div className="h-3 rounded bg-violet-400" style={{ width: `${(Number(row.sequential) / max) * 100}%` }} />
                            <span className="whitespace-nowrap font-mono text-[11px] text-violet-300">{row.sequential} ms</span>
                          </div>
                        </div>
                      </div>
                    );
                  })}
                </div>
                <div className="mt-6 flex flex-wrap gap-5 border-t border-slate-800 pt-4 text-xs text-slate-400">
                  <span><span className="mr-2 inline-block h-2.5 w-2.5 rounded bg-sky-400" />Six-thread pipeline</span>
                  <span><span className="mr-2 inline-block h-2.5 w-2.5 rounded bg-violet-400" />Sequential merge sort</span>
                </div>
                <p className="mt-4 text-sm leading-relaxed text-amber-200/80">
                  Thread creation, <code className="font-mono">join()</code> synchronization and memory copies cost time.
                  On this hardware the sequential version was faster — an honest OS lesson, not a bug.
                </p>
              </div>
            </section>
          </div>
        )}

        {tab === 'demo' && (
          <div>
            <SectionHeading
              eyebrow="Interactive"
              title="Six-stage sorting workflow"
              body="Enter values or load a preset, then watch initialization, division, concurrent sorting, merging and validation."
            />
            <LiveDemo />
          </div>
        )}

        {tab === 'source' && (
          <div>
            <SectionHeading
              eyebrow="Complete implementation"
              title="Browse every project file"
              body="All Java sources, tests, scripts and the README are embedded here and can be copied or downloaded individually."
            />
            <div className="mb-5 flex flex-wrap gap-3">
              <a href="/downloads/multithreaded-sorting-java.zip" className="rounded-xl bg-sky-500 px-5 py-2.5 text-sm font-bold text-slate-950 transition hover:brightness-110">
                Download complete ZIP
              </a>
              <button
                onClick={() => setSelectedFile(supportFiles[supportFiles.length - 1])}
                className="rounded-xl border border-slate-600 px-5 py-2.5 text-sm font-semibold text-slate-300 transition hover:border-sky-400"
              >
                Open README
              </button>
            </div>
            <div className="grid gap-5 lg:grid-cols-[290px_1fr]">
              <aside className="space-y-5">
                <div>
                  <p className="mb-2 text-xs font-bold uppercase tracking-wider text-slate-500">Application sources</p>
                  <div className="space-y-1">
                    {javaFiles.map((file) => (
                      <button
                        key={file.path}
                        onClick={() => setSelectedFile(file)}
                        className={`w-full rounded-lg px-3 py-2 text-left font-mono text-xs transition ${
                          selectedFile.path === file.path
                            ? 'bg-sky-400/15 text-sky-300'
                            : 'text-slate-400 hover:bg-slate-800/60 hover:text-slate-200'
                        }`}
                      >
                        {file.name}
                      </button>
                    ))}
                  </div>
                </div>
                <div>
                  <p className="mb-2 text-xs font-bold uppercase tracking-wider text-slate-500">Tests and scripts</p>
                  <div className="space-y-1">
                    {supportFiles.map((file) => (
                      <button
                        key={file.path}
                        onClick={() => setSelectedFile(file)}
                        className={`w-full rounded-lg px-3 py-2 text-left font-mono text-xs transition ${
                          selectedFile.path === file.path
                            ? 'bg-violet-400/15 text-violet-300'
                            : 'text-slate-400 hover:bg-slate-800/60 hover:text-slate-200'
                        }`}
                      >
                        {file.name}
                      </button>
                    ))}
                  </div>
                </div>
              </aside>
              <CodeBlock file={selectedFile} />
            </div>
          </div>
        )}

        {tab === 'docs' && (
          <div>
            <SectionHeading
              eyebrow="University deliverables"
              title="Documentation"
              body="The complete report, setup guide and test-case specification are rendered directly from the project files."
            />
            <div className="mb-5 flex gap-2">
              {([
                ['report', 'Full Report'],
                ['readme', 'README'],
                ['cases', 'Test Cases'],
              ] as const).map(([id, label]) => (
                <button
                  key={id}
                  onClick={() => setDocTab(id)}
                  className={`rounded-xl px-4 py-2 text-sm font-semibold transition ${
                    docTab === id
                      ? 'bg-sky-500/20 text-sky-300'
                      : 'border border-slate-700 text-slate-400 hover:text-slate-200'
                  }`}
                >
                  {label}
                </button>
              ))}
            </div>
            <div className="rounded-2xl border border-slate-700/60 bg-slate-900/50 p-6 sm:p-9">
              <MarkdownView
                content={docTab === 'report' ? documents.report : docTab === 'readme' ? documents.readme : documents.testCases}
              />
            </div>
          </div>
        )}

        {tab === 'slides' && (
          <div>
            <SectionHeading
              eyebrow="Defense material"
              title="9-slide presentation"
              body="Includes speaker notes and a live-demo script so every team member can defend the design."
            />
            <div className="rounded-2xl border border-slate-700/60 bg-slate-900/50 p-6 sm:p-9">
              <MarkdownView content={documents.presentation} />
            </div>
          </div>
        )}

        {tab === 'testing' && (
          <div className="space-y-8">
            <SectionHeading
              eyebrow="Verified"
              title="Tests and honest performance data"
              body="The Java sources were compiled with javac -Xlint:all and the headless suite was actually executed."
            />
            <div className="grid gap-4 sm:grid-cols-3">
              <StatCard value="12/12" label="Tests passed" accent="#34d399" />
              <StatCard value="0" label="Compiler warnings" accent="#38bdf8" />
              <StatCard value="2 CPUs" label="Benchmark environment" accent="#a78bfa" />
            </div>

            <div className="rounded-2xl border border-slate-700/60 bg-slate-900/50 p-6">
              <h3 className="mb-4 font-semibold text-white">Executed test cases</h3>
              <div className="overflow-x-auto">
                <table className="w-full min-w-[640px] text-left text-sm">
                  <thead className="text-xs uppercase tracking-wider text-slate-500">
                    <tr><th className="py-2">Case</th><th>Input type</th><th>Expected</th><th>Result</th></tr>
                  </thead>
                  <tbody className="divide-y divide-slate-800 text-slate-300">
                    {[
                      ['Small array', '10 mixed values', 'Sorted ascending', 'PASS'],
                      ['Already sorted', '10 sorted values', 'Unchanged', 'PASS'],
                      ['Reverse sorted', '10 descending values', 'Fully ascending', 'PASS'],
                      ['Duplicates', 'Repeated values', 'All values preserved', 'PASS'],
                      ['Negative numbers', 'Mixed negatives', 'Correct ordering', 'PASS'],
                      ['Odd-sized array', '11 values (5 + 6)', 'Second half larger', 'PASS'],
                      ['Single element', '1 value', 'Empty first half', 'PASS'],
                      ['Large case', '500 values', 'Sorted', 'PASS'],
                      ['Random case', '1,000 values', 'Sorted', 'PASS'],
                      ['Empty input', 'Empty string', 'Clear T1 error', 'PASS'],
                      ['Invalid integer', '4, abc, 9', 'T1 reports abc', 'PASS'],
                      ['Empty token', '4, , 9', 'T1 reports position 2', 'PASS'],
                    ].map((row) => (
                      <tr key={row[0]}>
                        <td className="py-2.5 font-medium">{row[0]}</td>
                        <td className="text-slate-400">{row[1]}</td>
                        <td className="text-slate-400">{row[2]}</td>
                        <td className="font-semibold text-emerald-400">{row[3]}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>

            <div className="rounded-2xl border border-slate-700/60 bg-slate-950/70 p-6">
              <p className="mb-3 text-xs font-bold uppercase tracking-wider text-slate-500">Observed console output</p>
              <pre className="overflow-x-auto font-mono text-xs leading-6 text-emerald-300">{`[PASS] Small array
[PASS] Already sorted
[PASS] Reverse sorted
[PASS] Duplicates
[PASS] Negative numbers
[PASS] Odd-sized array
[PASS] Single element
[PASS] Large generated case
[PASS] Large random array
[PASS] Empty input (rejected with clear message)
[PASS] Invalid integer (rejected with clear message)
[PASS] Empty token (rejected with clear message)

RESULT: 12/12 tests passed.`}</pre>
            </div>

            <div className="rounded-2xl border border-amber-400/25 bg-amber-400/5 p-6 text-sm leading-relaxed text-amber-100/80">
              <strong className="text-amber-300">GUI status:</strong> the Swing source compiles cleanly,
              but the build environment was headless, so the window was not launched and no screenshot
              is claimed. The report marks exact locations for real screenshots to be inserted after
              running <code className="font-mono">./java/run.sh</code> on a desktop.
            </div>
          </div>
        )}
      </main>

      <footer className="border-t border-slate-800 py-8 text-center text-xs text-slate-500">
        <p>Al al-Bayt University · Operating Systems (901332) · First Semester 2026</p>
        <p className="mt-1">Instructor: Dr. Ashraf Al-Aoun · Project: Multithreaded Sorting Application</p>
      </footer>
    </div>
  );
}
