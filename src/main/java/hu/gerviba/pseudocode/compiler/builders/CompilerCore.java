package hu.gerviba.pseudocode.compiler.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import hu.gerviba.pseudocode.CoreProcessor;
import hu.gerviba.pseudocode.PSeudocodeRuntimeEnvironment;
import hu.gerviba.pseudocode.compiler.modifiers.CompileInfo;
import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;
import hu.gerviba.pseudocode.compiler.units.Keyword;
import hu.gerviba.pseudocode.compiler.units.Pair;
import hu.gerviba.pseudocode.compiler.units.RelativeLine;
import hu.gerviba.pseudocode.exceptions.CompileException;
import hu.gerviba.pseudocode.streams.PConsoleStream;
import hu.gerviba.pseudocode.streams.PStream;
import hu.gerviba.pseudocode.streams.PStream.IOStreamState;
import hu.gerviba.pseudocode.utils.FormatUtil;
import hu.gerviba.pseudocode.utils.LineUtil;

public final class CompilerCore implements CoreProcessor {

	static enum Directive {
		DEBUG("^[ \\t]*\\#[ \\t]*\\@?(?i)Debug[ \\t]+",
				"^[ \\t]*\\#[ \\t]*\\@?(?i)Debug[ \\t]+(ON|OFF|1|0)[ \\t]*$") {
			final LinkedList<Pair<String, Boolean>> ALLOWED = Pair.<Pair<String, Boolean>>asList(
					new Pair<String, Boolean>("ON", true), 
					new Pair<String, Boolean>("OFF", false),
					new Pair<String, Boolean>("1", true),
					new Pair<String, Boolean>("0", false));
 
			@Override
			protected void process(CompilerCore core, String command) {
				final String cmd = command.replaceAll(regExPreCheck, "").replaceAll("[ \\t]*$", "");
				core.debug = ALLOWED.stream().filter(x -> x.getKey().equalsIgnoreCase(cmd))
						.limit(1)
						.findFirst()
						.get()
						.getValue();
			}
		},
		AUTHOR("^[ \\t]*\\#[ \\t]*\\@?(?i)Author[ \\t]+",
				"^[ \\t]*\\#[ \\t]*\\@?(?i)Author[ \\t]+[A-Za-z0-9_\\-\\ \\@]+[ \\t]*$") {
			@Override
			protected void process(CompilerCore core, String command) {
				core.author = command.replaceAll(regExPreCheck, "").replaceAll("[ \\t]*$", "");
			}
		},
		VERSION("^[ \\t]*\\#[ \\t]*\\@?(?i)Version[ \\t]+",
				"^[ \\t]*\\#[ \\t]*\\@?(?i)Version[ \\t]+[0-9\\.absr]+[ \\t]*$") {
			@Override
			protected void process(CompilerCore core, String command) {
				core.version = command.replaceAll(regExPreCheck, "").replaceAll("[ \\t]*$", "");
			}
		},
		DESCRIPTION("^[ \\t]*\\#[ \\t]*\\@?(?i)Description[ \\t]+",
				"^[ \\t]*\\#[ \\t]*\\@?(?i)Description[ \\t]+.+[ \\t]*$") {
			@Override
			protected void process(CompilerCore core, String command) {
				core.description = command.replaceAll(regExPreCheck, "").replaceAll("[ \\t]*$", "");
			}
		},
		COMPILEINFO("^[ \\t]*\\#[ \\t]*\\@?(?i)CompileInfo[ \\t]+",
				"^[ \\t]*\\#[ \\t]*\\@?(?i)CompileInfo[ \\t]+(ALL|FEW|NO)[ \\t]*$") {
			final LinkedList<Pair<String, CompileInfo>> ALLOWED = Pair.<Pair<String, CompileInfo>>asList(
					new Pair<String, CompileInfo>("ALL", CompileInfo.ALL),
					new Pair<String, CompileInfo>("FEW", CompileInfo.FEW),
					new Pair<String, CompileInfo>("NO", CompileInfo.NO));

			@Override
			protected void process(CompilerCore core, String command) {
				final String cmd = command.replaceAll(regExPreCheck, "").replaceAll("[ \\t]*$", "");
				core.compileInfo = ALLOWED.stream().filter(x -> x.getKey().equalsIgnoreCase(cmd))
						.limit(1)
						.findFirst()
						.get()
						.getValue();
			}
		},
		PSRE_MIN("^[ \\t]*\\#[ \\t]*\\@?(?i)PSRE\\-Min[ \\t]+",
				"^[ \\t]*\\#[ \\t]*\\@?(?i)PSRE\\-Min[ \\t]+[0-9\\.absr]+[ \\t]*$") {
			@Override
			protected void process(CompilerCore core, String command) {
				core.psreMin = command.replaceAll(regExPreCheck, "").replaceAll("[ \\t]*$", "");
			}
		},
		META("^[ \\t]*\\#[ \\t]*\\@?(?i)Meta[ \\t]+",
				"^[ \\t]*\\#[ \\t]*\\@?(?i)Meta[ \\t]+[A-Za-z0-9\\-]{1,64}[ \\t]*\\=[ \\t]*.+[ \\t]*$") {
			@Override
			protected void process(CompilerCore core, String command) {
				String[] parts = (command = command.replaceAll(regExPreCheck, "")
						.replaceAll("[ \\t]*$", ""))
						.split("[ \\t]*\\=[ \\t]*", 2);
				if (parts.length != 2) {
					throw new CompileException(core, "Invalid meta format: '" + command + "' @see PS-TOPIC-0#7");
				}
				core.unique.add(new Pair<String, String>(parts[0], parts[1]));
			}
		};

