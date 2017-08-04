package hu.gerviba.pseudocode.lang.primitive;

public interface PPrimitiveValue extends PPrimitiveOperatorTarget {
	
	public PText asText();
	public PText asTextClone();
	public PNumeric asNumeric();
	public PNumeric asNumericClone();
	public PLogical asLogical();
	public PLogical asLogicalClone();
	public String asStringValue();
	
	public void set(PPrimitiveValue value);
	public void set(String value);
	
	public PPrimitiveValue cloneValue();
	
	public default Class<? extends PPrimitiveValue> getType() {
		return getClass();
	}
}
