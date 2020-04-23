package de.schlemmersoft.bewerbung.test1.Proben.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.sqlite.SQLiteException;

import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI;
import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe.Interpretation;

/**
 * Implementation of the ProbenAPI using JDBC.
 *
 * This implementation is only efficient when accessing the whole sample data is
 * expensive, or when the database changes very frequently.
 *
 * @author Tobias Schlemmer
 *
 */
public class ProbenSQL implements ProbenAPI<Integer> {

	/**
	 * The sample of the ProbenSQL implementation.
	 *
	 * @author Tobias Schlemmer
	 *
	 */
	public class SQLProbe implements Probe<Integer> {
		String id;

		SQLProbe(String newid) {
			id = newid;
		}

		public boolean equals(Object other) {
			if (other instanceof SQLProbe)
				return id.equals(((SQLProbe) other).id);
			return false;
		}

		@Override
		public boolean equals(Probe<?> other) {
			return id.equals(other.getID());
		}

		@Override
		public Probe<Integer> clone() throws CloneNotSupportedException {
			SQLProbe newprobe = (ProbenSQL.SQLProbe) super.clone();
			newprobe.id = id;
			return newprobe;
		}

		@Override
		public String getID() {
			return id;
		}

		@Override
		public ZonedDateTime getTime() {
			try {
				if (SQLProbeGetTime == null)
					SQLProbeGetTime = connection.prepareStatement("SELECT time FROM " + tableName + " WHERE id = ?");
				SQLProbeGetTime.setString(1, id);
				ResultSet res = SQLProbeGetTime.executeQuery();
				res.next();
				ZonedDateTime retval = ZonedDateTime.parse(res.getString(1));
				res.close();
				return retval;
			} catch (SQLException e) {
				// TODO: implement error handling
				return null;
			}
		}

		@Override
		public Integer getValue() {
			try {
				if (SQLProbeGetValue == null)
					SQLProbeGetValue = connection.prepareStatement("SELECT value FROM " + tableName + " WHERE id = ?");
				SQLProbeGetValue.setString(1, id);
				ResultSet res = SQLProbeGetValue.executeQuery();
				res.next();
				int i = res.getInt(1);
				if (res.wasNull()) {
					res.close();
					return null;
				}
				res.close();
				return Integer.valueOf(i);
			} catch (SQLException e) {
				// TODO: implement error handling
				return null;
			}

		}

		@Override
		public void setValue(Integer v) {
			if (v == null)
				throw new IllegalArgumentException();
			try {
				if (SQLProbeSetValue == null)
					SQLProbeSetValue = connection
							.prepareStatement("UPDATE " + tableName + " SET value = ? WHERE id = ?");
				SQLProbeSetValue.setInt(1, v.intValue());
				SQLProbeSetValue.setString(2, id);
				SQLProbeSetValue.executeUpdate();
			} catch (SQLException e) {
				// TODO: implement error handling
			}
		}

		@Override
		public Interpretation getInterpretation() {
			Integer v = getValue();
			if (v == null)
				return Interpretation.FUZZY;
			int value = v.intValue();
			if (value < 0)
				return Interpretation.BAD;
			if (value > 0)
				return Interpretation.GOOD;
			return Interpretation.FUZZY;
		}

		@Override
		public String toString() {
			return "Probe " + id;
		}
	}

	/**
	 * SQL implementation of an iterator on samples.
	 *
	 * Actually this class is just a wrapper for ResultSet on the sample data table.
	 *
	 * It should be closed after usage to avoid resource leaks.
	 *
	 * @author Tobias Schlemmer
	 *
	 */
	public class ProbenIterator implements Iterator<Probe<Integer>> {
		ResultSet queryResult;
		String current;

