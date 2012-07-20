package jhn.wp.cocounts;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import jhn.Paths;
import jhn.counts.ints.IntIntIntRAMCounterMap;

/** Given a counts file (serialization of IntIntIntCounterMap) outputs an array mapping word index to position in the
 * file for any index that occurs as the first element in a key-value pair.
 */
public class CountsFileIndexer {
	public static final long KEY_NOT_FOUND = -1L;
	private static final int BATCH_SIZE = 10000;
	
	private ObjectInputStream ois;
	
	
	private Connection db;
	private final PreparedStatement insertStmt;

	public CountsFileIndexer(String countsFilename, String destFilename) throws Exception {
		ois = new ObjectInputStream(new FileInputStream(countsFilename));

		Class.forName("org.sqlite.JDBC");
		db = DriverManager.getConnection("jdbc:sqlite:"	+ destFilename);
		Statement statement = db.createStatement();

		statement.executeUpdate("drop table if exists cocounts");
		statement.executeUpdate("create table cocounts (word1idx integer, word2idx integer, count integer)");
		
		insertStmt = db.prepareStatement("insert into cocounts values(?, ?, ?)");
	}
	
	public void index() throws Exception {
		int key1;
		int key2;
		int value;
		
		
		try {
			while(true) {
				key1 = ois.readInt();
				
				System.out.println(key1);
				while(true) {
					key2 = ois.readInt();
					if(key2==IntIntIntRAMCounterMap.END_OF_KEY) {
						break;
					}
					value = ois.readInt();
					
					writeCount(key1, key2, value);
					
					System.out.print('\t');
					System.out.print(key2);
					System.out.print(": ");
					System.out.println(value);
				}
			}
		} catch(EOFException e) {
			//Done
		}

		ois.close();
		db.close();
	}
	
	int queued = 0;
	private void writeCount(int word1idx, int word2idx, int count) throws SQLException {
		insertStmt.setInt(1, word1idx);
		insertStmt.setInt(2, word2idx);
		insertStmt.setInt(3, count);
		insertStmt.addBatch();
		queued++;
		
		if(queued >= BATCH_SIZE) {
			db.setAutoCommit(false);
			insertStmt.executeBatch();
			db.setAutoCommit(true);
			queued = 0;
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		CountsFileIndexer cfi = new CountsFileIndexer(Paths.outputDir("JhnCommon") + "/cocounts/2/0.counts",
				Paths.outputDir("JhnCommon") + "/cocounts/cocounts.sqlite3");
		cfi.index();
	}
}
