package hu.gerviba.pseudocode.compiler.io;

import java.util.Arrays;
import java.util.List;

public class MemoryOutput implements ImportMethod, ExportMethod {

	private String output;

	@Override
	public void export(String fullCode) {
		this.output = fullCode;
	}

	@Override
	public List<String> read() {
		return Arrays.asList(output.split("\\n"));
	}
	
}
