package hu.gerviba.pseudocode.interpreter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;

import hu.gerviba.pseudocode.compiler.builders.CompilerCore;
import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;
import hu.gerviba.pseudocode.streams.PConsoleStream;

public class HeadProcessorTest {

	@Test
	public void testHeadProcessor_directives() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"# @CompileInfo ALL",
				"Program test",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		ApplicationProcessor processor = new ApplicationProcessor(CompileMode.SEMI_COMPRESSED);
		processor.loadFromString(cc.getCompiledHead() + cc.getCompiledBody());
		processor.loadHeader();
		
		assertTrue(processor.getMeta("MIN_PSRE") != null);
		assertTrue(processor.getMeta("ORIGINAL_CODE") == null);
		assertTrue(processor.getMeta("DESC") == null);
		assertTrue(processor.getMeta("AUTHOR") != null);
		assertTrue(processor.getMeta("CT") != null);
		assertTrue(processor.getMeta("CV") != null);
		assertTrue(processor.getMeta("OS") != null);
		assertTrue(processor.getMeta("OA") != null);
		assertTrue(processor.getMeta("JV") != null);
		assertTrue(processor.getMeta("VE") != null);
	}
	
	@Test
	public void testHeadProcessor_uniqueVars() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"# @Meta KULCS = Érték",
				"",
				"Program test",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		ApplicationProcessor processor = new ApplicationProcessor(CompileMode.SEMI_COMPRESSED);
		processor.loadFromString(cc.getCompiledHead() + cc.getCompiledBody());
		processor.loadHeader();
		
		assertEquals("Érték", processor.getMeta("KULCS"));
	}
	
	@Test
	public void testHeadProcessor_defaultName() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"Program test",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		ApplicationProcessor processor = new ApplicationProcessor(CompileMode.SEMI_COMPRESSED);
		processor.loadFromString(cc.getCompiledHead() + cc.getCompiledBody());
		processor.loadHeader();
		
		assertEquals("test$0", processor.getDefaultProgramName());
	}
	
	//TODO: default program direktíva teszt
	
	@Test
	public void testHeadProcessor_stream() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"Program test",
				"    BE: i",
				"    KI: (CLI)i",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		ApplicationProcessor processor = new ApplicationProcessor(CompileMode.SEMI_COMPRESSED);
		processor.loadFromString(cc.getCompiledHead() + cc.getCompiledBody());
		processor.loadHeader();
		
		assertEquals(PConsoleStream.class.getName(), processor.getStream(0).getClass().getName());
	}
	
	//TODO: multiple streams
	
}
