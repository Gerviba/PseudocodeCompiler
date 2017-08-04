package hu.gerviba.pseudocode.lang.primitive;

import static org.junit.Assert.*;

import org.junit.Test;

public class PNumericTest {

	@Test
	public void testPNumeric_constructor() throws Exception {
		{
			PNumeric pn = new PNumeric(1);
			assertFalse(pn.isRealType());
			assertTrue(pn.asLong() == 1L);
			assertTrue(pn.asDouble() == 1D);
			assertTrue(pn.asLogical().asBoolean());
		}
		{
			PNumeric pn = new PNumeric(0);
			assertFalse(pn.isRealType());
			assertTrue(pn.asLong() == 0L);
			assertTrue(pn.asDouble() == 0D);
			assertFalse(pn.asLogical().asBoolean());
		}
		{
			PNumeric pn = new PNumeric(1.0);
			assertTrue(pn.isRealType());
			assertTrue(pn.asLong() == 1L);
			assertTrue(pn.asDouble() == 1D);
			assertTrue(pn.asLogical().asBoolean());
		}
		{
			PNumeric pn = new PNumeric(0.0);
			assertTrue(pn.isRealType());
			assertTrue(pn.asLong() == 0L);
			assertTrue(pn.asDouble() == 0D);
			assertFalse(pn.asLogical().asBoolean());
		}
		{
			PNumeric pn = new PNumeric("1");
			assertFalse(pn.isRealType());
			assertTrue(pn.asLong() == 1L);
			assertTrue(pn.asDouble() == 1D);
			assertTrue(pn.asLogical().asBoolean());
		}
		{
			PNumeric pn = new PNumeric("0");
			assertFalse(pn.isRealType());
			assertTrue(pn.asLong() == 0L);
			assertTrue(pn.asDouble() == 0D);
			assertFalse(pn.asLogical().asBoolean());
		}
		{
			PNumeric pn = new PNumeric("1.0");
			assertTrue(pn.isRealType());
			assertTrue(pn.asLong() == 1L);
			assertTrue(pn.asDouble() == 1D);
			assertTrue(pn.asLogical().asBoolean());
		}
		{
			PNumeric pn = new PNumeric("0.0");
			assertTrue(pn.isRealType());
			assertTrue(pn.asLong() == 0L);
			assertTrue(pn.asDouble() == 0D);
			assertFalse(pn.asLogical().asBoolean());
		}
		{
			PNumeric pn = new PNumeric("612432");
			assertFalse(pn.isRealType());
			assertTrue(pn.asLong() == 612432L);
			assertTrue(pn.asDouble() == 612432D);
		}
		{
			PNumeric pn = new PNumeric("432367.234");
			assertTrue(pn.isRealType());
			assertTrue(pn.asLong() == 432367L);
			assertTrue(pn.asDouble() == 432367.234D);
		}
	}
	
}
