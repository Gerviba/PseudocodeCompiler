package hu.gerviba.pseudocode.interpreter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import hu.gerviba.pseudocode.CoreProcessor;
import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;
import hu.gerviba.pseudocode.compiler.units.Pair;
import hu.gerviba.pseudocode.exceptions.ExecutionException;
import hu.gerviba.pseudocode.lang.PProgram;
import hu.gerviba.pseudocode.lang.Scope;
import hu.gerviba.pseudocode.lang.complex.ComplexRegistry;
import hu.gerviba.pseudocode.lang.complex.PArrayField;
import hu.gerviba.pseudocode.lang.primitive.PPrimitiveValue;
import hu.gerviba.pseudocode.streams.PStream;

public final class ApplicationProcessor implements CoreProcessor {
	
	public static void warn(String warning) {
		System.out.println("[WARNING] "+warning);
	}

	public static boolean isDebugMode() {
		return instance.meta.get("DEBUG").equals("1");
	}
	
	public static boolean shouldReportWarings() {
		return !instance.meta.containsKey("WARNS") || instance.meta.get("WARNS").equals("1");
	}

	private static int scopeId = 0;
	
	private static ApplicationProcessor instance;
	
	public static ApplicationProcessor getInstance() {
		return instance;
	}	
	
	private int programStartsAt;
	private int returnValue = 0;
	private boolean running = true;
	private CompileMode mode;
	private String main;
	private PProgram lastRegisteredProgram;
	private ComplexRegistry complexRegistry;

	private List<String> rawLines;
	private LinkedList<Line> lines;
	
	private final HashMap<String, String> meta = new HashMap<>();
	private final HashMap<String, PProgram> programs = new HashMap<>();
	private final HashMap<Integer, PStream> streams = new HashMap<>();
	private final HashMap<String, Integer> labels = new HashMap<>();

	// Lehet, hogy ConcurrentLinkedDeque az ideális
	private final LinkedList<Scope> scopeStack = new LinkedList<>();


	public ApplicationProcessor(CompileMode cm) {
		instance = this;
		this.mode = cm;
	}
	
	public ApplicationProcessor loadFromString(String lines) {
		rawLines = Arrays.asList(lines.split(Pattern.quote(mode.getLineSeparator())));
		return this;
	}
	
	public ApplicationProcessor loadFromList(List<String> lines) {
		rawLines = lines;
		return this;
	}
	
	/**
	 * @next {@link #loadPrograms()});
	 */
	public ApplicationProcessor loadHeader() {
		HeadProcessor header = new HeadProcessor(this);
		header.process();
		this.programStartsAt = header.getProgramStartLine();
		
		if (!meta.containsKey("DEBUG"))
			meta.put("DEBUG", "0");
		if (meta.get("DEBUG").equals("1") && !meta.containsKey("ORIGINAL_CODE"))
            throw new ExecutionException("Debug mode enabled but no ORIGINAL_CODE meta found");
		
		return this;
	}
	
	/**
	 * @next {@link #runProgram()} or ({@link #prepareToDebug()} then {@link #debugProgram()})
	 */
	public ApplicationProcessor loadPrograms() {
		loadBasicComplexClasses();
		BodyPreprocessor body = new BodyPreprocessor(this, this.programStartsAt);
		body.process();
		lines = body.getLines();
		
		return this;
	}
	
	private void loadBasicComplexClasses() {
		complexRegistry = new ComplexRegistry();
		complexRegistry.registerComplex("PArray", (name, args) -> {
			List<Pair<Integer, Integer>> ranges = new ArrayList<>();
			for (int i = 0; i < args.size(); i += 2)
				ranges.add(new Pair<Integer, Integer>(Integer.parseInt(args.get(i)), Integer.parseInt(args.get(i + 1))));
				
			return PArrayField.newArray(name, ranges);
		});
	}

	public synchronized void runProgram() {
		scopeStack.add(new Scope(this, programs.get(main)));
		while (running && scopeStack.size() > 0 && lines.size() > scopeStack.getLast().getLineId()) {
			getDeepestScope().processLine();
			if (scopeStack.size() > 0)
				getDeepestScope().nextLine();
		}
	}
	
	public synchronized void prepareToDebug() {
		scopeStack.add(new Scope(this, programs.get(main)));
	}
	
	public synchronized boolean debugProgram() {
		if (running && scopeStack.size() > 0 && lines.size() > scopeStack.getLast().getLineId()) {
			getDeepestScope().processLine();
			getDeepestScope().nextLine();
			return true;
		}
		return false;
	}

	public void callExit(PPrimitiveValue p) {
		this.running = false;
		System.out.println("EndCalled");
		this.returnValue = (int) p.asNumeric().asLong();
		//System.exit((int) p.asNumeric().asLong());
	}
	
	public void registerLabel(String link, int line) {
		labels.put(link, line);
	}

	public int getLabelTarget(String link) {
		if (labels.containsKey(link))
			return labels.get(link);
		throw new ExecutionException("Invalid label target: " + link);
	}
	
	@Override
	public CompileMode getCompileMode() {
		return mode;
	}
	
	public void addMeta(String key, String value) {
		meta.put(key, value);
	}
	
	public String getMeta(String key) {
		return meta.get(key);
	}
	
	public String getMetaSafe(String key) {
		return meta.containsKey(key) ? meta.get(key) : "n/a";
	}
	
	public void registerStream(int id, PStream stream) {
		streams.put(id, stream);
	}

	public PStream getStream(int id) {
		return streams.get(id);
	}

	public String getRawLine(int line) {
		return rawLines.get(line);
	}
	
	public Line getLine(int line) {
		return lines.get(line);
	}

	public void setDefaultProgramName(String name) {
		main = name;
	}

	public int getRawLineCount() {
		return rawLines.size();
	}

	public String getDefaultProgramName() {
		return main;
	}

	public void registerProgram(String name, PProgram program) {
		programs.put(name, program);
		lastRegisteredProgram = program;
	}
	
	public PProgram getProgram(String name) {
		return programs.get(name);
	}

	public PProgram getLastRegisteredProgram() {
		return lastRegisteredProgram;
	}
	
	public void addScope(Scope scope) {
		scopeStack.addFirst(scope);
	}
	
	public void returnScope() {
		scopeStack.removeFirst();
	}

	public Scope getDeepestScope() {
		return scopeStack.getFirst();
	}

	public Scope findLastDefaultScope() {
		for (Scope s : scopeStack) {
			if (s.getMethod().getName().equals("$"))
				return s;
		}
		return null;
	}
	
	/**
	 * TODO: Ez így veszélyes is lehet. Lehet clonozni kéne. (encapsulation)
	 */
	public LinkedList<Scope> getScopeStack() {
		return scopeStack;
	}

	public LinkedList<Line> getLines() {
		return lines;
	}
	
	public ComplexRegistry getComplexRegistry() {
		return complexRegistry;
	}

	public int getReturnValue() {
		return returnValue;
	}
	
	
}
