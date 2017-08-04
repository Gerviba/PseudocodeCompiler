package hu.gerviba.pseudocode.compiler.builders;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;
import hu.gerviba.pseudocode.compiler.units.RelativeLine;

public class CompilerCoreTest {

	/*
	System.out.println("# Raw code:\n" + String.join("\n", preformattedCode));
	System.out.println("\n# Compiled Code:\n"+cc.getCompiledBody());
	*/
	

	@Test
	public void testIOBuilder_headerAndStream() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
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
		
		assertTrue(cc.getCompiledHead().startsWith(
				"#!/bin/psre\n" +
				"META DEBUG MQ==\n" +
				"META ORIGINAL_CODE "));
		
		assertTrue(cc.getCompiledHead().endsWith(
				"STREAM 0 3 hu.gerviba.pseudocode.streams.PConsoleStream\n" +
				"START test$0\n"));
	}
	
	//@Test
	public void testLambdas() throws Exception {
		List<RelativeLine> lines = Arrays.asList(new RelativeLine(0, "a"), new RelativeLine(1, "b"), new RelativeLine(2, "c"));
		
		System.out.println(String.join(CompileMode.SEMI_COMPRESSED.getLineSeparator(), lines.stream().map(x -> x.getCommand()).collect(Collectors.toList())));
	}
	
	@Test
	public void testDirective_debug() throws Exception {
		CompilerCore core = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		CompilerCore.Directive.DEBUG.identify(core, "# @Debug ON");
		assertTrue(core.isDebug());
		
		core = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		CompilerCore.Directive.DEBUG.identify(core, "# @Debug OFF");
		assertFalse(core.isDebug());
		
		core = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		CompilerCore.Directive.DEBUG.identify(core, "# @Debug 1");
		assertTrue(core.isDebug());
		
		core = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		CompilerCore.Directive.DEBUG.identify(core, "# @Debug 0");
		assertFalse(core.isDebug());
		
		core = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		CompilerCore.Directive.DEBUG.identify(core, "  # @Debug ON  ");
		assertTrue(core.isDebug());
		
		core = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		CompilerCore.Directive.DEBUG.identify(core, "\t  \t# @Debug ON \t    \t");
		assertTrue(core.isDebug());
		
		core = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		CompilerCore.Directive.DEBUG.identify(core, "#    \t @Debug OFF");
		assertFalse(core.isDebug());
		
		core = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		CompilerCore.Directive.DEBUG.identify(core, "# @Debug    \t ON");
		assertTrue(core.isDebug());
		
		core = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		CompilerCore.Directive.DEBUG.identify(core, "# @Debug    \t ON");
		assertTrue(core.isDebug());
		
		core = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		CompilerCore.Directive.DEBUG.identify(core, "#DEBUG ON");
		assertTrue(core.isDebug());
		
	}
	
}
