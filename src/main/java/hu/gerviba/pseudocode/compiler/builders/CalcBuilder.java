package hu.gerviba.pseudocode.compiler.builders;

import static hu.gerviba.pseudocode.utils.FormatUtil.hasOperator;
import static hu.gerviba.pseudocode.utils.FormatUtil.replaceAllNotInString;
import static hu.gerviba.pseudocode.utils.FormatUtil.replaceNotInString;
import static hu.gerviba.pseudocode.utils.FormatUtil.formatSimpleValue;

import java.util.LinkedList;

import hu.gerviba.pseudocode.compiler.units.Keyword;
import hu.gerviba.pseudocode.compiler.units.Operator;
import hu.gerviba.pseudocode.exceptions.CompileException;
import hu.gerviba.pseudocode.utils.FormatUtil;

public class CalcBuilder {

//	public static void main(String[] args) {
//		System.out.println("Loaded, starts...");
//
///* @formatter:off */
//		
//		// String s = "A + B * C / -D + E * F + G - H + ~I >> !A % 2";
//		// String s = "A + ( B * C / -D + \"String\" ) + ( ( V + X + \"We\\\"re\" ) * sin(E + 32,V * D + ( M * L ),fx(I)) * F + ( U + Y ) ) + G - H + ~I >> !A % 2 + ~( ASD * BSD )";
//
//		String s = "A + ( B * C / -D + \"String\" ) + ( ( V + X + \"We\\\"re\" ) * -s(sajt + sajt,1 + fx(1 * tenyér)) * F + ( U + Y ) ) + G - H + ~I >> !A % 2 + ~( ASD * BSD )";
//
//		System.out.println(s);
//		LinkedList<String> parts = split(s);// new LinkedList<>(Arrays.stream(s.split(" ")).collect(Collectors.toList()));
//
///* @formatter:on */
//		
//		// System.out.println(merge(parts));
//
//		CalcBuilder builder = new CalcBuilder(1000);
//		System.out.println("\n\nFinal:\n" + builder.build(parts));
//		// System.out.println(builder.processBrackets(parts));
//	}

	private LinkedList<String> code = new LinkedList<>();
	private final CompilerCore core;
	private boolean isSimple = false;
	private int lastTempVar = -1;
	
	protected CalcBuilder(CompilerCore core) {
		this.core = core;
	}
	
	public LinkedList<String> getFullResult() {
		return code;
	}
	
	public LinkedList<String> getWithoutLast() {
		return new LinkedList<String> (code.subList(0, Math.max(0, code.size() - 1)));
	}
	
	public String getLastCalculation() {
		if (isSimple)
			return code.get(code.size() - 1);
		core.breakLastTemp();
		return code.get(code.size() - 1)
				.substring((Keyword.TEMP.getByCore(core) 
						+ "$" + (core.getCurrentTempId() + 1) 
						+ Keyword.SEPARATOR.getByCore(core)).length());
	}
	
	public boolean isSimple() {
		return isSimple;
	}

	public boolean isLastCall() {
		return code.getLast().startsWith(Keyword.CALL.getByCore(core));
	}
	
	public String getLastTempVar() {
		if (lastTempVar == -1)
			throw new CompileException(core, "You must run process() before getLastTempVar()");
		return "$" + lastTempVar;
	}
	
	public CalcBuilder process(String calculation) {
		calculation = replaceAllNotInString(replaceAllNotInString(calculation, 
				"[ \\t]\\=\\=", " ="), "[ \\t]=", " ==");
		calculation = replaceNotInString(replaceNotInString(calculation, "[", "("), "]", ")");
		calculation = replaceAllNotInString(calculation, "[ \\t]*\\)[ \\t]*\\([ \\t]*", ",");
		if (FormatUtil.isSimple(calculation)) {
			isSimple = true;
			code.add(formatSimpleValue(calculation));
		} else {
			isSimple = false;
			build(split(calculation));
			lastTempVar = core.getCurrentTempId();
		}
		return this;
	}
	
