package de.schlemmersoft.bewerbung.test1.Proben.SQL;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.util.Iterator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe;
import de.schlemmersoft.bewerbung.test1.Proben.SQL.ProbenSQL.ProbenIterator;
import de.schlemmersoft.bewerbung.test1.Proben.SQL.ProbenSQL.SQLProbe;

/**
 * Unit test for simple App.
 */
public class TestProbenSQL
{
	ProbenSQL data;

	@Test
	void TestInstantiateWithNew() throws SQLException {
		data = new ProbenSQL("jdbc:sqlite::memory:","testProbenSQL");
		data.close();
		data = null;
	}

	@Nested
	public class WhenNew {
		@BeforeEach
		void createNewSQL() throws SQLException {
			data = new ProbenSQL("jdbc:sqlite::memory:","testProbenSQL");
			data.clearTable();
		}

		@AfterEach
		void closeSQL() throws SQLException {
			data.close();
			data = null;
		}

		SQLProbe getOnlyEntry () throws SQLException {
			ProbenIterator it =  (ProbenIterator) data.iterator();
			assertTrue(it.hasNext());
			SQLProbe retval = (SQLProbe) it.next();
			assertFalse(it.hasNext());
			it.close();
			return retval;
		}

/*
		@Test
		void TestIteratorEmpty() {
			Iterator<Probe<Integer>> it =  data.iterator();
			assertFalse(it.hasNext());
			assertThrows(NoSuchElementException.class,
					() -> { it.next(); });
		}

		@Test
		void TestRangeIteratorEmpty() {
			Iterator<Probe<Integer>> it =
					data.range(Integer.valueOf(-5), Integer.valueOf(9)).iterator();
			assertFalse(it.hasNext());
			assertThrows(NoSuchElementException.class,
					() -> { it.next(); });
		}

		@ParameterizedTest(name="Run {index}: testId={0}")
		@EnumSource(Interpretation.class)
		void TestResultIteratorEmpty(Interpretation i) {
		    Iterator<Probe<Integer>> it = data.result(i).iterator();
			assertFalse(it.hasNext());
			assertThrows(NoSuchElementException.class,
					() -> { it.next(); });
		}
*/
		@Test
		void addByDate() throws SQLException {
			assertNotNull(data);
			ZonedDateTime time = ZonedDateTime.now();
			assertNotNull(time);
			SQLProbe origsample = data.add(time);
			SQLProbe sample = getOnlyEntry();
			assertEquals(origsample,sample);
			assertNotEquals("", sample.getID());
			assertEquals(time,sample.getTime());
			assertNull(sample.getValue());

			// check order
			data.add(time.minusHours(1));
			data.add(time.plusHours(1));
			data.add(time);

			ProbenIterator it = (ProbenIterator) data.iterator();
			assertTrue(it.hasNext());
			assertEquals(time.minusHours(1),((SQLProbe)it.next()).getTime());
			assertTrue(it.hasNext());
			assertEquals(time,((SQLProbe)it.next()).getTime());
			assertTrue(it.hasNext());
			assertEquals(time,((SQLProbe)it.next()).getTime());
			assertTrue(it.hasNext());
			assertEquals(time.plusHours(1),((SQLProbe)it.next()).getTime());
			assertFalse(it.hasNext());
			it.close();
		}

		@Test
		void addByIdDate() throws SQLException {
			ZonedDateTime time = ZonedDateTime.now();
			SQLProbe origsample = data.add("Sample 1",time);
			SQLProbe sample = getOnlyEntry();
			assertEquals(origsample,sample);
			assertEquals("Sample 1",sample.getID());
			assertEquals(time,sample.getTime());
			assertNull(sample.getValue());

			assertThrows(IllegalArgumentException.class,
					() -> { data.add("Sample 1",time); });
		}

		@Test
		void addByDateValue() throws SQLException {
			ZonedDateTime time = ZonedDateTime.now();
			SQLProbe origsample = data.add(time,Integer.valueOf (3));
			SQLProbe sample = getOnlyEntry();
			assertEquals(origsample, sample);
			assertNotEquals("", sample.getID());
			assertEquals(time,sample.getTime());
			assertEquals(Integer.valueOf(3),sample.getValue());

			// 2nd element should be after 1st.
			data.add(time.plusHours(1),5);
			data.add(time,Integer.valueOf(4));
			Iterator<Probe<Integer>> it = data.iterator();
			assertTrue(it.hasNext());
			Integer expectedValue = Integer.valueOf(3);
			assertEquals(expectedValue,((SQLProbe)it.next()).getValue());
			assertTrue(it.hasNext());
			expectedValue = Integer.valueOf(4);
			assertEquals(expectedValue,((SQLProbe)it.next()).getValue());
			assertTrue(it.hasNext());
			expectedValue = Integer.valueOf(5);
			assertEquals(Integer.valueOf(5),((SQLProbe)it.next()).getValue());
			assertFalse(it.hasNext());
		}

