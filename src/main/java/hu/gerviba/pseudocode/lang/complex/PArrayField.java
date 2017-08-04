package hu.gerviba.pseudocode.lang.complex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.gerviba.pseudocode.compiler.units.Pair;
import hu.gerviba.pseudocode.exceptions.ExecutionException;
import hu.gerviba.pseudocode.lang.PField;
import hu.gerviba.pseudocode.lang.primitive.PNull;
import hu.gerviba.pseudocode.lang.primitive.PPrimitiveValue;

public class PArrayField extends PField implements PComplexField {

	private final int minIndex;
	private final int maxIndex;
	private final Map<Integer, PField> values = new HashMap<>();
	private final int dimensions;
	
	public PArrayField(String name, int minIndex, int maxIndex, int dimensions) {
		super(name, false);
		this.minIndex = minIndex;
		this.maxIndex = maxIndex;
		this.dimensions = dimensions;
		
		if (minIndex > maxIndex)
			throw new ExecutionException("Invalid array index range (min: " + minIndex + " max: " + maxIndex + ")");
	}
	
	@Override
	public Class<? extends PPrimitiveValue> getType() {
		return null;
	}

	@Override
	public PField getValue() {
		return this;
	}
	
	public void setValue(int index, PField value) {
		if (dimensions != 1 && !(value instanceof PArrayField && ((PArrayField) value).dimensions == dimensions))
			throw new ExecutionException("Invalid dimension sizes");
		if (isConstant())
			throw new ExecutionException("Cannot the the value of a constant");
		values.put(index, value);
	}

	@Override
	public boolean isComplex() {
		return true;
	}

	@Override
	public PField getValue(String[] args) {
		if (args == null || args.length == 0)
			return this;
		
		if (dimensions < args.length) //TODO: Test it out
			throw new ExecutionException("Too much arguments");
		
		int index;
		try {
			index = (int) Double.parseDouble(args[0]);
		} catch (NumberFormatException e) {
			throw new ExecutionException("Invalid index value '" + args[0] + "'");
		}
		
		if (index < minIndex)
			throw new ExecutionException("Array index out of bounds (min: " + minIndex + " requested: " + index + ")");
		if (index > maxIndex)
			throw new ExecutionException("Array index out of bounds (max: " + maxIndex + " requested: " + index + ")");
		
		return args.length == 1 
				? values.getOrDefault(index, PNull.NULL_POINTER_CONTAINER) 
				: (values.get(index)).getValue(removeArgument(args));
	}

	String[] removeArgument(String[] args) {
		String[] result = new String[args.length - 1];
		System.arraycopy(args, 1, result, 0, args.length - 1);
		return result;
	}

	public static PField newArray(String name, List<Pair<Integer, Integer>> ranges) {
		PArrayField array = new PArrayField(name, ranges.get(0).getKey(), ranges.get(0).getValue(), ranges.size());
		if (ranges.size() == 1) {
			for (int i = ranges.get(0).getKey(); i <= ranges.get(0).getValue(); ++i) {
				array.setValue(i, new PField(name, PNull.NULL_POINTER_CONTAINER, false));
			}
		} else {
			List<Pair<Integer, Integer>> subRanges = ranges.subList(1, ranges.size());
			for (int i = ranges.get(0).getKey(); i <= ranges.get(0).getValue(); ++i) {
				array.setValue(i, newArray(name, subRanges));
			}
		}
		return array;
	}

	@Override
	public String toString() {
		return "PArrayField [name=" + name + " minIndex=" + minIndex + ", maxIndex=" + maxIndex + ", values=" + values + ", dimensions="
				+ dimensions + "]";
	}
	
	
}