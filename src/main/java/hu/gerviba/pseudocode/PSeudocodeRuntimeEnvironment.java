package hu.gerviba.pseudocode;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import hu.gerviba.pseudocode.compiler.Compiler;
import hu.gerviba.pseudocode.compiler.io.Base64Input;
import hu.gerviba.pseudocode.compiler.io.ExportMethod;
import hu.gerviba.pseudocode.compiler.io.FileInput;
import hu.gerviba.pseudocode.compiler.io.FileOutput;
import hu.gerviba.pseudocode.compiler.io.ImportMethod;
import hu.gerviba.pseudocode.compiler.io.MemoryOutput;
import hu.gerviba.pseudocode.compiler.io.StandardInput;
import hu.gerviba.pseudocode.compiler.io.StandardOutput;
import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;
import hu.gerviba.pseudocode.interpreter.ApplicationProcessor;

public final class PSeudocodeRuntimeEnvironment {

	private static PSeudocodeRuntimeEnvironment instance;
	
	public static PSeudocodeRuntimeEnvironment getInstance() {
		return instance;
	}
	
	public static final String VERSION = "1.0.512";
	public static boolean NO_GUI = false;
	public static boolean JAILED = false;
	public static boolean DEBUG = false;
	
	public static void main(String[] in) {
        List<String> args = Arrays.asList(in);
        
        printDebugInfo(args);
        printAfterInstallInfo(args);
    	printHelp(args);
        indentifyFlags(args);
        printVersionInfo(args);
        
        PSeudocodeRuntimeEnvironment runtime = new PSeudocodeRuntimeEnvironment(args);
        runtime.process();
        
        printInvalidUsage();
    }
	
	private ImportMethod input = null;
	private ExportMethod output = null;
	private Object middle = null;
	private List<String> args;
	
	private PSeudocodeRuntimeEnvironment(List<String> args) {
		this.args = args;
	}

	private void process() {
		identifyInput();
		identifyOutput();        
		processCompiler();
		
		updateIO();
		printFileInfo();
		processRun();
		processDebug();
	}

