package hu.gerviba.pseudocode.compiler.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import hu.gerviba.pseudocode.PSeudocodeRuntimeEnvironment;

public class FileInput implements ImportMethod {

	private final String fileName;
	
	public FileInput(String fileName) {
		this.fileName = fileName;
	}
	
	@Override
	public List<String> read() {
		try {
			return Files.readAllLines(Paths.get(fileName));
		} catch (IOException e) {
			System.err.println("Cannot read the input file! (" + e.getMessage() + ")");
			if (PSeudocodeRuntimeEnvironment.DEBUG)
				throw new RuntimeException(e);
			System.exit(-1);
			return null;
		}
	}

}
