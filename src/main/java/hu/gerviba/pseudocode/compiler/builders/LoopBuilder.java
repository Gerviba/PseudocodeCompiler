package hu.gerviba.pseudocode.compiler.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import hu.gerviba.pseudocode.compiler.units.Keyword;
import hu.gerviba.pseudocode.compiler.units.Operator;
import hu.gerviba.pseudocode.exceptions.CompileException;
import hu.gerviba.pseudocode.utils.BuilderUtil;
import hu.gerviba.pseudocode.utils.FormatUtil;

/**
 * @see docs/LOOP.txt
 */
public class LoopBuilder extends Builder {

	enum ForDifference {
		INC1 {
			@Override
			public boolean matches(String difference) {
				return difference.matches("^(?i)egyes[\\É\\ée]vel$");
			}

			@Override
			public LinkedList<String> getOperations(CompilerCore core, String loopVariable, String difference) {
				return new LinkedList<String>(Arrays.asList(Keyword.INC.build(core, loopVariable)));
			}
			
			@Override
			public Operator getToOpeartor(String difference) {
    		    return Operator.LE;
    		}
		},
		INCN {
			@Override
			public boolean matches(String difference) {
				return difference.matches("^(?i)\\([ \\t]*\\-?[0-9]+[ \\t]*\\)( \\- )?((es[\\É\\ée]vel)|(as[\\Á\\áa]val)|(os[\\Á\\áa]val))$"); //NOTE: Ciklusváltozó csak egész szám lehet!
			}
			
			@Override
			public LinkedList<String> getOperations(CompilerCore core, String loopVariable, String difference) {
				difference = FormatUtil.trim(difference.substring(1).replaceAll("[ \\t]*\\)( \\- )?((es[\\É\\ée]vel)|(as[\\Á\\áa]val)|(os[\\Á\\áa]val))$", ""));
				return new LinkedList<String>(Arrays.asList(
						Keyword.CALC.build(core, loopVariable, loopVariable, 
								difference.matches("^[ \\t]*\\-.+$") ? Operator.SUB : Operator.ADD, difference.replaceAll("^[ \\t]*\\-", ""))));
			}
			
			@Override
			public Operator getToOpeartor(String difference) {
			    return difference.substring(1).replaceAll("[ \\t]*\\)( \\- )?((es[\\É\\ée]vel)|(as[\\Á\\áa]val)|(os[\\Á\\áa]val))$", "")
			            .matches("^[ \\t]*\\-.+$") ? Operator.GE : Operator.LE;
			}
		}/*, TEMPORARY REMOVED
		OPERATION {
			@Override
			public boolean matches(String difference) {
				return difference.matches("^(?i)"
						+ "[A-Za-z\\_\\á\\é\\í\\ó\\ö\\ő\\ú\\ü\\ű\\Á\\É\\Í\\Ó\\Ö\\Ő\\Ú\\Ü\\Ű]"
						+ "[A-Za-z0-9\\_\\á\\é\\í\\ó\\ö\\ő\\ú\\ü\\ű\\Á\\É\\Í\\Ó\\Ö\\Ő\\Ú\\Ü\\Ű]*"
						+ "[ \\t]*\\:?\\=\\ *.+$");
			}
			
			@Override
			public LinkedList<String> getOperations(CompilerCore core, String loopVariable, String difference) {
				CalcBuilder calc = new CalcBuilder(core).process(difference);
				if (calc.isSimple()) {
					return new LinkedList<String>(Arrays.asList(Keyword.TEMP.build(core, "$"+core.requestNewTempId(), calc.getLastCalculation())));
				}
				return calc.getFullResult();
			}
		}*/;
		
		public abstract boolean matches(String difference);
		public abstract LinkedList<String> getOperations(CompilerCore core, String loopVariable, String difference);
		public abstract Operator getToOpeartor(String difference);
	}
	
	private ArrayList<Integer> breakLines = new ArrayList<>();
	private ArrayList<Integer> continueLines = new ArrayList<>();
	private Operator toOperator = null;
	private ForDifference difference;
	private String afterthough;
	
