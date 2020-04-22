package de.schlemmersoft.bewerbung.test1.Proben.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

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
		String resourceName = "commands1.txt";

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(resourceName).getFile());
		String[] args = null;
	    final InputStream original = System.in;
	    final FileInputStream fips = new FileInputStream(file);
	    System.setIn(fips);
	    ConsoleApp.mainProgram(args);
	    System.setIn(original);
	}

	@Test
	void testMain2() throws FileNotFoundException, SQLException, SyntaxError {
		String resourceName = "commands1.txt";

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(resourceName).getFile());
	    String[] args = { ":memory:", "testTable" };
		final InputStream original = System.in;
		final FileInputStream fips = new FileInputStream(file);
		System.setIn(fips);
		ConsoleApp.mainProgram(args);
	    System.setIn(original);
	}
}
