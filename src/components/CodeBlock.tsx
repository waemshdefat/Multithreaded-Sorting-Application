import { useState } from 'react';
import type { ProjectFile } from '../data/projectFiles';

type Props = {
  file: ProjectFile;
};

export default function CodeBlock({ file }: Props) {
  const [copied, setCopied] = useState(false);

  function copy() {
    navigator.clipboard?.writeText(file.source).then(() => {
      setCopied(true);
      window.setTimeout(() => setCopied(false), 1600);
    });
  }

  function download() {
    const blob = new Blob([file.source], { type: 'text/plain;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement('a');
    anchor.href = url;
    anchor.download = file.name;
    anchor.click();
    URL.revokeObjectURL(url);
  }

  const lines = file.source.split('\n');

  return (
    <div className="overflow-hidden rounded-2xl border border-slate-700/70 bg-slate-950/70">
      <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-700/70 bg-slate-900/80 px-4 py-3">
        <div>
          <p className="font-mono text-sm font-semibold text-sky-300">{file.name}</p>
          <p className="mt-0.5 text-xs text-slate-500">{file.description}</p>
        </div>
        <div className="flex gap-2">
          <button
            onClick={copy}
            className="rounded-lg border border-slate-600 px-3 py-1.5 text-xs font-medium text-slate-300 transition hover:border-sky-400 hover:text-sky-300"
          >
            {copied ? 'Copied!' : 'Copy'}
          </button>
          <button
            onClick={download}
            className="rounded-lg bg-sky-500/15 px-3 py-1.5 text-xs font-semibold text-sky-300 transition hover:bg-sky-500/25"
          >
            Download
          </button>
        </div>
      </div>
      <div className="max-h-[560px] overflow-auto">
        <table className="w-full border-collapse font-mono text-[12px] leading-6">
          <tbody>
            {lines.map((line, index) => (
              <tr key={index} className="hover:bg-slate-800/40">
                <td className="w-12 select-none border-r border-slate-800 px-3 text-right text-slate-600">
                  {index + 1}
                </td>
                <td className="whitespace-pre px-4 text-slate-300">{line || ' '}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <div className="border-t border-slate-800 px-4 py-2 font-mono text-[11px] text-slate-600">
        {file.path}
      </div>
    </div>
  );
}
