import { useMemo } from 'react';
import { marked } from 'marked';

type Props = {
  content: string;
};

export default function MarkdownView({ content }: Props) {
  const html = useMemo(() => {
    marked.setOptions({ gfm: true, breaks: false });
    return marked.parse(content) as string;
  }, [content]);

  return <article className="md-body" dangerouslySetInnerHTML={{ __html: html }} />;
}
