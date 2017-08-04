package hu.gerviba.pseudocode.lang.primitive;

import hu.gerviba.pseudocode.exceptions.ExecutionException;
import hu.gerviba.pseudocode.lang.PField;

public class PPrimitiveField extends PField {

	private PPrimitiveValue primitiveValue;
	
	public PPrimitiveField(String name, PPrimitiveValue value, boolean constant) {
		super(name, constant);
		primitiveValue = value;
	}
	
	public PPrimitiveField(String name, PField value, boolean constant) {
		super(name, constant);
		primitiveValue = value.getPrimitiveValue();
	}
	
	@Override
	public PPrimitiveValue getPrimitiveValue() {
		return primitiveValue;
	}
	
	public void setPrimitiveValue(PPrimitiveValue value) {
		if (isConstant())
			throw new ExecutionException("Cannot set the value of a constant");
		this.primitiveValue = value;
	}
	
	@Override
	public Class<? extends PPrimitiveValue> getType() {
		return primitiveValue.getType();
	}

	@Override
	public boolean isComplex() {
		return false;
	}
	
	@Override
	public PField clone() {
		return new PPrimitiveField(name, primitiveValue.cloneValue(), isConstant());
	}

	@Override
	public String toString() {
		return "PPrimitiveField [primitiveValue=" + primitiveValue + ", name=" + name + "]";
	}
	
}
