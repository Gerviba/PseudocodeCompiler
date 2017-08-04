package hu.gerviba.pseudocode.utils;

import hu.gerviba.pseudocode.lang.Scope;
import hu.gerviba.pseudocode.lang.primitive.PNumeric;
import hu.gerviba.pseudocode.lang.primitive.PPrimitiveValue;
import hu.gerviba.pseudocode.lang.primitive.PLogical;
import hu.gerviba.pseudocode.lang.primitive.PNull;
import hu.gerviba.pseudocode.lang.primitive.PText;

public final class TypeParserUtil {

	/**
	 * TODO: Talán duplicate a PField#newValue
	 */
	public static PPrimitiveValue parsePrimitiveValueOrField(String value, Scope scope) {
		if (value.charAt(0) == '"') {
			return new PText(value);
		} else if (FormatUtil.isNumeric(value)) {
			return new PNumeric(value);
		} else if (FormatUtil.isLogical(value)) {
			return new PLogical(value);		
		} else if (value.equalsIgnoreCase("NULL")) {
			return PNull.NULL_POINTER;
		} else {
			return scope.getField(value).getPrimitiveValue();
		}
	}
	
	public static PPrimitiveValue parsePrimitiveValue(String value) {
		if (value.charAt(0) == '"') {
			return new PText(value);
		} else if (FormatUtil.isNumeric(value)) {
			return new PNumeric(value);
		} else if (FormatUtil.isLogical(value)) {
			return new PLogical(value);
		} else if (value.equalsIgnoreCase("NULL")) {
			return PNull.NULL_POINTER;
		} else {
			throw new IllegalArgumentException("Cannot cast '"+value+"' to any kind of primitive types");
		}
	}
	
}
