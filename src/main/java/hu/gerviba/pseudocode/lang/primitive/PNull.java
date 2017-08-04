package hu.gerviba.pseudocode.lang.primitive;

import hu.gerviba.pseudocode.exceptions.ExecutionException;
import hu.gerviba.pseudocode.lang.PField;
import hu.gerviba.pseudocode.lang.PFieldType;

public class PNull implements PPrimitiveValue {

	public static final PNull NULL_POINTER = new PNull();
	public static final PField NULL_POINTER_CONTAINER = new PPrimitiveField("null", NULL_POINTER, true);
	
	private PNull() {
	}
	
	@Override
	public void set(String value) {
		throw new NullPointerException("Cannot set the String value of the Null Pointer");
	}

	@Override
	public void set(PPrimitiveValue value) {
		throw new NullPointerException("Cannot set the value of the Null Pointer");
	}

	private static final PText NULL_TEXT = new PText("null", false);
	
	@Override
	public PText asText() {
		return NULL_TEXT.asTextClone();
	}
	
	@Override
	public PText asTextClone() {
		return asText();
	}

	@Override
	public PNumeric asNumeric() {
		throw new NullPointerException("Cannot get the Numeric value of the Null Pointer");
	}
	
	@Override
	public PNumeric asNumericClone() {
		return asNumeric();
	}

	@Override
	public PLogical asLogical() {
		throw new NullPointerException("Cannot get the Logical value of the Null Pointer");
	}
	
	@Override
	public PLogical asLogicalClone() {
		return asLogical();
	}

	@Override
	public PPrimitiveValue add(PPrimitiveValue p) {
		if (p.getType() == PFieldType.TEXT.getPrimitiveType())
			return new PText("null"+p.asText().asString());
		throw new ExecutionException("The operator + is undefined for the argument type(s) NULL, "+p.getType().getSimpleName());
	}

	@Override
	public PNumeric sub(PPrimitiveValue p) {
		throw new ExecutionException("The operator - is undefined for the argument type(s) NULL, "+p.getType().getSimpleName());
	}

	@Override
	public PNumeric mul(PPrimitiveValue p) {
		throw new ExecutionException("The operator * is undefined for the argument type(s) NULL, "+p.getType().getSimpleName());
	}

	@Override
	public PNumeric div(PPrimitiveValue p) {
		throw new ExecutionException("The operator / is undefined for the argument type(s) NULL, "+p.getType().getSimpleName());
	}

	@Override
	public PNumeric mod(PPrimitiveValue p) {
		throw new ExecutionException("The operator MOD is undefined for the argument type(s) NULL, "+p.getType().getSimpleName());
	}

	@Override
	public PPrimitiveValue binaryNot() {
		throw new ExecutionException("The operator binary negation is undefined for the argument NULL");
	}

	@Override
	public PPrimitiveValue neg() {
		throw new ExecutionException("The operator neg is undefined for the argument NULL");
	}
	
	// TODO: NOTE: JOKE: Should I return a random value which is not null?
	@Override
	public PPrimitiveValue not() {
		throw new ExecutionException("The operator not is undefined for the argument NULL");
	}

	@Override
	public PNumeric bitwiseOr(PPrimitiveValue p) {
		throw new ExecutionException("The operator bitwise OR is undefined for the argument type(s) NULL, "+p.getType().getSimpleName());
	}

	@Override
	public PNumeric bitwiseAnd(PPrimitiveValue p) {
		throw new ExecutionException("The operator bitwise AND is undefined for the argument type(s) NULL, "+p.getType().getSimpleName());
	}

	@Override
	public PNumeric binaryShiftLeft(PPrimitiveValue p) {
		throw new ExecutionException("The operator << is undefined for the argument type(s) NULL, "+p.getType().getSimpleName());
	}

	@Override
	public PNumeric binaryShiftRight(PPrimitiveValue p) {
		throw new ExecutionException("The operator >> is undefined for the argument type(s) NULL, "+p.getType().getSimpleName());
	}

	@Override
	public PNumeric binaryClearLeft(PPrimitiveValue p) {
		throw new ExecutionException("The operator >>> cannot be interpreted because of the BigDecimal style value storage");
	}

	@Override
	public PLogical or(PPrimitiveValue p) {
		throw new ExecutionException("The operator OR is undefined for the argument type(s) NULL, "+p.getType().getSimpleName());
	}

	@Override
	public PLogical and(PPrimitiveValue p) {
		throw new ExecutionException("The operator AND is undefined for the argument type(s) NULL, "+p.getType().getSimpleName());
	}

	@Override
	public PPrimitiveValue xor(PPrimitiveValue p) {
		throw new ExecutionException("The operator XOR is undefined for the argument type(s) NULL, "+p.getType().getSimpleName());
	}

	@Override
	public PLogical ifEquals(PPrimitiveValue p) {
		return new PLogical(p.getType() == PFieldType.NULL.getPrimitiveType());
	}

	@Override
	public PLogical notEquals(PPrimitiveValue p) {
		return new PLogical(p.getType() != PFieldType.NULL.getPrimitiveType());
	}

	@Override
	public PLogical lessThan(PPrimitiveValue p) {
		throw new ExecutionException("The operator > is undefined for the argument type(s) NULL, "+p.getType().getSimpleName());
	}

	@Override
	public PLogical greaterThan(PPrimitiveValue p) {
		throw new ExecutionException("The operator < is undefined for the argument type(s) NULL, "+p.getType().getSimpleName());
	}

	@Override
	public PLogical lessThanEquals(PPrimitiveValue p) {
		throw new ExecutionException("The operator >= is undefined for the argument type(s) NULL, "+p.getType().getSimpleName());
	}

	@Override
	public PLogical greaterThanEquals(PPrimitiveValue p) {
		throw new ExecutionException("The operator <= is undefined for the argument type(s) NULL, "+p.getType().getSimpleName());
	}

	@Override
	public String toString() {
		return "null";
	}

	@Override
	public PPrimitiveValue cloneValue() {
		return PNull.NULL_POINTER;
	}

	@Override
	public String asStringValue() {
		return "null";
	}
	
}
