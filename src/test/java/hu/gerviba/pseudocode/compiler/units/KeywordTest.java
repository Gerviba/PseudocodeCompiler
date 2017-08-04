package hu.gerviba.pseudocode.compiler.units;

import static org.junit.Assert.*;

import org.junit.Test;

import hu.gerviba.pseudocode.compiler.builders.CompilerCore;
import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;

public class KeywordTest {

	@Test
	public void testBuild() throws Exception {
		CompilerCore core = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		assertEquals(Keyword.CONST.build(core, "A", "B", "C", "D"), "CONST A B C D".replace(" ", Keyword.SEPARATOR.getByCore(core)));
		assertEquals(Keyword.CONST.build(core, "A", "BCDFG"), "CONST A BCDFG".replace(" ", Keyword.SEPARATOR.getByCore(core)));
		assertEquals(Keyword.CONST.build(core), "CONST".replace(" ", Keyword.SEPARATOR.getByCore(core)));
		assertEquals(Keyword.CALC.build(core, "i", 20, "MOD", 2), "CALC i 20 MOD 2".replace(" ", Keyword.SEPARATOR.getByCore(core)));
		assertEquals(Keyword.CALC.build(core, "i", 20, "MOD", null, 2), "CALC i 20 MOD 2".replace(" ", Keyword.SEPARATOR.getByCore(core)));
		assertEquals(Keyword.CALC.build(core, "i", 20, "MOD", 2, null), "CALC i 20 MOD 2".replace(" ", Keyword.SEPARATOR.getByCore(core)));
		assertEquals(Keyword.CALC.build(core, null, "i", 20, "MOD", 2), "CALC i 20 MOD 2".replace(" ", Keyword.SEPARATOR.getByCore(core)));
	}
	
}
