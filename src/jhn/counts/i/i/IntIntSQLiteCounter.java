package jhn.counts.i.i;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

public class IntIntSQLiteCounter extends AbstractIntIntCounter implements AutoCloseable {
	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private Connection db;
	private PreparedStatement stmt;
	private final int totalCount;
	public IntIntSQLiteCounter(String cocountsDbFilename) throws Exception {
		this(DriverManager.getConnection("jdbc:sqlite:"	+ cocountsDbFilename));
	}
	
	public IntIntSQLiteCounter(Connection db) throws Exception {
		this.db = db;
		stmt = db.prepareStatement("select count from counts where wordidx=?");
		totalCount = db.createStatement().executeQuery("select total_count from total_counts").getInt(1);
	}
	
	
	@Override
	public int getCount(int key) {
		try {
			stmt.setInt(1, key);
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
		return totalCount;
	}
	
	@Override
	public void set(int key, int count) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int inc(int key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int inc(int key, int inc) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(int key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ObjectSet<it.unimi.dsi.fastutil.ints.Int2IntMap.Entry> int2IntEntrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<it.unimi.dsi.fastutil.ints.Int2IntMap.Entry> fastTopN(int n) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IntSet keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Entry<Integer, Integer>> entries() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<Entry<Integer, Integer>> topN(int n) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException();
	}

}
