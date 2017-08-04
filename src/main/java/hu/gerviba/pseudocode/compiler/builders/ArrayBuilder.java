package hu.gerviba.pseudocode.compiler.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import hu.gerviba.pseudocode.compiler.units.Keyword;
import hu.gerviba.pseudocode.compiler.units.Pair;
import hu.gerviba.pseudocode.exceptions.CompileException;
import hu.gerviba.pseudocode.utils.FormatUtil;

import static hu.gerviba.pseudocode.utils.FormatUtil.replaceNotInString;
import static hu.gerviba.pseudocode.utils.FormatUtil.formatSimpleValue;

public class ArrayBuilder {

	private CompilerCore core;
	private String raw;
	private String varName;
	private List<Integer> dimensionSizes = new ArrayList<>();
	private List<Pair<Integer[], String>> values = new ArrayList<>();
	
	public ArrayBuilder(CompilerCore core, String varName, String raw) {
		this.core = core;
		this.varName = varName;
		this.raw = replaceNotInString(replaceNotInString(raw.replaceAll("^(?i)t[Ööo]mb", ""), "[", "("), "]", ")");
	}
	
	public ArrayBuilder process() {
		process(new int[] {}, raw);
		return this;
	}
	
	private void process(int[] coord, String subRaw) {
		if (coord.length == 255)
			throw new CompileException(core, "You've reached the maximum possible dimensions of array");
		List<String> parts = splitByBracket(subRaw);
		if (parts.size() == 1) {
			parts = splitByComma(parts.get(0));
			offerArrayDimensionSize(coord.length, parts.size());
			for (int i = 0; i < parts.size(); ++i)
				values.add(new Pair<Integer[], String>(incAllValues(addToArray(coord, i)), parts.get(i)));
		} else {
			offerArrayDimensionSize(coord.length, parts.size());
			for (int i = 0; i < parts.size(); ++i)
				process(addToArray(coord, i), parts.get(i));
		}
	}
	
	public int getDimensions() {
		return dimensionSizes.size();
	}
	
	/**
	 * @param keyword Only VAR CONST or CALC
	 */
	public String buildVar(Keyword keyword) {
		String result = keyword.build(core, 
				varName + "$" + getDimensions(),
				"PArray");
		
		for (int i : dimensionSizes)
			result += Keyword.SEPARATOR.getByCore(core) + "1" + 
				Keyword.SEPARATOR.getByCore(core) + i;

		return result;
	}
	
	public LinkedList<String> buildSetters() {
		LinkedList<String> result = new LinkedList<>();
		for (Pair<Integer[], String> entry : values) {
			if (FormatUtil.isSimple(entry.getValue())) {
				result.add(Keyword.SET.build(core, 
						formatSimpleValue(entry.getValue()), 
						varName + "$" + getDimensions(),
						String.join(Keyword.SEPARATOR.getByCore(core), 
								Arrays.stream(entry.getKey())
								.map(x -> "" + x)
								.collect(Collectors.toList()))));
			} else {
				CalcBuilder calc = new CalcBuilder(core).process(entry.getValue());
				result.addAll(calc.getFullResult());
				result.add(Keyword.SET.build(core, 
						calc.getLastTempVar(), 
						varName + "$" + getDimensions(),
						String.join(Keyword.SEPARATOR.getByCore(core),
								Arrays.stream(entry.getKey())
								.map(x -> "" + x)
								.collect(Collectors.toList()))));
			}
		}
		return result;
	}
	
	private void offerArrayDimensionSize(int dimension, int subItemsSize) {
		if (dimensionSizes.size() <= dimension)
			dimensionSizes.add(subItemsSize);
		else if (dimensionSizes.get(dimension) < subItemsSize)
			dimensionSizes.set(dimension, subItemsSize);
	}

	private List<String> splitByBracket(String subRaw) {
		List<String> result = new LinkedList<>();
		String buffer = "";
		boolean stringStarted = false;
		int level = -1;
		for (int i = 0; i < subRaw.length(); ++i) {
			if (stringStarted) {
				if (subRaw.charAt(i) == '"') {
					if (!buffer.endsWith("\\"))
						stringStarted = false;
					buffer += subRaw.charAt(i);
				} else {
					buffer += subRaw.charAt(i);
				}
			} else {
				if (subRaw.charAt(i) == '"') {
					stringStarted = true;
					buffer += subRaw.charAt(i);
				} else if (subRaw.charAt(i) == '(') {
					++level;
					if (level > 0)
						buffer += subRaw.charAt(i);
				} else if (subRaw.charAt(i) == ')') {
					--level;
					if (level >= 0)
						buffer += subRaw.charAt(i);
					if (level == 0) {
						buffer = FormatUtil.trim(buffer);
						if (buffer.length() > 0) {
							result.add(buffer);
							buffer = "";
						}
					} else if (level < -1) {
						throw new CompileException(core, "Invalid array definition. Too much end signs.");
					}
				} else if (level >= 0) {
					buffer += subRaw.charAt(i);
				}
			}
		}
		
		if (level >= 0)
			throw new CompileException(core, "Invalid array definition. Too much start signs.");
		
		buffer = FormatUtil.trim(buffer);
		if (buffer.length() > 0)
			result.add(buffer);
		return result;
	}
	
	private List<String> splitByComma(String subRaw) {
		List<String> result = new LinkedList<>();
		String buffer = "";
		boolean stringStarted = false;
		int level = 0;
		for (int i = 0; i < subRaw.length(); ++i) {
			if (stringStarted) {
				if (subRaw.charAt(i) == '"') {
					if (!buffer.endsWith("\\"))
						stringStarted = false;
					buffer += subRaw.charAt(i);
				} else {
					buffer += subRaw.charAt(i);
				}
			} else {
				if (subRaw.charAt(i) == ',' && level == 0) {
					buffer = FormatUtil.trim(buffer);
					if (buffer.length() > 0) {
						result.add(buffer);
						buffer = "";
					}
				} else {
					buffer += subRaw.charAt(i);
					if (subRaw.charAt(i) == '"') {
						stringStarted = true;
					} else if (subRaw.charAt(i) == '(') {
						++level;
					} else if (subRaw.charAt(i) == ')') {
						--level;
						if (level < 0)
							throw new CompileException(core, "Invalid array definition. Too much end signs.");
					}
				}
			}
		}
		
		if (level > 0)
			throw new CompileException(core, "Invalid array definition. Too much start signs.");
		
		buffer = FormatUtil.trim(buffer);
		if (buffer.length() > 0)
			result.add(buffer);
		return result;
	}
	
	int[] addToArray(int[] array, int value) {
		int[] result = new int[array.length + 1];
		System.arraycopy(array, 0, result, 0, array.length);
		result[array.length] = value;
		return result;
	}
	
	Integer[] incAllValues(int[] array) {
		Integer[] result = new Integer[array.length];
		for (int i = 0; i < array.length; ++i)
			result[i] = array[i] + 1;
		return result;
	}
}
