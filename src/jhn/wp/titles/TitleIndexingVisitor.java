package jhn.wp.titles;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import jhn.wp.exceptions.CountException;
import jhn.wp.visitors.AbstractLuceneVisitor;

public class TitleIndexingVisitor extends AbstractLuceneVisitor {
	public TitleIndexingVisitor(String luceneIndexDir) {
		super(luceneIndexDir, null);
		labelStorage = Field.Store.YES;
		labelAnalysis = Field.Index.NOT_ANALYZED_NO_NORMS;
	}

	@Override
	public void visitLabel(String label) throws CountException {
		super.visitLabel(label);
		try {
			Document d = new Document();
			d.add(labelField(label.toLowerCase()));
			writer.addDocument(d);
		} catch (IOException e) {
			throw new CountException(null, null, null){};
		}
	}
}
