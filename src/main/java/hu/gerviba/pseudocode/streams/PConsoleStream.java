package hu.gerviba.pseudocode.streams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import hu.gerviba.pseudocode.exceptions.ExecutionException;

@StreamHandler(pattern = "^\\((?i)CLI\\).+$")
public class PConsoleStream extends PStream {
	
	public PConsoleStream() {
	}
	
	@Override
	public String read() {
		if (!getIOState().read)
			throw new ExecutionException("You have no permission to read from console!");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			return br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return "N/A";
		}
	}

	@Override
	public void write(String content) {
		if (!getIOState().read)
			throw new ExecutionException("You have no permission to write to the console!");
		System.out.println(content);
	}

	@Override
	public String getPrefix() {
		return "(CLI)";
	}
	
}