	private CalcBuilder build(LinkedList<String> parts) {
		int bracketPos;
		while ((bracketPos = getInnerBracket(parts)) != -1) {
			if (parts.get(bracketPos + 2).equals(")")) {
				parts.set(bracketPos, parts.get(bracketPos + 1));
				parts.remove(bracketPos + 1);
				parts.remove(bracketPos + 1);
				continue;
			}
			
			LinkedList<String> bracket = new LinkedList<String>();
			int size = bracketPos + 1;
			while (!parts.get(size).equals(")")) {
				bracket.add(parts.get(size));
				++size;
			}
			int bracketSize = bracket.size() + 1;

			if (parts.get(bracketPos).equals("(")) {
				String lastVar = processBracket(bracket);
				parts.set(bracketPos, lastVar);
				for (int i = 0; i < bracketSize; ++i)
					parts.remove(bracketPos + 1);
				
			} else if (parts.get(bracketPos).equals("!(")) {
				String lastVar = processBracket(bracket);
				code.add(Keyword.TEMP.build(core, "$" + core.requestNewTempId(), lastVar, Operator.NOT));
				parts.set(bracketPos, "$" + core.getCurrentTempId());
				for (int i = 0; i < bracketSize; ++i)
					parts.remove(bracketPos + 1);
				
			} else if (parts.get(bracketPos).equals("~(")) {
				String lastVar = processBracket(bracket);
				code.add(Keyword.TEMP.build(core, "$" + core.requestNewTempId(), lastVar, Operator.BNEG));
				parts.set(bracketPos, "$" + core.getCurrentTempId());
				for (int i = 0; i < bracketSize; ++i)
					parts.remove(bracketPos + 1);
			}

		}

		if (parts.size() > 1) {
			processBracket(parts);
		} else if (parts.size() == 1) {
			if (parts.getFirst().indexOf('(') != -1 && !parts.getFirst().matches("^[\\!\\~\\-]*\\($")) {
				LinkedList<String> args = splitArgs(parts.getFirst());
				StringBuilder arguments = new StringBuilder(" ");
				for (String arg : args) {
					LinkedList<String> split = split(arg);
					if (split.size() == 1 && split.get(0).indexOf('(') == -1) {
						arguments.append(split.get(0) + Keyword.SEPARATOR.getByCore(core));
					} else {
						build(split(arg));
						arguments.append("$" + core.getCurrentTempId() + Keyword.SEPARATOR.getByCore(core));
					}
				}
				code.add(Keyword.CALL.getByCore(core) +
						getMethodName(parts.getFirst()) +
						"$" + args.size() +
						arguments.toString() +
						Keyword.TEMP.getByCore(core) + "$" +
						core.requestNewTempId());
				
			} else {
				if (parts.getFirst().startsWith("-")
						|| parts.getFirst().startsWith("!")
						|| parts.getFirst().startsWith("~"))
					attachPreoperatorKeyword(parts); // TODO: itt még lehet baj a negált függvényértékekből
			}
		}
			
			
		return this;
	}

	private void attachPreoperatorKeyword(LinkedList<String> parts) {
		code.add(Keyword.TEMP.build(core, 
				"$" + core.requestNewTempId(), //TODO: rm this comment, getCurrentTempId
				parts.getFirst().substring(1),
				Operator.getPreoperatorBySign(parts.getFirst().substring(0, 1))));
	}
	
