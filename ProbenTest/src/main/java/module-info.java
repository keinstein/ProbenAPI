module de.schlemmersoft.bewerbung.test1.ProbenTest {
    requires de.schlemmersoft.bewerbung.test1.ProbenAPI;
    requires de.schlemmersoft.bewerbung.test1.ProbenVector;
    requires de.schlemmersoft.bewerbung.test1.ProbenSQL;
    requires java.sql;


    exports de.schlemmersoft.bewerbung.test1.Proben.Test;
    opens  de.schlemmersoft.bewerbung.test1.Proben.Test;
}
