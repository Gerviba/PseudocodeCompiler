package hu.gerviba.pseudocode.lang;

import static hu.gerviba.pseudocode.utils.FormatUtil.isLogical;
import static hu.gerviba.pseudocode.utils.FormatUtil.isNumeric;
import static hu.gerviba.pseudocode.utils.FormatUtil.removeComplexArgCount;

import hu.gerviba.pseudocode.exceptions.ExecutionException;
import hu.gerviba.pseudocode.lang.primitive.PLogical;
import hu.gerviba.pseudocode.lang.primitive.PNumeric;
import hu.gerviba.pseudocode.lang.primitive.PPrimitiveField;
import hu.gerviba.pseudocode.lang.primitive.PPrimitiveValue;
import hu.gerviba.pseudocode.lang.primitive.PText;

public class PField implements Cloneable {

	protected final String name;
	protected PField value;
	private boolean constant;

	public PField(String name, PField value, boolean constant) {
		this.name = removeComplexArgCount(name.toLowerCase());
		this.value = value;
		this.constant = constant;
	}
	
	protected PField(String name, boolean constant) {
		this.name = name.toLowerCase();
		this.constant = constant;
	}

	public String getName() {
		return name;
	}
	
	public Class<? extends PPrimitiveValue> getType() {
		return value.getType();
	}
	
	public PField getValue() {
		return value;
	}
	
	public PField getValue(String[] args) {
		return value;
	}

	public void setValue(PField value) {
		if (isConstant())
			throw new ExecutionException("Cannot set the value of a constant");
		this.value = value;
	}
	
	public PPrimitiveValue getPrimitiveValue() {
		return ((PPrimitiveField) value).getPrimitiveValue();
	}
	
	public final boolean isConstant() {
		return constant;
	}
	
	public boolean isComplex() {
		return false;
	}
	
	public PField setConstant() {
		this.constant = true;
		return this;
	}
	
	@Override
	public PField clone() {
		PField f = new PField(name, constant);
		f.setValue(value.clone());
		return f;
	}
	
	@Override
	public String toString() {
		return "PField [name=" + name + ", value=" + value + ", constant=" + constant + "]";
	}

	public static PPrimitiveValue newValue(String value) {
		if (isNumeric(value))
			return new PNumeric(value);
		if (isLogical(value))
			return new PLogical(value);
		if (value.startsWith("\"") && value.endsWith("\"") && value.length() >= 2) 
			return new PText(value);
		throw new ExecutionException("Variable '" + value + "' is not declared in this scope");
	}
	
	public static PPrimitiveValue newValueDefaultString(String value) {
		if (isNumeric(value))
			return new PNumeric(value);
		if (isLogical(value))
			return new PLogical(value);
		return new PText(value, false);
	}
}
