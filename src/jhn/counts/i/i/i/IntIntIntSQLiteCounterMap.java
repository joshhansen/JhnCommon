package jhn.counts.i.i.i;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Set;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import jhn.counts.Counter;
import jhn.counts.i.i.IntIntCounter;

public class IntIntIntSQLiteCounterMap extends AbstractIntIntIntCounterMap implements AutoCloseable {

	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private Connection db;
	private final PreparedStatement stmt;
	private final PreparedStatement cocountEither;
	private final PreparedStatement entropyStmt;
	private final long totalCount;
	public IntIntIntSQLiteCounterMap(String cocountsDbFilename) throws Exception {
		this(DriverManager.getConnection("jdbc:sqlite:"	+ cocountsDbFilename));
	}
	
	public IntIntIntSQLiteCounterMap(Connection db) throws Exception {
		this.db = db;
		stmt = db.prepareStatement("select count from cocounts where word1idx=? and word2idx=?");
		cocountEither = db.prepareStatement("SELECT word1idx, word2idx FROM cocounts WHERE word1idx=? OR word2idx=?");
		entropyStmt = db.prepareStatement("SELECT count FROM cocounts WHERE word1idx=? OR word2idx=?");
		totalCount = db.createStatement().executeQuery("select total_cocount from total_counts").getLong(1);
	}
	
	private int[] arr = new int[2];
	@Override
	public int getCount(int key, int value) {
		arr[0] = key;
		arr[1] = value;
		Arrays.sort(arr);
		try {
			stmt.setInt(1, arr[0]);
			stmt.setInt(2, arr[1]);
			try(ResultSet rs = stmt.executeQuery()) {
				return rs.getInt(1);
			}
		} catch(SQLException e) {
			return 0;
		}
	}
	
	@Override
	public void close() throws SQLException {
		db.close();
	}
	
	public IntSet valuesForKey(int key) {
		try {
			cocountEither.setInt(1, key);
			cocountEither.setInt(2, key);
			
			try(final ResultSet rs = cocountEither.executeQuery()) {
			
				final IntSet values = new IntOpenHashSet();
				
				while(rs.next()) {
					final int first = rs.getInt(1);
					if(first != key) {
						values.add(first);
					} else {
						final int second = rs.getInt(2);
						values.add(second);//assumes second != key
					}
				}
				
				return values;
			}
		} catch(SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public int totalCountI() {
		throw new UnsupportedOperationException();
	}
	
	public long totalCountL() {
		return totalCount;
	}

	@Override
	public ObjectSet<it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry<Counter<Integer, Integer>>> int2ObjectEntrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void inc(int key, int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void inc(int key, int value, int inc) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(int key, int value, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int totalSize() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(int key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsValue(int key, int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Entry<Integer, Counter<Integer, Integer>>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IntIntCounter getCounter(int key) {
		throw new UnsupportedOperationException();
	}

	public double entropyGivenKey(int key) throws SQLException {
		entropyStmt.setInt(1, key);
		entropyStmt.setInt(2, key);
		
		int total = 0;
		IntList counts = new IntArrayList();
		try(ResultSet rs = entropyStmt.executeQuery()) {
			while(rs.next()) {
				int count = rs.getInt(1);
				counts.add(count);
				total += count;
			}
		}
		
		double entropy = 0.0;
		for(int count : counts) {
			double prob = (double)count / (double)total;
			entropy -= Math.log(prob) * prob;
		}
		return entropy;
	}
	
}
