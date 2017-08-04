package hu.gerviba.pseudocode.compiler.units;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import hu.gerviba.pseudocode.CoreProcessor;
import hu.gerviba.pseudocode.compiler.builders.CompilerCore;
import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;
import hu.gerviba.pseudocode.exceptions.CompileException;
import hu.gerviba.pseudocode.exceptions.ExecutionException;
import hu.gerviba.pseudocode.exceptions.IllegalAccessExecption;
import hu.gerviba.pseudocode.interpreter.ApplicationProcessor;
import hu.gerviba.pseudocode.interpreter.Line;
import hu.gerviba.pseudocode.lang.PField;
import hu.gerviba.pseudocode.lang.PMethod;
import hu.gerviba.pseudocode.lang.PProgram;
import hu.gerviba.pseudocode.lang.Scope;
import hu.gerviba.pseudocode.lang.primitive.PLogical;
import hu.gerviba.pseudocode.lang.primitive.PPrimitiveOperatorTarget;
import hu.gerviba.pseudocode.streams.PStream;

public enum Keyword {
	SEPARATOR(" ", '_', false, false, false),
	
	META('\u0020', true, true, false) {
		@Override
		public void onLoad(ApplicationProcessor processor, String[] args) {
			processor.addMeta(args[1], new String(Base64.getDecoder().decode(args[2])));
		}
	},
	START('\u0021', true, true, false) {
		@Override
		public void onLoad(ApplicationProcessor processor, String[] args) {
			processor.setDefaultProgramName(args[1]);
		}
	},
	STREAM('\u0022', true, true, false) {
		@Override
		public void onLoad(ApplicationProcessor processor, String[] args) {
			processor.registerStream(Integer.parseInt(args[1]), 
					PStream.newStream(Integer.parseInt(args[2]), args[3], 
							args.length > 4 ? args[4] : null));
		}
	},
	IMPORT('\u0023', true, true, false), //TODO: Implement import
	
