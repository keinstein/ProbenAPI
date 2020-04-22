package de.schlemmersoft.bewerbung.test1.Proben.Vector;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.Vector;
import java.time.ZonedDateTime;
import de.schlemmersoft.bewerbung.test1.Proben.Public.*;
import de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe;

import static de.schlemmersoft.bewerbung.test1.Proben.Public.ProbenAPI.Probe.Interpretation;


/**
 * Hello world!
 *
 */
public abstract class ProbenVector<T extends Comparable<T>,VectorProbe extends GenericProbe<T> >
	implements ProbenAPI<T>
{
	protected Vector<VectorProbe> data;


	interface Filter<P> {
		boolean accepts(P probe);
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


		class FilterIterator implements Iterator<Probe <T>> {
			int current = -1;
			int next = -1;

			int findNext(int i) {
				for (++i; i < data.size() ; ++i) {
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

			@Override
			public void remove() throws IllegalStateException {
				if (current < 0 || current >= data.size())
					throw new IllegalStateException();
				data.remove(current);
				current = next = data.size() + 1;
			}

			@Override
			public String toString() {
				StringBuffer buf = new StringBuffer();
				buf.append("FilterIterator ");
				buf.append(filter);
				buf.append("\n");
				buf.append("cur = ");
				buf.append(current);
				buf.append("; next = ");
				buf.append(next);
				return buf.toString();
			}
		}

		@Override
		public Iterator<Probe<T>> iterator() {
			return new FilterIterator();
		}

	}

	protected ProbenVector () {
		data = new Vector<>();
	}
	Iterator<Probe<T>> findId (String id) {
		FilteredProben search = new FilteredProben(new IdFilter<T,VectorProbe>(id));
		return search.iterator();
	}

	void checkId(String id) throws IllegalArgumentException {
		Iterator<Probe<T>> it;
		it = findId(id);
		if (it.hasNext()) {
			throw new IllegalArgumentException("Duplicate sample id.");
		}
	}


	/**
	 * Find the insertion point in the vector
	 * @param time Date, time and time zone when the sample is measured.
	 * @return Index to the element where that time should be included.
	 */
	int findInsertionPoint (ZonedDateTime time) {
		if (data.size() == 0) {
			return 0;
		}
		Comparator<Probe<T>> c = new Comparator<Probe<T>>() {
			public int compare(Probe<T> i1, Probe<T> i2) {
				int retval = i1.getTime().compareTo(i2.getTime());
				// force towards the lower end
				return (retval != 0?retval:-1);
			}
		};
		int index = Collections.binarySearch(data,
				createElement("",time,null), c);
		if (index < 0) index = -index -1;

		// this should do nothing, but it seems not to be
		// specified how the elements get compared above.
		// So we make sure it really gives us the right pointer.
		for (;index < data.size(); ++index)
			if (!data.elementAt(index).getTime().equals(time))
				break;

		return index;
	}

	abstract protected VectorProbe createElement(String id,
			ZonedDateTime time,
			T value);

	public int size() {
		return data.size();
	}

	public VectorProbe get(String id) {
		return (VectorProbe) findId(id).next();
	}

	/**
	 * @param sample
	 * @return
	 * @throws IllegalArgumentException
	 */
	public VectorProbe add (Probe<T>sample) throws IllegalArgumentException {
		checkId(sample.getID());
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
	public VectorProbe add (String id, ZonedDateTime time) throws IllegalArgumentException{
		checkId(id);
		int pos = findInsertionPoint (time);
		if (pos < data.size()) {
			data.add(pos,
					createElement(id, time, null));
			return data.elementAt(pos);
		} else {
			data.add(createElement(id, time, null));
			return data.lastElement();
		}
	}

	public VectorProbe add (String id, ZonedDateTime time, T value){
		VectorProbe sample = add(id,time);
		sample.setValue(value);
		return sample;
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

	public VectorProbe add(ZonedDateTime time, T value){
		VectorProbe sample = add(time);
		sample.setValue(value);
		return sample;
	}

	@Override
	public void remove (String id) throws IllegalArgumentException,
										  IllegalStateException {
		Iterator<Probe<T>> it = findId(id);
		it.next();
		it.remove();
	}

	@Override
	public void remove (Probe<T> sample) throws IllegalArgumentException,
	                                            IllegalStateException {
		Iterator<Probe<T>> it = findId(sample.getID());
		Probe<T> sample2 = it.next();
		if (!(sample2.getTime().equals(sample.getTime())))
			throw new NoSuchElementException();
		it.remove();
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
