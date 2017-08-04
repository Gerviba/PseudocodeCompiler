package hu.gerviba.pseudocode.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import hu.gerviba.pseudocode.CoreProcessor;
import hu.gerviba.pseudocode.UnTested;
import hu.gerviba.pseudocode.compiler.builders.CompilerCore;
import hu.gerviba.pseudocode.compiler.units.Keyword;
import hu.gerviba.pseudocode.compiler.units.Operator;
import hu.gerviba.pseudocode.exceptions.CompileException;
import hu.gerviba.pseudocode.lang.PField;
import hu.gerviba.pseudocode.lang.PFieldType;
import hu.gerviba.pseudocode.lang.primitive.PNumeric;

public final class LineUtil {

	@UnTested
	public static void inc(PField field, String[] args) {
		if (!field.getType().equals(PFieldType.NUMERIC.getPrimitiveType()))
			throw new IllegalArgumentException("Only numeric values can be incremented");
		//TODO: fix getValue: ((PNumeric)field.getValue(args)).inc();
	}
	
	@UnTested
	public static void dec(PField field, String[] args) {
		if (!field.getType().equals(PFieldType.NUMERIC.getPrimitiveType()))
			throw new IllegalArgumentException("Only numeric values can be decremented");
		//TODO: fix getValue: ((PNumeric)field.getValue(args)).dec();
	}
	
	private static final List<Character> OPERATORS = Arrays.asList('+', '-', '*', '/', '%', '<', '>', '=', '!', '&', '|', '~', ':', '^');
	
	public static List<String> getLineCommands(String line, CompilerCore core) {
		ArrayList<String> result = new ArrayList<>();
		String buffer = "";
		boolean isString = false;
		
		for (int i = 0; i < line.length(); ++i) {
		    char currentChar = line.charAt(i);
			if (core.isMultilineCommentStarted()) {
				if (currentChar == '*' && line.length() > i && line.charAt(i + 1) == '/') {
					++i;
					core.setMultilineCommentStarted(false);
				}
			} else {
			    if (i == 0) {
                    buffer += currentChar;
                } else if (currentChar == '"') {
					buffer += currentChar;
					if (line.charAt(i - 1) != '\\')
					   isString = !isString;
		        } else if (isString) {
                    buffer += currentChar;
				} else if (currentChar == '/' && line.length() > i) {
					if (line.charAt(i + 1) == '*') {
						core.setMultilineCommentStarted(true);
					} else if (line.charAt(i + 1) == '/') {
						break;
					} else {
						if (!buffer.endsWith(" ") && !buffer.endsWith("\t") 
								&& !OPERATORS.contains(line.charAt(i - 1)) 
								&& currentChar != ':')
	                        buffer += " ";
	                    buffer += currentChar;
	                    if (line.length() > i 
	                            && !OPERATORS.contains(line.charAt(i + 1)) 
	                            && (currentChar != '!' && currentChar != '~'))
	                        buffer += " ";
					}
				} else if (currentChar == '*' && line.length() > i && line.charAt(i + 1) == '/') {
					throw new CompileException(core, "Unexpected symbol: */");
				} else if (currentChar == ';') {
					result.add(buffer.toString());
					buffer = "";
				} else if (currentChar == ' ' || currentChar == '\t') {
					if (!buffer.endsWith(" ") && !buffer.endsWith("\t"))
					   buffer += currentChar;
		        } else if (OPERATORS.contains(currentChar)) {
                    if (!buffer.endsWith(" ") && !buffer.endsWith("\t") 
                    		&& !OPERATORS.contains(line.charAt(i - 1)) 
                    		&& currentChar != ':')
                        buffer += " ";
                    buffer += currentChar;
                    if (line.length() > i 
                            && !OPERATORS.contains(line.charAt(i + 1)) 
                            && (currentChar != '!' && currentChar != '~'))
                        buffer += " ";
                    if (buffer.matches("^.*[\\+\\-\\*\\/\\%\\<\\>\\=\\!\\&\\|\\~]\\-[ \\t]$"))
                        buffer = buffer.substring(0, buffer.length() - 2) + " -";
                    else if (buffer.matches("^.*[\\+\\-\\*\\/\\%\\<\\>\\=\\!\\&\\|\\~\\(][ \\t]\\-[ \\t]$"))
                        buffer = buffer.substring(0, buffer.length() - 2) + "-";
				} else {
				    buffer += currentChar;
				}
			}
		}
		
		if (buffer.length() != 0) {
			result.add(buffer);
		}
		
		return result.stream().map(Operator::applyAliases).collect(Collectors.toList());
	}
	
	public static ArrayList<String> splitLine(String line, CoreProcessor core) {
		ArrayList<String> result = new ArrayList<>();
		String buffer = "";
		boolean isString = false;
		
		for (int i = 0; i < line.length(); ++i) {
			if (line.charAt(i) == '"') {
				if (isString) {
					if (line.charAt(i - 1) != '\\') {
						isString = false;
					}
				} else {
					isString = true;
				}
				buffer += line.charAt(i);
			} else if (isString) {
				buffer += line.charAt(i);
			} else if (line.charAt(i) == Keyword.SEPARATOR.getByCore(core).charAt(0)) {
				result.add(FormatUtil.replaceNotInString(FormatUtil.replaceNotInString(buffer, "]", ")"), "[", "("));
				buffer = "";
			} else {
				buffer += line.charAt(i);
			}
		}
		
		if (buffer.length() != 0) {
			result.add(FormatUtil.replaceNotInString(FormatUtil.replaceNotInString(buffer, "]", ")"), "[", "("));
		}
		
		return result;
	}
//	
//	private static final List<String> MERGE_WITH_THE_FOLLOWING = Arrays.asList("!", "-", "");
//	
//	public static String preformatLine() {
//		
//	}
}
