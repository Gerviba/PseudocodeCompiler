package hu.gerviba.pseudocode.compiler.builders;

import java.util.HashMap;

import hu.gerviba.pseudocode.compiler.builders.CompilerCore;
import hu.gerviba.pseudocode.exceptions.CompileException;
import hu.gerviba.pseudocode.func.PFunction;

public final class FunctionStorage {
	
	private final HashMap<String, PFunction> functions = new HashMap<>();
	private final HashMap<String, String> aliases = new HashMap<>();
	
	public PFunction getFunction(CompilerCore core, String name) {
		if (functions.containsKey(name))
			return functions.get(name);
		else if (aliases.containsKey(name))
			return functions.get(aliases.get(name));
		else
			throw new CompileException(core, "Function '" + name + "' not found");
 	}
	
	public boolean isExists(String name) {
		return functions.containsKey(name) || aliases.containsKey(name);
	}
	
	
	
}