	protected LoopBuilder(CompilerCore core) {
		super(core, BuilderType.LOOP);
	}
	
	public void setContainsBreak(int atLine) {
		breakLines.add(atLine);
	}
	
	public void setContainsContinue(int atLine) {
		continueLines.add(atLine);
	}

	@Override
	protected void build() {
		if (isDoWhile())
			buildDoWhile();
		else if (isWhile())
			buildWhile();
		else
			buildFor();
		
		this.continueProcessing();
	}
	
	private void buildDoWhile() {
		String topLabel = BuilderUtil.convertLabelName(core.requestNewLabel());
		getProgram().appendBody(Keyword.LABEL.build(core, topLabel));
		
		doProcessingCycle();

		String continueLabel = BuilderUtil.convertLabelName(core.requestNewLabel());
		getProgram().appendBody(Keyword.LABEL.build(core, continueLabel));
		
		String condition = core.getCurrentLine().replaceAll("^(?i)CIKLUS[ \\t]+((AM[\\É\\ée\\Í\\íi]G)|(AMEDDIG))[ \\t]+", "");
		CalcBuilder calc = new CalcBuilder(core).process(condition);
		if (calc.isSimple()) {
			getProgram().appendBody(Keyword.IF.build(core, calc.getLastCalculation(), Keyword.GOTO, topLabel, Keyword.NEXT));
		} else {
			getProgram().appendBody(calc.getFullResult());
			getProgram().appendBody(Keyword.IF.build(core, calc.getLastTempVar(), Keyword.GOTO, topLabel, Keyword.NEXT));
		}
		
		formatLines(continueLabel);
	}

	private void buildWhile() {
		String continueLabel = BuilderUtil.convertLabelName(core.requestNewLabel());
		getProgram().appendBody(Keyword.LABEL.build(core, continueLabel));
		
		String condition = core.getCurrentLine().replaceAll("^(?i)CIKLUS[ \\t]+((AM[\\É\\ée\\Í\\íi]G)|(AMEDDIG))[ \\t]+", "");
		CalcBuilder calc = new CalcBuilder(core).process(condition);
		if (calc.isSimple()) {
			getProgram().appendBody(Keyword.IF.build(core, calc.getLastCalculation(), Keyword.NEXT, Keyword.GOTO, "%s"));
		} else {
			getProgram().appendBody(calc.getFullResult());
			getProgram().appendBody(Keyword.IF.build(core, calc.getLastTempVar(), Keyword.NEXT, Keyword.GOTO, "%s"));
		}
		breakLines.add(getProgram().getCurrentBodyLine());
		
		doProcessingCycle();
		
		getProgram().appendBody(Keyword.GOTO.build(core, continueLabel));
		formatLines(continueLabel);
	}
	
	/**
	 * Ezt refaktorálni kell, nem elég Clean a Code
	 */
	private void buildFor() {
		// TODO: Ha stringben van a -tól/-től akkor dobjon hibát.
		String[] parts = core.getCurrentLine().replaceAll("^(?i)CIKLUS[ \\t]+", "")
				.split(" \\- (?i)T[\\Ó\\ó\\Ö\\ö\\Ő\\őo]L", 2);
		if (parts.length != 2)
			throw new CompileException(core, "FOR loop doesn't cointain FROM argument");
		
		String from = FormatUtil.trim(parts[0]);
		String loopVar = from.split("\\=", 2)[0].replaceAll("[ \\t\\:]*$", "");
		boolean varCreated = !isFieldAvailable(loopVar);
		createLoopVariable(from, loopVar, varCreated);
		
		String continueLabel = BuilderUtil.convertLabelName(core.requestNewLabel());
		getProgram().appendBody(Keyword.LABEL.build(core, continueLabel));
		
		parts = parts[1].split(" \\- (?i)IG", 2);
        recogniseAfterthough(parts);		
		checkLoopVariable(loopVar, FormatUtil.trim(parts[0]));
		
		getProgram().appendBody(Keyword.IF.build(core, "$"+core.getCurrentTempId(), Keyword.NEXT, Keyword.GOTO, "%s"));
		breakLines.add(getProgram().getCurrentBodyLine());
		
		doProcessingCycle();
		attachAfterthough(loopVar);
		
		getProgram().appendBody(Keyword.GOTO.build(core, continueLabel));
		formatLines(continueLabel);		
		freeLoopVariable(loopVar, varCreated);
	}

