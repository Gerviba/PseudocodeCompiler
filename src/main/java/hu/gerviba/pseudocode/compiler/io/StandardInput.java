package hu.gerviba.pseudocode.compiler.io;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StandardInput implements ImportMethod {

	@Override
	public List<String> read() {
		List<String> code = new ArrayList<>();
		try (Scanner scanner = new Scanner(System.in)) {;
			while (scanner.hasNextLine())
				code.add(scanner.nextLine());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return code;
	}

}