		public static final ArrayList<String> META_KEYS = new ArrayList<String>(Arrays.asList(
				"DEBUG", "AUTHOR", "VERSION", "DESC", "PSRE_MIN", "ORIGINAL_CODE",
				"CT", "CV", "OS", "OA", "OV", "JV", "VE"));
		
		final String regExPreCheck;
		final String regExStrckCheck;

		Directive(String regExPreCheck, String regExStrckCheck) {
			this.regExPreCheck = regExPreCheck;
			this.regExStrckCheck = regExStrckCheck;
		}

		boolean identify(CompilerCore core, String command) {
			if (command.matches(regExPreCheck+".+$")) {
				if (command.matches(regExStrckCheck)) {
					process(core, command);
					++core.currentRelativeLine;
					return true;
				}
				throw new CompileException(core, "Invalid directive value: '" + command + "'");
			}

			return false;
		}

		protected abstract void process(CompilerCore core, String command);
	}

	public static final class StreamComponent {
		final int id;
		final PStream stream;
		private boolean readRequest = false;
		private boolean writeRequest = false;
		
		public StreamComponent(int id, PStream stream) {
			this.id = id;
			this.stream = stream;
		}
	
		public int getId() {
			return id;
		}
	
		public PStream getStream() {
			return stream;
		}
		
		public StreamComponent requestState(IOStreamState state) {
			if (state.canRead())
				readRequest = true;
			if (state.canWrite())
				writeRequest = true;
			return this;
		}
		
		public IOStreamState getState() {
			return IOStreamState.getByRights(readRequest, writeRequest);
		}
	}
	
	private int tempVariableId = 0;
	private int streamId = 0;
	private long labelIdentifier = 0;

	private StringBuilder head;
	private LinkedList<String> body;
	private LinkedList<RelativeLine> lines;
	private int currentRelativeLine = 0;
	private boolean multilineCommentStarted = false;
	private LinkedList<Builder> builders = new LinkedList<>();
	private ArrayList<String> programNames = new ArrayList<>();

	// COMPILE INFO BLOCK:
	private boolean debug = true;
	private final CompileMode mode;
	private CompileInfo compileInfo = CompileInfo.ALL;
	private String author = System.getProperty("user.name");
	private String version = PSeudocodeRuntimeEnvironment.VERSION;
	private String description = null;
	private String psreMin = PSeudocodeRuntimeEnvironment.VERSION;
	private List<Pair<String, String>> unique = new ArrayList<>();
	private String defaultProgramName = null;
	private HashMap<String, StreamComponent> streamStorage = new HashMap<>();
	
	public CompilerCore(CompileMode cm) {
		this.mode = cm;
	}

