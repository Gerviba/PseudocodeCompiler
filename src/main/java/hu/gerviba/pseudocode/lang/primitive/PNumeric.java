package hu.gerviba.pseudocode.lang.primitive;

import hu.gerviba.pseudocode.exceptions.ExecutionException;
import hu.gerviba.pseudocode.interpreter.ApplicationProcessor;
import hu.gerviba.pseudocode.lang.PFieldType;
import hu.gerviba.pseudocode.utils.FormatUtil;

/**
 * PNumeric
 * 
 * NOTES:
 * Minden esetben frissíteni kell az egész és a valós típusú értéket is. 
 * Az isRealType azt mutatja meg, hogy melyiket preferálja. 
 * Ha olyan műveletet végez, amit valós értékkel nem lehet, akkor az egész értéket fogja preferálni.
 */
public final class PNumeric implements PPrimitiveValue {

	private long valueInteger;
	private double valueReal;
	private boolean isRealType = false;
	
	public PNumeric(PNumeric value) {
		this.valueInteger = value.valueInteger;
		this.valueReal = value.valueReal;
		this.isRealType = value.isRealType;
	}
	
	public PNumeric(String value) {
		set(value);
	}

	public PNumeric(long value) {
		this.valueInteger = value;
		this.valueReal = value;
		this.isRealType = false;
	}

	public PNumeric(double value) {
		this.valueInteger = (long) value;
		this.valueReal = value;
		this.isRealType = !FormatUtil.isInteger(""+value);
	}
	
	@Override
	public void set(String value) {
		if (FormatUtil.isInteger(value)) {
			setIntegerValue(value);
		} else if (FormatUtil.isReal(value)) {
			setRealValue(value);
		} else {
			throw new ExecutionException(value);
		}
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
		set(value.asString());
	}

	public void set(PNumeric value) {
		if (value.isRealType) {
			this.valueInteger = (long) value.valueReal;
			this.valueReal = value.valueReal;
		} else {
			this.valueInteger = value.valueInteger;
			this.valueReal = value.valueInteger;
		}
		this.isRealType = value.isRealType;
	}

	public void set(PLogical value) {
		if (value.asBoolean()) {
			this.valueInteger = 1L;
			this.valueReal = 1D;
		} else {
			this.valueInteger = 0L;
			this.valueReal = 0D;
		}
		this.isRealType = false;
	}

	@Override
	public PText asText() {
		return new PText(isRealType ? "" + this.valueReal : "" + this.valueInteger, false);
	}
	
	@Override
	public PText asTextClone() {
		return asText();
	}

	@Override
	public PNumeric asNumeric() {
		return this;
	}
	
	@Override
	public PNumeric asNumericClone() {
		return new PNumeric(this);
	}

	@Override
	public PLogical asLogical() {
		if (valueInteger == 1L)
			return new PLogical(true);
		else if (valueInteger == 0L)
			return new PLogical(false);
		else
			throw new ExecutionException("Failed to cast Numeric value to Logical value");
	}
	
	@Override
	public PLogical asLogicalClone() {
		return asLogical();
	}
	
	public double asDouble() {
		return this.valueReal;
	}
	
	public long asLong() {
		return this.valueInteger;
	}
	
	public String asString() {
		return isRealType ? ""+this.valueReal : ""+this.valueInteger;
	}
	
	@Override
	public PPrimitiveValue cloneValue() {
		return new PNumeric(this);
	}
	
	public boolean isRealType() {
		return isRealType;
	}

	@Override
	public PPrimitiveValue add(PPrimitiveValue p) {
		if (p.getType() == PFieldType.TEXT.getPrimitiveType()) {
			return new PText(this.asString() + p.asText().asRawString(), false);
		} else if (p.getType() == PFieldType.NUMERIC.getPrimitiveType()) {
			if (this.isRealType || p.asNumeric().isRealType) {
				return new PNumeric(this.asDouble() + p.asNumeric().asDouble());
			}
			return new PNumeric(this.asLong() + p.asNumeric().asLong());
		} else {
			throw new ExecutionException("The operator + is undefined for the argument type(s) PNumeric, PLogical");
		}
	}

