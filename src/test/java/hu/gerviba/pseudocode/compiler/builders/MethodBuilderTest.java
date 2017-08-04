package hu.gerviba.pseudocode.compiler.builders;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;

import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;
import hu.gerviba.pseudocode.interpreter.ApplicationProcessor;
import hu.gerviba.pseudocode.streams.PJUnitStream;

public class MethodBuilderTest {
	
	@Test
	public void testMethodBuilder_simpleMethod() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program test",
				"    Eljárás kiír(str, init = 1)",
				"        Ki: (JUnit) str",
				"    Eljárás vége",
				"    ",
				"    kiír(\"sajt\")",
				"Program vége"
			));
		CompilerCore core = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		core.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG test$0\n" +
				"METHOD $\n" +
				"CALL kiír$1 \"sajt\"\n" +
				"RETURN\n" +
				"METHOD kiír$1 str\n" +
				"VAR init 1\n" +
				"OUT 1 str\n" +
				"FREE str\n" +
				"FREE init\n" +
				"RETURN\n" +
				"END 0",
				core.getCompiledBody());
	}
	
	@Test
	public void testMethodBuilder_simpleMethodNoArg() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program test",
				"    Eljárás kiír()",
				"        Ki: (JUnit) \"sajt\"",
				"    Eljárás vége",
				"    ",
				"    kiír()",
				"Program vége"
			));
		CompilerCore core = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		core.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG test$0\n" +
				"METHOD $\n" +
				"CALL kiír$0\n" +
				"RETURN\n" +
				"METHOD kiír$0\n" +
				"OUT 1 \"sajt\"\n" +
				"RETURN\n" +
				"END 0",
				core.getCompiledBody());
	}
	
	@Test
	public void testMethodBuilder_simpleMethodInAction() throws Exception {
		LinkedList<String> code = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program test",
				"    Eljárás kiír(str, init = 1)",
				"        Ki: (JUnit) str",
				"    Eljárás vége",
				"    ",
				"    kiír(\"sajt\")",
				"Program vége"
			));
		CompilerCore core = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		core.loadLines(code).loadDirectives().initHeader().startCompile();
		
		ApplicationProcessor processor = new ApplicationProcessor(CompileMode.SEMI_COMPRESSED);
		processor.loadFromString(core.getFullCompiledCode());
		processor.loadHeader().loadPrograms().runProgram();
		
		assertEquals("sajt", ((PJUnitStream) processor.getStream(1)).getStdOut().get(0));
	}
	
	@Test
	public void testMethodBuilder_methodInAction() throws Exception {
		LinkedList<String> code = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program test",
				"    Eljárás kiír2(str)",
				"        Ki: (JUnit) str",
				"    Eljárás vége",
				"    ",
				"    Eljárás kiír(str)",
				"        kiír2(str)",
				"    Eljárás vége",
				"    ",
				"    kiír(\"testmethod\")",
				"Program vége"
			));
		CompilerCore core = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		core.loadLines(code).loadDirectives().initHeader().startCompile();
		
		ApplicationProcessor processor = new ApplicationProcessor(CompileMode.SEMI_COMPRESSED);
		processor.loadFromString(core.getFullCompiledCode());
		processor.loadHeader().loadPrograms().runProgram();
		
		assertEquals("testmethod", ((PJUnitStream) processor.getStream(1)).getStdOut().get(0));
	}
	
	@Test
	public void testMethodBuilder_recursiveMethodInAction() throws Exception {
		LinkedList<String> code = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program test",
				"    Eljárás kiír(n, str)",
				"        KI: (JUnit) n + \" \" + str",
				"        Ha n >= 1 akkor",
				"            kiír(n - 1, str)",
				"        elág. vége",
				"    Eljárás vége",
				"    ",
				"    kiír(5, \"recursive\")",
				"Program vége"
			));
		CompilerCore core = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		core.loadLines(code).loadDirectives().initHeader().startCompile();
		
		ApplicationProcessor processor = new ApplicationProcessor(CompileMode.SEMI_COMPRESSED);
		processor.loadFromString(core.getFullCompiledCode());
		processor.loadHeader().loadPrograms().runProgram();
		
		assertArrayEquals(new String[] {
				"5 recursive", 
				"4 recursive", 
				"3 recursive", 
				"2 recursive", 
				"1 recursive", 
				"0 recursive"}, 
				((PJUnitStream) processor.getStream(1)).getStdOutAsArray());
	}
	
	// TODO: Method argument tests, recursive
}
