package hu.gerviba.pseudocode.lang.complex;

import static org.junit.Assert.*;

import org.junit.Test;

public class PArrayFieldTest {

	@Test
	public void testPArrayField_removeArgument() throws Exception {
		assertArrayEquals(new String[] {"2", "3", "4"},
				new PArrayField("", 0, 0, 1).removeArgument(new String[] {"1", "2", "3", "4"})); 
	}
	
}
