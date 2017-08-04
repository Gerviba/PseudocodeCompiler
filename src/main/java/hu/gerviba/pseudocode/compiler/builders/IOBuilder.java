package hu.gerviba.pseudocode.compiler.builders;

import java.util.ArrayList;
import java.util.Arrays;

import hu.gerviba.pseudocode.compiler.units.Keyword;
import hu.gerviba.pseudocode.exceptions.CompileException;
import hu.gerviba.pseudocode.streams.PStream;
import hu.gerviba.pseudocode.streams.PStream.IOStreamState;
import hu.gerviba.pseudocode.utils.FormatUtil;
import hu.gerviba.pseudocode.utils.StreamUtil;

public final class IOBuilder extends Builder {
	
	protected IOBuilder(CompilerCore core) {
		super(core, BuilderType.IO);
	}

	@Override
	protected void build() {
		if (isIn(core.getCurrentLine()))
			appendIn();
		else
			appendOut();

		this.continueProcessing();
	}

	@Override
	public ArrayList<String> getEndPatterns() {
		return new ArrayList<>(Arrays.asList(""));
	}

	protected boolean isIn(String line) {
		return line.matches("(?i)BE[ \\t]*\\:[ \\t]*.+") || line.matches("(?i)IN[ \\t]*\\:[ \\t]*.+");
	}
	
	private void appendOut() {
		String definition = FormatUtil.trim(core.getCurrentLine().split(" ", 2)[1]);
		PStream stream = StreamUtil.getStreamByDefinition(core, definition);
		int streamId = 0;
		
		if (stream != null) {
			definition = FormatUtil.trim(definition.substring(stream.getPrefix().length()));
			streamId = StreamUtil.getStreamComponentByStream(core, stream)
					.requestState(IOStreamState.OUTPUT_ONLY).getId();
		}
		
		//TODO: Hiba ha a változó nem létezik
		CalcBuilder calc = new CalcBuilder(core).process(definition);
		if (calc.isSimple()) {
			getProgram().appendBody(Keyword.OUT.build(core, streamId, calc.getLastCalculation()));
		} else {
			getProgram().appendBody(calc.getFullResult());
			getProgram().appendBody(Keyword.OUT.build(core, streamId, calc.getLastTempVar()));
		}
	}
	
	//TODO: Beolvasás komplex értékbe
	private void appendIn() {
		String definition = FormatUtil.trim(core.getCurrentLine().split(" ", 2)[1]);
		PStream stream = StreamUtil.getStreamByDefinition(core, definition);
		
		if (stream != null) {
			String var = FormatUtil.trim(definition.substring(stream.getPrefix().length()));
			if (FormatUtil.isSimple(var)) {
				checkVarExistance(var);
				getProgram().appendBody(Keyword.IN.build(core, 
						StreamUtil.getStreamComponentByStream(core, stream)
						.requestState(IOStreamState.INPUT_ONLY).getId(), var));
			} else {
				throw new CompileException(core, "Invalid input definition or variable name '" + var + "'");
			}
		} else if (FormatUtil.isSimple(definition) && FormatUtil.isVariableName(definition)) {
			checkVarExistance(definition);
			getProgram().appendBody(Keyword.IN.build(core, "0", definition));
		} else {
			throw new CompileException(core, "Invalid input definition or variable name '" + definition + "'");
		}
	}

	private void checkVarExistance(String fieldName) {
		if (!isFieldAvailable(fieldName)) {
			getProgram().appendBody(Keyword.VAR.build(core, fieldName, "null"));
			registerField(fieldName);
		}
	}
		
}
