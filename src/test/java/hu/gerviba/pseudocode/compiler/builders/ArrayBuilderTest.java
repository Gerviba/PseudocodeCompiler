package hu.gerviba.pseudocode.compiler.builders;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;
import hu.gerviba.pseudocode.compiler.units.Keyword;

public class ArrayBuilderTest {

	@Test
	public void testArrayBuilder_addToInt() throws Exception {
		ArrayBuilder test = new ArrayBuilder(new CompilerCore(CompileMode.SEMI_COMPRESSED), "a", "");
		
		assertArrayEquals(new int[] {10, 20, 30}, test.addToArray(new int[] {10, 20}, 30)); 
	}
	
	@Test
	public void testArrayBuilder_split() throws Exception {
		ArrayBuilder test = new ArrayBuilder(new CompilerCore(CompileMode.SEMI_COMPRESSED), 
				"a", "(((10, 20, 30), (40, 50, 60)), ((10, 20, 30, 100), (40, 50, 60)))");
		test.process();
		assertEquals("VAR a$3 PArray 1 2 1 2 1 4", test.buildVar(Keyword.VAR));
		assertEquals(Arrays.asList(
				"SET 10 a$3 1 1 1", 
				"SET 20 a$3 1 1 2",
				"SET 30 a$3 1 1 3", 
				"SET 40 a$3 1 2 1", 
				"SET 50 a$3 1 2 2", 
				"SET 60 a$3 1 2 3", 
				"SET 10 a$3 2 1 1", 
				"SET 20 a$3 2 1 2", 
				"SET 30 a$3 2 1 3", 
				"SET 100 a$3 2 1 4", 
				"SET 40 a$3 2 2 1", 
				"SET 50 a$3 2 2 2", 
				"SET 60 a$3 2 2 3"), test.buildSetters());
	}
	
	@Ignore
	@Test
	public void testArrayBuilder_tester() throws Exception {
		ArrayBuilder test = new ArrayBuilder(new CompilerCore(CompileMode.SEMI_COMPRESSED), 
				"a", "(10, 211, 214, 312, - 1)");
		test.process();
		System.out.println(test.buildVar(Keyword.VAR));
		System.out.println(test.buildSetters());
	}
	
	
	//TODO [1 .. 3] array

}
