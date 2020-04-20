package de.schlemmersoft.bewerbung.test1.Proben.Vector;

import static org.junit.jupiter.api.Assertions.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import de.schlemmersoft.bewerbung.test1.Proben.Public.GenericProbe;
import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe;
import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe.Interpretation;

/**
 * Unit test for simple App.
 */
public class TestProbenVector
{
	class IntProbe extends GenericProbe <Integer>{

		public IntProbe(String i, ZonedDateTime t) {
			super(i, t);
		}

		public IntProbe(String i, ZonedDateTime t, int v) {
			super(i, t, Integer.valueOf(v));
		}

		public IntProbe(IntProbe probe) {
			super(probe);
		}

		public IntProbe(String string, ZonedDateTime now, Integer Integer) {
			super(string, now, Integer);
		}

		@Override
		public Interpretation getInterpretation() {
			if (value == null)
				return Interpretation.FUZZY;
			switch (Integer.signum(value.intValue())) {
			case -1:
				return Interpretation.BAD;
			case 0:
				return Interpretation.FUZZY;
			case 1:
				return Interpretation.GOOD;
			}
			// dead code
			return null;
		}

		@Override
		protected Integer cloneValue(Integer v) {
			if (v == null) return null;
			int tmp = v.intValue();
			return Integer.valueOf(tmp);
		}

		@Override
		public IntProbe clone() throws CloneNotSupportedException {
			return (IntProbe)super.clone();
		}

		@Override
		public String toString() {
			StringBuffer buf = new StringBuffer();
			buf.append("IntProbe {Id='");
			buf.append(id);
			buf.append("', ");
			buf.append(time);
			buf.append(", ");
			buf.append(value);
			buf.append(" (");
			buf.append(getInterpretation());
			buf.append(")}");
			return buf.toString();
		}
	}
	class IntProbenVector extends ProbenVector<Integer, IntProbe> {

		@Override
		protected IntProbe createElement(String id, ZonedDateTime time, Integer value) {
			if (value == null)
				return new IntProbe(id,time);
			return new IntProbe(id,time,value.intValue());
		}

		@Override
		public String toString() {
			return data.toString();
		}

	}

	IntProbenVector data;

	IntProbe getOnlyEntry () {
		Iterator<Probe<Integer>> it =  data.iterator();
		assertTrue(it.hasNext());
		IntProbe retval = (IntProbe) it.next();
		assertFalse(it.hasNext());
		return retval;
	}

	@Test
	void TestInstantiateWithNew() {
		data = new IntProbenVector();
	}

	@Nested
	class WhenNew {
		@BeforeEach
		void createNewVector() {
			data = new IntProbenVector();
		}

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

		@Test
		void addByDate() {
			assertNotNull(data);
			ZonedDateTime time = ZonedDateTime.now();
			assertNotNull(time);
			IntProbe origsample = data.add(time);
			IntProbe sample = getOnlyEntry();
			assertTrue(sample == origsample);
			assertNotEquals("", sample.getID());
			assertEquals(time,sample.getTime());
			assertNull(sample.getValue());

			// check order
			data.add(time.minusHours(1));
			data.add(time.plusHours(1));
			data.add(time);

			Iterator<Probe<Integer>> it = data.iterator();
			assertTrue(it.hasNext());
			assertEquals(time.minusHours(1),((IntProbe)it.next()).getTime());
			assertTrue(it.hasNext());
			assertEquals(time,((IntProbe)it.next()).getTime());
			assertTrue(it.hasNext());
			assertEquals(time,((IntProbe)it.next()).getTime());
			assertTrue(it.hasNext());
			assertEquals(time.plusHours(1),((IntProbe)it.next()).getTime());
			assertFalse(it.hasNext());
		}

		@Test
		void addByIdDate() {
			ZonedDateTime time = ZonedDateTime.now();
			IntProbe origsample = data.add("Sample 1",time);
			IntProbe sample = getOnlyEntry();
			assertTrue(sample == origsample);
			assertEquals("Sample 1",sample.getID());
			assertEquals(time,sample.getTime());
			assertNull(sample.getValue());

			assertThrows(IllegalArgumentException.class,
					() -> { data.add("Sample 1",time); });
		}

		@Test
		void addByDateValue() {
			ZonedDateTime time = ZonedDateTime.now();
			IntProbe origsample = data.add(time,Integer.valueOf(3));
			IntProbe sample = getOnlyEntry();
			assertTrue(sample == origsample);
			assertNotEquals("", sample.getID());
			assertEquals(time,sample.getTime());
			assertEquals(Integer.valueOf(3),sample.getValue());

			// 2nd element should be after 1st.
			data.add(time.plusHours(1),Integer.valueOf(5));
			data.add(time,Integer.valueOf(4));
			Iterator<Probe<Integer>> it = data.iterator();
			assertTrue(it.hasNext());
			Integer expectedValue = Integer.valueOf(3);
			assertEquals(expectedValue,((IntProbe)it.next()).getValue());
			assertTrue(it.hasNext());
			expectedValue = Integer.valueOf(4);
			assertEquals(expectedValue,((IntProbe)it.next()).getValue());
			assertTrue(it.hasNext());
			expectedValue = Integer.valueOf(5);
			assertEquals(Integer.valueOf(5),((IntProbe)it.next()).getValue());
			assertFalse(it.hasNext());
		}

