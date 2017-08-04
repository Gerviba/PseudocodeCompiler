package hu.gerviba.pseudocode.func;

import hu.gerviba.pseudocode.lang.primitive.PPrimitiveValue;

public interface PFunction {

	public boolean isArgCountAccepted(int num);
	public PPrimitiveValue process(PPrimitiveValue... args);
	
}
