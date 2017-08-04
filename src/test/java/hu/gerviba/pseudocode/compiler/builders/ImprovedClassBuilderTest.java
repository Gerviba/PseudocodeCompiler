package hu.gerviba.pseudocode.compiler.builders;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;

@Deprecated
public class ImprovedClassBuilderTest {

	private CompilerCore core;
	
	@After
	public void init() {
		core = new CompilerCore(CompileMode.SEMI_COMPRESSED);
	}
	
	@Ignore
	@Test
	public void testImprovedClassBuilder_Calc() throws Exception {
		assertEquals(Arrays.asList("TEMP $1 1 MUL 1", "TEMP $2 1 ADD $1"), new ImprovedCalcBuilder().calc("1 + 1 * 2").getResult());
	}
	
}
