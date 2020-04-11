package de.schlemmersoft.bewerbung.test1.Proben.Public;
import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe;
import java.time.ZonedDateTime;

public class GenericProbe implements Probe {
	protected String id;
	protected ZonedDateTime time;
	protected Probe.Messwert value;
	protected boolean hasValue;

		
	public GenericProbe ( String i,
			      ZonedDateTime t) {
		id = i;
		time = t;
		value = null;
	}
	public GenericProbe ( String i,
			      ZonedDateTime t,
			      Probe.Messwert v) {
		id = i;
		time = t;
		value = v;
	}
	public String getID() {
		return id;
	}
	public ZonedDateTime getTime() {
		return time;
	}
	public Probe.Messwert getValue() {
		return value;
	}
	public void setValue( Probe.Messwert v ) {
		value = v;
	}
	public Probe.Interpretation getResult() {
		if (value != null)
			return value.getResult();
		return Probe.Interpretation.FUZZY;
	}
}

