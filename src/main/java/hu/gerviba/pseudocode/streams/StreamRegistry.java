package hu.gerviba.pseudocode.streams;

import java.util.ArrayList;
import java.util.Arrays;

public final class StreamRegistry {
	
	public static ArrayList<Class<? extends PStream>> getAllPStreams() {
		return new ArrayList<Class<? extends PStream>>(Arrays.asList(
				PConsoleStream.class,
				PJUnitStream.class
				));
	}
	
}

