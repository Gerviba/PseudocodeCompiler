package hu.gerviba.pseudocode.streams;

import static org.junit.Assert.*;

import org.junit.Test;

public class PStreamTest {

	@Test
	public void testIOStreamState() throws Exception {
		assertEquals(PStream.IOStreamState.getByRights(false, false), PStream.IOStreamState.NONE);
		assertEquals(PStream.IOStreamState.getByRights(true, false), PStream.IOStreamState.INPUT_ONLY);
		assertEquals(PStream.IOStreamState.getByRights(false, true), PStream.IOStreamState.OUTPUT_ONLY);
		assertEquals(PStream.IOStreamState.getByRights(true, true), PStream.IOStreamState.BOTH);
	}
	
}