	@Override
	public PNumeric sub(PPrimitiveValue p) {
		if (p.getType() == PFieldType.NUMERIC.getPrimitiveType()) {
			if (this.isRealType || p.asNumeric().isRealType) {
				return new PNumeric(this.asDouble() - p.asNumeric().asDouble());
			}
			return new PNumeric(this.asLong() - p.asNumeric().asLong());
		}
		throw new ExecutionException("The operator - is undefined for the argument type(s) PNumeric, "+p.getType().getSimpleName());
	}

	@Override
	public PNumeric mul(PPrimitiveValue p) {
		if (p.getType() == PFieldType.NUMERIC.getPrimitiveType()) {
			if (this.isRealType || p.asNumeric().isRealType) {
				return new PNumeric(this.asDouble() * p.asNumeric().asDouble());
			}
			return new PNumeric(this.asLong() * p.asNumeric().asLong());
		}
		throw new ExecutionException("The operator * is undefined for the argument type(s) PNumeric, "+p.getType().getSimpleName());
	}

	@Override
	public PNumeric div(PPrimitiveValue p) {
		if (p.getType() == PFieldType.NUMERIC.getPrimitiveType()) {
			if (this.isRealType || p.asNumeric().isRealType) {
				return new PNumeric(this.asDouble() / p.asNumeric().asDouble());
			}
			return new PNumeric(this.asLong() / p.asNumeric().asLong());
		}
		throw new ExecutionException("The operator / is undefined for the argument type(s) PNumeric, "+p.getType().getSimpleName());
	}

	@Override
	public PNumeric mod(PPrimitiveValue p) {
		if (p.getType() == PFieldType.NUMERIC.getPrimitiveType()) {
			if (this.isRealType || p.asNumeric().isRealType) {
				return new PNumeric(this.asDouble() % p.asNumeric().asDouble());
			}
			return new PNumeric(this.asLong() % p.asNumeric().asLong());
		}
		throw new ExecutionException("The operator MOD is undefined for the argument type(s) PNumeric, "+p.getType().getSimpleName());
	}

	@Override
	public PPrimitiveValue binaryNot() {
		if (this.isRealType && ApplicationProcessor.shouldReportWarings())
			ApplicationProcessor.warn("binaryNeg operator used in a real type PNumeric");
		return new PNumeric(~this.valueInteger);
	}

	@Override
	public PPrimitiveValue neg() {
		if (isRealType)
			return new PNumeric(-valueReal);
		return new PNumeric(valueInteger);
	}
	
	@Override
	public PPrimitiveValue not() {
		return new PLogical(!asLogical().asBoolean());
	}

	@Override
	public PNumeric bitwiseOr(PPrimitiveValue p) {
		if (p.getType() == PFieldType.NUMERIC.getPrimitiveType() || p.getType() == PFieldType.LOGICAL.getPrimitiveType()) {
			if ((this.isRealType || p.asNumeric().isRealType) && ApplicationProcessor.shouldReportWarings())
				ApplicationProcessor.warn("Bitwise OR operator used in a real type PNumeric");
		
			return new PNumeric(this.valueInteger | p.asNumeric().asLong());
		}
		throw new ExecutionException("The operator bitwise OR is undefined for the argument type(s) PNumeric, "+p.getType().getSimpleName());
	}

	@Override
	public PNumeric bitwiseAnd(PPrimitiveValue p) {
		if (p.getType() == PFieldType.NUMERIC.getPrimitiveType() || p.getType() == PFieldType.LOGICAL.getPrimitiveType()) {
			if ((this.isRealType || p.asNumeric().isRealType) && ApplicationProcessor.shouldReportWarings())
				ApplicationProcessor.warn("Bitwise AND operator used in a real type PNumeric");
		
			return new PNumeric(this.valueInteger & p.asNumeric().asLong());
		}
		throw new ExecutionException("The operator bitwise AND is undefined for the argument type(s) PNumeric, "+p.getType().getSimpleName());
	}

	@Override
	public PNumeric binaryShiftLeft(PPrimitiveValue p) {
		if (p.getType() == PFieldType.NUMERIC.getPrimitiveType() || p.getType() == PFieldType.LOGICAL.getPrimitiveType()) {
			if ((this.isRealType || p.asNumeric().isRealType) && ApplicationProcessor.shouldReportWarings())
				ApplicationProcessor.warn("Shift left operator used in a real type PNumeric");
		
			return new PNumeric(this.valueInteger << p.asNumeric().asLong());
		}
		throw new ExecutionException("The operator << is undefined for the argument type(s) PNumeric, "+p.getType().getSimpleName());
	}

