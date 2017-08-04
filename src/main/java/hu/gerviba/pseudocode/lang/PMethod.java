package hu.gerviba.pseudocode.lang;

import hu.gerviba.pseudocode.interpreter.Line;

public class PMethod {
	
	private final String name;
	private final int startLine;
	private final PProgram parent;
	private final String[] args;
	
	public PMethod(PProgram parent, String name, int startLine, Line line) {
		this.parent = parent;
		this.name = name;
		this.startLine = startLine;
		this.args = line.rangeToArray(1, line.getArgsSize());
	}

	public PProgram getParent() {
		return parent;
	}

	public String getName() {
		return name;
	}

	public int getStartLine() {
		return startLine;
	}

	public String getArgument(int n) {
		return args[n];
	}
	
}
