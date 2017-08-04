package hu.gerviba.pseudocode.compiler.builders;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;

import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;

public class IOBuilderTest {
	
	@Test
	public void testIOBuilder_inAndOut() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program test",
				"    BE: i",
				"    i := i * 2 + 1",
				"    KI: (CLI)i",
				"    KI: (CLI) i",
				"    KI: (CLI)   	 i",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG test$0\n"+
				"METHOD $\n"+
				"VAR i null\n"+
				"IN 0 i\n"+
				"TEMP $1 i MUL 2\n"+
				"CALC i $1 ADD 1\n"+
				"OUT 0 i\n"+
				"OUT 0 i\n"+
				"OUT 0 i\n"+
				"FREE i\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}
	
}
