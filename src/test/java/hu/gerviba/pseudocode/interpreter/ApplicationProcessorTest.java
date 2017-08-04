package hu.gerviba.pseudocode.interpreter;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Ignore;
import org.junit.Test;

import hu.gerviba.pseudocode.compiler.builders.CompilerCore;
import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;
import hu.gerviba.pseudocode.streams.PJUnitStream;

public class ApplicationProcessorTest {

	@Test
	public void testApplicationProcessor_basics() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program test",
				"    i := 2",
				"    Ha i == 2 akkor",
				"        KI: (JUnit) i",
				"    Elágazás vége",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		ApplicationProcessor processor = new ApplicationProcessor(CompileMode.SEMI_COMPRESSED);
		processor.loadFromString(cc.getCompiledHead() + cc.getCompiledBody());
		processor.loadHeader().loadPrograms();
		
		assertEquals(10, processor.getLines().size());
		
		processor.runProgram();
		
		assertEquals(Arrays.asList("2"), ((PJUnitStream) processor.getStream(1)).getStdOut());
	}
	
	@Test
	public void testApplicationProcessor_strings() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program test",
				"    KI: (JUnit) \"part1\" + \"part2\"",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		ApplicationProcessor processor = new ApplicationProcessor(CompileMode.SEMI_COMPRESSED);
		processor.loadFromString(cc.getCompiledHead() + cc.getCompiledBody());
		processor.loadHeader().loadPrograms().runProgram();
		
		assertEquals(Arrays.asList("part1part2"), ((PJUnitStream) processor.getStream(1)).getStdOut());
	}
	
	@Test
	public void testApplicationProcessor_strings2() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program test",
				"    temp := 60",
				"    KI: (JUnit) \"part1\" + temp + \"part2\"",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		ApplicationProcessor processor = new ApplicationProcessor(CompileMode.SEMI_COMPRESSED);
		processor.loadFromString(cc.getCompiledHead() + cc.getCompiledBody());
		processor.loadHeader().loadPrograms().runProgram();
		
		assertEquals(Arrays.asList("part160part2"), ((PJUnitStream) processor.getStream(1)).getStdOut());
	}
		
	@Test
	public void testApplicationProcessor_loop() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program test",
				"    Ciklus i := 1-től 10-ig",
				"        KI: (JUnit) i",
				"    Ciklus vége",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		ApplicationProcessor processor = new ApplicationProcessor(CompileMode.SEMI_COMPRESSED);
		processor.loadFromString(cc.getCompiledHead() + cc.getCompiledBody());
		processor.loadHeader().loadPrograms().runProgram();
		
		assertEquals(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10"), 
		      ((PJUnitStream) processor.getStream(1)).getStdOut());
	}	
	
	@Test
	public void testApplicationProcessor_loop2() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program test",
				"    Ciklus i := 100-től 32-ig (-10)-esével",
				"        KI: (JUnit) i",
				"    Ciklus vége",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		ApplicationProcessor processor = new ApplicationProcessor(CompileMode.SEMI_COMPRESSED);
		processor.loadFromString(cc.getCompiledHead() + cc.getCompiledBody());
		processor.loadHeader().loadPrograms().runProgram();
		
		assertEquals(Arrays.asList("100", "90", "80", "70", "60", "50", "40"), 
		      ((PJUnitStream) processor.getStream(1)).getStdOut());
	}
	
	@Test
	public void testApplicationProcessor_overriding() throws Exception {
		ApplicationProcessor processor = new ApplicationProcessor(CompileMode.SEMI_COMPRESSED);
		processor.loadFromString(
				"#!/usr/bin/psre -s\n"+
				"META DEBUG MA==\n"+
				"META AUTHOR cm9vdA==\n"+
				"META VERSION MS4wLjA=\n"+
				"META PSRE_MIN MS4wLjA=\n"+
				"META CT MTQ5NDEwMzcwODMwNA==\n"+
				"META CV MS4wLjA=\n"+
				"META OS TGludXg=\n"+
				"META OA YW1kNjQ=\n"+
				"META OV My4xNi4wLTQtYW1kNjQ=\n"+
				"META JV MS44LjBfMTEx\n"+
				"META VE T3JhY2xlIENvcnBvcmF0aW9u\n"+
				"STREAM 0 3 hu.gerviba.pseudocode.streams.PConsoleStream\n"+
				"STREAM 1 2 hu.gerviba.pseudocode.streams.PJUnitStream\n"+
				"START test$0\n"+
				// BODY:
				"PROG test$0\n"+
				"METHOD $\n"+
				"VAR a 1\n"+
				"VAR b 2\n"+
				"CALC a b\n"+
				"CALC a 1\n"+
				"OUT 1 b\n"+
				"RETURN\n"+
				"END 0");
		processor.loadHeader().loadPrograms().runProgram();

		assertEquals("2", ((PJUnitStream) processor.getStream(1)).getStdOut().get(0));
	}
	
	@Ignore
	@Test
	public void testApplicationProcessor_getset() throws Exception {
		ApplicationProcessor processor = new ApplicationProcessor(CompileMode.SEMI_COMPRESSED);
		processor.loadFromString(
				"#!/usr/bin/psre -s\n"+
				"META DEBUG MA==\n"+
				"META AUTHOR cm9vdA==\n"+
				"META VERSION MS4wLjA=\n"+
				"META PSRE_MIN MS4wLjA=\n"+
				"META CT MTQ5NDEwMzcwODMwNA==\n"+
				"META CV MS4wLjA=\n"+
				"META OS TGludXg=\n"+
				"META OA YW1kNjQ=\n"+
				"META OV My4xNi4wLTQtYW1kNjQ=\n"+
				"META JV MS44LjBfMTEx\n"+
				"META VE T3JhY2xlIENvcnBvcmF0aW9u\n"+
				"STREAM 0 3 hu.gerviba.pseudocode.streams.PConsoleStream\n"+
				"STREAM 1 2 hu.gerviba.pseudocode.streams.PJUnitStream\n"+
				"START test$0\n"+
				// BODY:
				"PROG test$0\n"+
				"METHOD $\n"+
				"VAR var1 12\n"+
				"OUT 1 var1\n"+
				"GET $1 var1\n"+
				"TEMP $2 42\n"+
				"SET $1 $2\n"+
				"OUT 1 var1\n"+
				"RETURN\n"+
				"END 0");
		processor.loadHeader().loadPrograms().runProgram();
		
		assertEquals(Arrays.asList("12", "42"), ((PJUnitStream) processor.getStream(1)).getStdOut());
	}
}
