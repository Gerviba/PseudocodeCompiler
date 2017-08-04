package hu.gerviba.pseudocode.compiler.io;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import hu.gerviba.pseudocode.PSeudocodeRuntimeEnvironment;

public class FileOutput implements ExportMethod {
	
	private final String fileName;
	
	public FileOutput(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void export(String fullCode) {
		File file = new File(fileName);
		Path path = Paths.get(file.toURI());
		
		try {
			Files.createDirectories(path.getParent());
			if (!file.exists())
				Files.createFile(path);
		} catch (Exception e) {
			System.err.println("Cannot create the output file! (" + e.getMessage() + ")");
			if (PSeudocodeRuntimeEnvironment.DEBUG)
				throw new RuntimeException(e);
		}
		
		try (PrintWriter pw = new PrintWriter(file)) {
			pw.append(fullCode);
		} catch (Exception e) {
			System.err.println("Cannot write to the output file! (" + e.getMessage() + ")");
			if (PSeudocodeRuntimeEnvironment.DEBUG)
				throw new RuntimeException(e);
		}
	}
	
}
