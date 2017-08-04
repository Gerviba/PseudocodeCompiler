package hu.gerviba.pseudocode.compiler.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import hu.gerviba.pseudocode.compiler.units.Keyword;
import hu.gerviba.pseudocode.exceptions.CompileException;
import hu.gerviba.pseudocode.utils.FormatUtil;

public class ProgramBuilder extends Builder {
	
	protected static final ArrayList<String> KEYWORDS = new ArrayList<>(
			Arrays.asList("program", "prog", "prog."));
	
	private boolean keyFound = false;
	private int bracketLevel = 0;
	private String buffer = "";
	private String name = "";
	private LinkedList<String> initVars = new LinkedList<>();
	private ArrayList<String> arguments = new ArrayList<>();
	private HashMap<String, LinkedList<String>> methodLines = new HashMap<>();
	
	protected ProgramBuilder(CompilerCore core) {
		super(core, BuilderType.PROGRAM);
		methodLines.put("$", new LinkedList<>());
		appendBody(Keyword.METHOD.build(core, "$"));
	}
	
	public void registerMethod(String methodName, ArrayList<String> arguments) {
		if (methodLines.containsKey(methodName))
			throw new CompileException(core, "Method with this name and arguments is already exists");
		
		methodLines.put(methodName, new LinkedList<>());
		appendBody(Keyword.METHOD.build(core, methodName, arguments));
	}
	
	public void appendBody(String line) {
		MethodBuilder method = getMethod();
		if (core.isDebug()) {
			line = Integer.valueOf(""+core.getAbsoluteLineNumber(), core.getCompileMode().getDebugRadix()) 
					+ Keyword.SEPARATOR.getByCore(core) + line;
		}
		methodLines.get(method == null ? "$" : method.getName()).add(line);
	}
	
	public void appendBody(List<String> lines) {
		for (String line : lines)
			appendBody(line);
	}
	
	public void formatBodyLine(int line, Object... args) {
		MethodBuilder method = getMethod();
		methodLines.get(method == null ? "$" : method.getName()).set(line, 
				String.format(methodLines.get(method == null ? "$" : method.getName()).get(line), args));
	}
	
	public int getCurrentBodyLine() {
		MethodBuilder method = getMethod();
		return methodLines.get(method == null ? "$" : method.getName()).size() - 1;
	}
	
	public LinkedList<String> getFullCode() {
		LinkedList<String> code = new LinkedList<>();
		
		for (LinkedList<String> lines : methodLines.values())
			for (String line : lines)
				code.addLast(line);
		
		code.addLast(Keyword.END.build(core, "0"));
		return code;
	}
	
	@Override
	protected void build() {
		this.onVirtualScopeStarts();
		processBegin();		

		this.continueProcessing();
		this.onVirtualScopeEnds();
		
		appendBody(Keyword.RETURN.build(core));
	}

	private void processBegin() {
		for (int i = 0, len = core.getCurrentLine().length(); i < len; ++i) {
			if (isNotControllChar(i)) {
				buffer += core.getCurrentLine().charAt(i);
			} else {
				if (!keyFound) {
					if (KEYWORDS.contains(buffer.toLowerCase())) {
						keyFound = true;
						buffer = "";
					} else {
						keyFound = true;
						String name = buffer;
						
						buffer = FormatUtil.trim(core.getCurrentLine().substring(i));
						if (buffer.startsWith("(")) 
							buffer = buffer.substring(1);
						else 
							buffer += ")";
						
						++bracketLevel;
						for (String arg : getArgmuents()) {
							if (FormatUtil.isVariableName(arg)) {
								arguments.add(arg);
								registerField(arg);
							} else {
								processArgument(arg);
							}
						}
						setProgramName(name);
						appendBody(initVars);
						
						return;
					}
				} else {
					setProgramName(buffer);
					return;
				}
			}
		}
		
		endBuffer();
	}