	private void printFileInfo() {
		if (args.contains("-i") || args.contains("--info")) {
			ApplicationProcessor processor = new ApplicationProcessor(getCompileMode());
			processor.loadFromList(input.read());
			processor.loadHeader();
			
			System.out.println("                     Author : " + processor.getMetaSafe("AUTHOR"));
			System.out.println("                    Version : " + processor.getMetaSafe("VERSION"));
			System.out.println("                Description : " + processor.getMetaSafe("DESC").replace("<br>", "\n"));
			System.out.println("           Min PSRE Version : " + processor.getMetaSafe("MIN_PSRE"));
			System.out.println("                Compiled at : " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.parseLong(processor.getMetaSafe("CT"))));
			System.out.println("           Compiler version : " + processor.getMetaSafe("CV"));
			System.out.println("         Compiler's OS name : " + processor.getMetaSafe("OS"));
			System.out.println("      Compiler's OS version : " + processor.getMetaSafe("OV"));
			System.out.println("        Compiler's OS arch. : " + processor.getMetaSafe("OA"));
			System.out.println("    Compiler's Java version : " + processor.getMetaSafe("JV"));
			System.out.println("     Compiler's Java vendor : " + processor.getMetaSafe("VE"));
			
			System.exit(0);
		}
	}


	private void processCompiler() {
		if (isPlannedToCompile()) {    	
        	Compiler compiler = new Compiler(input, output, getCompileMode());
        	compiler.start();
        	
        	if (!isPlannedToRun() && !isPlannedToDebug()) {
        		System.exit(0);
        	}
        }
	}
	
	private void processRun() {
		if (isPlannedToRun()) {    	
        	ApplicationProcessor processor = new ApplicationProcessor(getCompileMode());
        	processor.loadFromList(input.read());
    		processor.loadHeader().loadPrograms().runProgram();
    		System.exit(processor.getReturnValue());
        }
	}

	private CompileMode getCompileMode() {
		return args.contains("-f") ? CompileMode.FULLY_COMPRESSED : CompileMode.SEMI_COMPRESSED;
	}

	private void processDebug() {
		System.out.println("Debugging is not completely implemented yet."); //TODO: Implement debugging
		System.exit(0);
	}
	
	private boolean isPlannedToCompile() {
		return args.contains("-c") || args.contains("--compile") 
				|| args.contains("-cr") || args.contains("-rc")
				|| args.contains("-cd") || args.contains("-dc");
	}

	private boolean isPlannedToRun() {
		return (args.contains("-r") || args.contains("--run") 
				|| args.contains("-cr") || args.contains("-rc")) ||
				(!isPlannedToDebug() && !isPlannedToCompile());
	}
	
	private boolean isPlannedToDebug() {
		return args.contains("-d") || args.contains("--debug") 
				|| args.contains("-cd") || args.contains("-dc");
	}
	
	private void identifyInput() {
		if (args.contains("--input-file")) {
			input = new FileInput(args.get(args.indexOf("--input-file") + 1));
		} else if (args.contains("--input-base64")) {
			input = new Base64Input(args.get(args.indexOf("--input-base64") + 1));
		} else if (args.contains("--input-stdins")) {
			input = new StandardInput();
		} else {
			String path = findPath();
			if (path != null) {
				input = new FileInput(path);
				return;
			}
			
			System.err.println("Please enter an input method");
			printInvalidUsage();
		}
	}

	private String findPath() {
		for (int i = 0; i < args.size(); ++i)
			if (!args.get(i).startsWith("-") && (i == 0 || !args.get(i - 1).startsWith("--output")))
				return args.get(i);
		return null;
	}

	private void identifyOutput() {
		if (args.contains("--output-file")) {
			output = new FileOutput(args.get(args.indexOf("--output-file") + 1));
		} else if (args.contains("--output-stdout")) {
			output = new StandardOutput();
		}
		
		if (isPlannedToRun() || isPlannedToDebug()) {
			middle = output;
			output = new MemoryOutput();
			
			if (middle == null)
				middle = output;
		}
	}
	
	private void updateIO() {
		if (middle != null && isPlannedToCompile()) {
			input = (MemoryOutput) output;
			output = (ExportMethod) middle;
		}
	}

	private static void printVersionInfo(List<String> args) {
		if (args.contains("-v") || args.contains("--version")) {
			System.out.println("# PSeudoCode Runtime Environment");
			System.out.println("# Version: " + VERSION + " | Author: Szabó Gergely (Gerviba)");
			System.exit(0);
		}
		if (args.contains("--pure-version")) {
			System.out.println(VERSION);
			System.exit(0);
		}
	}	
	
	private static void printAfterInstallInfo(List<String> args) {
		if (args.contains("--after-install")) {
        	System.out.println("[i] Successfully installed, version: " + VERSION);
        	System.exit(0);
		}
	}
	
	private static void printHelp(List<String> args) {
		if (args.contains("--help") || args.contains("-h") || args.contains("?")) {
			System.out.println(" ");
			System.out.println("# PSeudoCode Runtime Environment");
			System.out.println("# Version: " + VERSION + " | Author: Szabó Gergely (Gerviba)");
			System.out.println("# Usage: psre [file] [Options and IO Options]");
			System.out.println(" ");
			System.out.println("Options: ");
			System.out.println(" -c, --compile           Compiles the input code");
			System.out.println(" -r, --run               Runs the compiled source (*1)");
			System.out.println(" -d, --debug             Debugs the compiled source");
			System.out.println(" -s, --semi-compressed   Runs with semi-compressed mode (default)");
			System.out.println(" -f, --fully-compressed  Runs with fully-compressed mode (beta)");
			System.out.println(" -h, --help              Shows this help");
			System.out.println(" -i, --info              Shows the info of the specified pseudo file");
			System.out.println(" -v, --version           Shows the version of the psre");
			System.out.println(" --nogui                 Disables GUI features");
			System.out.println(" --jailed                Allows all IO streams in its own directory");
			System.out.println("                         but denies all other IO streams");
			System.out.println(" ");
			System.out.println("IO Options: ");
			System.out.println(" --input-file       Sets the input source as file");
			System.out.println(" --input-base64     Sets the input source as Base64 code");
			System.out.println(" --input-stdin      Sets the input source as standard input");
			System.out.println(" --output-file      Sets the output source as file");
			System.out.println(" --output-stdout    Sets the output source as standard output");
			System.out.println(" ");
			System.out.println("*1: Optional if `-s` or `-f` attached");
			System.exit(0);
		}
	}
	
	private static void printInvalidUsage() {
		System.out.println("Type: `psre --help` to show usage");
		System.exit(-1);
	}
	
	private static void printDebugInfo(List<String> args) {
		if (args.contains("--psre-debug")) {
			DEBUG = true;
        	System.out.println("Args: " + args);
		}
	}

	private static void indentifyFlags(List<String> args) {
		if (args.contains("--nogui"))
        	NO_GUI = true;
        
        if (args.contains("--jailed"))
        	JAILED = true;
	}
}
