package hu.gerviba.pseudocode.compiler.builders;

import java.util.ArrayList;

import hu.gerviba.pseudocode.compiler.units.Keyword;

public abstract class Builder {

	private static long UID_COUNTER = 0;
	
	protected final CompilerCore core;
	protected final long uid;
	protected final BuilderType type;
	protected ArrayList<String> varsToDelete; // A Unittestek miatt nem lehet Set
	
	protected Builder(CompilerCore core, BuilderType type) {
		this.core = core;
		this.type = type;
		this.uid = UID_COUNTER++;
	}
	
	protected void onVirtualScopeStarts() {
		varsToDelete = new ArrayList<>();
		core.addCurrentBuilder(this);
	}
	
	protected void onVirtualScopeEnds() {
		varsToDelete.stream().forEach(var -> getProgram().appendBody(Keyword.FREE.build(core, var)));
		varsToDelete.clear();
		core.removeLastBuilder();
	}
	
	protected final void freeAllVariables() {
		varsToDelete.stream().forEach(var -> getProgram().appendBody(Keyword.FREE.build(core, var)));
		varsToDelete.clear();
	}
	
	protected final void continueProcessing() {
		core.getNextLine();
		BuilderType.callNextBuilder(core);
	}
	
	public final BuilderType getType() {
		return type;
	}
	
	protected abstract void build();
	
	public abstract ArrayList<String> getEndPatterns();
	
	protected void registerField(String fieldName) {
		if (!core.getCurrentBuilder().varsToDelete.contains(fieldName))
			core.getCurrentBuilder().varsToDelete.add(fieldName);
	}
	
	protected boolean isFieldAvailable(String fieldName) {
		return core.getAllBuilders().stream().anyMatch(x -> x.varsToDelete.contains(fieldName));
	}
	
	protected MethodBuilder getMethod() {
		for (Builder b : core.getAllBuilders()) {
			if (b.getType() == BuilderType.METHOD)
				return (MethodBuilder) b;
		}
		return null;
	}
	
	protected ProgramBuilder getProgram() {
		for (Builder b : core.getAllBuilders()) {
			if (b.getType() == BuilderType.PROGRAM)
				return (ProgramBuilder) b;
		}
		return null;
	}
	
}
