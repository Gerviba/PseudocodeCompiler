package hu.gerviba.pseudocode.compiler.units;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import hu.gerviba.pseudocode.compiler.builders.CompilerCore;
import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;
import hu.gerviba.pseudocode.lang.primitive.PPrimitiveValue;
import hu.gerviba.pseudocode.utils.FormatUtil;
import hu.gerviba.pseudocode.lang.primitive.PPrimitiveOperatorTarget;

public enum Operator {
	ADD("+", "ADD", '\u1000', (l, r) -> l.add(r)),
	SUB("-", "SUB", '\u1001', (l, r) -> l.sub(r)),
	MUL("*", "MUL", '\u1002', (l, r) -> l.mul(r)),
	DIV("/", "DIV", '\u1003', (l, r) -> l.div(r)),
	MOD("%", "MOD", '\u1004', (l, r) -> l.mod(r)),
	LT("<", "LT", '\u1005', (l, r) -> l.lessThan(r)),
	LE("<=", "LE", '\u1006', (l, r) -> l.lessThanEquals(r)),
	SL(">>", "SL", '\u1007', (l, r) -> l.binaryShiftLeft(r)),
	CL(">>>", "CL", '\u1008', (l, r) -> l.binaryClearLeft(r)),
	GT(">", "GT", '\u1009', (l, r) -> l.greaterThan(r)),
	GE(">=", "GE", '\u100A', (l, r) -> l.greaterThanEquals(r)),
	SR("<<", "SR", '\u100B', (l, r) -> l.binaryShiftRight(r)),
	BAND("&", "BAND", '\u100C', (l, r) -> l.bitwiseAnd(r)),
	AND("&&", "AND", '\u100D', (l, r) -> l.and(r)),
	BOR("|", "BOR", '\u100E', (l, r) -> l.bitwiseOr(r)),
	OR("||", "OR", '\u100F', (l, r) -> l.or(r)),
	XOR("^", "XOR", '\u1010', (l, r) -> l.xor(r)),
	EQ("==", "EQ", '\u1011', (l, r) -> l.ifEquals(r)),
	NEQ("!=", "NEQ", '\u1012', (l, r) -> l.notEquals(r)),
	
	// PreOperators
	NOT("!", "NOT", '\u1013', true, (l, r) -> l.not()),
	BNEG("~", "BNEG", '\u1014', true, (l, r) -> l.binaryNot()),
	NEG("-", "NEG", '\u1015', true, (l, r) -> l.neg()),
	;
	
	private final String sign;
	private final String semiCompressed;
	private final char fullyCompressed;
	private final BiFunction<PPrimitiveOperatorTarget, PPrimitiveValue, PPrimitiveValue> operation;
	private final boolean pre;
	
	private static final List<Operator> PRE_OPERATORS = new ArrayList<>();
	
	static {
		for (Operator op : values())
			if (op.pre)
				PRE_OPERATORS.add(op);
	}
	
	private Operator(String sign, String semiCompressed, char fullyCompressed, 
			BiFunction<PPrimitiveOperatorTarget, PPrimitiveValue, PPrimitiveValue> operation) {
		this(sign, semiCompressed, fullyCompressed, false, operation);
	}
	
	private Operator(String sign, String semiCompressed, char fullyCompressed, boolean pre,
			BiFunction<PPrimitiveOperatorTarget, PPrimitiveValue, PPrimitiveValue> operation) {
		this.sign = sign;
		this.semiCompressed = semiCompressed;
		this.fullyCompressed = fullyCompressed;
		this.operation = operation;
		this.pre = pre;
	}
	
	public String asString(CompilerCore core) {
		if (core.getCompileMode() == CompileMode.FULLY_COMPRESSED)
			return ""+fullyCompressed;
		return semiCompressed;
	}

	public String getSign() {
		return sign;
	}

	public String getSemiCompressed() {
		return semiCompressed;
	}

	public char getFullyCompressed() {
		return fullyCompressed;
	}

	public static Operator getBySign(String sign) {
		for (Operator o : values()) {
			if (o.sign.equals(sign))
				return o;
		}
		return null;
	}
	
	public static Operator getPreoperatorBySign(String sign) {
		for (Operator o : PRE_OPERATORS) {
			if (o.sign.equals(sign))
				return o;
		}
		return null;
	}
	
	public static Operator getByOperator(String operator) {
		for (Operator o : values()) {
			if (o.semiCompressed.equals(operator) || o.fullyCompressed == operator.charAt(0))
				return o;
		}
		return null;
	}

	public PPrimitiveValue fireOperator(PPrimitiveOperatorTarget left, PPrimitiveValue right) {
		return operation.apply(left, right);
	}

	public static String applyAliases(String line) {
		line = FormatUtil.replaceAllNotInString(line, "(?i)[ \\t]nem[ \\t]+", " !");
		line = FormatUtil.replaceAllNotInString(line, "(?i)[ \\t]nem\\(", " !(");
		line = FormatUtil.replaceAllNotInString(line, "(?i)[ \\t]nem\\[", " ![");
		line = FormatUtil.replaceAllNotInString(line, "(?i)[ \\t]+[Éée]s[ \\t]+", " && ");
		line = FormatUtil.replaceAllNotInString(line, "(?i)[ \\t]+vagy[ \\t]+", " || ");
		
		return line;
	}
	
}