	private boolean isNotControllChar(int i) {
		return core.getCurrentLine().charAt(i) != ' ' && 
				core.getCurrentLine().charAt(i) != '(' &&
				core.getCurrentLine().charAt(i) != ')';
	}

	private void processArgument(String arg) {
		if (arg.indexOf('=') == -1)
			throw new CompileException(core, "Invalid program argument");
		
		String definition = FormatUtil.trim(arg.split("\\=", 2)[1]);
		String fieldName = arg.split("\\=", 2)[0].replaceAll("[ \\t\\:]*$", "");
		
		if (!FormatUtil.isVariableName(fieldName))
			throw new CompileException(core, "Invalid field name");
		
		CalcBuilder calc = new CalcBuilder(core).process(definition);
		if (calc.isSimple()) {
			initVars.add(Keyword.VAR.build(core, fieldName, calc.getLastCalculation()));
		} else {
			initVars.addAll(calc.getWithoutLast());
			initVars.add(Keyword.VAR.build(core, fieldName, calc.getLastCalculation()));
		}
		registerField(fieldName);
	}

	private LinkedList<String> getArgmuents() {
		LinkedList<String> args = new LinkedList<>();
		
		String arg = "";
		do {
			for (int i = 0; i < buffer.length(); ++i) {
				if (bracketLevel == 1 && (buffer.charAt(i) == ',' || buffer.charAt(i) == ';')) {
					if (arg.length() != 0) {
						args.add(FormatUtil.trim(arg));
						arg = "";
					}
				} else if (buffer.charAt(i) == '(') {
					++bracketLevel;
					arg += buffer.charAt(i);
				} else if (buffer.charAt(i) == ')') {
					--bracketLevel;
					if (bracketLevel != 0) {
						arg += buffer.charAt(i);
					} else {
						if (arg.length() != 0)
							args.add(FormatUtil.trim(arg));
						return args;
					}
				} else {
					arg += buffer.charAt(i);
				}
			}
			
			if (arg.length() != 0) {
				args.add(FormatUtil.trim(arg));
				arg = "";
			}
			buffer = core.getNextLine();
		} while(bracketLevel > 0);
		
		return args;
	}

	private void endBuffer() {
		if (buffer.length() != 0) {
			if (!keyFound) {
				if (KEYWORDS.contains(buffer.toLowerCase())) {
					keyFound = true;
					buffer = "";
				} else {
					keyFound = true;
					setProgramName(buffer);
				}
			} else if (name.length() == 0) {
				setProgramName(buffer);
			} else {
				System.err.println("maradt: "+buffer); // TODO: Mivan ha marad valami benne? elvileg nem szabadna
				throw new CompileException(core, "WTF happend?");
			}
		}
	}
	
	private void setProgramName(String name) {
		if (!FormatUtil.isVariableName(name))
			throw new CompileException(core, "Invalid program name format");
		if (!core.canUseProgramName(name, arguments.size()))
			throw new CompileException(core, "Duplication of program name and arguments size");
		
		if (arguments.size() == 0)
			core.appendBody(Keyword.PROG.build(core, name + "$0"));
		else
			core.appendBody(Keyword.PROG.build(core, name + "$" + arguments.size(), arguments));
		
		this.name = name + "$" + arguments.size();
		setItToDefault();
	}
	
	private void setItToDefault() {
		if (core.getDefaultProgramName() == null)
			core.setDefaultProgramName(name);
	}

	private static final ArrayList<String> END_PATTERNS = new ArrayList<>(Arrays.asList(
			"^(?i)program[ \\t]*v[\\É\\ée]ge$", //TODO: Merge with the following line
			"^prog\\.*[ \\t]*v[\\É\\ée]ge$",
			"^(?i)elj([\\Á\\áa]r[\\Á\\áa]s)?\\.?[ \\t]*v[\\É\\ée]ge\\.?$"
			));
	
	@Override
	public ArrayList<String> getEndPatterns() {
		return END_PATTERNS;
	}

	
}