		@Test
		void addByAll() {
			ZonedDateTime time = ZonedDateTime.now();
			assertNotNull(time);
			IntProbe origsample = data.add("Sample 2",time,Integer.valueOf(-18));
			assertNotNull(origsample);
			IntProbe sample = getOnlyEntry();
			assertNotNull(sample);
			assertTrue(sample == origsample);
			assertEquals("Sample 2",sample.getID());
			assertEquals(time,sample.getTime());
			assertEquals(Integer.valueOf(-18),sample.getValue());

			assertThrows(IllegalArgumentException.class,
					() -> { data.add("Sample 2",time,Integer.valueOf(-18)); });
		}

		@Test
		void addByObject() {
			ZonedDateTime time = ZonedDateTime.now();
			IntProbe original = new IntProbe("Sample 3",time,Integer.valueOf(79));
			IntProbe inserted = data.add(original);
			assertFalse(original == inserted);
			IntProbe sample = getOnlyEntry();
			assertTrue(sample == inserted);
			assertEquals("Sample 3",sample.getID());
			assertEquals(time,sample.getTime());
			assertEquals(Integer.valueOf(79),sample.getValue());

			assertThrows(IllegalArgumentException.class,
					() -> { data.add(original); });
		}

		@Nested
		class WhenAdd {
			class TestIntProbe {
				public String id;
				public ZonedDateTime time;
				public Integer value;

				TestIntProbe(String s, ZonedDateTime t, Integer v) {
					id = s;
					time = t;
					value = v;
				}
				TestIntProbe(String s, ZonedDateTime t) {
					id = s;
					time = t;
					value = null;
				}
			}
			ArrayList<TestIntProbe> samples;

			@BeforeEach
			void fillDatabase() {
				ZonedDateTime time = ZonedDateTime.now();
				Stream<TestIntProbe> sampleStream =
						Stream.of(
						 new TestIntProbe((String)null, time.plusHours(1)),
						 new TestIntProbe((String)null, time.minusHours(2)),
						 new TestIntProbe((String)null, time.plusHours(2)),
						 new TestIntProbe((String)null, time.plusHours(7)),
						 new TestIntProbe("Sample 1",time.plusHours(4)),
						 new TestIntProbe((String)null, time,Integer.valueOf(3)),
						 new TestIntProbe((String)null, time.plusHours(3),Integer.valueOf(5)),
						 new TestIntProbe((String)null, time.plusHours(6),Integer.valueOf(4)),
						 new TestIntProbe("Sample 2",time.minusHours(1),Integer.valueOf(-18)),
						 new TestIntProbe("Sample 3",time.plusHours(5),Integer.valueOf(79))
			        );
				samples = sampleStream.collect(Collectors.toCollection(ArrayList::new));
				for (TestIntProbe sample : samples) {
					if (sample.id == null) {
						if (sample.value == null)
							data.add(sample.time);
						else
							data.add(sample.time,sample.value);
					} else {
						if (sample.id.equals("Sample 3"))
							data.add(new IntProbe(sample.id,sample.time,sample.value));
						else if (sample.value == null)
							data.add(sample.id,sample.time);
						else
							data.add(sample.id,sample.time,sample.value);
					}
				}

				Collections.sort(samples, (Comparator<? super TestIntProbe>) (TestIntProbe a, TestIntProbe b) -> a.time.compareTo(b.time));
			}

			@Test
			void orderedIterator() {
				Iterator<Probe<Integer>> it = data.iterator();
				Iterator<TestIntProbe> it2 = samples.iterator();

				// don't use hasNext on it (this execution path must be checked, too)
				while(it2.hasNext()) {
					Probe<Integer> sample1 = it.next();
					TestIntProbe sample2 = it2.next();
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
				Iterator<TestIntProbe> it2 = samples.iterator();

				// don't use hasNext on it (this execution path must be checked, too)
				while(it2.hasNext()) {
					TestIntProbe sample2 = it2.next();
					if (sample2.value == null) continue;
					if (sample2.value.intValue() < 3 || sample2.value.intValue() > 5)
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
				Iterator<TestIntProbe> it2 = samples.iterator();

				// don't use hasNext on it (this execution path must be checked, too)
				while(it2.hasNext()) {
					TestIntProbe sample2 = it2.next();
					if (interpretation != Interpretation.FUZZY && sample2.value == null)
						continue;
					if (sample2.value != null)
						switch (interpretation) {
						case GOOD:
							if (sample2.value.intValue() <= 0) continue;
							break;
						case BAD:
							if (sample2.value.intValue() >= 0) continue;
							break;
						case FUZZY:
							if (sample2.value.intValue() != 0) continue;
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
						() -> { data.remove(new IntProbe("",ZonedDateTime.now())); });
			}

			@RepeatedTest(10)
			void testDeleteById (RepetitionInfo repetitionInfo ) {
				System.out.print("Current rep. ");
				System.out.println(repetitionInfo.getCurrentRepetition());
				TestIntProbe current = samples.get(repetitionInfo.getCurrentRepetition()-1);
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
				TestIntProbe current;
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
	}
}
