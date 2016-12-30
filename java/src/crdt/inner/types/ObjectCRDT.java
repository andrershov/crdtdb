package crdt.inner.types;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import crdt.api.Crdt;
import crdt.inner.causal.CausalContext;

public abstract class ObjectCRDT<T extends ObjectCRDT<T>> implements Crdt {
	private Constructor<T> constructor;
	private List<Field> crdtFields = new ArrayList<>();

	@SuppressWarnings("unchecked")
	protected ObjectCRDT() {
		try {
			Class<T> clazz = (Class<T>) this.getClass();
			constructor = clazz.getDeclaredConstructor();
			for (Field field : clazz.getDeclaredFields()) {
				if (Crdt.class.isAssignableFrom(field.getType())) {
					crdtFields.add(field);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean join(Crdt that) {
		if (that == null)
			return false;
		if (this.getClass() != that.getClass())
			throw new RuntimeException("Illegal types");

		boolean changed = false;
		try {

			for (Field field : crdtFields) {
				Crdt thisCRDT;
				thisCRDT = (Crdt) field.get(this);

				Crdt thatCRDT = (Crdt) field.get(that);

				if (thatCRDT != null) {
					if (thisCRDT == null) {
						changed = true;
						field.set(this, thatCRDT);
					} else {
						changed |= thisCRDT.join(thatCRDT);
					}
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return changed;
	}

	@Override
	public Crdt clone(CausalContext cc) {
		try {
			T that = constructor.newInstance();
			for (Field field : crdtFields) {
				Crdt val = (Crdt) field.get(this);
				if (val != null) {
					field.set(that, val.clone(cc));
				}
			}
			return that;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	@JsonIgnore
	public Crdt getDelta() {
		try {
			T that = constructor.newInstance();
			boolean changed = false;
			for (Field field : crdtFields) {
				Crdt val = (Crdt) field.get(this);
				changed |= (val != null && val.getDelta() != null);
				if (val != null) {
					field.set(that, val.getDelta());
				}
			}
			if (changed) {
				return that;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return null;
	}
}
