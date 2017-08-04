package hu.gerviba.pseudocode.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import hu.gerviba.pseudocode.UnTested;
import hu.gerviba.pseudocode.compiler.units.Pair;
import hu.gerviba.pseudocode.lang.primitive.PNumeric;

public final class FormatUtil {

	public static final String[] FULL_OP_LIST = { 
			"+", "-", "*", "/", "%", 
			"^", "&", "|", "~", "&&",
			"||", ">", ">=", "<", "<=",
			">>", "<<", ">>>", "==", "!=" };
	private static final String[] LOGICAL_VALUES = {"igaz", "true", "1", "hamis", "false", "0"};
	
	private static final String INTEGER_PATTERN = "^(([\\-]?00?)|([\\-]?[1-9]+[0-9]{0,18})"
			+ "|(0[xX][0-9a-fA-F]{1,16})|(0[bB][01]{1,64})|(0[1-7]{1,24}))$";
	private static final String REAL_PATTERN = "^\\-?(0|([1-9][0-9]*)|(0\\.[0-9]+)"
			+ "|([1-9]*[0-9]*\\.[0-9]+))([eE]\\-?[0-9]{1,4})?$";
	private static final String DIRECTIVE_PATTERN = "^[ \\t]*\\#.*$";
	private static final String BLANK_LINE_PATTERN = "^[ \\t]*$";
	private static final String VAR_NAME_PATTERN = "^[A-Za-z\\_\\á\\é\\í\\ó\\ö\\ő\\ú\\ü\\ű\\Á\\É\\Í\\Ó\\Ö\\Ő\\Ú\\Ü\\Ű]"
			+ "[A-Za-z0-9\\_\\á\\é\\í\\ó\\ö\\ő\\ú\\ü\\ű\\Á\\É\\Í\\Ó\\Ö\\Ő\\Ú\\Ü\\Ű]*$";
	private static final String ARRAY_NAME_DEFINITION_PATTERN = 
			"^[A-Za-z\\_\\á\\é\\í\\ó\\ö\\ő\\ú\\ü\\ű\\Á\\É\\Í\\Ó\\Ö\\Ő\\Ú\\Ü\\Ű]"
			+ "[A-Za-z0-9\\_\\á\\é\\í\\ó\\ö\\ő\\ú\\ü\\ű\\Á\\É\\Í\\Ó\\Ö\\Ő\\Ú\\Ü\\Ű]*[ \\t]*[\\(\\[].+$";
	private static final String ARRAY_DEFINITION_PATTERN = "^(?i)(t[Ööo]mb)?[\\(\\[].*[\\)\\]]$";
	
	
	public static boolean isInteger(String value) {
		return value.matches(INTEGER_PATTERN);
	}
	
	public static boolean isReal(String value) {
		return value.matches(REAL_PATTERN);
	}
	
	public static boolean isDirective(String value) {
		return value.matches(DIRECTIVE_PATTERN);
	}
	
	public static boolean isBlankLine(String value) {
		return value.matches(BLANK_LINE_PATTERN);
	}
	
	public static boolean isLogical(String value) {
		value = value.toLowerCase();
		for (String s : LOGICAL_VALUES) {
			if (s.equals(value))
				return true;
		}
		return false;
	}
	
	public static boolean isLogical(PNumeric value) {
		if (value.isRealType()) {
			return Math.floor(value.asDouble()) == 0D || Math.floor(value.asDouble()) == 1D;
		}
		return value.asLong() == 0L || value.asLong() == 1L;
	}
	
	public static boolean isNumeric(String number) {
		return isInteger(number) || isReal(number);
	}


	public static boolean isTrue(String value) {
		return value.equalsIgnoreCase("igaz") || value.equals("1") || 
				value.equalsIgnoreCase("igen") || value.equalsIgnoreCase("true");
	}
	
	public static String trim(String raw) {
		return raw.replaceAll("^[ \\t]*", "").replaceAll("[ \\t]*$", "");
		
	}

	/**
	 * TODO: Exponent support
	 */
	public static boolean isSimple(String calculation) {
		if (calculation.startsWith("\"") && calculation.endsWith("\"")) {
			if (charCount(calculation, '"') != 2) 
				return false;
			return true;
		} else if (charCount(calculation, '"') != 0) {
			return false;
		} else if (calculation.startsWith("-") 
				&& charCount(calculation.substring(1), '-') == 0 
				&& calculation.substring(1).matches("^[ ]?[0-9\\.]+$")) { // NOTE: Talán a HEX is lehet - (NEM!!! NEM LEHET!!!)
			return true;
		} else if (calculation.startsWith("-") 
				|| calculation.startsWith("!") 
				|| calculation.startsWith("~")) {
			return false;
		} else if (calculation.matches("[A-Za-z0-9\\.\\_\\á\\é\\í\\ó\\ö\\ő\\ú\\ü\\ű\\Á\\É\\Í\\Ó\\Ö\\Ő\\Ú\\Ü\\Ű]+")
				&& charCount(calculation, '.') <= 1) {
			return true;
		}
		
		return false;
	}
	
