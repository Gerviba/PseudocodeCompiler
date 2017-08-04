package hu.gerviba.pseudocode.interpreter;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;

import hu.gerviba.pseudocode.compiler.builders.CompilerCore;
import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;
import hu.gerviba.pseudocode.exceptions.ExecutionException;

public class BodyPreprocessorTest {

	@Test
	public void testBodyProcessor_programSingle() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program test",
				"    i := 2",
				"    Ha i == 2 akkor",
				"        KI: i",
				"    Elágazás vége",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		ApplicationProcessor processor = new ApplicationProcessor(CompileMode.SEMI_COMPRESSED);
		processor.loadFromString(cc.getCompiledHead() + cc.getCompiledBody());
		processor.loadHeader().loadPrograms();
		
		assertEquals(processor.getDefaultProgramName(), "test$0");
		assertTrue(processor.getProgram("test$0") != null);
		assertTrue(processor.getProgram("test$0").getMethod("$") != null);
		assertEquals(6, processor.getLabelTarget("0"));
		
	}
	
	@Test(expected = ExecutionException.class)
	public void testBodyProcessor_missingLabel() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program test",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		ApplicationProcessor processor = new ApplicationProcessor(CompileMode.SEMI_COMPRESSED);
		processor.loadFromString(cc.getCompiledHead() + cc.getCompiledBody());
		processor.loadHeader().loadPrograms();
		
		processor.getLabelTarget("missing");
	}
	
	//TODO: multiprogram
	
}
