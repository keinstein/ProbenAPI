package de.schlemmersoft.bewerbung.test1.Proben.Public;

import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe;

import java.time.ZonedDateTime;

/**
 * A simple in-memory storage sample class.
 *
 * @author Tobias Schlemmer
 */
public abstract class GenericProbe<T> implements Probe<T>, Cloneable {
	/**
	 * Storage for the ID of the sample.
	 */
	protected String id;
	/**
	 * Storage for date, time and time zone of the measurement.
	 */
	final protected ZonedDateTime time;
	/**
	 * Storage for the value of the measurement.
	 */
	protected T value;

	/**
	 * Constructs a sample without measurement data.
	 *
	 * @param i Unique dentifier as provided by the global storage or other means.
	 * @param t Date/Time/Timezone of the measurement.
	 */
	public GenericProbe(String i, ZonedDateTime t) {
		id = new String(i);
		time = t;
		value = null;
	}

	/**
	 * Constructs a sample with measurement data. The data is cloned.
	 *
	 * @param i Unique dentifier as provided by the global storage or other means.
	 * @param t Date/Time/Timezone of the measurement.
	 * @param v Value object conaining the measurement value of the sample.
	 */
	public GenericProbe(String i, ZonedDateTime t, T v) {
		id = new String(i);
		time = t;
		setValue(v);
	}

	/**
	 * Copy constructor for a compatible sample. The data is cloned.
	 *
	 * @param sample The sample that will be copied.
	 */
	public GenericProbe(Probe<T> sample) {
		id = new String(sample.getID());
		time = sample.getTime();
		setValue (sample.getValue());
	}

	/**
	 * Compare a sample to some JAVA object.
	 * @param other the object which shall be compared.
	 */
	@Override
	public boolean equals (Object other) {
		if (this == other) return true;
		if (other instanceof Probe<?>) return equals((Probe<T>)other);
		return false;
	}

	@Override
	public boolean equals(Probe<T> other) {
		if (value == null && other.getValue() != null)
			return false;
		if (value != null && other.getValue() == null)
			return false;
		if (value != null) {
			T o = other.getValue();
				if (!value.equals(o))
				return false;
		}
		return id.equals(other.getID())
				&& time.equals(other.getTime());
	}


	public GenericProbe<T> clone() throws CloneNotSupportedException {
		GenericProbe<T> c = (GenericProbe<T>)super.clone();
		if (c == null) throw new CloneNotSupportedException();
		c.id = new String(c.id);
		if (c.value != null)
			c.value = cloneValue(c.value);
		return c;
	}

	public String getID() {
		return new String(id);
	}

	public ZonedDateTime getTime() {
		return time;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T v) {
		value = cloneValue(v);
	}

	protected abstract T cloneValue(T v);

	/*
	public Probe.Interpretation getInterpretation() {
		if (value != null)
			return value.getInterpretation();
		return Probe.Interpretation.FUZZY;
	}
	*/
}