		ProbenIterator(ResultSet res) {
			current = null;
			queryResult = res;
			try {
				queryResult.next();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}

		/**
		 * Finish the underlying query by closing its result set. This function should
		 * be called as early as possible when the data is not needed anymore.
		 *
		 * @throws SQLException If an error in the underlying SQL implementation is
		 *                      detected this exception is thrown.
		 */
		public void close() throws SQLException {
			queryResult.close();
		}

		@Override
		public boolean hasNext() {
			try {
				return !queryResult.isAfterLast();
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		public SQLProbe next() {
			if (!hasNext())
				throw new NoSuchElementException();
			try {
				current = queryResult.getString("id");
				queryResult.next();
				return new SQLProbe(current);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	class Range implements Iterable<Probe<Integer>> {
		Integer minimum;
		Integer maximum;

		Range(Integer min, Integer max) {
			minimum = min;
			maximum = max;
		}

		@Override
		public Iterator<Probe<Integer>> iterator() {
			try {
				if (SQLGetRangeIds == null)
					SQLGetRangeIds = connection.prepareStatement(
							"SELECT id FROM " + tableName + " WHERE value >= ? and value <= ? ORDER BY time");
				SQLGetRangeIds.setInt(1, minimum.intValue());
				SQLGetRangeIds.setInt(2, maximum.intValue());
				ResultSet res = SQLGetRangeIds.executeQuery();
				return new ProbenIterator(res);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

	}

	class FuzzyResult implements Iterable<Probe<Integer>> {

		@Override
		public Iterator<Probe<Integer>> iterator() {
			try {
				if (SQLGetFuzzyResultIds == null)
					SQLGetFuzzyResultIds = connection.prepareStatement(
							"SELECT id FROM " + tableName + " WHERE value IS NULL OR value == 0 ORDER BY time");
				ResultSet res = SQLGetFuzzyResultIds.executeQuery();
				return new ProbenIterator(res);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
	}

	private Connection connection;
	private Statement statement;
	private PreparedStatement SQLProbeGetId;
	private PreparedStatement SQLProbeGetTime;
	private PreparedStatement SQLProbeGetValue;
	private PreparedStatement SQLProbeSetValue;
	private PreparedStatement SQLGetAllIds;
	private PreparedStatement SQLGetRangeIds;
	private PreparedStatement SQLGetFuzzyResultIds;
	private PreparedStatement SQLProbeNew;
	private PreparedStatement SQLProbeRemove;
	private PreparedStatement SQLProbeRemoveDate;
	String tableName;

	ProbenSQL(Connection c, String tablename) throws SQLException {
		connection = c;
		try {
			statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
		} catch (SQLException e) {
			connection = null;
			throw e;
		}
		tableName = statement.enquoteIdentifier(tablename, true);
	}

	/**
	 * Construct a ProbenSQL instance from URI and tablename.
	 *
	 * @param uri       JDBC URI to open the database connection.
	 * @param tablename Name of the database table where the sample data is stored
	 * @throws SQLException If the underlying SQL implementation encounters an
	 *                      error, it throws an SQLException.
	 */
	public ProbenSQL(String uri, String tablename) throws SQLException {
		// create a database connection
		connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
		try {
			statement = connection.createStatement();
			statement.setQueryTimeout(30); // set timeout to 30 sec.
		} catch (SQLException e) {
			connection = null;
			throw e;
		}
		tableName = statement.enquoteIdentifier(tablename, true);
	}

	Connection getConnection() {
		return connection;
	}

	public void close() throws SQLException {
		if (statement != null) {
			statement.close();
			statement = null;
		}
		if (SQLProbeGetId != null) {
			SQLProbeGetId.close();
			SQLProbeGetId = null;
		}
		if (SQLProbeGetTime != null) {
			SQLProbeGetTime.close();
			SQLProbeGetTime = null;
		}
		if (SQLProbeGetValue != null) {
			SQLProbeGetValue.close();
			SQLProbeGetValue = null;
		}
		if (SQLGetAllIds != null) {
			SQLGetAllIds.close();
			SQLGetAllIds = null;
		}
		if (SQLProbeSetValue != null) {
			SQLProbeSetValue.close();
			SQLProbeSetValue = null;
		}
		if (SQLProbeNew != null) {
			SQLProbeNew.close();
			SQLProbeNew = null;
		}
		if (connection != null) {
			connection.close();
			connection = null;
		}
	}

	public void createTable() throws SQLException {
		statement.executeUpdate(
				"CREATE TABLE IF NOT EXISTS " + tableName + "(id STRING not null primary key, time STRING not null, value INTEGER)");
	}

	public void clearTable() throws SQLException {
		statement.executeUpdate("DROP TABLE IF EXISTS " + tableName);
		createTable();
	}

	int size() throws SQLException {
		ResultSet res = statement.executeQuery("SELECT count(*) from " + tableName);
		res.next();
		int retval = res.getInt(1);
		res.close();
		return retval;
	}

	@Override
	public Iterator<Probe<Integer>> iterator() {
		try {
			if (SQLGetAllIds == null)
				SQLGetAllIds = connection.prepareStatement("SELECT id FROM " + tableName + " ORDER BY time");
			ResultSet res = SQLGetAllIds.executeQuery();
			return new ProbenIterator(res);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Iterable<Probe<Integer>> range(Integer min, Integer max) {
		return new Range(min, max);
	}

	@Override
	public Iterable<Probe<Integer>> result(Interpretation key) {
		switch (key) {
		case GOOD:
			return new Range(Integer.valueOf(1), Integer.valueOf(Integer.MAX_VALUE));
		case FUZZY:
			return new FuzzyResult();
		case BAD:
			return new Range(Integer.MIN_VALUE, -1);
		}
		// dead code
		return null;
	}

	@Override
	public SQLProbe get(String id) {
		try {
			if (SQLProbeGetId == null)
				SQLProbeGetId = connection
						.prepareStatement("SELECT count(*) FROM " + tableName + " WHERE id = ? ORDER BY time");
			SQLProbeGetId.setString(1, id);
			ResultSet res = SQLProbeGetId.executeQuery();
			res.next();
			SQLProbe retval = null;
			if (res.getInt(1) > 0)
				retval = new SQLProbe(id);
			res.close();
			if (retval == null)
				throw new NoSuchElementException();
			return retval;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public SQLProbe add(ZonedDateTime time) {
		try {
			if (SQLProbeGetId == null)
				SQLProbeGetId = connection
						.prepareStatement("SELECT count(*) FROM " + tableName + " WHERE id = ? ORDER BY time");
			String id;
			ResultSet res = null;
			do {
				UUID uuid = UUID.randomUUID();
				id = uuid.toString();
				SQLProbeGetId.setString(1, id);
				res = SQLProbeGetId.executeQuery();
				res.next();
			} while (res.getInt(1) > 0);
			if (res != null)
				res.close();
			return add(id, time);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public SQLProbe add(String id, ZonedDateTime time) {
		try {
			if (SQLProbeNew == null)
				SQLProbeNew = connection.prepareStatement("INSERT into " + tableName + " (id,time) VALUES (?,?)");
			SQLProbeNew.setString(1, id);
			SQLProbeNew.setString(2, time.toString());
			if (SQLProbeNew.executeUpdate() > 0)
				return new SQLProbe(id);
			else
				return null;
		} catch (SQLiteException e) {

			switch (e.getResultCode()) {
			case SQLITE_CONSTRAINT:
				throw new IllegalArgumentException("Trying to add " + id + e.getMessage(),
								   e);
			default:
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * Add a complete sample row to the database table.
	 *
	 * @param id    Unique identifier of the sample.
	 * @param time  Time the measurement has been performed.
	 * @param value measured value
	 * @return A sample object that references the table row.
	 */
	public SQLProbe add(String id, ZonedDateTime time, Integer value) {
		SQLProbe sample = add(id, time);
		if (value != null)
			sample.setValue(value);
		return sample;
	}

	/**
	 * Add a complete sample row to the database table. The uniqe identifier will be
	 * assigned to the measurement. It can be retrieved from the returned sample
	 * object.
	 *
	 * @param time  Time the measurement has been performed.
	 * @param value measured value
	 * @return A sample object that references the table row.
	 */
	public SQLProbe add(ZonedDateTime time, Integer value) {
		SQLProbe sample = add(time);
		sample.setValue(value);
		return sample;
	}

	@Override
	public void remove(String id) {
		try {
			if (SQLProbeRemove == null)
				SQLProbeRemove = connection.prepareStatement("DELETE from " + tableName + " WHERE id = ?");
			SQLProbeRemove.setString(1, id);
			if (SQLProbeRemove.executeUpdate() == 0)
				throw new NoSuchElementException();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void remove(Probe<Integer> sample) {
		try {
			if (SQLProbeRemoveDate == null)
				SQLProbeRemoveDate = connection
						.prepareStatement("DELETE from " + tableName + " WHERE id = ? AND time = ?");
			SQLProbeRemoveDate.setString(1, sample.getID());
			SQLProbeRemoveDate.setString(2, sample.getTime().toString());
			if (SQLProbeRemoveDate.executeUpdate() == 0)
				throw new NoSuchElementException();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