	@Override
	public PNumeric binaryShiftRight(PPrimitiveValue p) {
		if (p.getType() == PFieldType.NUMERIC.getPrimitiveType() || p.getType() == PFieldType.LOGICAL.getPrimitiveType()) {
			if ((this.isRealType || p.asNumeric().isRealType) && ApplicationProcessor.shouldReportWarings())
				ApplicationProcessor.warn("Shift right operator used in a real type PNumeric");
		
			return new PNumeric(this.valueInteger >> p.asNumeric().asLong());
		}
		throw new ExecutionException("The operator >> is undefined for the argument type(s) PNumeric, "+p.getType().getSimpleName());
	}

	@Override
	public PNumeric binaryClearLeft(PPrimitiveValue p) {
		if (p.getType() == PFieldType.NUMERIC.getPrimitiveType() || p.getType() == PFieldType.LOGICAL.getPrimitiveType()) {
			if ((this.isRealType || p.asNumeric().isRealType) && ApplicationProcessor.shouldReportWarings())
				ApplicationProcessor.warn("Clear left operator used in a real type PNumeric");
		
			return new PNumeric(this.valueInteger >>> p.asNumeric().asLong());
		}
		throw new ExecutionException("The operator >>> is undefined for the argument type(s) PNumeric, "+p.getType().getSimpleName());
	}
	
	@Override
	public PLogical or(PPrimitiveValue p) {
		if (!FormatUtil.isLogical(this))
			throw new ExecutionException("The operator OR is undefined for the argument type(s) PNumeric, "+p.getType().getSimpleName());
		if (p.getType() != PFieldType.LOGICAL.getPrimitiveType() && !FormatUtil.isLogical(p.asText().asString())) 
			throw new ExecutionException("The operator OR is undefined for the argument type(s) PNumeric/PLogical, "+p.getType().getSimpleName());
		return new PLogical(this.asLogical().asBoolean() || p.asLogical().asBoolean());
	}

	@Override
	public PLogical and(PPrimitiveValue p) {
		if (!FormatUtil.isLogical(this))
			throw new ExecutionException("The operator OR is undefined for the argument type(s) PNumeric, "+p.getType().getSimpleName());
		if (p.getType() != PFieldType.LOGICAL.getPrimitiveType() && !FormatUtil.isLogical(p.asText().asString())) 
			throw new ExecutionException("The operator OR is undefined for the argument type(s) PNumeric/PLogical, "+p.getType().getSimpleName());
		return new PLogical(this.asLogical().asBoolean() && p.asLogical().asBoolean());
	}

	@Override
	public PPrimitiveValue xor(PPrimitiveValue p) {
		if (p.getType() == PFieldType.NUMERIC.getPrimitiveType() || p.getType() == PFieldType.LOGICAL.getPrimitiveType()) {
			if ((this.isRealType || p.asNumeric().isRealType) && ApplicationProcessor.shouldReportWarings())
				ApplicationProcessor.warn("Bitwise XOR operator used in a real type PNumeric");
		
			return new PNumeric(this.valueInteger ^ p.asNumeric().asLong());
		}
		throw new ExecutionException("The operator XOR is undefined for the argument type(s) PNumeric, "+p.getType().getSimpleName());
	}

	@Override
	public PLogical ifEquals(PPrimitiveValue p) {
		if (p.getType() == PFieldType.NUMERIC.getPrimitiveType() || p.getType() == PFieldType.LOGICAL.getPrimitiveType()) {
			if (this.isRealType || p.asNumeric().isRealType) {
				return new PLogical(this.asDouble() == p.asNumeric().asDouble());
			}
			return new PLogical(this.asLong() == p.asNumeric().asLong());
		}
		throw new ExecutionException("The operator == is undefined for the argument type(s) PNumeric, "+p.getType().getSimpleName());
	}

