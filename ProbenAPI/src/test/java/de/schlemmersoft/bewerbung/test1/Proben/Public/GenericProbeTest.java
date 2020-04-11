package de.schlemmersoft.bewerbung.test1;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.time.ZonedDateTime;

import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe;
import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe.Interpretation;
import de.schlemmersoft.bewerbung.test1.Proben.Public.GenericProbe;



public class GenericProbeTest {

	class intvalue implements Probe.Messwert {
		public int value;
		public intvalue(int v) {
			value = v;
		}
		public Interpretation getResult() {
			if (value > 0) return Interpretation.GOOD;
			if (value < 0) return Interpretation.BAD;
			return Interpretation.FUZZY;
		}
	}
	
	@Test
	public void testFullConstructor() {
		ZonedDateTime time = ZonedDateTime.now();

		GenericProbe probe = new GenericProbe("This is an Id",
						      time,
						      new intvalue(5));
		assertEquals("This is an Id",probe.getID());
		intvalue value  = (intvalue)probe.getValue();
		assertEquals(5,value.value);
		assertEquals(Interpretation.GOOD,probe.getResult());
		assertEquals(time,probe.getTime());
	}

	@Test
	public void testReducedConstructor() {
		ZonedDateTime time = ZonedDateTime.now();

		GenericProbe probe = new GenericProbe("This is another Id",
						      time);
		assertEquals("This is another Id",probe.getID());
		intvalue value  = (intvalue)probe.getValue();
		assertEquals(null,value);
		assertEquals(Interpretation.FUZZY,probe.getResult());
		assertEquals(time,probe.getTime());
	}
}
