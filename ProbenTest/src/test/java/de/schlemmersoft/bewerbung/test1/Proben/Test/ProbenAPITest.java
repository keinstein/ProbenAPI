package de.schlemmersoft.bewerbung.test1.Proben.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.schlemmersoft.bewerbung.test1.Proben.Public.GenericProbe;
import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI;
import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe;
import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe.Interpretation;
import de.schlemmersoft.bewerbung.test1.Proben.Test.ConsoleApp.SyntaxError;
import de.schlemmersoft.bewerbung.test1.Proben.Vector.ProbenVector;


/**
 * Unit test for simple App.
 */
public class ProbenAPITest
{
	@Test
	void testMain() throws FileNotFoundException, SQLException, SyntaxError {
		String[] args = null;
	    final InputStream original = System.in;
	    final FileInputStream fips = new FileInputStream(new File("src/test/text/commands1.txt"));
	    System.setIn(fips);
	    ConsoleApp.mainProgram(args);
	    String[] args2 = { ":memory:", "testTable" };
	    final FileInputStream fips2 = new FileInputStream(new File("src/test/text/commands1.txt"));
	    System.setIn(fips2);
	    ConsoleApp.mainProgram(args2);
	    System.setIn(original);
	}
	/*
		class Probe extends GenericProbe<Integer>{

		}
	class Mwt implements Messwert, Cloneable {
		int value;

		Mwt(int v) {
			value = v;
		}

		@Override
		public boolean equals (Object other) {
			if (this == other) return true;
			if (other instanceof Mwt) return equals((Mwt)other);
			return false;
		}

		@Override
		public boolean equals(Messwert other) {
			if (other instanceof Mwt) return equals((Mwt)other);
			return false;
		}

		public boolean equals(Mwt other) {
			return value == other.value;
		}


		@Override
		public Mwt clone() {
			return new Mwt(value);
		}

		@Override
		public Interpretation getInterpretation() {
			// TODO Auto-generated method stub
			if (value < 0) return Interpretation.BAD;
			if (value > 0) return Interpretation.GOOD;
			return Interpretation.FUZZY;
		}
	}

	static Stream<Arguments> testFullApi_Parameters() throws Throwable {
        return Stream.of(
			 Arguments.of(new ProbenVector<Mwt>())/*,
			 Arguments.of("1", ZonedDateTime.now(),  0, Interpretation.FUZZY),
			 Arguments.of("2", ZonedDateTime.now(), -1, Interpretation.BAD)*
			 );
	}

    /*
     * Rigorous Test :-)
     *
	@ParameterizedTest(name="Run {index}: testId={0}")
	@MethodSource("testFullApi_Parameters")
    public void testFullApi(ProbenAPI api)
    {
		ZonedDateTime time = ZonedDateTime.now();
		Probe sample1 = api.add(time);
		Probe sample2 = api.add("Sample id 1",time.plusHours(1));
		Probe sample3 = api.add("Sample id 2",time.plusHours(2));
		sample3.setValue(api.makeValue(3));
		Probe sample4 = api.add(time.plusHours(3),-3);
		Probe sample5 = api.add(time.plusHours(4),0);
		Probe sample6 = api.add(sample4);
    }
    */
}