	PROG('\u0024', true, false, true) {
		@Override
		public void onLoad(ApplicationProcessor processor, Line line) {
			processor.registerProgram(line.getArg(0), 
					new PProgram(line.getRelativeLine(), line));
		}
		
		@Override
		public void onExecute(Scope scope, Line line) {
			scope.linkTo(scope.getProcessor()
					.getProgram(line.getArg(0))
					.getMethod("$")
					.getStartLine());
		}
	},
	VAR('\u0025', true, false, true) {
//		@Override
//		public void onLoad(ApplicationProcessor processor, Line line) {
//			if (line.getArgsSize() > 2 && line.getArg(1).indexOf('$') != -1) {
//				// TODO: Ellenőrizzem, hogy van-e invalid ComplexPseudoClass
//			}
//		}
		
		@Override
		public void onExecute(Scope scope, Line line) {
			if (line.getArgsSize() > 2 && line.getArg(0).indexOf('$') != -1) {
				scope.addVar(scope.getProcessor().getComplexRegistry()
						.constructField(line.getArg(1), line.getArg(0), line.getArgsFrom(2)));
				
			} else if (line.getArgsSize() == 2) {
				scope.addVar(line.getArg(0), scope.getField(line.getArg(1)).getPrimitiveValue());
				
			} else if (line.getArgsSize() == 3) {
				scope.addVar(line.getArg(0), PPrimitiveOperatorTarget.calc(
						scope.getField(line.getArg(1)), line.getArg(2), null));
				
			} else if (line.getArgsSize() == 4) {
				scope.addVar(line.getArg(0), PPrimitiveOperatorTarget.calc(
						scope.getField(line.getArg(1)), line.getArg(2), scope.getField(line.getArg(3))));
			} else {
				throw new ExecutionException("Invalid VAR definition '" + line.lineAsString() + "'");
			}
		}
	},
	CONST('\u0026', true, false, true) {
		@Override
		public void onExecute(Scope scope, Line line) {
			if (line.getArgsSize() == 2) {
				scope.addConst(line.getArg(0), scope.getField(line.getArg(1)).getPrimitiveValue());
				
			} else if (line.getArgsSize() == 3) {
				scope.addConst(line.getArg(0), PPrimitiveOperatorTarget.calc(
						scope.getField(line.getArg(1)), line.getArg(2), null));
				
			} else if (line.getArgsSize() == 4) {
				scope.addConst(line.getArg(0), PPrimitiveOperatorTarget.calc(
						scope.getField(line.getArg(1)), line.getArg(2), scope.getField(line.getArg(3))));
				
			} else {
				throw new ExecutionException("Invalid CONST definition");
			}
		}
	},
	TEMP('\'', true, false, true) {
		@Override
		public void onExecute(Scope scope, Line line) {
			if (line.getArgsSize() == 2) {
				scope.addTemp(line.getArg(0), scope.getField(line.getArg(1)).getPrimitiveValue());
				
			} else if (line.getArgsSize() == 3) {
				scope.addTemp(line.getArg(0), PPrimitiveOperatorTarget.calc(
						scope.getField(line.getArg(1)), line.getArg(2), null));
				
			} else if (line.getArgsSize() == 4) {
				scope.addTemp(line.getArg(0), PPrimitiveOperatorTarget.calc(
						scope.getField(line.getArg(1)), line.getArg(2), scope.getField(line.getArg(3))));
				
			} else {
				throw new ExecutionException("Invalid TEMP definition");
			}
		}
	},
	FREE('\u0028', true, false, true) {
		@Override
		public void onExecute(Scope scope, Line line) {
			scope.free(line.getArg(0));
		}
	},
	CALC('\u0029', true, false, true) {
		@Override
		public void onExecute(Scope scope, Line line) {
			if (line.getArgsSize() == 2) {
				scope.setVar(line.getArg(0), scope.getField(line.getArg(1)));
			} else if (line.getArgsSize() == 3) {
				scope.setVar(line.getArg(0), PPrimitiveOperatorTarget.calc(
						scope.getField(line.getArg(1)), line.getArg(2), null));
			} else if (line.getArgsSize() == 4) {
				scope.setVar(line.getArg(0), PPrimitiveOperatorTarget.calc(
						scope.getField(line.getArg(1)), line.getArg(2), scope.getField(line.getArg(3))));
			} else {
				throw new ExecutionException("Invalid CALC definition");
			}
		}
	},
	IN('\u002A', true, false, true) {
		@Override
		public void onExecute(Scope scope, Line line) {
			PStream stream = scope.getProcessor().getStream(Integer.parseInt(line.getArg(0)));
			if (!stream.getIOState().canRead())
				throw new IllegalAccessExecption(scope.getProcessor(), "The selected stream has no reading permission");
			scope.setVar(line.getArg(1), PField.newValueDefaultString(stream.read()));
		}
	},
	OUT('\u002B', true, false, true) {
		@Override
		public void onExecute(Scope scope, Line line) {
			PStream stream = scope.getProcessor().getStream(Integer.parseInt(line.getArg(0)));
			if (!stream.getIOState().canWrite())
				throw new IllegalAccessExecption(scope.getProcessor(), "The selected stream has no writing permission");
			stream.write(scope.getField(line.getArg(1)).getPrimitiveValue().asText().asRawString());
		}
	},
	LABEL('\u002C', true, false, true) {
		@Override
		public void onLoad(ApplicationProcessor processor, Line line) {
			processor.registerLabel(line.getArg(0), line.getRelativeLine());
		}
	},
	GOTO('\u002D', true, false, true) {
		@Override
		public void onExecute(Scope scope, Line line) {
			scope.linkTo(scope.getProcessor().getLabelTarget(line.getArg(0)));
		}
	},
	RETURN('\u002E', true, false, true) {
		@Override
		public void onExecute(Scope scope, Line line) {
			scope.getProcessor().returnScope();
		}
	},
	INC('\u002F', true, false, true) {
	   @Override
    	public void onExecute(Scope scope, Line line) {
		   	//TODO: Support to Complex fields
            scope.getField(line.getArg(0)).getPrimitiveValue().asNumeric().inc();
    	}   
	},
	DEC('\u0030', true, false, true) {
	   @Override
    	public void onExecute(Scope scope, Line line) {
		   	//TODO: Support to Complex fields
            scope.getField(line.getArg(0)).getPrimitiveValue().asNumeric().dec();
    	}   
	},
	IF('\u0031', true, false, true) {
		@Override
		public void onExecute(Scope scope, Line line) {
			PField field = scope.getField(line.getArg(0));
			if (field.getType() != PLogical.class)
				throw new ExecutionException("Conditions must have PLogical type");
			
			List<List<String>> thenElse = parseIfArguments(line);
			Line virtualLine = field.getPrimitiveValue().asLogical().asBoolean() ?
				new Line(line.getAbsoluteLine(), line.getRelativeLine(), Keyword.forName(thenElse.get(0).get(0)), 
						thenElse.get(0).subList(1, thenElse.get(0).size())) :
				new Line(line.getAbsoluteLine(), line.getRelativeLine(), Keyword.forName(thenElse.get(1).get(0)), 
						thenElse.get(1).subList(1, thenElse.get(1).size()));
				
			virtualLine.getKeyword().onExecute(scope, virtualLine);
		}

		private List<List<String>> parseIfArguments(Line line) {
			List<List<String>> result = new ArrayList<>();
			
			int i = 1;
			List<String> temp = new ArrayList<>(Arrays.asList(line.getArg(i)));
			if (Keyword.forName(line.getArg(i)) != Keyword.NEXT)
				temp.add(line.getArg(++i));
			result.add(temp);
			
			temp = new ArrayList<>(Arrays.asList(line.getArg(++i)));
			if (Keyword.forName(line.getArg(i)) != Keyword.NEXT)
				temp.add(line.getArg(++i));
			result.add(temp);
			
			return result;
		}
	},
	NEXT('\u0032', false, false, true),
	METHOD('\u0033', true, false, true) {
		@Override
		public void onLoad(ApplicationProcessor processor, Line line) {
			processor.getLastRegisteredProgram().registerMethod(line.getArg(0), 
					new PMethod(processor.getLastRegisteredProgram(), line.getArg(0), 
							line.getRelativeLine(), line));
		}

		@Override
		public void onExecute(Scope scope, Line line) {
			// TODO: Ide jön a változók deklarálása az argumentumok alapján
		}
	},
	CALL('\u0034', true, false, true) {
		@Override
		public void onExecute(Scope scope, Line line) {
			if (scope.containsField(line.getArg(0))) {
				PField cplx = scope.getField(line.getArg(0));

				if (!cplx.isComplex())
					throw new ExecutionException("Cannot CALL not complex type");
				
				if (line.getArgsSize() > 2 && Keyword.forNameSafe(line.getLastNthArg(2)) == Keyword.TEMP) {
					scope.addTemp(line.getLastNthArg(1), cplx.getValue(
							scope.resolve(line.rangeToArray(1, line.getArgsSize() - 2))));
				} else {
					throw new ExecutionException("Invalid CALL definition"); // talán mégis csak, mert function, nem complex
				}
			} else if (scope.getProgram().hasMethod(line.getArg(0))) { 
				PMethod callable = scope.getProgram().getMethod(line.getArg(0));
				
				if (line.getArgsSize() > 2 && Keyword.forNameSafe(line.getLastNthArg(2)) == Keyword.TEMP) {
					scope.getProcessor().addScope(
							new Scope(scope.getProcessor(), callable, scope.getGlobalParentScope())
								.invoke(scope.resolve(line.rangeToArray(1, line.getArgsSize() - 2))));
				} else {
					scope.getProcessor().addScope(
							new Scope(scope.getProcessor(), callable, scope.getGlobalParentScope())
								.invoke(scope.resolve(line.rangeToArray(1, line.getArgsSize()))));
				}
			}
			
			// TODO Function, Imported Function, Program
		}
	},
	END('\u0035', true, false, true) {
		@Override
		public void onExecute(Scope scope, Line line) {
			scope.getProcessor().callExit(scope.getField(line.getArg(0)).getPrimitiveValue());
		}
	},
	SET('\u0036', true, false, true) {
		@Override
		public void onExecute(Scope scope, Line line) {
			if (line.getArgsSize() < 2)
				throw new ExecutionException("Invalid SET definition");
			

			if (line.getArgsSize() == 2) {
				scope.getField(line.getArg(1)).setValue(scope.getField(line.getArg(0)).getValue());
			} else {
				scope.getField(line.getArg(1)).getValue(
						line.rangeToArray(2, line.getArgsSize())).setValue(scope.getField(line.getArg(0)).getValue());
			}
		}
	},
	;
	
