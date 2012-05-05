package jhn.wp.visitors;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import jhn.wp.Fields;

public class LuceneVisitor extends LabelAwareVisitor {
	private static final Version version = Version.LUCENE_36;
	private final String luceneIndexDir;
	private final String field;
	private final boolean removeStopwords;
	private final boolean storeText;
	private IndexWriter writer;
	private StringBuilder text;
	public LuceneVisitor(String luceneIndexDir, String field) {
		this(luceneIndexDir, field, true);
	}
	
	public LuceneVisitor(String luceneIndexDir, String field, boolean removeStopwords) {
		this(luceneIndexDir, field, removeStopwords, false);
	}
	
	public LuceneVisitor(String luceneIndexDir, String field, boolean removeStopwords, boolean storeText) {
		this.luceneIndexDir = luceneIndexDir;
		this.field = field;
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

		StandardAnalyzer analyzer = removeStopwords ? new StandardAnalyzer(version)
													: new StandardAnalyzer(version, Collections.emptySet());

		IndexWriterConfig config = new IndexWriterConfig(version, analyzer);

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
		final Field labelField = new Field(Fields.label, currentLabel, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS);
		
		
		Field.Store textStorage = storeText ? Field.Store.YES : Field.Store.NO;
		
		final Field textField = new Field(field, text.toString(), textStorage, Field.Index.ANALYZED_NO_NORMS, TermVector.WITH_POSITIONS_OFFSETS);
		
		doc.add(labelField);
		doc.add(textField);
		
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
