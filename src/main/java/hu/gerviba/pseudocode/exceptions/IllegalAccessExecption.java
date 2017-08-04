package hu.gerviba.pseudocode.exceptions;

import hu.gerviba.pseudocode.interpreter.ApplicationProcessor;

public class IllegalAccessExecption extends RuntimeException {

	public IllegalAccessExecption(ApplicationProcessor processor, String message) {
		super(message);
	}
	
}
