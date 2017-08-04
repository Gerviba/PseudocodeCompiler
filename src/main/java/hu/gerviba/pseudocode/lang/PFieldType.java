package hu.gerviba.pseudocode.lang;

import hu.gerviba.pseudocode.lang.primitive.PNumeric;
import hu.gerviba.pseudocode.lang.primitive.PPrimitiveValue;
import hu.gerviba.pseudocode.lang.primitive.PLogical;
import hu.gerviba.pseudocode.lang.primitive.PNull;
import hu.gerviba.pseudocode.lang.primitive.PText;

public enum PFieldType {
	VOID(false, false, false, false, null),
	
	NUMERIC(true, true, false, false, PNumeric.class),
	TEXT(true, false, true, false, PText.class),
	LOGICAL(true, false, false, true, PLogical.class),
	
	NULL(true, false, false, false, PNull.class),
	
	COMPLEX(true, false, false, false);
	
	private final boolean returnValue;
	private final boolean numeric;
	private final boolean text;
	private final boolean logical;
	
	private final Class<? extends PPrimitiveValue> primitiveType;

	private PFieldType(boolean returnValue, boolean numeric, boolean text, boolean logical, 
			Class<? extends PPrimitiveValue> primitiveType) {
		this.returnValue = returnValue;
		this.numeric = numeric;
		this.text = text;
		this.logical = logical;
		this.primitiveType = primitiveType;
	}
	
	private PFieldType(boolean returnValue, boolean numeric, boolean text, boolean logical) {
		this.returnValue = returnValue;
		this.numeric = numeric;
		this.text = text;
		this.logical = logical;
		this.primitiveType = null;
	}

	public boolean isReturnValue() {
		return returnValue;
	}

	public boolean isNumeric() {
		return numeric;
	}

	public boolean isText() {
		return text;
	}

	public boolean isLogical() {
		return logical;
	}
	
	public Class<? extends PPrimitiveValue> getPrimitiveType() {
		return primitiveType;
	}
	
}
