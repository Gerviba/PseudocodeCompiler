package hu.gerviba.pseudocode.lang;

import java.util.HashMap;

import hu.gerviba.pseudocode.interpreter.Line;

public class PProgram {

	private PMethod defaultMethod;
	private HashMap<String, PMethod> methods = new HashMap<>();
	private int startLine;
	
	public PProgram(int fromLine, Line line) {
		this.startLine = fromLine;
	}

	public void registerMethod(String name, PMethod method) {
		if ("$".equals(name))
			defaultMethod = method;
		else
			methods.put(name.toLowerCase(), method);
	}

	public PMethod getMethod(String method) {
		if ("$".equals(method))
			return defaultMethod;
		return methods.get(method.toLowerCase());
	}
	
	public int getStartLine() {
		return startLine;
	}

	public boolean hasMethod(String name) {
		return methods.containsKey(name.toLowerCase());
	}
	
}
