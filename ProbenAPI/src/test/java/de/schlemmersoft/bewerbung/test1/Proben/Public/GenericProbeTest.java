package de.schlemmersoft.bewerbung.test1.Proben.Public;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.ZonedDateTime;
import java.util.stream.Stream;

import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe;
import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe.Interpretation;



public class GenericProbeTest {

	class intvalue implements Probe.Messwert {
		public int value;
		public intvalue(int v) {
			value = v;
		}
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
	}
}
