package jhn.counts.i.i.i;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Set;

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
	private PreparedStatement stmt;
	private final long totalCount;
	public IntIntIntSQLiteCounterMap(String cocountsDbFilename) throws Exception {
		this(DriverManager.getConnection("jdbc:sqlite:"	+ cocountsDbFilename));
	}
	
	public IntIntIntSQLiteCounterMap(Connection db) throws Exception {
		this.db = db;
		stmt = db.prepareStatement("select count from cocounts where word1idx=? and word2idx=?");
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

}
