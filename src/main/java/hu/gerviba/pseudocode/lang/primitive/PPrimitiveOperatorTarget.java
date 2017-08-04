package hu.gerviba.pseudocode.lang.primitive;

import hu.gerviba.pseudocode.compiler.units.Operator;
import hu.gerviba.pseudocode.lang.PField;

public interface PPrimitiveOperatorTarget {

	public PPrimitiveValue add(PPrimitiveValue p);
	public default PPrimitiveValue add(PField p) { return add(p.getValue()); }
	
	public PNumeric sub(PPrimitiveValue p);
	public default PNumeric sub(PField p) { return sub(p.getValue()); }
	
	public PNumeric mul(PPrimitiveValue p);
	public default PNumeric mul(PField p) { return mul(p.getValue()); }
	
	public PNumeric div(PPrimitiveValue p);
	public default PNumeric div(PField p) { return div(p.getValue()); }
	
	public PNumeric mod(PPrimitiveValue p);
	public default PNumeric mod(PField p) { return mod(p.getValue()); }
	
	public PPrimitiveValue binaryNot(); // NOTE: ~
	public PPrimitiveValue neg(); // NOTE: -
	public PPrimitiveValue not();  // NOTE: !
	
	public PNumeric bitwiseOr(PPrimitiveValue p);
	public default PNumeric bitwiseOr(PField p) { return bitwiseOr(p.getValue()); }
	
	public PNumeric bitwiseAnd(PPrimitiveValue p);
	public default PNumeric bitwiseAnd(PField p) { return bitwiseAnd(p.getValue()); }
	
	public PNumeric binaryShiftLeft(PPrimitiveValue p);
	public default PNumeric binaryShiftLeft(PField p) { return binaryShiftLeft(p.getValue()); }
	
	public PNumeric binaryShiftRight(PPrimitiveValue p);
	public default PNumeric binaryShiftRight(PField p) { return binaryShiftRight(p.getValue()); }
	
	public PNumeric binaryClearLeft(PPrimitiveValue p);
	public default PNumeric binaryClearLeft(PField p) { return binaryClearLeft(p.getValue()); }
	
	public PLogical or(PPrimitiveValue p);
	public default PLogical or(PField p) { return or(p.getValue()); }
	
	public PLogical and(PPrimitiveValue p);
	public default PLogical and(PField p) { return and(p.getValue()); }
	
	public PPrimitiveValue xor(PPrimitiveValue p);
	public default PPrimitiveValue xor(PField p) { return xor(p.getValue()); }
	
	public PLogical ifEquals(PPrimitiveValue p);
	public default PLogical ifEquals(PField p) { return ifEquals(p.getValue()); }
	
	public PLogical notEquals(PPrimitiveValue p);
	public default PLogical notEquals(PField p) { return notEquals(p.getValue()); }
	
	public PLogical lessThan(PPrimitiveValue p);
	public default PLogical lessThan(PField p) { return lessThan(p.getValue()); }
	
	public PLogical greaterThan(PPrimitiveValue p);
	public default PLogical greaterThan(PField p) { return greaterThan(p.getValue()); }
	
	public PLogical lessThanEquals(PPrimitiveValue p);
	public default PLogical lessThanEquals(PField p) { return lessThanEquals(p.getValue()); }
	
	public PLogical greaterThanEquals(PPrimitiveValue p);
	public default PLogical greaterThanEquals(PField p) { return greaterThanEquals(p.getValue()); }
	
	public static PPrimitiveValue calc(PField left, String operator, PField right) {
		return Operator.getByOperator(operator).fireOperator(left.getPrimitiveValue(), right.getPrimitiveValue());
	}
	
}