	private String processBracket(LinkedList<String> parts) {
		//TODO: Clean it
		COMPRESS: while (hasOperator(parts) && parts.size() > 1) {
			int pos = getLeftStartsOf(parts, new String[] { "!", "~", "-" });
			if (pos != -1) {
				if (parts.get(pos).indexOf('(') != -1 && !parts.get(pos).matches("^[\\!\\~\\-]*\\($")) {
					LinkedList<String> args = splitArgs(parts.get(pos));
					StringBuilder arguments = new StringBuilder(" ");
					for (String arg : args) {
						LinkedList<String> split = split(arg);
						if (split.size() == 1 && split.get(0).indexOf('(') == -1) {
							arguments.append(split.get(0) + Keyword.SEPARATOR.getByCore(core));
						} else {
							build(split(arg));
							arguments.append("$" + core.getCurrentTempId() + Keyword.SEPARATOR.getByCore(core));
						}
					}
					long called = core.requestNewTempId();
					
					code.add(Keyword.CALL.getByCore(core) +
							getMethodName(parts.get(pos)) +
							"$" + args.size() +
							arguments.toString() +
							Keyword.TEMP.getByCore(core) + "$" +
							core.getCurrentTempId());
					
					code.add(Keyword.TEMP.build(core, 
							"$" + core.requestNewTempId(),
							"$" + called +
							arguments.toString() +
							Operator.getBySign(parts.get(pos).substring(0, 1))));
					
					parts.set(pos, "$" + core.getCurrentTempId());
				} else {
					if (parts.getFirst().startsWith("-")
							|| parts.getFirst().startsWith("!")
							|| parts.getFirst().startsWith("~"))
						attachPreoperatorKeyword(parts);

					parts.set(pos, "$" + core.getCurrentTempId());
				}
				continue;
			}

			for (String[] ops : new String[][] { 
    				{ ">>", "<<", ">>>", "%" },
    				{ "*", "/" },
    				{ "+", "-" },
    				{ "&", "|" },
    				{ "==", "!=", "<", "<=", ">", ">=" },
    				{ "&&", "||", "^" } 
    				}) { // TODO: Függvényeknél goto-s kizárás, sztem új design kell hozzá
				
				pos = getLeftOf(parts, ops);
				if (pos != -1) {
					String left = parts.get(pos - 1), right = parts.get(pos + 1);
					
					if (parts.get(pos - 1).indexOf('(') != -1 && !parts.get(pos - 1).matches("^[\\!\\~\\-]*\\($")) {
						LinkedList<String> args = splitArgs(parts.get(pos - 1));
						StringBuilder arguments = new StringBuilder(" ");
						for (String s : args) {
							LinkedList<String> split = split(s);
							if (split.size() == 1 && split.get(0).indexOf('(') == -1) {
								arguments.append(split.get(0)+" ");
							} else {
								build(split(s));
								arguments.append("$" + core.getCurrentTempId() + Keyword.SEPARATOR.getByCore(core));
							}
						}
						
						code.add(Keyword.CALL.getByCore(core) + 
								getMethodName(parts.get(pos - 1)) +
								"$" + args.size() +
								arguments.toString() + 
								Keyword.TEMP.getByCore(core) + "$" +
								core.requestNewTempId());
						left = "$" + core.getCurrentTempId();

					}
					
					if (parts.get(pos + 1).indexOf('(') != -1 && !parts.get(pos + 1).matches("^[\\!\\~\\-]*\\($")) {
						LinkedList<String> args = splitArgs(parts.get(pos + 1));
						StringBuilder arguments = new StringBuilder(" ");
						for (String s : args) {
							LinkedList<String> split = split(s);
							if (split.size() == 1 && split.get(0).indexOf('(') == -1) {
								arguments.append(split.get(0) + Keyword.SEPARATOR.getByCore(core));
							} else {
								build(split(s));
								arguments.append("$" + core.getCurrentTempId()  +" ");
							}
						}
						
						code.add(Keyword.CALL.getByCore(core) + 
								getMethodName(parts.get(pos + 1)) +
								"$" + args.size() +
								arguments.toString() + 
								Keyword.TEMP.getByCore(core) + "$" +
								core.requestNewTempId());
						right = "$" + core.getCurrentTempId();
					}
					
					code.add(Keyword.TEMP.build(core, 
							"$" + core.requestNewTempId(), left, 
							Operator.getBySign(parts.get(pos)), right));
					
					parts.set(pos - 1, "$" + core.getCurrentTempId());
					parts.remove(pos);
					parts.remove(pos);

					continue COMPRESS;
				}
			}
		}

		return parts.getFirst();
	}

