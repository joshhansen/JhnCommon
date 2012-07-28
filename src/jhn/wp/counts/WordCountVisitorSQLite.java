package jhn.wp.counts;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;

import it.unimi.dsi.fastutil.objects.Object2IntMap;

import jhn.counts.i.o.ObjIntRAMCounter;
import jhn.wp.visitors.Visitor;

public class WordCountVisitorSQLite extends Visitor implements AutoCloseable {
	private static final int INTERVAL = 5000000;
	
	private final Set<String> stopwords = jhn.util.Util.stopwords();
	
	private Connection db;
	private PreparedStatement upsertStmt;
	private ObjIntRAMCounter<String> wordCounts;
	private int i = 0;
	public WordCountVisitorSQLite(String dbFilename) throws Exception {
		final boolean fileExists = new File(dbFilename).exists();
		
		Class.forName("org.sqlite.JDBC");
		db = DriverManager.getConnection("jdbc:sqlite:" + dbFilename);
		
		if(!fileExists) {
			db.createStatement().executeUpdate("create table counts(word string primary key, count integer)");
		}
		upsertStmt = db.prepareStatement("insert or replace into counts values(?, ? + (select coalesce(count,0) from counts where word=?))");
	}
	
	private void increment(String word, int inc) throws SQLException {
		upsertStmt.setString(1, word);
		upsertStmt.setInt(2, inc);
		upsertStmt.setString(3, word);
		upsertStmt.executeUpdate();
	}
	
	private void resetCounts() {
		wordCounts = new ObjIntRAMCounter<>();
	}

	private void flushCountsToDB() throws SQLException {
		db.setAutoCommit(false);
		System.out.print("Flushing...");
		for(Object2IntMap.Entry<String> entry : wordCounts.object2IntEntrySet()) {
			increment(entry.getKey(), entry.getIntValue());
		}
		System.out.println("done.");
		db.commit();
		db.setAutoCommit(true);
	}

	@Override
	public void beforeEverything() throws Exception {
		resetCounts();
	}
	
	@Override
	public void visitWord(String word) {
		if(!word.isEmpty() && !stopwords.contains(word)) {
			super.visitWord(word);
			wordCounts.inc(word);
			i++;
			if(i >= INTERVAL) {
				try {
					flushCountsToDB();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				resetCounts();
				
				i = 0;
			}
		}
	}
	
	@Override
	public void afterEverything() throws Exception {
		flushCountsToDB();
	}

	@Override
	public void close() throws Exception {
		upsertStmt.close();
		db.close();
	}	
}
