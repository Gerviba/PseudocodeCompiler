package hu.gerviba.pseudocode.compiler;

import hu.gerviba.pseudocode.compiler.builders.CompilerCore;
import hu.gerviba.pseudocode.compiler.io.ExportMethod;
import hu.gerviba.pseudocode.compiler.io.ImportMethod;
import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;

public class Compiler {

	private final CompilerCore core;
	private final ImportMethod input;
	private final ExportMethod export;
	
	public Compiler(ImportMethod input, ExportMethod export, CompileMode mode) {
		this.core = new CompilerCore(mode);
		this.input = input;
		this.export = export;
	}
	
	public void start() {
		core.loadLines(input.read())
			.loadDirectives()
			.initHeader()
			.startCompile();
		
		export.export(core.getFullCompiledCode());
	}
	
}
