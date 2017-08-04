package hu.gerviba.pseudocode.lang.primitive;

import hu.gerviba.pseudocode.exceptions.ExecutionException;
import hu.gerviba.pseudocode.interpreter.ApplicationProcessor;
import hu.gerviba.pseudocode.lang.PFieldType;
import hu.gerviba.pseudocode.utils.FormatUtil;

public final class PLogical implements PPrimitiveValue {

	private boolean value;

	public PLogical(PLogical value) {
		this.value = value.value;
	}
	
	public PLogical(boolean value) {
		this.value = value;
	}

	public PLogical(String value) {
		value = value.toLowerCase();
		if (value.equalsIgnoreCase("igaz") 
				|| value.equalsIgnoreCase("true") 
				|| value.equals("1") 
				|| value.equalsIgnoreCase("igen"))
			this.value = true;
		else if (value.equalsIgnoreCase("hamis") 
				|| value.equalsIgnoreCase("false") 
				|| value.equals("0") 
				|| value.equalsIgnoreCase("nem")) 
			this.value = false;
		else
			throw new ExecutionException("Cannot cast text '"+value+"' to any logical value");
	}
	
	@Override
	public void set(String value) {
		value = value.toLowerCase();
		if (value.equalsIgnoreCase("igaz") 
				|| value.equalsIgnoreCase("true") 
				|| value.equals("1") 
				|| value.equalsIgnoreCase("igen"))
			this.value = true;
		else if (value.equalsIgnoreCase("hamis") 
				|| value.equalsIgnoreCase("false") 
				|| value.equals("0") 
				|| value.equalsIgnoreCase("nem")) 
			this.value = false;
		else
			throw new ExecutionException("Cannot cast text '"+value+"' to any logical value");
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
	
	public void set(PText value) {
		throw new ExecutionException("Cannot cast text to any logical value");
	}

	public void set(PNumeric value) {
		this.value = value.asLogical().asBoolean();
	}

	public void set(PLogical value) {
		this.value = value.value;
	}

	@Override
	public PText asText() {
		return new PText(value ? "igaz" : "hamis");
	}

	@Override
	public PText asTextClone() {
		return asText();
	}
	
	@Override
	public PNumeric asNumeric() {
		return new PNumeric(value ? 1L : 0L);
	}
	
	@Override
	public PNumeric asNumericClone() {
		return asNumeric();
	}

	@Override
	public PLogical asLogical() {
		return this;
	}
	
	@Override
	public PLogical asLogicalClone() {
		return new PLogical(this);
	}

	public boolean asBoolean() {
		return value;
	}
	
	public long asLong() {
		return this.value ? 1L : 0L;
	}
	
	@Override
	public PPrimitiveValue cloneValue() {
		return new PLogical(this.value);
	}

	@Override
	public PPrimitiveValue add(PPrimitiveValue p) {
		if (p.getType() == PFieldType.TEXT.getPrimitiveType())
			return new PText(this.asText().asString() + p.asText().asString());
		if (p.getType() != PFieldType.LOGICAL.getPrimitiveType() && p.getType() != PFieldType.NUMERIC.getPrimitiveType())
			throw new ExecutionException("The operator + is undefined for the argument type(s) PLogical, "+p.getType().getSimpleName());
		
		if (p.asNumeric().isRealType())
			return new PNumeric(this.asNumeric().asLong() + p.asNumeric().asDouble());
		return new PNumeric(this.asNumeric().asLong() + p.asNumeric().asLong());
	}

	@Override
	public PNumeric sub(PPrimitiveValue p) {
		throw new ExecutionException("The operator - is undefined for the argument type(s) PLogical, "+p.getType().getSimpleName());
	}

	@Override
	public PNumeric mul(PPrimitiveValue p) {
		throw new ExecutionException("The operator * is undefined for the argument type(s) PLogical, "+p.getType().getSimpleName());
	}

	@Override
	public PNumeric div(PPrimitiveValue p) {
		throw new ExecutionException("The operator / is undefined for the argument type(s) PLogical, "+p.getType().getSimpleName());
	}

	@Override
	public PNumeric mod(PPrimitiveValue p) {
		throw new ExecutionException("The operator MOD is undefined for the argument type(s) PLogical, "+p.getType().getSimpleName());
	}

	@Override
	public PPrimitiveValue binaryNot() {
		return new PLogical(value);
	}

	@Override
	public PPrimitiveValue neg() {
		return new PNumeric(value ? -1 : 0);
	}
	
	@Override
	public PPrimitiveValue not() {
		return new PLogical(!value);
	}

	@Override
	public PNumeric bitwiseOr(PPrimitiveValue p) {
		if (p.getType() == PFieldType.LOGICAL.getPrimitiveType()) {
			return new PNumeric(this.value || p.asLogical().asBoolean() ? 1L : 0L);
		} else if (p.getType() == PFieldType.NUMERIC.getPrimitiveType()){
			if (p.asNumeric().isRealType() && ApplicationProcessor.shouldReportWarings())
				ApplicationProcessor.warn("Bitwise OR operator used in a real type PNumeric");
		
			return new PNumeric(this.asLong() | p.asNumeric().asLong());
		} else {
			throw new ExecutionException("The operator bitwise OR is undefined for the argument type(s) PLogical, "+p.getType().getSimpleName());
		}
	}

	@Override
	public PNumeric bitwiseAnd(PPrimitiveValue p) {
		if (p.getType() == PFieldType.LOGICAL.getPrimitiveType()) {
			return new PNumeric(this.value && p.asLogical().asBoolean() ? 1L : 0L);
		} else if (p.getType() == PFieldType.NUMERIC.getPrimitiveType()){
			if (p.asNumeric().isRealType() && ApplicationProcessor.shouldReportWarings())
				ApplicationProcessor.warn("Bitwise AND operator used in a real type PNumeric");
		
			return new PNumeric(this.asLong() & p.asNumeric().asLong());
		} else {
			throw new ExecutionException("The operator bitwise AND is undefined for the argument type(s) PLogical, "+p.getType().getSimpleName());
		}
	}

	@Override
	public PNumeric binaryShiftLeft(PPrimitiveValue p) {
		throw new ExecutionException("The operator << is undefined for the argument type(s) PLogical, "+p.getType().getSimpleName());
	}

	@Override
	public PNumeric binaryShiftRight(PPrimitiveValue p) {
		throw new ExecutionException("The operator >> is undefined for the argument type(s) PLogical, "+p.getType().getSimpleName());
	}

	@Override
	public PNumeric binaryClearLeft(PPrimitiveValue p) {
		throw new ExecutionException("The operator >>> cannot be interpreted because of the BigDecimal style value storage");
	}

	@Override
	public PLogical or(PPrimitiveValue p) {
		if (p.getType() == PFieldType.LOGICAL.getPrimitiveType() || (p.getType() == PFieldType.NUMERIC.getPrimitiveType() && FormatUtil.isLogical(p.asNumeric()))) {
			return new PLogical(this.value || p.asLogical().asBoolean());
		}
		throw new ExecutionException("The operator OR is undefined for the argument type(s) PLogical, "+p.getType().getSimpleName());
	}

	@Override
	public PLogical and(PPrimitiveValue p) {
		if (p.getType() == PFieldType.LOGICAL.getPrimitiveType() || (p.getType() == PFieldType.NUMERIC.getPrimitiveType() && FormatUtil.isLogical(p.asNumeric()))) {
			return new PLogical(this.value && p.asLogical().asBoolean());
		}
		throw new ExecutionException("The operator AND is undefined for the argument type(s) PLogical, "+p.getType().getSimpleName());
	}

	@Override
	public PPrimitiveValue xor(PPrimitiveValue p) {
		if (p.getType() == PFieldType.LOGICAL.getPrimitiveType()) {
			return new PLogical(this.value ^ p.asLogical().asBoolean());
		} else if (p.getType() == PFieldType.NUMERIC.getPrimitiveType()){
			if (p.asNumeric().isRealType() && ApplicationProcessor.shouldReportWarings())
				ApplicationProcessor.warn("Bitwise XOR operator used in a real type PNumeric");
		
			return new PNumeric(this.asLong() ^ p.asNumeric().asLong());
		} else {
			throw new ExecutionException("The operator XOR is undefined for the argument type(s) PLogical, "+p.getType().getSimpleName());
		}
	}

	@Override
	public PLogical ifEquals(PPrimitiveValue p) {
		if (p.getType() == PFieldType.LOGICAL.getPrimitiveType() || (p.getType() == PFieldType.NUMERIC.getPrimitiveType() && FormatUtil.isLogical(p.asNumeric()))) {
			return new PLogical(this.value == p.asLogical().asBoolean());
		}
		throw new ExecutionException("The operator == is undefined for the argument type(s) PLogical, "+p.getType().getSimpleName());
	}

	@Override
	public PLogical notEquals(PPrimitiveValue p) {
		if (p.getType() == PFieldType.LOGICAL.getPrimitiveType() || (p.getType() == PFieldType.NUMERIC.getPrimitiveType() && FormatUtil.isLogical(p.asNumeric()))) {
			return new PLogical(this.value != p.asLogical().asBoolean());
		}
		throw new ExecutionException("The operator != is undefined for the argument type(s) PLogical, "+p.getType().getSimpleName());
	}

	@Override
	public PLogical lessThan(PPrimitiveValue p) {
		throw new ExecutionException("The operator > is undefined for the argument type(s) PLogical, "+p.getType().getSimpleName());
	}

	@Override
	public PLogical greaterThan(PPrimitiveValue p) {
		throw new ExecutionException("The operator < is undefined for the argument type(s) PLogical, "+p.getType().getSimpleName());
	}

	@Override
	public PLogical lessThanEquals(PPrimitiveValue p) {
		throw new ExecutionException("The operator >= is undefined for the argument type(s) PLogical, "+p.getType().getSimpleName());
	}

	@Override
	public PLogical greaterThanEquals(PPrimitiveValue p) {
		throw new ExecutionException("The operator <= is undefined for the argument type(s) PLogical, "+p.getType().getSimpleName());
	}

	@Override
	public String toString() {
		return "PLogical [value=" + value + "]";
	}

	@Override
	public String asStringValue() {
		return value ? "igaz" : "hamis";
	}
	
}