	/**
	 * @param lines Preformatted lines
	 * @next loadDirectives
	 */
	public synchronized CompilerCore loadLines(List<String> lines) {
		this.lines = new LinkedList<RelativeLine>();
		int i = 0;
		for (String l : lines) {
			++i;
			for (String command : LineUtil.getLineCommands(l, this)) {
				if (!FormatUtil.isBlankLine(command))
					this.lines.add(new RelativeLine(i, FormatUtil.trim(command)));
			}
		}
		
		return this;
	}
	
	/**
	 * @next initHeader()
	 */
	public synchronized CompilerCore loadDirectives() {
		for (int i = 0; i < lines.size(); ++i) {
			if (FormatUtil.isBlankLine(lines.get(i).getCommand())) {
				++currentRelativeLine;
				continue;
			}
			if (FormatUtil.isDirective(lines.get(i).getCommand())) {
				if (!identifyDirective(i))
					throw new CompileException(this, "Invalid directive '" + lines.get(i).getCommand() + "'");
				continue;
			}
			if (FormatUtil.isImport(lines.get(i).getCommand())) {
				++currentRelativeLine;
				//TODO: Implement
				continue;
			}
		}
		
		return this;
	}

	private boolean identifyDirective(int lineNumer) {
		for (Directive d : Directive.values())
			if (d.identify(this, lines.get(lineNumer).getCommand()))
				return true;
		return false;
	}
	
	/**
	 * @next startCompile()
	 */
	public synchronized CompilerCore initHeader() {
		head = new StringBuilder(128);
		head.append(mode == CompileMode.FULLY_COMPRESSED ? 
				"#!/bin/psre -f\n" : 
				"#!/bin/psre\n");
		
		attachDebug();
		attachDefaultInfo();

		if (compileInfo != CompileInfo.NO)
			appendCompileInfo();
		
		if (unique != null)
			attachUniqueMeta();

		initStreams();
		
		return this;
	}

	private void attachDebug() {
		if (debug) {
			head.append(Keyword.META.build(this, "DEBUG", toBase64("1") + mode.getLineSeparator()));
			head.append(Keyword.META.build(this, "ORIGINAL_CODE", toBase64(
					String.join(mode.getLineSeparator(), lines.stream()
					.map(x -> x.getCommand()).collect(Collectors.toList())))
					+ mode.getLineSeparator()));
		} else {
			head.append(Keyword.META.build(this, "DEBUG", toBase64("0") + mode.getLineSeparator()));
		}
	}

	private void attachDefaultInfo() {
		head.append(Keyword.META.build(this, "AUTHOR", toBase64(author) + mode.getLineSeparator()));
		head.append(Keyword.META.build(this, "VERSION", toBase64(version) + mode.getLineSeparator()));
		
		if (description != null && description.length() > 0)
			head.append(Keyword.META.build(this, "DESC", toBase64(description) + mode.getLineSeparator()));
		head.append(Keyword.META.build(this, "MIN_PSRE", toBase64(psreMin) + mode.getLineSeparator()));
	}

	private void attachUniqueMeta() {
		for (Pair<String, String> meta : unique) {
			if (!Directive.META_KEYS.contains(meta.getKey().toUpperCase()))
				head.append(Keyword.META.build(this, meta.getKey(), 
						toBase64(meta.getValue()) + mode.getLineSeparator()));
		}
	}

	private void initStreams() {
		PStream cli = new PConsoleStream();
		streamStorage.put(cli.getPrefix(), newStreamComponent(cli).requestState(IOStreamState.BOTH));
	}
	
	private String toBase64(String input) {
		return Base64.getEncoder().encodeToString(input.getBytes());
	}

	/**
	 * @next Use {@link #getCompiledBody()} and {@link #getCompiledHead()} 
	 */
	public synchronized void startCompile() {
		body = new LinkedList<String>();
		if (getCurrentLine() != null)
			try {
				do {
					ProgramBuilder program = new ProgramBuilder(this);
					program.build();
					for (String str : program.getFullCode())
						body.addLast(str);
				} while(getNextLine() != null);
			} catch (Exception e) {
				if (e instanceof RuntimeException)
					throw e;
				throw new RuntimeException(e);
			}
		
		attachStreams();
		attachStart();
	}
	
