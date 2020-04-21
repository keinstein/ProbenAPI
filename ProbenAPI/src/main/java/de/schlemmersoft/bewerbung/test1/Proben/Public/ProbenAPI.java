package de.schlemmersoft.bewerbung.test1.Proben.Public;
import java.time.ZonedDateTime;
import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe;


/**
 *
 * API for the storage of some samples.
 * This interface is dedicated to the storage
 *  of medical samples in some data base. It does not
 *  provide means for opening or closing the data base
 *  as they may be very different for different backends
 *  like SQL databases or storage in memory.
 *
 *  The structure of a single sample is given as a subinterface.
 *  The interface defines functions for adding,
 *  removing and iterating the samples.
 *
 *  Samples can be retrieved from the storage via iterators. Filtering
 *  by additional conditions is done by constructing an iterable object that
 *  can be processd by the calling function. This avoids unnecessary copies or
 *  retrieving unnecessary data from a larger database.
 *
 * 	@author  Tobias Schlemmer
 */
public interface ProbenAPI<T> extends Iterable<Probe<T> > {
	/**
	 *
	 * API for a single sample.
	 *
	 * This API provides prototypes for the necessary operations on a single sample.
	 * This includes retrieving any data field and interpretation of the value.
	 * It excludes any changes except to adding a missing measurement value.
	 *
	 * A sample is identified by unique identifier. Data fields contain the date
	 * and the time of the measurement and the measured value.
	 *
	 * In order to be independent from the backend the identifier is considered
	 * to be a string. It is recommendet to generate it as an UUID.
	 *
	 * @author Tobias Schlemmer
	 */
	public static interface Probe<T> {
		/**
		 *
		 * Return values for the interpretation of a sample measurement.
		 * @author Tobias Schlemmer
		 */
		public static enum Interpretation {
			/**
			 * The result was bad (whatever that means).
			 */
			BAD,
			/**
			 * The result was good (whatever that means).
			 */
			GOOD,
			/**
			 * We cannot really say whether the result was bad or good (whatever that means).
			 */
			FUZZY
		}

		/**
		 * Compare two samples.
		 *
		 * @param other the other sample to compare.
		 * @return #true if both samples describe the same measurement and outcome.
		 */
		boolean equals (Probe<T> other);

		/**
		 * Create a copy of this Probe
		 * @return a new object created as a copy of this Probe.
		 * @throws CloneNotSupportedException
		 */
		Probe<T> clone() throws CloneNotSupportedException;

		/**
		 * Return the identifier of the sample.
		 * @return String containing the identifier of the sample.
		 */
		String getID() ;

		/**
		 * Return the date, time and time zone when the measurement has been performed.
		 *
		 * @return Date, Time and time zone when the value has been measured
		 */
		ZonedDateTime getTime() ;

		/**
		 * Return the measurement value of the current sample.
		 *
		 * @return The measurement value of the current sample.
		 */
		T getValue();

		/**
		 * Set a missing measurement value.
		 *  If there exists already a valid measurement value, the behaviour is undefined.
		 * @param v Value to be set.
		 */
		void setValue( T v ) ;

		/**
		 * This method returns whether the measurement value is to be considered
		 * good, bad or whether we cannot decide, how to interpret the measurement result.
		 * @return The corresponding value for the interpretation.
		 */
		Interpretation getInterpretation() ;
	}

	/**
	 * Construct an iterable object containing all samples that lie within a
	 * given range of measurement results.
	 *
	 * This function can be used whenever all samples shall be retrieved, that lie between min and max.
	 * @param min Minimal measurement value of the samples that shall be retrieved.
	 * @param max Maximal measurement value of the samples that shall be retrieved.
	 * @return An iterable whose iterator traverses all samples that match the search criteria.
	 */
	Iterable<Probe<T> > range(T min, T max);

	/**
	 * Construct an iterable object containing all samples give rise to the
	 * same interpretation of the measurement result.
	 *
	 * This function can be used whenever all samples shall be retrieved,
	 * that match the search criteria.
	 * @param key Interpretation of all the samples that shall be retrieved.
	 * @return An iterable whose iterator traverses all samples can be interpreted as being as good as key.
	 */
	Iterable<Probe<T> > result( Probe.Interpretation key );

	/**
	 * Construct a sample without knowing its measurement result in the database.
	 *
	 * The database assigns a unique identifier to the sample.
	 *
	 * @param time Date and Time (including timezone) when the measurement has to be taken.
	 * @return The constructed sample in the database.
	 * @see #add(Probe), {@link #add(String, ZonedDateTime)}
	 */
	Probe<T> add (ZonedDateTime time);


	/**
	 * Construct a sample without knowing its measurement result in the database.
	 *
	 * The unique identifier of the sample must be provided. If it already exists this
	 * results in undefined behaviour.
	 *
	 * @param id Unique identifier.
	 * @param time Date and Time (including timezone) when the measurement has to be taken.
	 * @return The constructed sample in the database.
	 * @see #add(Probe)
	 * @see #add(ZonedDateTime)
	 */
	Probe<T> add (String id, ZonedDateTime time);



	/**
	 * Delete the sample that is referenced by the passed unique identifier.
	 *
	 * If it does not exist, undefined behavior may
	 * occur.
	 * @param id The unique identifier of the sample that shall be removed from the database.
	 */
	void remove (String id);

	/**
	 * Delete the sample from the database that has the same identifier and time information as the given sample.
	 *
	 * Even if the passed sample is not in the database its copy in the database is deleted.
	 * The copy is identified solely by the identifier. So if the values do
	 * not match the sample gets removed anyway. If it does not exist, undefined behavior may
	 * occur.
	 * @param sample Sample that shall be removed.
	 */
	void remove (Probe<T> sample);
}
