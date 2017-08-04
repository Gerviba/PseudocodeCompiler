package hu.gerviba.pseudocode.streams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import hu.gerviba.pseudocode.exceptions.ExecutionException;

@StreamHandler(pattern = "^\\((?i)JUnit\\).+$")
public class PJUnitStream extends PStream {
	
	public ArrayList<String> stdIn = new ArrayList<String>(), stdOut = new ArrayList<String>();
	private int stdInLine = 0;
	
	public PJUnitStream() {
	}
	
	@Override
	public String read() {
		if (!getIOState().read)
			throw new ExecutionException("You have no permission to read from JUnit stream!");
		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
			return stdIn.get(stdInLine++);
		} catch (IOException e) {
			e.printStackTrace();
			return "N/A";
		}
	}

	@Override
	public void write(String content) {
		if (!getIOState().write)
			throw new ExecutionException("You have no permission to write to the JUnit stream!");
		stdOut.add(content);
	}

	@Override
	public String getPrefix() {
		return "(JUnit)";
	}
	
	public ArrayList<String> getStdIn() {
		return stdIn;
	}

	public void setStdIn(ArrayList<String> stdIn) {
		stdInLine = 0;
		this.stdIn = stdIn;
	}

	public ArrayList<String> getStdOut() {
		return stdOut;
	}

	public Integer[] getStdOutAsIntArray() {
		return stdOut.stream().map(Integer::parseInt).toArray(x -> new Integer[x]);
	}
	
	public String[] getStdOutAsArray() {
		return stdOut.stream().toArray(x -> new String[x]);
	}

	public void setStdOut(ArrayList<String> stdOut) {
		this.stdOut = stdOut;
	}

	
}
