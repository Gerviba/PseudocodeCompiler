package hu.gerviba.pseudocode.compiler.builders;

import java.util.ArrayList;
import java.util.LinkedList;

import hu.gerviba.pseudocode.compiler.units.Keyword;

public class CallBuilder extends Builder {

	protected CallBuilder(CompilerCore core) {
		super(core, BuilderType.CALL);
	}

	@Override
	protected void build() {
		CalcBuilder defCalc = new CalcBuilder(core).process(core.getCurrentLine());
		LinkedList<String> result = defCalc.getFullResult();
		if (result.getLast().endsWith(Keyword.TEMP.build(core, "$" + core.getCurrentTempId()))) {
			result.set(result.size() - 1, result.getLast().substring(0,
					result.getLast().length() - Keyword.TEMP.build(core, "$" + core.getCurrentTempId()).length() - 1));
			core.breakLastTemp();
		}
		getProgram().appendBody(result);
		
		this.continueProcessing();
	}

	@Override
	public ArrayList<String> getEndPatterns() {
		return null;
	}

}
