package de.schlemmersoft.bewerbung.test1.Proben.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI;
import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe.Interpretation;
import de.schlemmersoft.bewerbung.test1.Proben.Vector.ProbenVector;


/**
 * Unit test for simple App.
 */
public class ProbenAPITest 
{
	static Stream<Arguments> testFullApi_Parameters() throws Throwable {
        return Stream.of(
			 Arguments.of(new ProbenVector())/*,
			 Arguments.of("1", ZonedDateTime.now(),  0, Interpretation.FUZZY),
			 Arguments.of("2", ZonedDateTime.now(), -1, Interpretation.BAD)*/
			 );
	}

    /**
     * Rigorous Test :-)
     */
	@ParameterizedTest(name="Run {index}: testId={0}")
	@MethodSource("testFullApi_Parameters")
    public void testFullApi(ProbenAPI api)
    {
        assertTrue( true );
    }
}
