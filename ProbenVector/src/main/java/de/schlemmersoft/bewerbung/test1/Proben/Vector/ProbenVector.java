package de.schlemmersoft.bewerbung.test1.Proben.Vector;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.Vector;
import java.time.ZonedDateTime;
import de.schlemmersoft.bewerbung.test1.Proben.Public.*;
import static de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe.Interpretation;
import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe.Messwert;

/**
 * Hello world!
 *
 */
public class ProbenVector
	implements ProbenAPI
{
	protected Vector<Probe> data;
	
	class Mwt implements Messwert, Cloneable {
		int value;
			
		Mwt(int v) {
			value = v;
		}
		
		@Override
		public boolean equals (Object other) {
			if (this == other) return true;
			if (other instanceof Mwt) return equals((Mwt)other);
			return false;
		}
		
		@Override
		public boolean equals(Messwert other) {
			if (other instanceof Mwt) return equals((Mwt)other);
			return false;			
		}
		
		public boolean equals(Mwt other) {
			return value == other.value;
		}
		
		
		@Override
		public Mwt clone() {
			return new Mwt(value);
		}
		
		@Override
		public Interpretation getInterpretation() {
			// TODO Auto-generated method stub
			if (value < 0) return Interpretation.BAD;
			if (value > 0) return Interpretation.GOOD;
			return Interpretation.FUZZY;
		}
	}
	
	interface Filter {
		boolean accepts(Probe probe);
	}
	
	class RangeFilter implements Filter {
		int minimum;
		int maximum;
		RangeFilter (int min, int max) {
			minimum = min;
			maximum = max;
		}
		@Override
		public boolean accepts(Probe probe) {
			Mwt measurement = (Mwt)probe.getValue();
			return minimum <= measurement.value && measurement.value <= maximum;
		}
		
	}
	
	class InterpretationFilter implements Filter {
		Interpretation interpretation;
		InterpretationFilter (Interpretation key) {
			interpretation = key;
		}
		@Override
		public boolean accepts(Probe probe) {
			return interpretation == probe.getInterpretation();
		}
		
	}


	class IdFilter implements Filter {
		String identifier;
		IdFilter (String id) {
			identifier = id;
		}
		@Override
		public boolean accepts (Probe probe) {
			return identifier.equals(probe.getID());
		}
	}

	class FilteredProben implements Iterable<Probe> {		
		Filter filter;
		FilteredProben(Filter probenFilter) {
			filter = probenFilter;
		}
		
		
		class FilterIterator implements Iterator<Probe> {
			int current = 0;
			int next = 0;
			
			int findNext(int i) {
				for (; i < data.size() ; ++i) {
					if (filter.accepts(data.elementAt(i)))
						return i;
				}
				return i;
			}
			
			@Override
			public boolean hasNext() {
				if (next > current && next < data.size() )
					return true;
				next = findNext( current );
				
				return next < data.size();
			}
			
			@Override
			public Probe next() {
				if (next > current) {
					current = next;
				} else {
					current = findNext(current);
				}
				if (current < data.size())
					return data.elementAt(current);
				else
					throw new NoSuchElementException();
			}		
		}

		@Override
		public Iterator<Probe> iterator() {
			// TODO Auto-generated method stub
			return new FilterIterator();
		}
		
	}
	
	Iterator<Probe> findId (String id) {
		FilteredProben search = new FilteredProben(new IdFilter(id)); 
		return search.iterator();
	}
	
	int findDate (ZonedDateTime time) {
		if (data.size() == 0) return 0;
		int lower = 0;
		if (data.elementAt(lower).getTime() == time)
			return lower;
		int upper = data.size()-1;
		if (data.elementAt(upper).getTime() == time)
			return upper;
		int middle = lower+upper/2 ;
		for(middle = lower+upper/2;
				middle != lower && middle != upper;
				middle = lower+upper/2) {
			if (data.elementAt(middle).getTime().isBefore(time)) {
				lower = middle;
			} else if (data.elementAt(middle).getTime().equals(time)) {
				for (;middle <= upper; ++middle)
					if (!data.elementAt(middle).getTime().equals(time))
						break;
				return middle;
			} else {
				upper = middle;
			}
		}
		for (;middle <= upper; ++middle)
			if (!data.elementAt(middle).getTime().equals(time))
				break;
		return middle;
	}
	
	@Override
	public GenericProbe add (Probe sample){
		int pos = findDate (sample.getTime());
		if (pos < data.size()) {
			data.add(pos,sample.clone());
			return (GenericProbe)data.elementAt(pos);
		} else {
			data.add(sample.clone());
			return (GenericProbe)data.lastElement();
		}
	}
	
	@Override
	public GenericProbe add (String id, ZonedDateTime time){
		int pos = findDate (time);
		if (pos < data.size()) {
			data.add(pos,new GenericProbe(id, time));
			return (GenericProbe)data.elementAt(pos);
		} else {
			data.add(new GenericProbe(id, time));
			return (GenericProbe)data.lastElement();
		}
	}
	
	@Override
	public GenericProbe add(ZonedDateTime time){
		Iterator<Probe> it;
		String id;
		do {
			UUID uuid = UUID.randomUUID();
			id = uuid.toString();
			it = findId(id);
		} while (it.hasNext());
		return add (id,time);
	}
	
	@Override
	public void del (String id){
		Iterator it = findId(id);
		it.remove();
	}
	
	@Override
	public void del (Probe sample){
		del(sample.getID());
	}
	
	@Override
	public Iterable<Probe> range(int min, int max) {
		return new FilteredProben(new RangeFilter (min, max));
	}
	
	@Override
	public Iterable<Probe> result(Interpretation key) {
		return new FilteredProben(new InterpretationFilter(key));
	}

	@Override
	public Iterator<Probe> iterator() {
		return data.iterator();
	}

}
