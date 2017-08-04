package hu.gerviba.pseudocode.compiler.builders;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;

import hu.gerviba.pseudocode.compiler.builders.CompilerCore;
import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;
import hu.gerviba.pseudocode.interpreter.ApplicationProcessor;
import hu.gerviba.pseudocode.streams.PJUnitStream;

public class OperationBuilderTest {

	@Test
	public void testOperationBuilder_variableAndSum() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program test1",
				"    i := 1",
				"    i := i + 1",
				"    KI: i",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG test1$0\n"+
				"METHOD $\n"+
				"VAR i 1\n"+
				"CALC i i ADD 1\n"+
				"OUT 0 i\n"+
				"FREE i\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}
	
	@Test
	public void testOperationBuilder_variableAndOperations() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program test2",
				"    i := 1",
				"    i := i + 1 * ( 80 + 40 )",
				"    i := i + 3000 / ( 70 * ( 9 % 2 ) )",
				"    KI: i",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG test2$0\n"+
				"METHOD $\n"+
				"VAR i 1\n"+
				"TEMP $1 80 ADD 40\n"+
				"TEMP $2 1 MUL $1\n"+
				"CALC i i ADD $2\n"+
				"TEMP $3 9 MOD 2\n"+
				"TEMP $4 70 MUL $3\n"+
				"TEMP $5 3000 DIV $4\n"+
				"CALC i i ADD $5\n"+
				"OUT 0 i\n"+
				"FREE i\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}
	
	@Test
	public void testOperationBuilder_arrayBasics() throws Exception {
		
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program test",
				"    a := tömb(10, 210, 213, 312, -1)",
				"    b :=     [10, 211, 214, 312, -1]",
				"    Ciklus i := 1-től 5-ig",
				"        Ha a(i) != b[i] AKKOR",
				"            b[i] := 11",
				"        elág. vége",
				"    Ciklus vége",
				"    KI: (JUnit) \"processing\"",
				"    Ciklus i := 1-től 5-ig",
				"        KI: (JUnit) b[i]",
				"    Ciklus vége",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		ApplicationProcessor processor = new ApplicationProcessor(CompileMode.SEMI_COMPRESSED);
		processor.loadFromString(cc.getCompiledHead() + cc.getCompiledBody());
		processor.loadHeader().loadPrograms().runProgram();
		
		assertArrayEquals(new String[] {"processing", "10", "11", "11", "312", "-1"}, 
				((PJUnitStream) processor.getStream(1)).getStdOutAsArray());
	}
	
}
