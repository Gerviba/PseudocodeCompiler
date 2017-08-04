package hu.gerviba.pseudocode.lang;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;

import hu.gerviba.pseudocode.compiler.builders.CompilerCore;
import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;
import hu.gerviba.pseudocode.exceptions.ExecutionException;
import hu.gerviba.pseudocode.interpreter.ApplicationProcessor;

public class ScopeTest {

	@Test(expected = ExecutionException.class)
	public void minSort() throws Exception {
		LinkedList<String> code = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program test",
				"    a := (10, 210, 213, 312, -1, -30, 230, 500, 0, 1001)",
				"    Ciklus i := 1-től n-ig",
				"        KI: a[i]",
				"    Ciklus vége",
				"Program vége"
			));
		CompilerCore core = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		core.loadLines(code).loadDirectives().initHeader().startCompile();
		
		ApplicationProcessor processor = new ApplicationProcessor(CompileMode.SEMI_COMPRESSED);
		processor.loadFromString(core.getFullCompiledCode());
		processor.loadHeader().loadPrograms().runProgram();
	}
	
	
}
