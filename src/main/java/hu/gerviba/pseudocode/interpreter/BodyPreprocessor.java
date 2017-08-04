package hu.gerviba.pseudocode.interpreter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Pattern;

import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;
import hu.gerviba.pseudocode.compiler.units.Keyword;
import hu.gerviba.pseudocode.utils.LineUtil;

public final class BodyPreprocessor {

	private static final ArrayList<Keyword> BODY_KEYS = new ArrayList<>();

	static {
		for (Keyword k : Keyword.values()) {
			if (k.isBody())
				BODY_KEYS.add(k);
		}
	}

	private ApplicationProcessor processor;
	private LinkedList<Line> lines = new LinkedList<>();
	private int line = 0;
	
	BodyPreprocessor(ApplicationProcessor processor, int fromLine) {
		this.processor = processor;
		this.line = fromLine;
	}

	void process() {
		while (line < processor.getRawLineCount()) {
			boolean found = false;
			for (Keyword k : BODY_KEYS) {
				if (keyFound(k)) {
					found = true;
					ArrayList<String> args = LineUtil.splitLine(processor.getRawLine(line), processor);
					
					if (processor.getMeta("DEBUG").equals("1")) {
						lines.add(new Line(Integer.parseInt(args.get(0), processor.getCompileMode().getDebugRadix()), 
								lines.size(), Keyword.forName(args.get(1)), args.subList(2, args.size())));
					} else {
						lines.add(new Line(-1, lines.size(), Keyword.forName(args.get(0)), args.subList(1, args.size())));
					}
					
					k.onLoad(processor, lines.get(lines.size() - 1));
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
					+ "(" + Pattern.quote(Keyword.SEPARATOR.getFullyCompressed()) + ".+)?$");
		}
		return processor.getRawLine(line).matches("^" + Pattern.quote(k.getSemiCompressed())
				+ "(" + Pattern.quote(Keyword.SEPARATOR.getSemiCompressed()) + ".+)?$");
	}

	public LinkedList<Line> getLines() {
		return lines;
	}

}