	@Override
	public PLogical notEquals(PPrimitiveValue p) {
		if (p.getType() == PFieldType.NUMERIC.getPrimitiveType() || p.getType() == PFieldType.LOGICAL.getPrimitiveType()) {
			if (this.isRealType || p.asNumeric().isRealType) {
				return new PLogical(this.asDouble() != p.asNumeric().asDouble());
			}
			return new PLogical(this.asLong() != p.asNumeric().asLong());
		}
		throw new ExecutionException("The operator != is undefined for the argument type(s) PNumeric, "+p.getType().getSimpleName());
	}

	@Override
	public PLogical lessThan(PPrimitiveValue p) {
		if (p.getType() == PFieldType.NUMERIC.getPrimitiveType() || p.getType() == PFieldType.LOGICAL.getPrimitiveType())
			return new PLogical((this.isRealType ? this.valueReal : this.valueInteger) <
				(p.asNumeric().isRealType ? p.asNumeric().valueReal : p.asNumeric().valueInteger));
		throw new ExecutionException("The operator < is undefined for the argument type(s) PNumeric, "+p.getType().getSimpleName());
	}

	@Override
	public PLogical greaterThan(PPrimitiveValue p) {
		if (p.getType() == PFieldType.NUMERIC.getPrimitiveType() || p.getType() == PFieldType.LOGICAL.getPrimitiveType())
			return new PLogical((this.isRealType ? this.valueReal : this.valueInteger) >
					(p.asNumeric().isRealType ? p.asNumeric().valueReal : p.asNumeric().valueInteger));
		throw new ExecutionException("The operator < is undefined for the argument type(s) PNumeric, "+p.getType().getSimpleName());
	}

	@Override
	public PLogical lessThanEquals(PPrimitiveValue p) {
		if (p.getType() == PFieldType.NUMERIC.getPrimitiveType() || p.getType() == PFieldType.LOGICAL.getPrimitiveType())
			return new PLogical((this.isRealType ? this.valueReal : this.valueInteger) <= 
					(p.asNumeric().isRealType ? p.asNumeric().valueReal : p.asNumeric().valueInteger));
		throw new ExecutionException("The operator <= is undefined for the argument type(s) PNumeric, "+p.getType().getSimpleName());
	}

	@Override
	public PLogical greaterThanEquals(PPrimitiveValue p) {
		if (p.getType() == PFieldType.NUMERIC.getPrimitiveType() || p.getType() == PFieldType.LOGICAL.getPrimitiveType()) {
			return new PLogical((this.isRealType ? this.valueReal : this.valueInteger) >= 
					(p.asNumeric().isRealType ? p.asNumeric().valueReal : p.asNumeric().valueInteger));
		}
		throw new ExecutionException("The operator >= is undefined for the argument type(s) PNumeric, "+p.getType().getSimpleName());
	}
	
	public void inc() {
		if (isRealType) {
			++this.valueReal;
			this.valueInteger = (long) this.valueReal;
		} else {
			++this.valueInteger;
			this.valueReal = this.valueInteger;
		}
	}
	
	public void dec() {
		if (isRealType) {
			--this.valueReal;
			this.valueInteger = (long) this.valueReal;
		} else {
			--this.valueInteger;
			this.valueReal = this.valueInteger;
		}
	}
	
	private void setIntegerValue(String integerValue) {
		if (integerValue.length() >= 2)
			if(integerValue.startsWith("0x")) {
				this.valueInteger = Long.parseLong(integerValue.substring(2), 16);
			} else if (integerValue.startsWith("0b")) {
				this.valueInteger = Long.parseLong(integerValue.substring(2), 2);
			} else if (integerValue.startsWith("0")) {
				this.valueInteger = Long.parseLong(integerValue.substring(1), 8);
			} else {
				this.valueInteger = Long.parseLong(integerValue);
			}
		else
			this.valueInteger = Long.parseLong(integerValue);
		this.valueReal = this.valueInteger;
		this.isRealType = false;
	}
	
	private void setRealValue(String realValue) {
		this.valueReal = Double.parseDouble(realValue);
		this.valueInteger = (long) this.valueReal;
		this.isRealType = true;
	}

	@Override
	public String toString() {
		return "PNumeric [value=" + (isRealType ? valueReal : valueInteger) + ", real=" + isRealType + "]";
	}

	@Override
	public String asStringValue() {
		return isRealType ? "" + this.valueReal : "" + this.valueInteger;
	}
	
}
