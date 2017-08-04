package hu.gerviba.pseudocode.compiler.builders;

import static hu.gerviba.pseudocode.utils.FormatUtil.formatSimpleValue;
import static hu.gerviba.pseudocode.utils.FormatUtil.getRawArrayName;
import static hu.gerviba.pseudocode.utils.FormatUtil.isArrayDefinition;
import static hu.gerviba.pseudocode.utils.FormatUtil.isArrayNameDefinition;
import static hu.gerviba.pseudocode.utils.FormatUtil.isSimple;
import static hu.gerviba.pseudocode.utils.FormatUtil.isVariableName;
import static hu.gerviba.pseudocode.utils.FormatUtil.removeComplexArgCount;
import static hu.gerviba.pseudocode.utils.FormatUtil.trim;

import java.util.ArrayList;

import hu.gerviba.pseudocode.compiler.units.Keyword;
import hu.gerviba.pseudocode.exceptions.CompileException;

public class OperationBuilder extends Builder {

	protected OperationBuilder(CompilerCore core) {
		super(core, BuilderType.OPERATION);
	}

	@Override
	protected void build() {
		String definition = trim(core.getCurrentLine().split("\\=", 2)[1]);
		
		if (core.getCurrentLine().toLowerCase().startsWith("const ") || 
			core.getCurrentLine().toLowerCase().startsWith("konstans ")) {
			createConst(definition);
		} else {
			createVar(definition);
		}
		
		this.continueProcessing();
	}

	private void createVar(String definition) {
		String fieldName = core.getCurrentLine().split("\\=", 2)[0].replaceAll("[ \\t\\:]*$", "");
				
		if (isArrayNameDefinition(fieldName)) {
			appendArrayCalc(definition, fieldName);
			return;
		} else if (isArrayDefinition(definition)) {
			appendPredefiniedArray(definition, fieldName, false);
			return;
		}
		
		if (!isVariableName(fieldName))
			throw new CompileException(core, "Invalid field name (" + fieldName + ")");
		
		if (isFieldAvailable(getRawArrayName(fieldName))) {
			append(Keyword.CALC, fieldName, definition);
		} else {
			append(Keyword.VAR, fieldName, definition);
			registerField(fieldName);
		}
	}
	
	private void createConst(String definition) {
		String fieldName = core.getCurrentLine().split("\\=", 2)[0].replaceAll("[ \\t\\:]*$", "")
				.replaceAll("^(?i)((const)|(konstans))[ \\t]*", "");
		
		//TODO Const arrayt nem lehet feltölteni, de lehessen már
		if (isArrayNameDefinition(fieldName)) {
			throw new CompileException(core, "Cannot set the value of a constant array");
		} else if (isArrayDefinition(definition)) {
			appendPredefiniedArray(definition, fieldName, true);
			return;
		}
		
		if (!isVariableName(fieldName))
			throw new CompileException(core, "Invalid field name");
		
		if (isFieldAvailable(fieldName)) {
			throw new CompileException(core, "A field with this name is already in use (" + fieldName + ")");
		}
		
		append(Keyword.CONST, fieldName, definition);
		registerField(fieldName);
	}

	private void appendArrayCalc(String definition, String fieldName) {
		CalcBuilder nameCalc = new CalcBuilder(core).process(fieldName);
		getProgram().appendBody(nameCalc.getFullResult());
		
		if (isSimple(definition)) {
			getProgram().appendBody(Keyword.SET.build(core, formatSimpleValue(definition), nameCalc.getLastTempVar()));
			
		} else if (isArrayDefinition(definition)) {
			ArrayBuilder ab = new ArrayBuilder(core, nameCalc.getLastTempVar(), definition).process();
			getProgram().appendBody(ab.buildVar(Keyword.CALC));
			getProgram().appendBody(ab.buildSetters());
			
		} else {
			CalcBuilder defCalc = new CalcBuilder(core).process(definition);
			getProgram().appendBody(defCalc.getFullResult());
			getProgram().appendBody(Keyword.SET.build(core, defCalc.getLastTempVar(), nameCalc.getLastTempVar()));
		}
	}

	private void append(Keyword keyword, String fieldName, String definition) {
		CalcBuilder calc = new CalcBuilder(core).process(definition);
		if (calc.isSimple()) {
			getProgram().appendBody(keyword.build(core, fieldName, calc.getLastCalculation()));
		} else if (calc.isLastCall()) {
			getProgram().appendBody(calc.getFullResult());
			getProgram().appendBody(keyword.build(core, fieldName, calc.getLastTempVar()));
		} else {
			getProgram().appendBody(calc.getWithoutLast());
			getProgram().appendBody(keyword.build(core, fieldName, calc.getLastCalculation()));
		}
	}
	
	private void appendPredefiniedArray(String definition, String fieldName, boolean constant) {
		ArrayBuilder ab  = new ArrayBuilder(core, fieldName, definition).process();
		getProgram().appendBody(ab.buildVar(getKeyword(fieldName, constant)));
		registerField(removeComplexArgCount(fieldName));
		getProgram().appendBody(ab.buildSetters());
	}
	
	private Keyword getKeyword(String fieldName, boolean constant) {
		return isFieldAvailable(fieldName) ? Keyword.CALC : constant ? Keyword.CONST : Keyword.VAR;
	}
	
	@Override
	public ArrayList<String> getEndPatterns() {
		return null;
	}

}
