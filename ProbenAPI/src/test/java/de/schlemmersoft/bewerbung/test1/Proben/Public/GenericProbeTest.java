package de.schlemmersoft.bewerbung.test1.Proben.Public;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe.Interpretation;

/**
 * Test cases for the class GenericProbe
 * 
 * @author Tobias Schlemmer
 *
 */
public class GenericProbeTest {
	static class MyInteger {
		int value;

		MyInteger(int i) {
			value = i;
		}

		static MyInteger valueOf(int i) {
			return new MyInteger(i);
		}

		public int intValue() {
			return value;
		}

		public boolean equals(Object other) {
			if (!(other instanceof MyInteger))
				return false;
			MyInteger tmp = (MyInteger) other;
			return value == tmp.value;
		}

		public boolean equals(int other) {
			return value == other;
		}

		public boolean equals(MyInteger other) {
			return value == other.intValue();
		}

		public String toString() {
			return Integer.valueOf(value).toString();
		}
	}

	class IntProbe extends GenericProbe<MyInteger> {

		public IntProbe(String i, ZonedDateTime t) {
			super(i, t);
		}

		public IntProbe(String i, ZonedDateTime t, int v) {
			super(i, t, MyInteger.valueOf(v));
		}

		public IntProbe(IntProbe probe) {
			super(probe);
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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected MyInteger cloneValue(MyInteger v) {
			if (v == null)
				return null;
			int tmp = v.intValue();
			return MyInteger.valueOf(tmp);
		}

		@Override
		public IntProbe clone() throws CloneNotSupportedException {
			return (IntProbe) super.clone();
		}
	}
	/*
	 * class Integer implements Probe.Messwert { public int value; public
	 * Integer.valueOf(int v) { value = v; }
	 * 
	 * Override public boolean equals (Object other) { if (this == other) return
	 * true; if (other instanceof Integer) return equals((Integer)other); return
	 * false; }
	 * 
	 * Override public boolean equals(Messwert other) { if (other instanceof
	 * Integer) return equals((Integer)other); return false; }
	 * 
	 * public boolean equals(Integer other) { return value == other.value; }
	 * 
	 * 
	 * Override public Integer clone() { return Integer.valueOf(value); }
	 * 
	 * Override public Interpretation getInterpretation() { if (value > 0) return
	 * Interpretation.GOOD; if (value < 0) return Interpretation.BAD; return
	 * Interpretation.FUZZY; } }
	 */

	void checkCloneResult(IntProbe probe, IntProbe probe2) {
		assertNotNull(probe2);
		assertFalse(probe2 == probe);
		assertTrue(probe.equals(probe2));
		assertEquals(probe, probe2);
		assertTrue(probe2.getID() == probe.getID());
		assertEquals(probe2.getID(), probe.getID());
		assertTrue(probe2.getInterpretation() == probe.getInterpretation());
		if (probe.getValue() == null)
			assertTrue(probe2.getValue() == probe.getValue());
		else
			assertFalse(probe2.getValue() == probe.getValue());
		assertEquals(probe2.getValue(), probe.getValue());
	}

	/**
	 * Test cloning of a particular IntProbe object.
	 * 
	 * @param probe the object to be cloned.
	 * @throws CloneNotSupportedException If the underlying clone operation fails.
	 */
	void testClone(IntProbe probe) throws CloneNotSupportedException {
		IntProbe probe2 = probe.clone();
		checkCloneResult(probe, probe2);

		probe2 = new IntProbe(probe);
		checkCloneResult(probe, probe2);
	}

