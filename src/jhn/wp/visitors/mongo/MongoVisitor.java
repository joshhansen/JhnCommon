package jhn.wp.visitors.mongo;

import com.mongodb.DB;
import com.mongodb.Mongo;

import jhn.wp.exceptions.CountException;
import jhn.wp.visitors.WordCountingVisitor;

public abstract class MongoVisitor extends WordCountingVisitor {

	protected final String server;
	protected final int port;
	protected final String dbName;
	protected Mongo m;
	protected DB db;
	
	protected int currentLabelIdx;
	
	public MongoVisitor(String server, int port, String dbName) {
		this.server = server;
		this.port = port;
		this.dbName = dbName;
	}

	@Override
	public void beforeEverything() throws Exception {
		super.beforeEverything();
		m = new Mongo(server, port);
		db = m.getDB(dbName);
	}

	@Override
	public void visitLabel(String label) throws CountException {
		super.visitLabel(label);
		currentLabelIdx = labelIdx(label);
	}
	
	protected abstract int labelIdx(String label);
	
	protected abstract int wordIdx(String word);

	@Override
	public void afterEverything() {
		m.close();
	}

}