	private static int charCount(String in, char chr) {
		int count = 0;
		for (char c : in.toCharArray()) {
			if (c == chr)
				++count;
		}
		return count;
	}
	
	public static boolean isVariableName(String definition) {
		return definition.matches(VAR_NAME_PATTERN);
	}
	
	public static boolean isArrayNameDefinition(String definition) {
		return definition.matches(ARRAY_NAME_DEFINITION_PATTERN);
	}
	
	public static boolean isArrayDefinition(String definition) {
		return definition.matches(ARRAY_DEFINITION_PATTERN);
	}
	
	@UnTested
	public static boolean isImport(String string) {
		return false;
	}

	@UnTested
	public static String merge(LinkedList<String> parts) {
		StringBuilder sb = new StringBuilder();
		parts.forEach(x -> sb.append(x + " "));
		return sb.toString();
	}
	
	@UnTested
	public static boolean hasOperator(LinkedList<String> parts) {
		for (int i = 0; i < parts.size(); ++i) {
			String part = parts.get(i);
			for (int op = 0; op < FULL_OP_LIST.length; ++op) {
				if (part.startsWith(FULL_OP_LIST[op]))
					return true;
			}
		}
		return false;
	}

	public static String replaceNotInString(String input, String from, String to) {
		List<Pair<String, Boolean>> result = new ArrayList<>();
		boolean stringStarted = false;
		String buffer = "";
		
		for (int i = 0; i < input.length(); ++i) {
			buffer += input.charAt(i);
			if (input.charAt(i) == '"' && !buffer.endsWith("\\")) {
				result.add(new Pair<String, Boolean>(buffer, stringStarted));
				stringStarted = !stringStarted;
				buffer = "";
			}
		}
		
		result.add(new Pair<String, Boolean>(buffer, stringStarted));
		
		return String.join("", result.stream().map(x -> x.getValue() 
				? x.getKey() 
				: x.getKey().replace(from, to)).collect(Collectors.toList()));
	}

	public static String replaceAllNotInString(String input, String from, String to) {
		List<Pair<String, Boolean>> result = new ArrayList<>();
		boolean stringStarted = false;
		String buffer = "";
		
		for (int i = 0; i < input.length(); ++i) {
			buffer += input.charAt(i);
			if (input.charAt(i) == '"' && !buffer.endsWith("\\")) {
				result.add(new Pair<String, Boolean>(buffer, stringStarted));
				stringStarted = !stringStarted;
				buffer = "";
			}
		}
		
		result.add(new Pair<String, Boolean>(buffer, stringStarted));
		
		return String.join("", result.stream().map(x -> x.getValue() 
				? x.getKey() 
				: x.getKey().replaceAll(from, to)).collect(Collectors.toList()));
	}
	
	public static String formatSimpleValue(String calculation) {
		return calculation.length() > 1 && calculation.charAt(1) == ' ' 
				? calculation.charAt(0) + calculation.substring(2)
				: calculation;
	}
	
	public static boolean isValidOperation(String line) {
		line = trim(line.split("\\=", 2)[0].replaceAll("[ \\t\\:]*$", ""));
		
		if (line.startsWith("(") || line.startsWith("[")) // TODO: Itt kezdődhet [-el? 
			return false;
		
		int level = 0;
		boolean stringStarted = false;
		for (int i = 0; i < line.length(); ++i) {
			if (stringStarted) {
				if (line.charAt(i) == '"') {
					if (line.charAt(i - 1) != '\\')
						stringStarted = false;
				}
			} else {
				if (line.charAt(i) == '"') {
					stringStarted = true;
				} else if (line.charAt(i) == '(' || line.charAt(i) == '[') {
					++level;
				} else if (line.charAt(i) == ')' || line.charAt(i) == ']') {
					--level;
					if (level == 0 && i != line.length() -1)
						return false;
				} else if (level == 0 && line.charAt(i) == ' ') {
					return false;
				}
			}
		}
		return level == 0;
	}
	
	public static String removeComplexArgCount(String fieldName) {
		return fieldName.matches("^\\$\\d+$") ? fieldName : fieldName.replaceAll("\\$\\d+$", "");
	}

	public static String getRawArrayName(String fieldName) {
		return fieldName.replace("[", "(").substring(
				fieldName.charAt(0) == '-' || 
				fieldName.charAt(0) == '!' || 
				fieldName.charAt(0) == '~' 
					? 1 : 0, 
				fieldName.indexOf('(') == -1 
					? fieldName.length() : fieldName.indexOf('('));
	}
	
}
