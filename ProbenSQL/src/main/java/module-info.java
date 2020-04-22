/**
 * An implementation using JDBC and sqlite.
 * @author Tobias Schlemmer
 *
 */
module de.schlemmersoft.bewerbung.test1.ProbenSQL{
    requires transitive de.schlemmersoft.bewerbung.test1.ProbenAPI;
    requires java.sql;
    requires sqlite.jdbc;

    exports de.schlemmersoft.bewerbung.test1.Proben.SQL;
    opens de.schlemmersoft.bewerbung.test1.Proben.SQL;
}
