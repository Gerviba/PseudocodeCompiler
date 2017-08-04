package hu.gerviba.pseudocode.interpreter;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import hu.gerviba.pseudocode.compiler.units.Keyword;

public class LineTest {

	@Test
	public void testLine_rangeToArray() throws Exception {
		assertArrayEquals(new String[] {"10", "20", "30"}, 
				new Line(0, 0, Keyword.CALL, Arrays.asList("cplx$3", "10", "20", "30", "TEMP", "$24")).rangeToArray(1, 4));
	}
	
}
