package hu.gerviba.pseudocode.interpreter;

import java.util.ArrayList;
import java.util.regex.Pattern;

import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;
import hu.gerviba.pseudocode.compiler.units.Keyword;

public final class HeadProcessor {

	private static final ArrayList<Keyword> HEAD_KEYS = new ArrayList<>();

	static {
		for (Keyword k : Keyword.values()) {
			if (k.isHead())
				HEAD_KEYS.add(k);
		}
	}

	private ApplicationProcessor processor;
	private int line = 0;

	HeadProcessor(ApplicationProcessor processor) {
		this.processor = processor;
	}

	void process() {
		while (line < processor.getRawLineCount()) {
			if (line == 0 && processor.getRawLine(line).matches("^\\#\\!\\/.+$"))
				++line;

			boolean found = false;
			for (Keyword k : HEAD_KEYS) {
				if (keyFound(k)) {
					found = true;
					k.onLoad(processor, processor.getRawLine(line).split(
							Pattern.quote(Keyword.SEPARATOR.getByCore(processor))));
					break;
				}
			}
			if (!found)
				return;
			++line;
		}
	}

	private boolean keyFound(Keyword k) {
		if (processor.getCompileMode() == CompileMode.FULLY_COMPRESSED) {
			return processor.getRawLine(line).matches("^" + Pattern.quote(k.getFullyCompressed())
					+ Pattern.quote(Keyword.SEPARATOR.getFullyCompressed()) + ".+$");
		}
		return processor.getRawLine(line).matches("^" + Pattern.quote(k.getSemiCompressed())
				+ Pattern.quote(Keyword.SEPARATOR.getSemiCompressed()) + ".+$");
	}

	int getProgramStartLine() {
		return line;
	}

}
