package jhn.wp.visitors.mongo;

import jhn.wp.visitors.LabelAwareVisitor;

import com.mongodb.DB;
import com.mongodb.Mongo;

public abstract class BasicMongoVisitor extends LabelAwareVisitor {

	protected final String server;
	protected final int port;
	protected final String dbName;
	protected Mongo m;
	protected DB db;
	
	public BasicMongoVisitor(String server, int port, String dbName) {
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
	public void afterEverything() {
		m.close();
	}

}