package jhn.wp.visitors.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class LuceneVisitor3 extends AbstractLuceneVisitor {

	public LuceneVisitor3(String luceneIndexDir, String field) {
		super(luceneIndexDir, field);
		this.textStorage = Field.Store.NO;
		this.textAnalysis = Field.Index.ANALYZED;
		this.textTermVector = Field.TermVector.WITH_POSITIONS_OFFSETS;
	}

	@Override
	public void visitDocument(String text) {
		Document doc = newDoc();
		
		doc.add(textField(text));
		
		super.visitDocument(text);
	}

}
