package hu.gerviba.pseudocode.compiler.builders;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;

import hu.gerviba.pseudocode.compiler.builders.CompilerCore;
import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;
import hu.gerviba.pseudocode.exceptions.CompileException;

public class IfBuilderTest {

	@Test
	public void testIfBuilder_if() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program testIf",
				"    Ha i % 5 == 0 akkor",
				"        KI: \"Osztható\"",
				"    elág. vége",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();

		assertEquals(
				"PROG testIf$0\n"+
				"METHOD $\n"+
				"TEMP $1 i MOD 5\n"+
				"TEMP $2 $1 EQ 0\n"+
				"IF $2 NEXT GOTO 0\n"+
				"OUT 0 \"Osztható\"\n"+
				"LABEL 0\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}
	
	@Test
	public void testIfBuilder_ifElse() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program testIfElse",
				"    Ha i % 5 == 0 akkor",
				"        KI: \"Osztható\"",
				"    különben",
				"        KI: \"Nem osztható\"",
				"    elág. vége",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();

		assertEquals( 
				"PROG testIfElse$0\n"+
				"METHOD $\n"+
				"TEMP $1 i MOD 5\n"+
				"TEMP $2 $1 EQ 0\n"+
				"IF $2 NEXT GOTO 0\n"+
				"OUT 0 \"Osztható\"\n"+
				"GOTO 1\n"+
				"LABEL 0\n"+
				"OUT 0 \"Nem osztható\"\n"+
				"LABEL 1\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}
	
	@Test
	public void testIfBuilder_ifThenElse() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program test3",
				"    i := 30 + ( 120 * 9 )",
				"    Ha i == 30 akkor",
				"        i = i + 10",
				"        KI: \"IGAZ\"",
				"    különben",
				"        KI: i",
				"        KI: \"HAMIS\"",
				"    elág. vége",
				"    KI: \"ki nem?\"",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG test3$0\n"+
				"METHOD $\n"+
				"TEMP $1 120 MUL 9\n"+
				"VAR i 30 ADD $1\n"+
				"TEMP $2 i EQ 30\n"+
				"IF $2 NEXT GOTO 0\n"+
				"CALC i i ADD 10\n"+
				"OUT 0 \"IGAZ\"\n"+
				"GOTO 1\n"+
				"LABEL 0\n"+
				"OUT 0 i\n"+
				"OUT 0 \"HAMIS\"\n"+
				"LABEL 1\n"+
				"OUT 0 \"ki nem?\"\n"+
				"FREE i\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}
	
	@Test
	public void testIfBuilder_ifElseIfElse() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program ifElseIfElse",
				"    Ha i % 5 == 0 akkor",
				"        KI: \"5-el osztható\"",
				"    különben ha i % 2 == 0 akkor",
				"        KI: \"2-vel osztható\"",
				"    különben",
				"        KI: \"Egyikkel sem\"",
				"    elág. vége",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();

		assertEquals(
				"PROG ifElseIfElse$0\n"+
				"METHOD $\n"+
				"TEMP $1 i MOD 5\n"+
				"TEMP $2 $1 EQ 0\n"+
				"IF $2 NEXT GOTO 0\n"+
				"OUT 0 \"5-el osztható\"\n"+
				"GOTO 2\n"+
				"LABEL 0\n"+
				"TEMP $3 i MOD 2\n"+
				"TEMP $4 $3 EQ 0\n"+
				"IF $4 NEXT GOTO 1\n"+
				"OUT 0 \"2-vel osztható\"\n"+
				"GOTO 2\n"+
				"LABEL 1\n"+
				"OUT 0 \"Egyikkel sem\"\n"+
				"LABEL 2\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}
	
	@Test
	public void testIfBuilder_ifBoolCondition() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program testIfBoolCondition",
				"    Ha boolVar akkor",
				"        KI: \"Igaz\"",
				"    különben",
				"        KI: \"Hamis\"",
				"    elág. vége",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();

		assertEquals(
				"PROG testIfBoolCondition$0\n"+
				"METHOD $\n"+
				"IF boolVar NEXT GOTO 0\n"+
				"OUT 0 \"Igaz\"\n"+
				"GOTO 1\n"+
				"LABEL 0\n"+
				"OUT 0 \"Hamis\"\n"+
				"LABEL 1\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}
	
	@Test
	public void testIfBuilder_if5xElseIfElse() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program if5xElseIfElse",
				"    Ha 1 * i == 100 akkor",
				"        KI: \"i = 100\"",
				"    különben ha 2 * i == 100 akkor",
				"        KI: \"2i = 100\"",
				"    különben ha 3 * i == 100 akkor",
				"        KI: \"3i = 100\"",
				"    különben ha 4 * i == 100 akkor",
				"        KI: \"4i = 100\"",
				"    különben ha 5 * i == 100 akkor",
				"        KI: \"5i = 100\"",
				"    különben ha 6 * i == 100 akkor",
				"        KI: \"6i = 100\"",
				"    különben",
				"        KI: \"i =\" + i",
				"    elág. vége",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();

		assertEquals(
				"PROG if5xElseIfElse$0\n"+
				"METHOD $\n"+
				"TEMP $1 1 MUL i\n"+
				"TEMP $2 $1 EQ 100\n"+
				"IF $2 NEXT GOTO 0\n"+
				"OUT 0 \"i = 100\"\n"+
				"GOTO 6\n"+
				"LABEL 0\n"+
				"TEMP $3 2 MUL i\n"+
				"TEMP $4 $3 EQ 100\n"+
				"IF $4 NEXT GOTO 1\n"+
				"OUT 0 \"2i = 100\"\n"+
				"GOTO 6\n"+
				"LABEL 1\n"+
				"TEMP $5 3 MUL i\n"+
				"TEMP $6 $5 EQ 100\n"+
				"IF $6 NEXT GOTO 2\n"+
				"OUT 0 \"3i = 100\"\n"+
				"GOTO 6\n"+
				"LABEL 2\n"+
				"TEMP $7 4 MUL i\n"+
				"TEMP $8 $7 EQ 100\n"+
				"IF $8 NEXT GOTO 3\n"+
				"OUT 0 \"4i = 100\"\n"+
				"GOTO 6\n"+
				"LABEL 3\n"+
				"TEMP $9 5 MUL i\n"+
				"TEMP $10 $9 EQ 100\n"+
				"IF $10 NEXT GOTO 4\n"+
				"OUT 0 \"5i = 100\"\n"+
				"GOTO 6\n"+
				"LABEL 4\n"+
				"TEMP $11 6 MUL i\n"+
				"TEMP $12 $11 EQ 100\n"+
				"IF $12 NEXT GOTO 5\n"+
				"OUT 0 \"6i = 100\"\n"+
				"GOTO 6\n"+
				"LABEL 5\n"+
				"TEMP $13 \"i =\" ADD i\n"+
				"OUT 0 $13\n"+
				"LABEL 6\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}

	@Test
	public void testIfBuilder_ifElseIf() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program ifElseIf",
				"    Ha i % 5 == 0 akkor",
				"        KI: \"5-el osztható\"",
				"    különben ha i % 2 == 0 akkor",
				"        KI: \"2-vel osztható\"",
				"    elág. vége",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();

		assertEquals(
				"PROG ifElseIf$0\n"+
				"METHOD $\n"+
				"TEMP $1 i MOD 5\n"+
				"TEMP $2 $1 EQ 0\n"+
				"IF $2 NEXT GOTO 0\n"+
				"OUT 0 \"5-el osztható\"\n"+
				"GOTO 1\n"+
				"LABEL 0\n"+
				"TEMP $3 i MOD 2\n"+
				"TEMP $4 $3 EQ 0\n"+
				"IF $4 NEXT GOTO 1\n"+
				"OUT 0 \"2-vel osztható\"\n"+
				"LABEL 1\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}

	@Test(expected = CompileException.class)
	public void testIfBuilder_ifError() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program ifElseIfElse",
				"    Ha i % 5 == 0 akkor",
				"        KI: \"5-el osztható\"",
				"    különben",
				"        KI: \"Egyikkel sem\"",
				"    különben ha i % 2 == 0 akkor",
				"        KI: \"2-vel osztható\"",
				"    elág. vége",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
	}
	
	@Test
	public void testIfBuilder_ifFree() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program ifElseIfElse",
				"    Ha i % 5 == 0 akkor",
				"        KI: \"5-el osztható\"",
				"    különben ha i % 2 == 0 akkor",
				"        var := 30",
				"        KI: \"2-vel osztható\"",
				"    különben",
				"        KI: \"Egyikkel sem\"",
				"    elág. vége",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();

		assertEquals(
				"PROG ifElseIfElse$0\n"+
				"METHOD $\n"+
				"TEMP $1 i MOD 5\n"+
				"TEMP $2 $1 EQ 0\n"+
				"IF $2 NEXT GOTO 0\n"+
				"OUT 0 \"5-el osztható\"\n"+
				"GOTO 2\n"+
				"LABEL 0\n"+
				"TEMP $3 i MOD 2\n"+
				"TEMP $4 $3 EQ 0\n"+
				"IF $4 NEXT GOTO 1\n"+
				"VAR var 30\n"+
				"OUT 0 \"2-vel osztható\"\n"+
				"FREE var\n"+
				"GOTO 2\n"+
				"LABEL 1\n"+
				"OUT 0 \"Egyikkel sem\"\n"+
				"LABEL 2\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}
	
}
