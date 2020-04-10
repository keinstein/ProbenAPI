package de.schlemmersoft.bewerbung.test1.Proben.Public;
import java.time.ZonedDateTime;
import java.util.Vector;
import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe;

public interface ProbenAPI extends Iterable<Probe> {
	public interface Probe {
		enum Interpretation {
			BAD,
			GOOD,
			FUZZY
		}
		public interface Messwert {
			Interpretation getResult();
		}
		
		String getID() ;
		ZonedDateTime getTime() ;
		Messwert getValue() ;
		void setValue( Messwert v ) ;
		Interpretation getResult() ;
	}

	Iterable<Probe> range(int min, int max);
	Iterable<Probe> result( Probe.Interpretation key );

	Probe add (Probe sample);
	Probe add (String id,
		  ZonedDateTime time);
	Probe add(ZonedDateTime time);
	
	void del (String id);
	void del (Probe sample);
}
