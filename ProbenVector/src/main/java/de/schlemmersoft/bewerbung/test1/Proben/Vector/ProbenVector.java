package de.schlemmersoft.bewerbung.test1.Proben.Vector;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.Vector;
import java.time.ZonedDateTime;
import de.schlemmersoft.bewerbung.test1.Proben.Public.*;
import static de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe.Interpretation;


/**
 * Hello world!
 *
 */
public abstract class ProbenVector<T extends Comparable<T>,VectorProbe extends GenericProbe<T> >
	implements ProbenAPI<T>
{
	protected Vector<VectorProbe> data;
	}
	

	class RangeFilter<T1 extends Comparable<T1>, P extends Probe<T1> > implements Filter<P> {
		T1 minimum;
		T1 maximum;
		RangeFilter (T1 min, T1 max) {
			minimum = min;
			maximum = max;
		}
		@Override
		public boolean accepts(P probe) {
			T1 value = probe.getValue();
			if (value == null) return false;
			return minimum.compareTo(value) <= 0 && maximum.compareTo(value) >= 0;
		}
		
	}
	

	class InterpretationFilter<T1 extends Comparable<T1>, P extends Probe<T1> > implements Filter<P> {
		Interpretation interpretation;
		InterpretationFilter (Interpretation key) {
			interpretation = key;
		}
		@Override
		public boolean accepts(P probe) {
			return interpretation == probe.getInterpretation();
		}
		
	}


	class IdFilter<T1, P extends Probe<T1> > implements Filter<P> {
		String identifier;
		IdFilter (String id) {
			identifier = id;
		}
		@Override
		public boolean accepts (P probe) {
			return identifier.equals(probe.getID());
		}
	}

	class AllFilter<T1, P extends Probe<T1> > implements Filter<P> {
		@Override
		public boolean accepts (P probe) {
			return true;
		}
	}

	class FilteredProben implements Iterable<Probe< T>> {
		Filter<VectorProbe> filter;
		FilteredProben(Filter<VectorProbe> probenFilter) {
			filter = probenFilter;
		}
		
		
			int current = 0;
			int next = 0;
			
		class FilterIterator implements Iterator<Probe <T>> {
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
			public VectorProbe next() {
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
		public Iterator<Probe<T>> iterator() {
			// TODO Auto-generated method stub
			return new FilterIterator();
		}
		
	}
	
	Iterator<Probe<T>> findId (String id) {
		FilteredProben search = new FilteredProben(new IdFilter<T,VectorProbe>(id));
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

	abstract protected VectorProbe createElement(String id,
			ZonedDateTime time,
			T value);

	@Override
	public VectorProbe add (Probe<T>sample) throws IllegalArgumentException {
		int pos = findInsertionPoint (sample.getTime());
		if (pos < data.size()) {
			data.add(pos,
					createElement(sample.getID(),
								  sample.getTime(),
								  sample.getValue()));
			return data.elementAt(pos);
		} else {
			data.add(createElement(sample.getID(),
					  sample.getTime(),
					  sample.getValue()));
			return data.lastElement();
		}
	}
	
	@Override
	public GenericProbe add (String id, ZonedDateTime time){
		int pos = findDate (time);
		checkId(id);
		if (pos < data.size()) {
			data.add(pos,
					createElement(id, time, null));
			return data.elementAt(pos);
		} else {
			data.add(createElement(id, time, null));
			return data.lastElement();
		}
	}
	
	@Override
	public VectorProbe add(ZonedDateTime time){
		Iterator<Probe<T>> it;
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
		Iterator<Probe<T>> it = findId(id);
		it.remove();
	}
	
	@Override
	public void remove (Probe<T> sample) throws IllegalArgumentException,
	                                            IllegalStateException {
	}
	
	@Override
	public Iterable<Probe <T>> range(T min, T max) {
		return new FilteredProben(new RangeFilter<T,VectorProbe> (min, max));
	}
	
	@Override
	public Iterable<Probe<T>> result(Interpretation key) {
		return new FilteredProben(new InterpretationFilter<T,VectorProbe>(key));
	}

	@Override
	public Iterator<Probe<T> > iterator() {
		FilteredProben tmp = new FilteredProben(new AllFilter<T,VectorProbe>());
		return tmp.iterator();
	}

}
