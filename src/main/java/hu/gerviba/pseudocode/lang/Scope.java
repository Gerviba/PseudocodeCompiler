package hu.gerviba.pseudocode.lang;

import static hu.gerviba.pseudocode.utils.FormatUtil.isVariableName;
import static hu.gerviba.pseudocode.utils.FormatUtil.removeComplexArgCount;

import java.util.HashMap;

import hu.gerviba.pseudocode.compiler.units.Pair;
import hu.gerviba.pseudocode.exceptions.ExecutionException;
import hu.gerviba.pseudocode.interpreter.ApplicationProcessor;
import hu.gerviba.pseudocode.lang.primitive.PNull;
import hu.gerviba.pseudocode.lang.primitive.PPrimitiveField;
import hu.gerviba.pseudocode.lang.primitive.PPrimitiveValue;

public class Scope {

	private static class FieldStorage {
		private final HashMap<String, Pair<PField, Boolean>> data = new HashMap<>();
		
		public void put(String name, PField value, boolean temp) {
			data.put(removeComplexArgCount(name.toLowerCase()), new Pair<PField, Boolean>(value, temp));
		}
		
		public boolean containsKey(String name) {
			return data.containsKey(removeComplexArgCount(name.toLowerCase()));
		}
		
		public PField get(String name) {
			return data.get(removeComplexArgCount(name.toLowerCase())).getKey();
		}
		
		public PField remove(String name) {
			return data.remove(removeComplexArgCount(name.toLowerCase())).getKey();
		}
		
		public boolean isTemp(String name) {
			return data.get(removeComplexArgCount(name.toLowerCase())).getValue();
		}
	}
	
	private static int scopeId = -1;
	
	private final ApplicationProcessor processor;
	private final PMethod method;
	private final int id;
	private int line;
	private FieldStorage storage = new FieldStorage();
	private final Scope globalParent;
	
	public Scope(ApplicationProcessor processor, PProgram program) {
		this.id = ++scopeId;
		
		this.processor = processor;
		this.method = program.getMethod("$");
		this.globalParent = null;
		this.line = program.getStartLine();
		this.storage.put("null", PNull.NULL_POINTER_CONTAINER, false);
	}
	
	public Scope(ApplicationProcessor processor, PMethod method, Scope globalParentScope) {
		this.id = ++scopeId;
		
		this.processor = processor;
		this.method = method;
		this.globalParent = globalParentScope;
		this.line = method.getStartLine();
		this.storage.put("null", PNull.NULL_POINTER_CONTAINER, false);
	}
	
	public PField getField(String name) {
		// System.out.println("get: " + name + " = " + getSafeField(name).toString());
		if (storage.containsKey(name))
			return storage.isTemp(name) ? storage.remove(name) : storage.get(name);
		if (globalParent != null)
			return globalParent.getField(name);
		return new PField("#value", new PPrimitiveField("#value", PField.newValue(name), false), false);
	}
	
	/**
	 * Without removing it (usecase: Debug watchlist)
	 */
	public PField getSafeField(String name) {
		if (storage.containsKey(name) && (name.startsWith("$") || isVariableName(name)))
			return storage.get(name);
		if (globalParent != null)
			return globalParent.getSafeField(name);
		return new PField("#value", new PPrimitiveField("#value", PField.newValue(name), false), false);
	}

	public void addTemp(PField value) {
		storage.put(value.getName(), value, true);
	}
	
	public void addTemp(String name, PPrimitiveValue value) {
		storage.put(name, new PField(name, new PPrimitiveField(name, value, false), false), true);
	}
	
	//TODO: Talán klónozni kellene ha constant
	public void addTemp(String name, PField value) {
		storage.put(name, value, true);
	}

	public void addVar(PField value) {
		storage.put(value.getName(), value, false);
	}

	public void addVar(String name, PPrimitiveValue value) {
		storage.put(name, new PField(name, new PPrimitiveField(name, value, false), false), false);
	}

	public void addVar(String name, PField value) {
		storage.put(name, value, false);
	}
	
	public void addConst(PField value) {
		storage.put(value.getName(), value.setConstant(), false);
	}

	public void addConst(String name, PPrimitiveValue value) {
		storage.put(name, new PField(name, new PPrimitiveField(name, value, true), true), false);
	}
	
	public void free(String name) {
		if ("null".equalsIgnoreCase(name))
			throw new ExecutionException("Cannot free the Null Pointer");
		storage.remove(name);
	}
	
	public void setVar(String name, PField value) {
		if (name.equals("null")) {
			throw new ExecutionException("Cannot declarate null named variable or constant");
		}
		PField field = storage.get(name);
		if (field == null)
			throw new ExecutionException("Field '" + name + "' is not declarated in this scope");
		if (storage.isTemp(name))
			throw new ExecutionException("Cannot set the value of a temp field");
		
		storage.put(name, value.clone(), false);
	}

	public void setVar(String name, PPrimitiveValue value) {
		if (name.equals("null")) {
			throw new ExecutionException("Cannot declarate null named variable or constant");
		}
		PField field = storage.get(name);
		if (field == null)
			throw new ExecutionException("Field '" + name + "' is not declarated in this scope");
		if (storage.isTemp(name))
			throw new ExecutionException("Cannot set the value of a temp field");
		
		//TODO: REMOVE((PPrimitiveField) field.getValue()).setPrimitiveValue(value);
		storage.put(name, new PField(name, new PPrimitiveField(name, value, false), false), false);
	}
	
	public int getScopeId() {
		return id;
	}

	public boolean hasField(String fieldName) {
		return fieldName.equals("null") ? false : storage.containsKey(fieldName);
	}

	public int getLineId() {
		return line;
	}

	/**
	 * 
	 * @return true ha link történik
	 */
	public void processLine() {
		if (System.getProperty("EBUG", "false").equalsIgnoreCase("true"))
			System.out.println(processor.getLine(line).toString());
		processor.getLine(line).getKeyword().onExecute(this, processor.getLine(line));
	}

	public void nextLine() {
		++line;
	}

	public void linkTo(int line) {
		this.line = line - 1;
	}

	public ApplicationProcessor getProcessor() {
		return processor;
	}

	public PMethod getMethod() {
		return method;
	}

	public boolean containsField(String name) {
		return storage.containsKey(name) ? true : (globalParent != null && globalParent.containsField(name));
	}

	public String[] resolve(String[] resolvable) {
		for (int i = 0; i < resolvable.length; ++i) {
			if (storage.containsKey(resolvable[i])) {
				resolvable[i] = getField(resolvable[i]).getPrimitiveValue().asStringValue();
			}
		}
		return resolvable;
	}

	public PProgram getProgram() {
		return method.getParent();
	}
	
	public Scope getGlobalParentScope() {
		return globalParent == null ? this : globalParent;
	}

	public Scope invoke(String[] rangeToArray) {
		for (int i = 0; i < rangeToArray.length; ++i) {
			addVar(method.getArgument(i),
					new PPrimitiveField(method.getArgument(i), PField.newValue(rangeToArray[i]), false));
		}
		return this;
	}
	
}
