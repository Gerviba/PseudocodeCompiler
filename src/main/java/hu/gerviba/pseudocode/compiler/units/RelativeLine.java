package hu.gerviba.pseudocode.compiler.units;

public final class RelativeLine {

	private final int originalLine;
	private final String command;
	
	public RelativeLine(int originalLine, String command) {
		this.originalLine = originalLine;
		this.command = command;
	}
	
	public int getAbsoluteLine() {
		return originalLine;
	}
	public String getCommand() {
		return command;
	}
	
}
