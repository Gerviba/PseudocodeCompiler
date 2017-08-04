package hu.gerviba.pseudocode.compiler.units;

import static org.junit.Assert.*;

import org.junit.Test;

public class OperatorTest {

	@Test
	public void testOperator_applyAliases() throws Exception {
		assertEquals("ciklus amég !func(10)", Operator.applyAliases("ciklus amég NEM func(10)"));
		assertEquals("ciklus amég !func(10) && lol == 1", Operator.applyAliases("ciklus amég NEM func(10) ÉS lol == 1"));
		assertEquals("ciklus amég !func(10) || test != 2", Operator.applyAliases("ciklus amég NEM func(10) Vagy test != 2"));
		assertEquals("ciklus amég !(test != 2)", Operator.applyAliases("ciklus amég nem(test != 2)"));
	}
	
}
