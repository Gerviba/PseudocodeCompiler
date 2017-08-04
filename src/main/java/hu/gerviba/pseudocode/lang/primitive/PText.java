package hu.gerviba.pseudocode.lang.primitive;

import hu.gerviba.pseudocode.exceptions.ExecutionException;
import hu.gerviba.pseudocode.utils.FormatUtil;

public final class PText implements PPrimitiveValue {

	private String value;

	public PText(PText value) {
		this.value = value.value; //NOTE: " jelek eltávolítása
	}
	
	public PText(String value) {
		this.value = value.substring(1, value.length() - 1);
	}
	
	public PText(String value, boolean quoted) {
		this.value = quoted ? value.substring(1, value.length() - 1) : value;
	}

	@Override
	public void set(String value) {
		this.value = value.substring(1, value.length() - 1); //NOTE: " jelek eltávolítása
		
		/*
		if(value.charAt(0) == '"' && value.charAt(value.length()-1) == '"') {
			this.value = value.substring(1, value.length() - 1); //NOTE: " jelek eltávolítása
		} else if(FormatUtil.isNumeric(value)) {
			this.value = FormatUtil.numericToText(value);
		} else if (FormatUtil.isLogical(value)) {
			this.value = FormatUtil.isTrue(value) ? "1" : "0";
		}*/
	}
	
	@Override
	public void set(PPrimitiveValue value) {
		if (value.getType() == PText.class)
			set((PText) value);
		else if (value.getType() == PNumeric.class)
			set((PNumeric) value);
		else if (value.getType() == PLogical.class)
			set((PLogical) value);
		else
			throw new ExecutionException("Unknown PPrimitive type");
	}
	
	public void set(PNumeric value) {
		this.value = ""+value;
	}

	public void set(PText value) {
		this.value = value.value;
	}

	public void set(PLogical value) {
		this.value = value.asBoolean() ? "1" : "0";
	}

	public String asString() {
		return "\"" + value + "\"";
	}

	public String asRawString() {
		return value;
	}

	@Override
	public PText asText() {
		return this;
	}
	
	@Override
	public PText asTextClone() {
		return new PText(this);
	}

	@Override
	public PNumeric asNumeric() {
		if (FormatUtil.isNumeric(value)) {
			return new PNumeric(value);
		}
		throw new ExecutionException("Cannot cast Text to Numeric value (PText.asLogical())");
	}
	
	@Override
	public PNumeric asNumericClone() {
		return asNumeric();
	}

	@Override
	public PLogical asLogical() {
		if (FormatUtil.isLogical(value))
			return new PLogical(value);
		throw new ExecutionException("Cannot cast Text to Logical value (PText.asLogical())"); 
	
		/*String value = this.value.toLowerCase();
		if (value.equalsIgnoreCase("igaz") || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("i") || value.equalsIgnoreCase("1"))
			return new PLogical(true);
		else if (value.equalsIgnoreCase("hamis") || value.equalsIgnoreCase("false") || value.equalsIgnoreCase("h") || value.equalsIgnoreCase("0")) 
			return new PLogical(false);
		else*/
	}

	@Override
	public PLogical asLogicalClone() {
		return asLogical();
	}
	
	@Override
	public PPrimitiveValue cloneValue() {
		return new PText(this.value, false);
	}


	@Override
	public PPrimitiveValue add(PPrimitiveValue p) {
		return new PText(this.value + p.asText().asRawString(), false);
	}
	
	@Override
	public PNumeric sub(PPrimitiveValue p) {
		throw new ExecutionException("Subtraction is not supported in type PText");
	}

	@Override
	public PNumeric mul(PPrimitiveValue p) {
		throw new ExecutionException("Multiplication is not supported in type PText");
	}

	@Override
	public PNumeric div(PPrimitiveValue p) {
		throw new ExecutionException("Division is not supported in type PText");
	}

	@Override
	public PNumeric mod(PPrimitiveValue p) {
		throw new ExecutionException("Mod is not supported in type PText");
	}

	@Override
	public PPrimitiveValue binaryNot() {
		throw new ExecutionException("Binary negation is not supported in type PText");
	}

	@Override
	public PPrimitiveValue neg() {
		throw new ExecutionException("NEG is not supported in type PText");
	}
	
	@Override
	public PPrimitiveValue not() {
		throw new ExecutionException("NOT gate is not supported in type PText");
	}

	@Override
	public PNumeric bitwiseOr(PPrimitiveValue p) {
		throw new ExecutionException("Bitwise OR gate is not supported in type PText");
	}

	@Override
	public PNumeric bitwiseAnd(PPrimitiveValue p) {
		throw new ExecutionException("Bitwise AND gate is not supported in type PText");
	}

	@Override
	public PNumeric binaryShiftLeft(PPrimitiveValue p) {
		throw new ExecutionException("Move left is not supported in type PText");
	}

	@Override
	public PNumeric binaryShiftRight(PPrimitiveValue p) {
		throw new ExecutionException("Move right is not supported in type PText");
	}

	@Override
	public PNumeric binaryClearLeft(PPrimitiveValue p) {
		throw new ExecutionException("The operator >>> cannot be interpreted because of the BigDecimal style value storage");
	}

	@Override
	public PLogical or(PPrimitiveValue p) {
		throw new ExecutionException("OR gate is not supported in type PText");
	}

	@Override
	public PLogical and(PPrimitiveValue p) {
		throw new ExecutionException("AND gate is not supported in type PText");
	}

	@Override
	public PPrimitiveValue xor(PPrimitiveValue p) {
		throw new ExecutionException("XOR gate is not supported in type PText");
	}

	@Override
	public PLogical ifEquals(PPrimitiveValue p) {
		return new PLogical(this.value.equalsIgnoreCase(p.asText().value));
	}

	@Override
	public PLogical notEquals(PPrimitiveValue p) {
		return new PLogical(!this.value.equalsIgnoreCase(p.asText().value));
	}

	@Override
	public PLogical lessThan(PPrimitiveValue p) {
		throw new ExecutionException("Less than operator is not supported in type PText");
	}

	@Override
	public PLogical greaterThan(PPrimitiveValue p) {
		throw new ExecutionException("Greater than operator is not supported in type PText");
	}

	@Override
	public PLogical lessThanEquals(PPrimitiveValue p) {
		throw new ExecutionException("Less than or equals operator is not supported in type PText");
	}

	@Override
	public PLogical greaterThanEquals(PPrimitiveValue p) {
		throw new ExecutionException("Greater than or equals operator is not supported in type PText");
	}

	@Override
	public String toString() {
		return "PText [value=\"" + value + "\"]";
	}

	@Override
	public String asStringValue() {
		return asString();
	}
	
}