		@Test
		void addByAll() throws SQLException {
			ZonedDateTime time = ZonedDateTime.now();
			assertNotNull(time);
			SQLProbe origsample = data.add("Sample 2",time,Integer.valueOf(-18));
			assertNotNull(origsample);
			SQLProbe sample = getOnlyEntry();
			assertNotNull(sample);
			assertEquals(origsample,sample);
			assertEquals("Sample 2",sample.getID());
			assertEquals(time,sample.getTime());
			assertEquals(Integer.valueOf(-18),sample.getValue());

			assertThrows(IllegalArgumentException.class,
					() -> { data.add("Sample 2",time,Integer.valueOf(-18)); });
		}
/*
		@Test
		void addByObject() {
			ZonedDateTime time = ZonedDateTime.now();
			SQLProbe original = new SQLProbe("Sample 3",time,Integer.valueOf(79));
			SQLProbe inserted = data.add(original);
			assertFalse(original == inserted);
			SQLProbe sample = getOnlyEntry();
			assertTrue(sample == inserted);
			assertEquals("Sample 3",sample.getID());
			assertEquals(time,sample.getTime());
			assertEquals(Integer.valueOf(79),sample.getValue());

			assertThrows(IllegalArgumentException.class,
					() -> { data.add(original); });
		}

		@Nested
		class WhenAdd {
			class TestSQLProbe {
				public String id;
				public ZonedDateTime time;
				public Integer value;

				TestSQLProbe(String s, ZonedDateTime t, Integer v) {
					id = s;
					time = t;
					value = v;
				}
				TestSQLProbe(String s, ZonedDateTime t) {
					id = s;
					time = t;
					value = null;
				}
			}
			ArrayList<TestSQLProbe> samples;

			@BeforeEach
			void fillDatabase() {
				ZonedDateTime time = ZonedDateTime.now();
				Stream<TestSQLProbe> sampleStream =
						Stream.of(
						 new TestSQLProbe((String)null, time.plusHours(1)),
						 new TestSQLProbe((String)null, time.minusHours(2)),
						 new TestSQLProbe((String)null, time.plusHours(2)),
						 new TestSQLProbe((String)null, time.plusHours(7)),
						 new TestSQLProbe("Sample 1",time.plusHours(4)),
						 new TestSQLProbe((String)null, time,Integer.valueOf(3)),
						 new TestSQLProbe((String)null, time.plusHours(3),Integer.valueOf(5)),
						 new TestSQLProbe((String)null, time.plusHours(6),Integer.valueOf(4)),
						 new TestSQLProbe("Sample 2",time.minusHours(1),Integer.valueOf(-18)),
						 new TestSQLProbe("Sample 3",time.plusHours(5),Integer.valueOf(79))
			        );
				samples = sampleStream.collect(Collectors.toCollection(ArrayList::new));
				for (TestSQLProbe sample : samples) {
					if (sample.id == null) {
						if (sample.value == null)
							data.add(sample.time);
						else
							data.add(sample.time,sample.value);
					} else {
						if (sample.id.equals("Sample 3"))
							data.add(new SQLProbe(sample.id,sample.time,sample.value));
						else if (sample.value == null)
							data.add(sample.id,sample.time);
						else
							data.add(sample.id,sample.time,sample.value);
					}
				}

				Collections.sort(samples, (Comparator<? super TestSQLProbe>) (TestSQLProbe a, TestSQLProbe b) -> a.time.compareTo(b.time));
			}

			@Test
			void orderedIterator() {
				Iterator<Probe<Integer>> it = data.iterator();
				Iterator<TestSQLProbe> it2 = samples.iterator();

				// don't use hasNext on it (this execution path must be checked, too)
				while(it2.hasNext()) {
					Probe<Integer> sample1 = it.next();
					TestSQLProbe sample2 = it2.next();
					if (sample2.id != null) {
						assertEquals(sample1.getID(),sample2.id);
					}
					assertEquals(sample1.getTime(),sample2.time);
					assertEquals(sample1.getValue(),sample2.value);
				}
				assertThrows(NoSuchElementException.class,
						() -> { it.next(); });
			}

			@Test
			void testRangeIterator() {
				Iterator<Probe<Integer>> it = data.range(Integer.valueOf(3),Integer.valueOf(5)).iterator();
				Iterator<TestSQLProbe> it2 = samples.iterator();

				// don't use hasNext on it (this execution path must be checked, too)
				while(it2.hasNext()) {
					TestSQLProbe sample2 = it2.next();
					if (sample2.value == null) continue;
					if (sample2.value.value < 3 || sample2.value.value > 5)
						continue;
					Probe<Integer> sample1 = it.next();
					assertEquals(sample2.value,sample1.getValue());
					assertEquals(sample2.time,sample1.getTime());
					if (sample2.id != null) {
						assertEquals(sample2.id,sample1.getID());
					}
				}
				assertThrows(NoSuchElementException.class,
						() -> { it.next(); });
			}

			@ParameterizedTest(name="Run {index}: testId={0}")
			@EnumSource(Interpretation.class)
			void testInterpretationIterator(Interpretation interpretation) {
				Iterator<Probe<Integer>> it = data.result(interpretation).iterator();
				Iterator<TestSQLProbe> it2 = samples.iterator();

				// don't use hasNext on it (this execution path must be checked, too)
				while(it2.hasNext()) {
					TestSQLProbe sample2 = it2.next();
					if (interpretation != Interpretation.FUZZY && sample2.value == null)
						continue;
					if (sample2.value != null)
						switch (interpretation) {
						case GOOD:
							if (sample2.value.value <= 0) continue;
							break;
						case BAD:
							if (sample2.value.value >= 0) continue;
							break;
						case FUZZY:
							if (sample2.value.value != 0) continue;
							break;
						}
					Probe<Integer> sample1 = it.next();
					assertEquals(sample2.value,sample1.getValue());
					assertEquals(sample2.time,sample1.getTime());
					if (sample2.id != null) {
						assertEquals(sample2.id,sample1.getID());
					}
				}
				assertThrows(NoSuchElementException.class,
						() -> { it.next(); });
			}


			@Test
			void testRemoveNonexistingId() {
				assertThrows(NoSuchElementException.class,
						() -> { data.remove(""); });
				assertThrows(NoSuchElementException.class,
						() -> { data.remove(new SQLProbe("",ZonedDateTime.now())); });
			}

			@RepeatedTest(10)
			void testDeleteById (RepetitionInfo repetitionInfo ) {
				System.out.print("Current rep. ");
				System.out.println(repetitionInfo.getCurrentRepetition());
				TestSQLProbe current = samples.get(repetitionInfo.getCurrentRepetition()-1);
				if (current.id == null) return;
				assertTrue(data.size() >= 10);
				int oldsize = data.size();
				data.remove(current.id);
				assertEquals(oldsize-1, data.size());

				Iterator<Probe<Integer>> it = data.iterator();
				Probe<Integer> sample1;

				for (int i = 0 ; i != samples.size(); ++i) {
					System.out.printf("#%d/%d: \n",i,repetitionInfo.getCurrentRepetition());
					if (i == repetitionInfo.getCurrentRepetition()-1) continue;
					current = samples.get(i);
					sample1 = it.next();
					System.out.print(current.id);
					System.out.print(" == ");
					System.out.println(sample1);
					assertEquals(current.value,sample1.getValue());
					assertEquals(current.time,sample1.getTime());
					if (current.id != null) {
						assertEquals(current.id,sample1.getID());
					}
				}
				assertThrows(NoSuchElementException.class,
						() -> { it.next(); });
			}

			@RepeatedTest(10)
			void testDeleteByObject (RepetitionInfo repetitionInfo ) {
				System.out.print("Current rep. ");
				System.out.println(repetitionInfo.getCurrentRepetition());

				assertTrue(data.size() >= 10);
				Probe<Integer> sample = null;
				Iterator<Probe<Integer>> it = data.iterator();
				for (int i = repetitionInfo.getCurrentRepetition(); i > 0; --i) {
					sample = it.next();
				}
				assertNotNull(sample);

				int oldsize = data.size();
				data.remove(sample);
				assertEquals(oldsize-1, data.size());

				it = data.iterator();
				TestSQLProbe current;
				for (int i = 0 ; i != samples.size(); ++i) {
					System.out.printf("#%d/%d: \n",i,repetitionInfo.getCurrentRepetition());
					if (i == repetitionInfo.getCurrentRepetition()-1) continue;
					current = samples.get(i);
					sample = it.next();
					System.out.print(current.id);
					System.out.print(" == ");
					System.out.println(sample);
					assertEquals(current.value,sample.getValue());
					assertEquals(current.time,sample.getTime());
					if (current.id != null) {
						assertEquals(current.id,sample.getID());
					}
				}
				Iterator<Probe<Integer>> it2 = it;
				assertThrows(NoSuchElementException.class,
						() -> { it2.next(); });
			}
		}
*/

	}

	@Test
    public static void sqliteExample()
    {
      Connection connection = null;
      try
      {
        // create a database connection
        connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
        Statement statement = connection.createStatement();
        statement.setQueryTimeout(30);  // set timeout to 30 sec.

        statement.executeUpdate("drop table if exists person");
        statement.executeUpdate("create table person (id integer, name string)");
        statement.executeUpdate("insert into person values(1, 'leo')");
        statement.executeUpdate("insert into person values(2, 'yui')");
        ResultSet rs = statement.executeQuery("select * from person");
        while(rs.next())
        {
          // read the result set
          System.out.println("name = " + rs.getString("name"));
          System.out.println("id = " + rs.getInt("id"));
        }
      }
      catch(SQLException e)
      {
        // if the error message is "out of memory",
        // it probably means no database file is found
        System.err.println(e.getMessage());
      }
      finally
      {
        try
        {
          if(connection != null)
            connection.close();
        }
        catch(SQLException e)
        {
          // connection close failed.
          System.err.println(e.getMessage());
        }
      }
    }
}
