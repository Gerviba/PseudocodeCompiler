package hu.gerviba.pseudocode.compiler.builders;

import java.util.ArrayList;

import hu.gerviba.pseudocode.compiler.units.Keyword;
import hu.gerviba.pseudocode.exceptions.CompileException;

public class BreakBuilder extends Builder {

	protected BreakBuilder(CompilerCore core) {
		super(core, BuilderType.BREAK);
	}

	@Override
	protected void build() {
		Builder builder = null;
		for (Builder b : core.getAllBuilders()) {
			if (b.getType() == BuilderType.LOOP)
				builder = b;			
		}
		
		if (builder == null)
			throw new CompileException(core, "Breaks must be inside a loop");
		if (!core.getCurrentLine().matches("^(?i)t[\\Ö\\öo]r([\\É\\ée]s)?$"))
			throw new CompileException(core, "Breaks mustn't have any argmunets");
		
		getProgram().appendBody(Keyword.GOTO.build(core, "%s"));
		((LoopBuilder)builder).setContainsBreak(getProgram().getCurrentBodyLine());
		
		this.continueProcessing();
	}

	@Override
	public ArrayList<String> getEndPatterns() {
		return null;
	}

}
