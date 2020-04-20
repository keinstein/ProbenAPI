package de.schlemmersoft.bewerbung.test1.Proben.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI;
import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe.Interpretation;

/**
 * Hello world!
 *
 */
public class ProbenSQL implements ProbenAPI<Integer>
{

	public class SQLProbe implements Probe<Integer> {
		String id;

		SQLProbe(String newid) {
			id = newid;
		}

		public boolean equals(Object other) {
			if (other instanceof SQLProbe)
				return id.equals(((SQLProbe)other).id);
			return false;
		}

		@Override
		public boolean equals(Probe<Integer> other) {
			return getValue().equals(other.getValue());
		}

		@Override
		public Probe<Integer> clone() throws CloneNotSupportedException {
			SQLProbe newprobe = (ProbenSQL.SQLProbe)super.clone();
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
			} catch (SQLException e){
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
			} catch (SQLException e){
				// TODO: implement error handling
				return null;
			}

		}

		@Override
		public void setValue(Integer v) {
			try {
				if (SQLProbeSetValue == null)
					SQLProbeSetValue = connection.prepareStatement("UPDATE " + tableName + " SET value = ? WHERE id = ?");
				SQLProbeSetValue.setInt(0, v.intValue());
				SQLProbeSetValue.setString(1, id);
				SQLProbeGetValue.executeUpdate();
			} catch (SQLException e){
				// TODO: implement error handling
			}
		}

		@Override
		public Interpretation getInterpretation() {
			Integer v = getValue();
			if (v == null) return Interpretation.FUZZY;
			int value = v.intValue();
			if (value < 0) return Interpretation.BAD;
			if (value > 0 ) return Interpretation.GOOD;
			return Interpretation.FUZZY;
		}

		@Override
		public String toString() {
			return "Probe " + id;
		}
	}

	public class ProbenIterator implements Iterator<Probe<Integer>> {
		ResultSet queryResult;
		String current;

		ProbenIterator (ResultSet res) {
			current = null;
			queryResult = res;
			try {
				queryResult.next();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new NoSuchElementException();
			}
		}

		public void close() throws SQLException  {
			queryResult.close();
		}

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
				throw new NoSuchElementException();
			}
		}
	}

	private Connection connection;
	private Statement statement;
	private PreparedStatement SQLProbeGetId;
	private PreparedStatement SQLProbeGetTime;
	private PreparedStatement SQLProbeGetValue;
	private PreparedStatement SQLProbeSetValue;
	private PreparedStatement SQLProbeGetAllIds;
	private PreparedStatement SQLProbeNew;
	String tableName;

	ProbenSQL (Connection c, String tablename) throws SQLException {
		connection = c;
		try {
			statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.
		} catch (SQLException e) {
			connection = null;
			throw e;
		}
		tableName = tablename;
	}

	ProbenSQL (String uri, String tablename) throws SQLException {
		 // create a database connection
		connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
		try {
			statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.
		} catch (SQLException e) {
			connection = null;
			throw e;
		}
		tableName = tablename;
	}

	Connection getConnection () {
		return connection;
	}

	void close() throws SQLException {
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
		if (SQLProbeGetAllIds != null) {
			SQLProbeGetAllIds.close();
			SQLProbeGetAllIds = null;
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

	void clearTable() throws SQLException {
		statement.executeUpdate("DROP TABLE IF EXISTS "+ tableName);
        statement.executeUpdate("CREATE TABLE " + tableName + "(id STRING not null primary key, time STRING not null, value INTEGER)");
	}

	@Override
	public Iterator<Probe<Integer>> iterator() {
		try {
			if (SQLProbeGetAllIds == null)
				SQLProbeGetAllIds = connection.prepareStatement("SELECT id FROM " + tableName + " ORDER BY time");
			ResultSet res = SQLProbeGetAllIds.executeQuery();
			// TODO Auto-generated method stub
			return new ProbenIterator(res);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Iterable<Probe<Integer>> range(Integer min, Integer max) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Probe<Integer>> result(Interpretation key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Probe<Integer> add(Probe<Integer> sample) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SQLProbe add(ZonedDateTime time) {
		try {
			if (SQLProbeGetId == null)
					SQLProbeGetId = connection.prepareStatement("SELECT count(*) FROM " + tableName + " WHERE id = ? ORDER BY time");
			String id;
			ResultSet res = null;
			do {
				UUID uuid = UUID.randomUUID();
				id = uuid.toString();
				SQLProbeGetId.setString(1,id);
				res = SQLProbeGetId.executeQuery();
				res.next();
			} while (res.getInt(1) > 0);
			if (res != null)
				res.close();
			return add(id,time);
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
			SQLProbeNew.setString(1,id);
			SQLProbeNew.setString(2,time.toString());
			if (SQLProbeNew.executeUpdate() > 0)
				return new SQLProbe(id);
			else
				return null;
		} catch (SQLiteException e) {
			// TODO Auto-generated catch block

			switch (e.getResultCode()) {
			case SQLITE_CONSTRAINT:
				throw new IllegalArgumentException();
			default:
				e.printStackTrace();
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void remove(String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove(Probe<Integer> sample) {
		// TODO Auto-generated method stub

	}


}
