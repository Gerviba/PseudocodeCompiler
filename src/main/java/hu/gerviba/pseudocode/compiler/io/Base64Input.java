package hu.gerviba.pseudocode.compiler.io;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class Base64Input implements ImportMethod {

	private final String base64;
	
	public Base64Input(String base64) {
		this.base64 = base64;
	}
	
	@Override
	public List<String> read() {
		return Arrays.asList(new String(Base64.getDecoder().decode(base64)));
	}


}