	//TODO: Lehet probléma a COMPRESSEDnek a neveikből, szerintem kellene egy prefixum neki egy tiltott karakterrel (pl: &)
	
	private final String semiCompressed;
	private final String fullyCompressed;
	private final boolean allowBuild;
	private final boolean head;
	private final boolean body;
	
	private Keyword(char fullyCompressed, boolean allowBuild, boolean head, boolean body) {
		this.semiCompressed = name();
		this.fullyCompressed = "" + fullyCompressed;
		this.allowBuild = allowBuild;
		this.head = head;
		this.body = body;
	}
	
	private Keyword(String semiCompressed, char fullyCompressed, boolean allowBuild, boolean head, boolean body) {
		this.semiCompressed = semiCompressed;
		this.fullyCompressed = "" + fullyCompressed;
		this.allowBuild = allowBuild;
		this.head = head;
		this.body = body;
	}
	
	/**
	 * Only compatible with {@link #body} = true
	 */
	public void onLoad(ApplicationProcessor processor, Line line) {
		
	}
	
	/**
	 * Only compatible with {@link #head} = true
	 */
	public void onLoad(ApplicationProcessor processor, String[] args) {
		
	}

	public void onExecute(Scope scope, Line line) {
		
	}
	
	public final String getSemiCompressed() {
		return semiCompressed;
	}
	
