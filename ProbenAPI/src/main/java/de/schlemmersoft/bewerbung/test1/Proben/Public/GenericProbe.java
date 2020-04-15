package de.schlemmersoft.bewerbung.test1.Proben.Public;

import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe;
import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe.Messwert;

import java.time.ZonedDateTime;

/**
 * A simple in-memory storage sample class.
 *
 * @author Tobias Schlemmer
 */
public class GenericProbe implements Probe, Cloneable {
	/**
	 * Storage for the ID of the sample.
	 */
	protected String id;
	/**
	 * Storage for date, time and time zone of the measurement.
	 */
	protected ZonedDateTime time;
	/**
	 * Storage for the value of the measurement.
	 */
	protected Messwert value;

	/**
	 * Constructs a sample without measurement data.
	 *
	 * @param i Unique dentifier as provided by the global storage or other means.
	 * @param t Date/Time/Timezone of the measurement.
	 */
	public GenericProbe(String i, ZonedDateTime t) {
		id = i;
		time = t;
		value = null;
	}

	/**
	 * Constructs a sample with measurement data.
	 *
	 * @param i Unique dentifier as provided by the global storage or other means.
	 * @param t Date/Time/Timezone of the measurement.
	 * @param v Value object conaining the measurement value of the sample.
	 */
	public GenericProbe(String i, ZonedDateTime t, Probe.Messwert v) {
		id = i;
		time = t;
		value = v;

	/**
	 * Compare a sample to some JAVA object.
	 * @param other the object which shall be compared.
	 */
	@Override
	public boolean equals (Object other) {
		if (this == other) return true;
		if (other instanceof Probe) return equals((Probe)other);
		return false;
	}

	@Override
	public boolean equals(Probe other) {
		return id.equals(other.getID())
				&& time.equals(other.getTime())
				&& value.equals(other.getValue());
	}


	public GenericProbe clone() {
		return new GenericProbe(new String(id), time, value.clone());
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

	public void setValue(Probe.Messwert v) {
		value = v;
	}

	public Probe.Interpretation getInterpretation() {
		if (value != null)
			return value.getInterpretation();
		return Probe.Interpretation.FUZZY;
	}
}
