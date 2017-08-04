package hu.gerviba.pseudocode.interpreter;

import java.util.List;

import hu.gerviba.pseudocode.compiler.units.Keyword;

public class Line {

	private final int absoluteLine;
	private final int relativeLine;
	private final Keyword keyword;
	private final List<String> arguments;
	
	public Line(int absoluteLine, int relativeLine, Keyword keyword, List<String> arguments) {
		this.absoluteLine = absoluteLine;
		this.relativeLine = relativeLine;
		this.keyword = keyword;
		this.arguments = arguments;
	}

	public int getAbsoluteLine() {
		return absoluteLine;
	}

	public int getRelativeLine() {
		return relativeLine;
	}

	public Keyword getKeyword() {
		return keyword;
	}
	
	public String getArg(int id) {
		return arguments.get(id);
	}
	
	public String getLastNthArg(int n) {
		return arguments.get(arguments.size() - n);
	}
	
	public int getArgsSize() {
		return arguments.size();
	}
	
	public List<String> getArgsFrom(int firstIdToInclide) {
		return arguments.subList(firstIdToInclide, arguments.size());
	}
	
	public String[] rangeToArray(int from, int to) {
		String[] result = new String[to - from];
		for (int i = from; i < to; ++i)
			result[i - from] = arguments.get(i);
		return result;
	}

	@Override
	public String toString() {
		return "Line [absoluteLine=" + absoluteLine + ", relativeLine=" + relativeLine + ", keyword=" + keyword.name() 
				+ ", arguments=" + arguments + "]";
	}

	public String lineAsString() {
		return keyword.name() + " " + String.join(" ", arguments);
	}

	
}
