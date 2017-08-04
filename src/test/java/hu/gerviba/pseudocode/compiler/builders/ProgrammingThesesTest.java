package hu.gerviba.pseudocode.compiler.builders;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;

import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;
import hu.gerviba.pseudocode.interpreter.ApplicationProcessor;
import hu.gerviba.pseudocode.streams.PJUnitStream;

public class ProgrammingThesesTest {

	@Test
	public void minSort() throws Exception {
		LinkedList<String> code = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program test",
				"    a := (10, 210, 213, 312, -1, -30, 230, 500, 0, 1001)",
				"    n := 10",
				// Rendezés
				"    Ciklus i := 1-től n-1-ig",
				"        min := i",
				"        Ciklus j := i + 1-től n-ig",
				"            Ha a[min] > a[j] akkor",
				"                min := j",
				"            elág. vége",
				"        Ciklus vége",
				"        ",
				"        temp := a[min]",
				"        a[min] := a[i]",
				"        a[i] := temp",
				"    Ciklus vége",
				// Kiírás
				"    Ciklus i := 1-től n-ig",
				"        KI: (JUnit) a[i]",
				"    Ciklus vége",
				"Program vége"
			));
		CompilerCore core = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		core.loadLines(code).loadDirectives().initHeader().startCompile();
//		System.out.println(core.getFullCompiledCode());
		
		ApplicationProcessor processor = new ApplicationProcessor(CompileMode.SEMI_COMPRESSED);
		processor.loadFromString(core.getFullCompiledCode());
		processor.loadHeader().loadPrograms().runProgram();
		
		assertArrayEquals(new Integer[] {-30, -1, 0, 10, 210, 213, 230, 312, 500, 1001}, 
				((PJUnitStream) processor.getStream(1)).getStdOut().stream()
					.map(Integer::parseInt)
					.toArray(size -> new Integer[size]));
		
		// DEBUG:
//		System.out.println("PseudoJUnitStream -> minSort:");
//		System.out.println(String.join("\n", ((PJUnitStream) processor.getStream(1)).getStdOut()));
	}
	
}