	private void appendCompileInfo() {
		if (compileInfo.canUse(CompileInfo.FEW)) {
			appendHead(Keyword.META.build(this, "CT", toBase64(""+System.currentTimeMillis())));
		}
		if (compileInfo.canUse(CompileInfo.ALL)) {
			appendHead(Keyword.META.build(this, "CV", toBase64(PSeudocodeRuntimeEnvironment.VERSION)));
			appendHead(Keyword.META.build(this, "OS", toBase64(System.getProperty("os.name"))));
			appendHead(Keyword.META.build(this, "OA", toBase64(System.getProperty("os.arch"))));
			appendHead(Keyword.META.build(this, "OV", toBase64(System.getProperty("os.version"))));
			appendHead(Keyword.META.build(this, "JV", toBase64(System.getProperty("java.version"))));
			appendHead(Keyword.META.build(this, "VE", toBase64(System.getProperty("java.vendor"))));
		}
	}
	
	private void attachStreams() {
		for (StreamComponent storage : streamStorage.values()) {
			appendHead(Keyword.STREAM.build(this, 
					storage.getId(), 
					storage.getState().getId(), 
					storage.getStream().getClass().getName(), 
					storage.getStream().getOption()));
		}
	}
	
	private void attachStart() {
		appendHead(Keyword.START.build(this, defaultProgramName));
	}
	
	public String getNextLine() {
		return lines.size() <= ++currentRelativeLine ? null : lines.get(currentRelativeLine).getCommand();
	}

	public String getCurrentLine() {
		return lines.size() <= currentRelativeLine ? null : lines.get(currentRelativeLine).getCommand();
	}

	public void appendHead(String str) {
		head.append(str + mode.getLineSeparator());
	}

	protected void appendBody(String str) {
		body.addLast((debug ? Integer.valueOf(""+getAbsoluteLineNumber(), mode.getDebugRadix()) 
				+ Keyword.SEPARATOR.getByCore(this) : "") + str);
	}

	protected void appendBody(LinkedList<String> strList) {
		for (String str : strList)
			appendBody(str);
	}

	public void registerProgram(String name, int arguments) {
		programNames.add(name + "$" + arguments);
	}
	
	public boolean canUseProgramName(String name, int arguments) {
		return !programNames.contains(name + "$" + arguments);
	}
	
	public boolean isDebug() {
		return debug;
	}
	
	@Override
	public CompileMode getCompileMode() {
		return mode;
	}

	public int getAbsoluteLineNumber() {
		return lines.get(currentRelativeLine).getAbsoluteLine();
	}
	
	public int getRelativeLineNumber() {
		return currentRelativeLine;
	}

	public boolean isMultilineCommentStarted() {
		return multilineCommentStarted;
	}

	public void setMultilineCommentStarted(boolean multilineCommentStarted) {
		this.multilineCommentStarted = multilineCommentStarted;
	}

	public void addCurrentBuilder(Builder builder) {
		builders.add(builder);
	}

	public void removeLastBuilder() {
		builders.removeLast();
	}
	
	public LinkedList<Builder> getAllBuilders() {
		return builders;
	}
	
	public Builder getCurrentBuilder() {
		return builders.getLast();
	}

	public String getCompiledHead() {
		return head.toString();
	}
	
	public String getCompiledBody() {
		return String.join(mode.getLineSeparator(), body);
	}

	public String getCodeLines() {
		return String.join(mode.getLineSeparator(), lines.stream().map(x -> x.getCommand()).collect(Collectors.toList()));
	}
	
	public String getFullCompiledCode() {
		return this.getCompiledHead() + this.getCompiledBody();
	}

	public String getDefaultProgramName() {
		return defaultProgramName;
	}

	public void setDefaultProgramName(String defaultProgramName) {
		this.defaultProgramName = defaultProgramName;
	}

	public HashMap<String, StreamComponent> getStreamStorage() {
		return streamStorage;
	}

	public StreamComponent newStreamComponent(PStream stream) {
		return new StreamComponent(streamId++, stream);
	}

	public long requestNewLabel() {
		return labelIdentifier++;
	}
	
	public long requestNewTempId() {
		return ++tempVariableId;
	}
	
	public int getCurrentTempId() {
		return tempVariableId;
	}

	public void breakLastTemp() {
		--tempVariableId;
	}

}
