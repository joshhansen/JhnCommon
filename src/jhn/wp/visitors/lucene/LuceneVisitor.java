package jhn.wp.visitors.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;


public class LuceneVisitor extends AbstractLuceneVisitor {
	protected StringBuilder text;
	
	public LuceneVisitor(String luceneIndexDir, String field) {
		super(luceneIndexDir, field);
		this.textStorage = Field.Store.NO;
		this.textAnalysis = Field.Index.ANALYZED_NO_NORMS;
		this.textTermVector = Field.TermVector.WITH_POSITIONS_OFFSETS;
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
		Document doc = newDoc();
		doc.add(textField(text.toString()));
		
		writer.addDocument(doc);
		
		super.afterLabel();
	}

	
}
