/**
 * A vector based implementation of {@link ProbenAPI}
 *
 * @author Tobias Schlemmer
 *
 */
module de.schlemmersoft.bewerbung.test1.ProbenVector {
    requires transitive de.schlemmersoft.bewerbung.test1.ProbenAPI;

    exports de.schlemmersoft.bewerbung.test1.Proben.Vector;
    opens de.schlemmersoft.bewerbung.test1.Proben.Vector;
}
