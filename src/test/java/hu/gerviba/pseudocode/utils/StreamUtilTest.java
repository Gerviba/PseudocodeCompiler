package hu.gerviba.pseudocode.utils;

import static org.junit.Assert.*;
import org.junit.Test;

import hu.gerviba.pseudocode.compiler.builders.CompilerCore;
import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;
import hu.gerviba.pseudocode.streams.PConsoleStream;

public class StreamUtilTest {
	
	@Test(expected = NullPointerException.class)
	public void testStream_NULL() throws Exception {
		assertEquals(StreamUtil.getStreamByDefinition(
				new CompilerCore(CompileMode.SEMI_COMPRESSED), "() i")
				.getClass().getName(), PConsoleStream.class.getName());
	}
	
	@Test
	public void testStream_CLI() throws Exception {
		assertEquals(StreamUtil.getStreamByDefinition(
				new CompilerCore(CompileMode.SEMI_COMPRESSED), "(CLI) i")
				.getClass().getName(), PConsoleStream.class.getName());
	}
	
}
