package jhn.wp.visitors.lucene;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import jhn.wp.Fields;
import jhn.wp.visitors.LabelAwareVisitor;

public class AbstractLuceneVisitor extends LabelAwareVisitor {
	private static final Version version = Version.LUCENE_36;
	private final String luceneIndexDir;
	private final String field;

	protected Field.Store labelStorage = Field.Store.YES;
	protected Field.Index labelAnalysis = Field.Index.NOT_ANALYZED_NO_NORMS;
	
	protected Field.Store textStorage = Field.Store.NO;
	protected Field.Index textAnalysis = Field.Index.NO;
	protected Field.TermVector textTermVector = Field.TermVector.NO;
	
	protected IndexWriter writer;
	protected Set<String> stopwords = null;
	
	public AbstractLuceneVisitor(String luceneIndexDir, String field) {
		this.luceneIndexDir = luceneIndexDir;
		this.field = field;
	}
	
	@Override
	public void beforeEverything() throws Exception {
		super.beforeEverything();
		
		File file = new File(luceneIndexDir);
		file.mkdirs();
		
		FSDirectory dir = FSDirectory.open(file);

		StandardAnalyzer analyzer = stopwords == null ? new StandardAnalyzer(version) : new StandardAnalyzer(version, stopwords);

		IndexWriterConfig config = new IndexWriterConfig(version, analyzer);

		writer = new IndexWriter(dir, config);
	}
	
	@Override
	public void afterEverything() throws Exception {
		try {
			writer.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		super.afterEverything();
	}
	
	protected Field labelField() {
		return new Field(Fields.label, currentLabel, labelStorage, labelAnalysis);
	}
	
	protected Field textField(String text) {
		return new Field(field, text, textStorage, textAnalysis, textTermVector);
	}
	
	protected Document newDoc() {
		Document doc = new Document();
		doc.add(labelField());
		return doc;
	}
}
