package de.schlemmersoft.bewerbung.test1.Proben.Public;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.ZonedDateTime;
import java.util.stream.Stream;

import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe;
import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe.Messwert;

import static de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe.Interpretation;



public class GenericProbeTest {

	class intvalue implements Probe.Messwert {
		public int value;
		public intvalue(int v) {
			value = v;
		}

		@Override
		public boolean equals (Object other) {
			if (this == other) return true;
			if (other instanceof intvalue) return equals((intvalue)other);
			return false;
		}

		@Override
		public boolean equals(Messwert other) {
			if (other instanceof intvalue) return equals((intvalue)other);
			return false;
		}

		public boolean equals(intvalue other) {
			return value == other.value;
		}


		@Override
		public intvalue clone() {
			return new intvalue(value);
		}

		@Override
		public Interpretation getInterpretation() {
			if (value > 0) return Interpretation.GOOD;
			if (value < 0) return Interpretation.BAD;
			return Interpretation.FUZZY;
		}
	}

	static Stream<Arguments> testFullConstructor_Parameters() throws Throwable {
        return Stream.of(
			 Arguments.of("0", ZonedDateTime.now(),  1, Interpretation.GOOD),
			 Arguments.of("1", ZonedDateTime.now(),  0, Interpretation.FUZZY),
			 Arguments.of("2", ZonedDateTime.now(), -1, Interpretation.BAD)
			 );
	}

	public void testClone(GenericProbe probe) {
		GenericProbe probe2 = probe.clone();
		assertFalse ( probe2 == probe );
		assertEquals( probe, probe2 );
		assertFalse ( probe2.getID() == probe.getID());
		assertEquals( probe2.getID(), probe.getID());
		assertTrue  ( probe2.getInterpretation() == probe.getInterpretation());
		assertFalse ( probe2.getValue() == probe.getValue());
		assertEquals( probe2.getValue(), probe.getValue());
	}

	@Test
	public void testEquals() {
		String id1 = "Id 1";
		String id2 = "Id 2";
		ZonedDateTime t1 = ZonedDateTime.now();
		ZonedDateTime t2 = t1.minusYears(1);
		int i1 = 7;
		int i2 = 8;
		intvalue v1 = new intvalue(i1);
		intvalue v2 = new intvalue(i2);
		GenericProbe sample = new GenericProbe(id1,t1,v1);
		assertTrue(sample.equals(sample));
		sample = new GenericProbe(id1,t1,new intvalue(i1));
		assertTrue(sample.equals(sample));
		GenericProbe sample2 = sample.clone();
		assertTrue(sample.equals(sample));
		sample2 = new GenericProbe(id2,t1,v1);
		assertFalse(sample.equals(sample2));
		sample2 = new GenericProbe(id1,t2,v1);
		assertFalse(sample.equals(sample2));
		sample2 = new GenericProbe(id2,t2,v1);
		assertFalse(sample.equals(sample2));
		sample2 = new GenericProbe(id1,t1,v2);
		assertFalse(sample.equals(sample2));
		sample2 = new GenericProbe(id2,t1,v2);
		assertFalse(sample.equals(sample2));
		sample2 = new GenericProbe(id1,t2,v2);
		assertFalse(sample.equals(sample2));
		sample2 = new GenericProbe(id2,t2,v2);
		assertFalse(sample.equals(sample2));
		sample2 = new GenericProbe(id1,t1,v1);
		assertTrue(sample.equals(sample2));
		assertFalse(sample.equals(this));
		assertTrue(sample.equals((Object)sample));
	}

	@ParameterizedTest(name="Run {index}: testId={0}")
	@MethodSource("testFullConstructor_Parameters")
	public void testFullConstructor( String testId,
					 ZonedDateTime testtime,
					 int testValue,
					 Interpretation testInterpretation) {

		GenericProbe probe = new GenericProbe(testId,
						      testtime,
						      new intvalue( testValue ) );
		assertEquals(testId,probe.getID());
		intvalue newvalue  = (intvalue)probe.getValue();
		assertEquals(testValue,newvalue.value);
		assertEquals(testInterpretation,probe.getInterpretation());
		assertEquals(testtime,probe.getTime());

		testClone(probe);
	}

	@ParameterizedTest(name="Run {index}: testId={0}")
	@MethodSource("testFullConstructor_Parameters")
	public void testReducedConstructor( String testId,
					    ZonedDateTime testtime,
					    int testValue,
					    Interpretation testInterpretation ) {
		GenericProbe probe = new GenericProbe( testId,
						       testtime );
		assertEquals( testId, probe.getID() );
		intvalue value  = ( intvalue )probe.getValue();
		assertEquals( null, value );
		assertEquals( Interpretation.FUZZY, probe.getInterpretation() ); // undefined behaviour.
		assertEquals( testtime, probe.getTime() );

		probe.setValue( new intvalue( testValue ) );

		assertEquals(testId,probe.getID());
		intvalue newvalue  = (intvalue)probe.getValue();
		assertEquals(testValue,newvalue.value);
		assertEquals(testInterpretation,probe.getInterpretation());
		assertEquals(testtime,probe.getTime());

		testClone(probe);
	}
}
