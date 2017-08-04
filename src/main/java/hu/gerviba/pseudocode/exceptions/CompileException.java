package hu.gerviba.pseudocode.exceptions;

import hu.gerviba.pseudocode.compiler.builders.CompilerCore;

@SuppressWarnings("serial")
public class CompileException extends RuntimeException {

	public CompileException(CompilerCore core, String message) {
		super("[Error] "+message + " @line: " + core.getAbsoluteLineNumber());
	}
	
}
