package hu.gerviba.pseudocode.lang.complex;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

import hu.gerviba.pseudocode.exceptions.ExecutionException;
import hu.gerviba.pseudocode.lang.PField;

public final class ComplexRegistry {

	private final HashMap<String, BiFunction<String, List<String>, PField>> STORAGE = new HashMap<>();
	
	public void registerComplex(String pseudoClassName, BiFunction<String, List<String>, PField> instanceCreator) {
		STORAGE.put(pseudoClassName, instanceCreator);
	}
	
	public PField constructField(String psudoClass, String name, List<String> arguments) {
		if (!STORAGE.containsKey(psudoClass))
			throw new ExecutionException("No PseudoClass with name '" + psudoClass + "' found");
		return STORAGE.get(psudoClass).apply(name, arguments);
	}
	
}
