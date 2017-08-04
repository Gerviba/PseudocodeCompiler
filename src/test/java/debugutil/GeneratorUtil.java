package debugutil;

import hu.gerviba.pseudocode.compiler.builders.CompilerCore;
import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;

public class GeneratorUtil {

	public static CompilerCore newEmptyCompilerCore() {
		return new CompilerCore(CompileMode.SEMI_COMPRESSED);
	}

	
}
