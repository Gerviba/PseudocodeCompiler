package hu.gerviba.pseudocode.compiler.builders;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;

import hu.gerviba.pseudocode.compiler.builders.CompilerCore;
import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;

public class LoopBuilderTest {
	
	@Test
	public void testIfBuilder_while() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program while",
				"    i := 1",
				"    Ciklus amíg i <= 10",
				"        KI: \"i=\" + i",
				"        i := i + 1",
				"    Ciklus vége",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG while$0\n" +
				"METHOD $\n" +
				"VAR i 1\n" +
				"LABEL 0\n" +
				"TEMP $1 i LE 10\n" +
				"IF $1 NEXT GOTO 1\n" +
				"TEMP $2 \"i=\" ADD i\n" +
				"OUT 0 $2\n" +
				"CALC i i ADD 1\n" + // INC i
				"GOTO 0\n" +
				"LABEL 1\n" +
				"FREE i\n" +
				"RETURN\n" +
				"END 0",
				cc.getCompiledBody());
	}
	
	@Test
	public void testIfBuilder_doWhile() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program doWhile",
				"    i := 1",
				"    Ciklus",
				"        KI: \"i=\" + i",
				"        i := i + 1",
				"    Amíg i < 10",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG doWhile$0\n" +
				"METHOD $\n" +
				"VAR i 1\n" +
				"LABEL 0\n" +
				"TEMP $1 \"i=\" ADD i\n" +
				"OUT 0 $1\n" +
				"CALC i i ADD 1\n" +
				"LABEL 1\n" +
				"TEMP $2 i LT 10\n" +
				"IF $2 GOTO 0 NEXT\n" +
				"LABEL 2\n" +
				"FREE i\n" +
				"RETURN\n" +
				"END 0",
				cc.getCompiledBody());
	}

	@Test
	public void testIfBuilder_for() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program for",
				"    Ciklus i := 1-től 10-ig egyesével",
				"        KI: \"i=\" + i",
				"    Ciklus vége",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG for$0\n" +
				"METHOD $\n" +
				"VAR i 1\n" +
				"LABEL 0\n" +
				"TEMP $1 i LE 10\n" +
				"IF $1 NEXT GOTO 1\n" +
				"TEMP $2 \"i=\" ADD i\n" +
				"OUT 0 $2\n" +
				"INC i\n" +
				"GOTO 0\n" +
				"LABEL 1\n" +
				"FREE i\n" +
				"RETURN\n" +
				"END 0",
				cc.getCompiledBody());
	}
	
	@Test
	public void testIfBuilder_for2() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program for2",
				"    j := 2",
				"    Ciklus i := j - 1-től j + 10-ig egyesével",
				"        KI: \"i=\" + i",
				"    Ciklus vége",
				"    j = j + 50",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG for2$0\n" +
				"METHOD $\n" +
				"VAR j 2\n" +
				"VAR i j SUB 1\n" +
				"LABEL 0\n" +
				"TEMP $1 j ADD 10\n" +
				"TEMP $2 i LE $1\n" +
				"IF $2 NEXT GOTO 1\n" +
				"TEMP $3 \"i=\" ADD i\n" +
				"OUT 0 $3\n" +
				"INC i\n" +
				"GOTO 0\n" +
				"LABEL 1\n" +
				"FREE i\n" +
				"CALC j j ADD 50\n" +
				"FREE j\n" +
				"RETURN\n" +
				"END 0",
				cc.getCompiledBody());
	}	
	
	@Test
	public void testIfBuilder_for3() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program for3",
				"    j := 2",
				"    Ciklus i := j - 1-től j + 10-ig (3)-asával",
				"        KI: \"i=\" + i",
				"    Ciklus vége",
				"    j = j + 50",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG for3$0\n" +
				"METHOD $\n" +
				"VAR j 2\n" +
				"VAR i j SUB 1\n" +
				"LABEL 0\n" +
				"TEMP $1 j ADD 10\n" +
				"TEMP $2 i LE $1\n" +
				"IF $2 NEXT GOTO 1\n" +
				"TEMP $3 \"i=\" ADD i\n" +
				"OUT 0 $3\n" +
				"CALC i i ADD 3\n" + // INC i
				"GOTO 0\n" +
				"LABEL 1\n" +
				"FREE i\n" +
				"CALC j j ADD 50\n" +
				"FREE j\n" +
				"RETURN\n" +
				"END 0",
				cc.getCompiledBody());
	}
	
	@Test
	public void testIfBuilder_for4() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program for3",
				"    j := 2",
				"    Ciklus i := 100-tól 1-ig (-3)-asával",
				"        KI: \"i=\" + i",
				"    Ciklus vége",
				"    j = j + 50",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG for3$0\n" +
				"METHOD $\n" +
				"VAR j 2\n" +
				"VAR i 100\n" +
				"LABEL 0\n" +
				"TEMP $1 i GE 1\n" +
				"IF $1 NEXT GOTO 1\n" +
				"TEMP $2 \"i=\" ADD i\n" +
				"OUT 0 $2\n" +
				"CALC i i SUB 3\n" +
				"GOTO 0\n" +
				"LABEL 1\n" +
				"FREE i\n" +
				"CALC j j ADD 50\n" +
				"FREE j\n" +
				"RETURN\n" +
				"END 0",
				cc.getCompiledBody());
	}

	@Test
	public void testIfBuilder_for5() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program for2",
				"    j := 2",
				"    Ciklus i := j - 1-től j + 10-ig",
				"        KI: \"i=\" + i",
				"    Ciklus vége",
				"    j = j + 50",
				"Program vége"
				));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG for2$0\n" +
						"METHOD $\n" +
						"VAR j 2\n" +
						"VAR i j SUB 1\n" +
						"LABEL 0\n" +
						"TEMP $1 j ADD 10\n" +
						"TEMP $2 i LE $1\n" +
						"IF $2 NEXT GOTO 1\n" +
						"TEMP $3 \"i=\" ADD i\n" +
						"OUT 0 $3\n" +
						"INC i\n" +
						"GOTO 0\n" +
						"LABEL 1\n" +
						"FREE i\n" +
						"CALC j j ADD 50\n" +
						"FREE j\n" +
						"RETURN\n" +
						"END 0",
						cc.getCompiledBody());
	}	
}

