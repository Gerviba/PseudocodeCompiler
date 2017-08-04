package hu.gerviba.pseudocode.compiler.modifiers;

public enum CompileMode {
	SEMI_COMPRESSED("\n", 10),
	FULLY_COMPRESSED("\n", 36);
	
	private final String lineSeparator;
	private final int debugRadix;
	
	private CompileMode(String lineSeparator, int debugRadix) {
		this.lineSeparator = lineSeparator;
		this.debugRadix = debugRadix;
	}

	public String getLineSeparator() {
		return lineSeparator;
	}

	public int getDebugRadix() {
		return debugRadix;
	}
	
}
