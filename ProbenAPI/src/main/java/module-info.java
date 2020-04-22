/**
 * Public API for storing sample data.
 *
 * This Module provides the interface and some generic classes
 * for storing sample data of simple measurements. Every sample is
 * considered to be measured with one value at a given date and time
 * somewhere in the world. As the samples may come from different time
 * zones, the date and time is stored together with the time zone data.
 *
 * @author Tobias Schlemmer
 * @since 1.0
 */
module de.schlemmersoft.bewerbung.test1.ProbenAPI {
	exports de.schlemmersoft.bewerbung.test1.Proben.Public;
	requires java.base;
	requires java.rmi;
	requires jdk.compiler;
	opens de.schlemmersoft.bewerbung.test1.Proben.Public;
}
