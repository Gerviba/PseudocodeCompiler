package hu.gerviba.pseudocode.func;

import hu.gerviba.pseudocode.lang.primitive.PNumeric;
import hu.gerviba.pseudocode.lang.primitive.PPrimitiveValue;

@FunctionRegisterer(path = "Math", name = "sin")
public final class FuncSin implements PFunction {

	@Override
	public boolean isArgCountAccepted(int num) {
		return num == 1;
	}

	@Override
	public PPrimitiveValue process(PPrimitiveValue... args) {
		return new PNumeric(Math.sin(args[0].asNumeric().asDouble()));
	}

}