	private static int getLeftOf(LinkedList<String> parts, String[] operators) {
		for (int i = 0; i < parts.size(); ++i) {
			String part = parts.get(i);
			for (String op : operators) {
				if (part.equals(op))
					return i;
			}
		}
		return -1;
	}

	private static int getLeftStartsOf(LinkedList<String> parts, String[] operators) {
		for (int i = 0; i < parts.size(); ++i) {
			String part = parts.get(i);
			if (part.length() <= 1)
				continue;
			for (String op : operators) {
				if (part.startsWith(op) && !part.startsWith(op + "="))
					return i;
			}
		}
		return -1;
	}

	private static LinkedList<String> split(String str) {
		int level = 0;
		boolean string = false;
		LinkedList<String> result = new LinkedList<>();
		StringBuilder sb = new StringBuilder();
		
		for (char currentChar : str.toCharArray()) {
			if (level == 0) {
				if (currentChar == ' ') {
					if (string) {
						sb.append(currentChar);
					} else {
						if (sb.length() > 0)
							result.addLast(sb.toString());
						sb = new StringBuilder("");
					}
				} else if (currentChar == '(') {
					sb.append(currentChar);
					if (!sb.substring(0, 1).equals("(") && (sb.length() < 2 || (!sb.substring(0, 2).equals("!(")
							&& !sb.substring(0, 2).equals("~(") && !sb.substring(0, 1).equals("!(")))) {
						++level;
					}
				} else if (currentChar == '"') {
					if (string) {
						if (sb.charAt(sb.length() - 1) == '\\') {
							sb.setCharAt(sb.length() - 1, '"');
						} else {
							sb.append('"');
							result.addLast(sb.toString());
							sb = new StringBuilder("");
							string = false;
						}
					} else {
						string = true;
						if (sb.length() > 0)
							result.addLast(sb.toString());
						sb = new StringBuilder("\"");
					}
				} else {
					sb.append(currentChar);
				}
			} else {
				sb.append(currentChar);
				if (currentChar == '(') {
					++level;
				} else if (currentChar == ')') {
					--level;
				}
				if (level == 0) {
					result.addLast(sb.toString());
					sb = new StringBuilder("");
				}
			}
		}

		if (sb.length() > 0)
			result.addLast(sb.toString());

		return result;
	}

	public static String getMethodName(String block) {
		return block.substring(
				block.charAt(0) == '-' || 
				block.charAt(0) == '!' || 
				block.charAt(0) == '~' 
					? 1 : 0, 
				block.indexOf('(') == -1 
					? block.length() : block.indexOf('('));
	}

	private static LinkedList<String> splitArgs(String block) {
		int level = 0;
		LinkedList<String> result = new LinkedList<>();
		StringBuilder sb = new StringBuilder();
		
		for (char currentChar : block.substring(block.indexOf('(') + 1, block.length() - 1).toCharArray()) {
			if (currentChar == ',' && level == 0) {
				result.addLast(sb.toString());
				sb = new StringBuilder();
				continue;
			}
			
			sb.append(currentChar);
			if (currentChar == '(') {
				++level;
			} else if (currentChar == ')') {
				--level;
			}
		}
		if (sb.length() > 0)
			result.addLast(sb.toString());

		return result;
	}
	
	private static int getInnerBracket(LinkedList<String> parts) {
		int level = 0;
		int maxLevel = 0;
		int maxPos = -1;
		
		for (int i = 0; i < parts.size(); ++i) {
			if (parts.get(i).endsWith("(")) {
				++level;
				if (level > maxLevel) {
					maxLevel = level;
					maxPos = i;
				}
			} else if (parts.get(i).equals(")")) {
				--level;
			}
		}
		return maxPos;
	}
	
}
