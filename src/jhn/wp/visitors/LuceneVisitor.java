package jhn.wp.visitors;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class LuceneVisitor extends LabelAwareVisitor {
	private final String luceneIndexDir;
	private final boolean removeStopwords;
	private final boolean storeText;
	private IndexWriter writer;
	private StringBuilder text;
	public LuceneVisitor(String luceneIndexDir) {
		this(luceneIndexDir, true);
	}
	
	public LuceneVisitor(String luceneIndexDir, boolean removeStopwords) {
		this(luceneIndexDir, removeStopwords, false);
	}
	
	public LuceneVisitor(String luceneIndexDir, boolean removeStopwords, boolean storeText) {
		this.luceneIndexDir = luceneIndexDir;
		this.removeStopwords = removeStopwords;
		this.storeText = storeText;
//		Runtime.getRuntime().addShutdownHook(new Thread(){
//			@Override
//			public void run() {
//				super.run();
//				afterEverything();
//			}
//		});
	}
	
	@Override
	public void beforeEverything() throws Exception {
		super.beforeEverything();
		
		File file = new File(luceneIndexDir);
		file.mkdirs();
		
		FSDirectory dir = FSDirectory.open(file);

		StandardAnalyzer analyzer = removeStopwords ? new StandardAnalyzer(Version.LUCENE_35)
													: new StandardAnalyzer(Version.LUCENE_35, Collections.emptySet());

		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_35, analyzer);

		writer = new IndexWriter(dir, config);
	}

	@Override
	public void beforeLabel() {
		super.beforeLabel();
		text = new StringBuilder();
	}

	@Override
	public void visitWord(String word) {
		super.visitWord(word);
		boolean needsSpace = text.length() > 0;
		if(needsSpace) {
			text.append(' ');
		}
		text.append(word);
	}

	@Override
	public void afterLabel() throws Exception {
		Document doc = new Document();
		doc.add(new Field("label", currentLabel, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
		
		Field.Store textStorage = storeText ? Field.Store.YES : Field.Store.NO;
		doc.add(new Field("text", text.toString(), textStorage, Field.Index.ANALYZED_NO_NORMS));
		writer.addDocument(doc);
		
		super.afterLabel();
	}

	@Override
	public void afterEverything() {
		try {
			writer.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.afterEverything();
	}
	
	
}