	public final String getFullyCompressed() {
		return fullyCompressed;
	}
	
	public final boolean isHead() {
		return head;
	}

	public final boolean isBody() {
		return body;
	}

	public final String name(CompileMode cm) {
		if (cm == CompileMode.FULLY_COMPRESSED)
			return fullyCompressed;
		return semiCompressed;
	}

	public final String build(CompilerCore core, Object... parts) {
		if (!allowBuild)
			throw new CompileException(core, "Invalid use of keyword" + name());
		return core.getCompileMode() == CompileMode.FULLY_COMPRESSED ? 
				fullyCompressed + joinObject(core, SEPARATOR.fullyCompressed, parts) : 
					semiCompressed + joinObject(core, SEPARATOR.semiCompressed, parts);
	}

	// Ez talán nem is kell
	public final String build(CompilerCore core, String... parts) {
		if (!allowBuild)
			throw new CompileException(core, "Invalid use of keyword" + name());
		return core.getCompileMode() == CompileMode.FULLY_COMPRESSED
				? fullyCompressed + (parts.length > 0 ? SEPARATOR.fullyCompressed : "") 
						+ String.join(SEPARATOR.fullyCompressed, parts)
				: semiCompressed + (parts.length > 0 ? SEPARATOR.semiCompressed : "")
						+ String.join(SEPARATOR.semiCompressed, parts);
	}
	
	/**
	 * @warning Use {@link Keyword#build(CompilerCore, Object...)} insted if possible
	 */
	public final String getByCore(CoreProcessor core) {
		if (this == Keyword.SEPARATOR)
			return core.getCompileMode() == CompileMode.FULLY_COMPRESSED ? 
					SEPARATOR.fullyCompressed : 
					SEPARATOR.semiCompressed;
		return core.getCompileMode() == CompileMode.FULLY_COMPRESSED ? 
				fullyCompressed + SEPARATOR.fullyCompressed : 
				semiCompressed + SEPARATOR.semiCompressed;
	}
	
	private final String joinObject(CompilerCore core, String separator, Object[] items) {
		String joint = "";
		for (int i = 0; i < items.length; ++i) {
			if (items[i] != null)
				if (items[i] instanceof Keyword)
					joint += separator + (core.getCompileMode() == CompileMode.FULLY_COMPRESSED ? 
							((Keyword)items[i]).fullyCompressed : ((Keyword)items[i]).semiCompressed);
				else if (items[i] instanceof Operator)
					joint += separator + (core.getCompileMode() == CompileMode.FULLY_COMPRESSED ? 
							((Operator)items[i]).getFullyCompressed() : ((Operator)items[i]).getSemiCompressed());
				else if (items[i] instanceof List)
					joint += joinObject(core, separator, (List<?>)items[i]);
				else
					joint += separator + items[i].toString();
		}
		return joint;
	}
	
	private final String joinObject(CompilerCore core, String separator, List<?> items) {
		String joint = "";
		for (int i = 0; i < items.size(); ++i) {
			if (items.get(i) != null)
				if (items.get(i) instanceof Keyword)
					joint += separator + (core.getCompileMode() == CompileMode.FULLY_COMPRESSED ? 
							((Keyword)items.get(i)).fullyCompressed : ((Keyword)items.get(i)).semiCompressed );
				else if (items.get(i) instanceof Operator)
					joint += separator + (core.getCompileMode() == CompileMode.FULLY_COMPRESSED ? 
							((Operator)items.get(i)).getFullyCompressed() : ((Operator)items.get(i)).getSemiCompressed());
				else if (items.get(i) instanceof List)
					joint += joinObject(core, separator, ((List<?>) items.get(i)));
				else
					joint += separator + items.get(i).toString();
		}
		return joint;
	}

	public static Keyword forName(String string) {
		for (Keyword k : values()) {
			if (k.getSemiCompressed().equals(string) || k.getFullyCompressed().equals(string))
				return k;
		}
		throw new ExecutionException("Invalid keyword '" + string + "'");
	}

	public static Keyword forNameSafe(String string) {
		for (Keyword k : values()) {
			if (k.getSemiCompressed().equals(string) || k.getFullyCompressed().equals(string))
				return k;
		}
		return null;
	}
}
