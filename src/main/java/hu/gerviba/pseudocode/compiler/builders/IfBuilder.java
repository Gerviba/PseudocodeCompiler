package hu.gerviba.pseudocode.compiler.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import hu.gerviba.pseudocode.compiler.units.Keyword;
import hu.gerviba.pseudocode.exceptions.CompileException;
import hu.gerviba.pseudocode.utils.BuilderUtil;

public class IfBuilder extends Builder {
	
	private static final ArrayList<String> ELSE_PATTERNS = new ArrayList<>(Arrays.asList(
			"^(?i)k[\\Ü\\üu]l[\\Ö\\öo]nben$", 
			"^(?i)egy[\\É\\ée]bk[\\É\\ée]nt$", 
			"^(?i)am[\\Ú\\úu]gy$"));
	
	private static final ArrayList<String> IF_END_PATTERNS = new ArrayList<>(Arrays.asList(
			"^(?i)el([\\Á\\áa]g(az[\\Á\\áa]s)?)?\\.?[ \\t]*v([\\É\\ée]ge)?\\.?$"));
	
	static final ArrayList<String> END_PATTERNS = new ArrayList<>(Arrays.asList(
			"^(?i)k[\\Ü\\üu]l[\\Ö\\öo]nben.*", 
			"^(?i)egy[\\É\\ée]bk[\\É\\ée]nt.*", 
			"^(?i)am[\\Ú\\úu]gy.*",
			"^(?i)el([\\Á\\áa]g(az[\\Á\\áa]s)?)?\\.?[ \\t]*v([\\É\\ée]ge)?\\.?$"));
	
	private LinkedList<Integer> blockEndLabelLines = new LinkedList<>();
	private LinkedList<Integer> ifEndLabelLines = new LinkedList<>();
	private LinkedList<Integer> conditionLines = new LinkedList<>();
	private boolean lastElse = false;
	
	protected IfBuilder(CompilerCore core) {
		super(core, BuilderType.IF);
	}

	@Override
	protected void build() {
		processCondition();
		do {
			this.onVirtualScopeStarts();
			this.continueProcessing();
			this.onVirtualScopeEnds();
			
			if (ends()) {
				getProgram().appendBody(Keyword.LABEL.build(core, "%s"));
				blockEndLabelLines.add(getProgram().getCurrentBodyLine());
				break;
			}
			
			prepareNewBlock();
		} while(true);
		
		formatLines();
		this.continueProcessing();
	}

	private void prepareNewBlock() {
		getProgram().appendBody(Keyword.GOTO.build(core, "%s"));
		ifEndLabelLines.add(getProgram().getCurrentBodyLine());
		getProgram().appendBody(Keyword.LABEL.build(core, "%s"));
		blockEndLabelLines.add(getProgram().getCurrentBodyLine());
		if (!isLastElse())
			processCondition();
	}

	private void formatLines() {
		String label = null;
		for (int i = 0; i < blockEndLabelLines.size(); ++i) {
			getProgram().formatBodyLine(blockEndLabelLines.get(i), 
					label = BuilderUtil.convertLabelName(core.requestNewLabel()));
			if (i < conditionLines.size())
				getProgram().formatBodyLine(conditionLines.get(i), label);
		}
		
		if (label != null)
			for (int line : ifEndLabelLines) {
				getProgram().formatBodyLine(line, label);
			}
	}
	
	private boolean ends() {
		for (String pattern : IF_END_PATTERNS)
			if (core.getCurrentLine().matches(pattern))
				return true;
		
		if (lastElse)
			throw new CompileException(core, "An else block is already exists");
		return false;
	}
	
	private boolean isLastElse() {
		for (String pattern : ELSE_PATTERNS) {
			if (core.getCurrentLine().matches(pattern)) {
				if (lastElse)
					throw new CompileException(core, "An else block is already exists");
				lastElse = true;
				return true;
			}
		}
		return false;
	}

	private void processCondition() {
		String condition = core.getCurrentLine().replaceAll("^(?i)ha[ \\t]*", "").replaceAll("[ \\t]*(?i)akkor$", "");
		CalcBuilder calc = new CalcBuilder(core).process(condition);
		if (calc.isSimple()) {
			getProgram().appendBody(Keyword.IF.build(core, calc.getLastCalculation(), Keyword.NEXT, Keyword.GOTO, "%s"));
		} else {
			getProgram().appendBody(calc.getFullResult());
			getProgram().appendBody(Keyword.IF.build(core, calc.getLastTempVar(), Keyword.NEXT, Keyword.GOTO, "%s"));
		}
		conditionLines.add(getProgram().getCurrentBodyLine());
	}
	
	@Override
	public ArrayList<String> getEndPatterns() {
		return END_PATTERNS;
	}
	
}
