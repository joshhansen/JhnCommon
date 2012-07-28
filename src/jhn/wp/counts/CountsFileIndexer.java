package jhn.wp.counts;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jhn.Paths;
import jhn.counts.i.i.IntIntRAMCounter;

public class CountsFileIndexer implements AutoCloseable {
	private final String countsFilename;
	private Connection db;
	private PreparedStatement insertStmt;
	public CountsFileIndexer(String countsFilename, String dbFilename) throws Exception {
		this.countsFilename = countsFilename;
		
		final boolean fileExists = new File(dbFilename).exists();
		
		Class.forName("org.sqlite.JDBC");
		db = DriverManager.getConnection("jdbc:sqlite:" + dbFilename);
		
		if(!fileExists) {
			db.createStatement().executeUpdate("create table counts(wordidx integer primary key, count integer)");
		}
		insertStmt = db.prepareStatement("insert into counts values(?, ?)");
	}
	
	public void run() throws Exception {
		db.setAutoCommit(false);
		
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(countsFilename));
		int key;
		int count;
		int i = 0;
		while(true) {
			key = ois.readInt();
			if(key == IntIntRAMCounter.NO_MORE_ENTRIES) {
				break;
			}
			count = ois.readInt();
			
			insertStmt.setInt(1, key);
			insertStmt.setInt(2, count);
			insertStmt.executeUpdate();
			
			i++;
			if(i % 10000 == 0) {
				System.out.println(i);
				db.commit();
			}
		}
	}
	
	@Override
	public void close() throws SQLException {
		db.commit();
		insertStmt.close();
		db.close();
	}
	
	public static void main(String[] args) throws Exception {
		final String countsFilename = Paths.outputDir("JhnCommon") + "/counts/all.counts";
		final String outputFilename = Paths.outputDir("JhnCommon") + "/counts/counts.sqlite3";
		try(CountsFileIndexer cfi = new CountsFileIndexer(countsFilename, outputFilename)) {
			cfi.run();
		}
	}
}
