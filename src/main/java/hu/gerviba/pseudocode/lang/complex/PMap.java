package hu.gerviba.pseudocode.lang.complex;

import java.util.HashMap;

import hu.gerviba.pseudocode.lang.PField;
import hu.gerviba.pseudocode.lang.primitive.PPrimitiveValue;

public class PMap extends PField implements PComplexField {

	public PMap(String name, boolean constant) {
		super(name, constant);
	}

	private HashMap<String, PPrimitiveValue> values = new HashMap<>();
	
	@Override
	public String getName() {
		return null;
	}

	@Override
	public Class<? extends PPrimitiveValue> getType() {
		return null;
	}

	@Override
	public PField getValue() {
		return null;
	}

	@Override
	public PField getValue(String[] args) {
		return null;
	}

	@Override
	public boolean isComplex() {
		return true;
	}

}