	private void doProcessingCycle() {
		this.onVirtualScopeStarts();
		this.continueProcessing();
		this.onVirtualScopeEnds();
	}

	private void freeLoopVariable(String loopVar, boolean varCreated) {
		if (varCreated)
			getProgram().appendBody(Keyword.FREE.build(core, loopVar));
	}

	private void createLoopVariable(String from, String loopVar, boolean varCreated) {
		Keyword keyword = varCreated ? Keyword.VAR : Keyword.CALC;
		String initialisation = FormatUtil.trim(from.split("\\=", 2)[1]);
		CalcBuilder calc = new CalcBuilder(core).process(initialisation);
		if (!calc.isSimple())
			getProgram().appendBody(calc.getWithoutLast());
		getProgram().appendBody(keyword.build(core, loopVar, calc.getLastCalculation()));
	}

	private void checkLoopVariable(String loopVar, String to) {
		CalcBuilder calc = new CalcBuilder(core).process(to);
		if (calc.isSimple()) {
			getProgram().appendBody(Keyword.TEMP.build(core, 
					"$"+core.requestNewTempId(),
					loopVar,
					toOperator,
					calc.getLastCalculation()));
		} else {
			getProgram().appendBody(calc.getFullResult());
			getProgram().appendBody(Keyword.TEMP.build(core,
					"$"+core.requestNewTempId(),
					loopVar,
					toOperator,
					calc.getLastTempVar()));
		}
	}

	private void recogniseAfterthough(String[] parts) {
		String afterthough = "";
		if (parts.length == 2)
			afterthough = FormatUtil.trim(parts[1]);
		if (afterthough.equals(""))
		  afterthough = "egyesével";
		
		boolean found = false;
		for (ForDifference fd : ForDifference.values()) {
			if (fd.matches(afterthough)) {
				found = true;
				this.toOperator = fd.getToOpeartor(afterthough);
				this.difference = fd;
				this.afterthough = afterthough;
				break;
			}
		}
		
		if (!found)
			throw new CompileException(core, "Invalid afterthough '" + afterthough  + "' for FOR loop");
	}
	
	private void attachAfterthough(String loopVar) {
	    getProgram().appendBody(difference.getOperations(core, loopVar, this.afterthough));
	}
	
	private void formatLines(String continueLabel) {
		for (int lineId : continueLines)
			getProgram().formatBodyLine(lineId, continueLabel);
		
		String breakLabel = BuilderUtil.convertLabelName(core.requestNewLabel());
		getProgram().appendBody(Keyword.LABEL.build(core, breakLabel));
		
		for (int lineId : breakLines)
			getProgram().formatBodyLine(lineId, breakLabel);
	}
	

	private boolean isDoWhile() {
		return core.getCurrentLine().equalsIgnoreCase("CIKLUS");
	}
	
	private boolean isWhile() {
		return core.getCurrentLine().matches("^(?i)CIKLUS[ \\t]+((AM[\\É\\ée\\Í\\íi]G)|(AMEDDIG))[ \\t]+.+$");
	}
	
	@Override
	public ArrayList<String> getEndPatterns() {
		return new ArrayList<>(Arrays.asList("^(?i)((AM[\\É\\ée\\Í\\íi]G)|(AMEDDIG))[ \\t]+.+$",
				"^(?i)CIK(LUS)?\\.?[ \\t]*V([\\É\\ée]GE)\\.?$"));
	}
	
}
