package hu.gerviba.pseudocode.func;

import hu.gerviba.pseudocode.lang.primitive.PNumeric;
import hu.gerviba.pseudocode.lang.primitive.PPrimitiveValue;

@FunctionRegisterer(path = "Math", name = "pow", usage = {"base", "exponent"})
public final class FuncPow implements PFunction {

	@Override
	public boolean isArgCountAccepted(int num) {
		return num == 2;
	}

	@Override
	public PPrimitiveValue process(PPrimitiveValue... args) {
		return new PNumeric(Math.pow(args[0].asNumeric().asDouble(), args[1].asNumeric().asDouble()));
	}
	
}
