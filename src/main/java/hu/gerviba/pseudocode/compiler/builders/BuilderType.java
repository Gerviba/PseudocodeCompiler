package hu.gerviba.pseudocode.compiler.builders;

import static hu.gerviba.pseudocode.utils.FormatUtil.isValidOperation;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Function;

public enum BuilderType {
	IF(IfBuilder.class, new String[] {"^(?i)HA[ \\t].+AKKOR$"}),
	LOOP(LoopBuilder.class, new String[] {
			"^(?i)((CIKLUS\\.?)|(CIKLUS[ \\t]+((AM[\\Í\\íi\\É\\ée]G)|(AMEDDIG)).+)|(CIKLUS.+\\-.+))$"}),
	IO(IOBuilder.class, new String[] {
			"^(?i)KI[ \\t]*\\:[ \\t]*.+","^(?i)OUT[ \\t]*\\:[ \\t]*.+",
			"^(?i)BE[ \\t]*\\:[ \\t]*.+", "^(?i)IN[ \\t]*\\:[ \\t]*.+"}),
	OPERATION(OperationBuilder.class, line -> {
		return line.matches("^(?i)((CONST )|(KONSTANS ))?[ \\t]*"
				+ "[A-Za-z\\_\\á\\é\\í\\ó\\ö\\ő\\ú\\ü\\ű\\Á\\É\\Í\\Ó\\Ö\\Ő\\Ú\\Ü\\Ű]"
				+ ".*[ \\t]*\\:?\\=\\ *.+$") && isValidOperation(line);
	}),
	METHOD(MethodBuilder.class, new String[] {
			"^(?i)(((ELJ([\\Á\\áa]R[\\Á\\áa]S)?\\.?)|(F[\\Ü\\üu]GGV([\\É\\ée]NY)?\\.?)"
			+ "|(MET[\\Ó\\óo]DUS\\.?))[ \\t]*)((?!v([\\É\\ée]ge)?).)*$"}),
	BREAK(BreakBuilder.class, new String[] {"^(?i)T[\\Ö\\öo]R([\\É\\ée]S)?.*"}),
	CONTINUE(BreakBuilder.class, new String[] {"^(?i)FOLYTAT([\\Á\\áa]S)?.*"}),
	RETURN(ReturnBuilder.class, new String[] {"^(?i)((visszat[Éée]r)|(eredm[Éée]ny))[ \\t]*.+$"}),
	CASE(CaseBuilder.class, new String[] {""}), // Eljárás
	PROGRAM(ProgramBuilder.class), 
	CALL(CallBuilder.class, line -> {
		return !matches(IfBuilder.END_PATTERNS, line) && isValidOperation(line);
	}),
	;
	
	private final Class<? extends Builder> builder;
	private final Function<String, Boolean> matcher;
	
	private BuilderType(Class<? extends Builder> builder, final String[] startRegEx) {
		this.builder = builder;
		this.matcher = startRegEx == null ? null : line -> {
			for (String regEx : startRegEx)
				if (line.matches(regEx))
					return true;
			return false;
		};
	}
	
	private BuilderType(Class<? extends Builder> builder, Function<String, Boolean> matcher) {
		this.builder = builder;
		this.matcher = matcher;
	}
	
	private BuilderType(Class<? extends Builder> builder) {
		this.builder = builder;
		this.matcher = null;
	}

	public static void callNextBuilder(CompilerCore core) {
		if (core.getCurrentLine() == null)
			return;
		for (BuilderType builder : values()) {
			if (builder.matcher == null)
				continue;
		
			if (builder.matcher.apply(core.getCurrentLine())) {
				try {
					((Builder) builder.builder
							.getDeclaredConstructor(CompilerCore.class)
							.newInstance(core)).build();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e.getMessage());
				}
				return;
			}
		
		}
		
		for (String regEx : core.getCurrentBuilder().getEndPatterns()) {
			if (core.getCurrentLine().matches(regEx))
				return;
		}
		
		System.err.println("Cannot identify this line: " + core.getCurrentLine());
	}
	
	private static boolean matches(List<String> regExes, String line) {
		for (String regEx : regExes)
			if (line.matches(regEx))
				return true;
		return false;
	}
	
}
