package jhn.wp.visitors.lucene;

import java.util.Collections;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import jhn.wp.exceptions.CountException;

/**
 * Like LuceneVisitor, but rather than aggregating the words into a whole document adds each word individually.
 * 
 * <i>Does</i> store text, does not analyze or store norms, does not store term vectors, and does not remove stopwords.
 * @author Josh Hansen
 *
 */
public class LuceneVisitor2 extends AbstractLuceneVisitor {

	public LuceneVisitor2(String luceneIndexDir, String field) {
		super(luceneIndexDir, field);
		this.textStorage = Field.Store.YES;
		this.textAnalysis = Field.Index.NOT_ANALYZED_NO_NORMS;
		this.textTermVector = Field.TermVector.NO;
		this.stopwords = Collections.emptySet();
	}

	private Document doc = null;
	@Override
	public void visitLabel(String label) throws CountException {
		super.visitLabel(label);
		doc = newDoc();
	}
	
	@Override
	public void visitWord(String word) {
		super.visitWord(word);
		doc.add(textField(word));
	}

	@Override
	public void afterLabel() throws Exception {
		writer.addDocument(doc);
		doc = null;
		
		super.afterLabel();
	}

}
