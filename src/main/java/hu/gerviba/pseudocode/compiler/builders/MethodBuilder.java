package hu.gerviba.pseudocode.compiler.builders;

import static hu.gerviba.pseudocode.utils.FormatUtil.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import hu.gerviba.pseudocode.compiler.units.Keyword;
import hu.gerviba.pseudocode.exceptions.CompileException;

public class MethodBuilder extends Builder {

	protected static final String KEYWORD_PATTERN = 
			"^(?i)((ELJ([\\Á\\áa]R[\\Á\\áa]S)?)|(F[\\Ü\\üu]GGV([\\É\\ée]NY))|(MET[\\Ó\\óo]DUS))$";
	
	private String name = "";
	private LinkedList<String> initVars = new LinkedList<>();
	private ArrayList<String> arguments = new ArrayList<>();
	
	private String buffer = "";
	private boolean keyFound = false;
	private int bracketLevel = 0;
	
	protected MethodBuilder(CompilerCore core) {
		super(core, BuilderType.METHOD);
	}

	@Override
	protected void build() {
		this.onVirtualScopeStarts();
		processBegin();
		getProgram().registerMethod(name + "$" + arguments.size(), arguments);
		getProgram().appendBody(initVars);
		registerFields();
		
		this.continueProcessing();
		this.onVirtualScopeEnds();
		this.continueProcessing();
	}

	@Override
	protected void onVirtualScopeEnds() {
		this.freeAllVariables();
		getProgram().appendBody(Keyword.RETURN.build(core));
		core.removeLastBuilder();
	}

	private void registerFields() {
		arguments.stream().forEach(this::registerField);
	}
	
	private void processBegin() {
		for (int i = 0, len = core.getCurrentLine().length(); i < len; ++i) {
			if (isNotControllerChar(i)) {
				buffer += core.getCurrentLine().charAt(i);
			} else {
				if (!keyFound) {
					if (buffer.matches(KEYWORD_PATTERN)) {
						keyFound = true;
						buffer = "";
					} else {
						keyFound = true;
						String name = buffer;
						
						fixBrackets(i);
						processAllArguments();
						this.name = name;
						
						return;
					}
				} else if ("".equals(name)) {
					name = buffer;
					buffer = "";
					fixBrackets(i);
					processAllArguments();
					return;
				}
			}
		}
		
		endBuffer();
	}

	private void fixBrackets(int i) {
		buffer = trim(core.getCurrentLine().substring(i));
		buffer = buffer.startsWith("(") ? buffer.substring(1) : buffer + ")";
	}

	private boolean isNotControllerChar(int i) {
		return core.getCurrentLine().charAt(i) != ' ' && 
				core.getCurrentLine().charAt(i) != '(' &&
				core.getCurrentLine().charAt(i) != ')';
	}
	
	private void endBuffer() {
		if (buffer.length() != 0) {
			if (!keyFound) {
				if (buffer.matches(KEYWORD_PATTERN)) {
					keyFound = true;
					buffer = "";
				} else {
					keyFound = true;
					this.name = buffer;
				}
			} else if (name.length() == 0) {
				this.name = buffer;
			} else {
				processAllArguments();
			}
		}
	}
	
	private void processAllArguments() {
		++bracketLevel;
		for (String arg : getArgmuents()) {
			if (isVariableName(arg)) {
				arguments.add(arg);
				registerField(arg);
			} else {
				processArgument(arg);
			}
		}
	}

	private void processArgument(String arg) {
		if (arg.indexOf('=') == -1)
			throw new CompileException(core, "Invalid method argument '" + arg + "'");
		
		String definition = trim(arg.split("\\=", 2)[1]);
		String fieldName = arg.split("\\=", 2)[0].replaceAll("[ \\t\\:]*$", "");
		
		if (!isVariableName(fieldName))
			throw new CompileException(core, "Invalid field name '" + fieldName + "'");
		
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
						args.add(trim(arg));
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
							args.add(trim(arg));
						return args;
					}
				} else {
					arg += buffer.charAt(i);
				}
			}
			
			if (arg.length() != 0) {
				args.add(trim(arg));
				arg = "";
			}
			buffer = core.getNextLine();
		} while(bracketLevel > 0);
		
		return args;
	}

	private static final ArrayList<String> END_PATTERNS = 
			new ArrayList<>(Arrays.asList(
					"^(?i)elj([\\Á\\áa]r[\\Á\\áa]s)?\\.?[ \\t]*v[\\É\\ée]ge\\.?$",
					"^(?i)f[\\Ü\\üu]ggv[\\É\\ée]ny\\.?[ \\t]*v[\\É\\ée]ge\\.?$",
					"^(?i)met[\\Ó\\óo]dus\\.?[ \\t]*v[\\É\\ée]ge\\.?$"
					));

	@Override
	public ArrayList<String> getEndPatterns() {
		return END_PATTERNS;
	}

	public String getName() {
		return name + "$" + arguments.size();
	}

}
