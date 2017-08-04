package hu.gerviba.pseudocode.compiler.io;

public class StandardOutput implements ExportMethod {

	@Override
	public void export(String fullCode) {
		System.out.println(fullCode);
	}
	
}
