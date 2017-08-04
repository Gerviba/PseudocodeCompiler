package hu.gerviba.pseudocode.compiler.modifiers;

public enum CompileInfo {
	ALL(Integer.MAX_VALUE),
	FEW(1),
	NO(0);
	
	private final int priority;
	
	private CompileInfo(int priority) {
		this.priority = priority;
	}
	
	public boolean canUse(CompileInfo min) {
		return priority >= min.priority;
	}
	
}