	/**
	 * Test the equals operator of the GenericProbe class.
	 *
	 * @throws CloneNotSupportedException if cloning fails at some point. This
	 *                                    should not occur during this test.
	 */
	@Test
	public void testEquals() throws CloneNotSupportedException {
		String id1 = "Id 1";
		String id2 = "Id 2";
		ZonedDateTime t1 = ZonedDateTime.now();
		ZonedDateTime t2 = t1.minusYears(1);
		int i1 = 7;
		int i2 = 8;
		Integer v1 = Integer.valueOf(i1);
		Integer v2 = Integer.valueOf(i2);
		IntProbe sample = new IntProbe(id1, t1, v1);
		assertTrue(sample.equals(sample));
		sample = new IntProbe(id1, t1, Integer.valueOf(i1));
		assertTrue(sample.equals(sample));
		IntProbe sample2 = sample.clone();
		assertTrue(sample.equals(sample));
		sample2 = new IntProbe(id2, t1, v1);
		assertFalse(sample.equals(sample2));
		sample2 = new IntProbe(id1, t2, v1);
		assertFalse(sample.equals(sample2));
		sample2 = new IntProbe(id2, t2, v1);
		assertFalse(sample.equals(sample2));
		sample2 = new IntProbe(id1, t1, v2);
		assertFalse(sample.equals(sample2));
		sample2 = new IntProbe(id2, t1, v2);
		assertFalse(sample.equals(sample2));
		sample2 = new IntProbe(id1, t2, v2);
		assertFalse(sample.equals(sample2));
		sample2 = new IntProbe(id2, t2, v2);
		assertFalse(sample.equals(sample2));
		sample2 = new IntProbe(id1, t1, v1);
		assertTrue(sample.equals(sample2));
		assertFalse(sample.equals(this));
		assertTrue(sample.equals((Object) sample));
	}

	/**
	 * Test the full constructor of GenericProbe objects.
	 * 
	 * @param testId             Identifier of the generated sample.
	 * @param testtime           Time of the generated sample.
	 * @param testValue          The measurement value to be tested.
	 * @param testInterpretation The expected result from evaluation of the
	 *                           measurement value.
	 * @throws CloneNotSupportedException if a cloning operation fails. This is an
	 *                                    internal error.
	 */
	@ParameterizedTest(name = "Run {index}: testId={0}")
	@MethodSource("testFullConstructor_Parameters")
	public void testFullConstructor(String testId, ZonedDateTime testtime, int testValue,
			Interpretation testInterpretation) throws CloneNotSupportedException {

		IntProbe probe = new IntProbe(testId, testtime, Integer.valueOf(testValue));
		assertEquals(testId, probe.getID());
		MyInteger newvalue = probe.getValue();
		assertEquals(testValue, newvalue.intValue());
		assertEquals(testInterpretation, probe.getInterpretation());
		assertEquals(testtime, probe.getTime());

		testClone(probe);
	}

	private static Stream<Arguments> paramsFullConstructor() throws Throwable {
		return Stream.of(Arguments.of("0", ZonedDateTime.now(), 1, Interpretation.GOOD),
				Arguments.of("1", ZonedDateTime.now(), 0, Interpretation.FUZZY),
				Arguments.of("2", ZonedDateTime.now(), -1, Interpretation.BAD));
	}


	/**
	 * Test the constructor without the measurement value. The value is added afterwards.
	 * @param testId             Identifier of the generated sample.
	 * @param testtime           Time of the generated sample.
	 * @param testValue          The measurement value to be tested.
	 * @param testInterpretation The expected result from evaluation of the
	 *                           measurement value.
	 * @throws CloneNotSupportedException if a cloning operation fails. This is an
	 *                                    internal error.
	 */
	@ParameterizedTest(name = "Run {index}: testId={0}")
	@MethodSource("testFullConstructor_Parameters")
	public void testReducedConstructor(String testId, ZonedDateTime testtime, int testValue,
			Interpretation testInterpretation) throws CloneNotSupportedException {
		IntProbe probe = new IntProbe(testId, testtime);
		assertEquals(testId, probe.getID());
		MyInteger value = probe.getValue();
		assertEquals(null, value);
		assertEquals(Interpretation.FUZZY, probe.getInterpretation()); // undefined behaviour.
		assertEquals(testtime, probe.getTime());

		testClone(probe);

		IntProbe probe2 = probe.clone();
		assertNotNull(probe2);

		probe.setValue(MyInteger.valueOf(testValue));

		assertNotEquals(probe, probe2);
		assertNotEquals(probe2, probe);

		assertEquals(testId, probe.getID());
		MyInteger newvalue = probe.getValue();
		assertEquals(testValue, newvalue.intValue());
		assertEquals(testInterpretation, probe.getInterpretation());
		assertEquals(testtime, probe.getTime());

		testClone(probe);
	}

}
