package de.schlemmersoft.bewerbung.test1.Proben.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import de.schlemmersoft.bewerbung.test1.Proben.SQL.ProbenSQL;
import de.schlemmersoft.bewerbung.test1.Proben.Test.ConsoleApp.SyntaxError;


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

	void clearTable(String uri, String table) throws java.sql.SQLException {
		ProbenSQL sqlapi = new ProbenSQL("jdbc:sqlite:" + uri, table);
		sqlapi.clearTable();
	}
	
	@Test
	void testMain2() throws FileNotFoundException, SQLException, SyntaxError {
		String resourceName = "commands1.txt";

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(resourceName).getFile());
		
		String[] args = { ":memory:", "testTable" };
		clearTable(args[0],args[1]); // remove artifacts from other tests
		final InputStream original = System.in;
		final FileInputStream fips = new FileInputStream(file);
		System.setIn(fips);
		ConsoleApp.mainProgram(args);
	    System.setIn(original);
	}
}
