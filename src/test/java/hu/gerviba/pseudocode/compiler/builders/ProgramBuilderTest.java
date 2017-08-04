package hu.gerviba.pseudocode.compiler.builders;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;

import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;
import hu.gerviba.pseudocode.exceptions.CompileException;

public class ProgramBuilderTest {

	@Test
	public void testProgramBuilder_withoutProgramKeyword() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"test1",
				"    i := 1",
				"    i := i + 1",
				"    KI: i",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG test1$0\n"+
				"METHOD $\n"+
				"VAR i 1\n"+
				"CALC i i ADD 1\n"+
				"OUT 0 i\n"+
				"FREE i\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
		
	}

	@Test
	public void testProgramBuilder_argument() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"testArg N = 1",
				"    i := 1",
				"    i := i + 1",
				"    KI: i",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals( 
				"PROG testArg$0\n"+
				"METHOD $\n"+
				"VAR N 1\n"+
				"VAR i 1\n"+
				"CALC i i ADD 1\n"+
				"OUT 0 i\n"+
				"FREE N\n"+
				"FREE i\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
		
	}
	
	@Test
	public void testProgramBuilder_argumentMultiple() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"testArgMul N = 1, J = 40; K = 20",
				"    i := 1",
				"    i := i + 1",
				"    KI: i",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG testArgMul$0\n"+
				"METHOD $\n"+
				"VAR N 1\n"+
				"VAR J 40\n"+
				"VAR K 20\n"+
				"VAR i 1\n"+
				"CALC i i ADD 1\n"+
				"OUT 0 i\n"+
				"FREE N\n"+
				"FREE J\n"+
				"FREE K\n"+
				"FREE i\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
		
	}
	
	@Test
	public void testProgramBuilder_argumentBracket() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"testArgBrckt (N = 1, J = 40, K = 20)",
				"    i := 1",
				"    i := i + 1",
				"    KI: i",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG testArgBrckt$0\n"+
				"METHOD $\n"+
				"VAR N 1\n"+
				"VAR J 40\n"+
				"VAR K 20\n"+
				"VAR i 1\n"+
				"CALC i i ADD 1\n"+
				"OUT 0 i\n"+
				"FREE N\n"+
				"FREE J\n"+
				"FREE K\n"+
				"FREE i\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
		
	}
	
	@Test
	public void testProgramBuilder_argumentMutliBracket() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"testMulBrckt (N = 1, J = ( 40 + 50 ), K := 20)",
				"    i := 1",
				"    i := i + 1",
				"    KI: i",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG testMulBrckt$0\n"+
				"METHOD $\n"+
				"VAR N 1\n"+
				"VAR J 40 ADD 50\n"+
				"VAR K 20\n"+
				"VAR i 1\n"+
				"CALC i i ADD 1\n"+
				"OUT 0 i\n"+
				"FREE N\n"+
				"FREE J\n"+
				"FREE K\n"+
				"FREE i\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
		
	}
	
	@Test
	public void testProgramBuilder_argumentMutliBracket2() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"testMulBrckt (N = 1, J = ( 40 + 50 ), K := 20, varO, varOO, varOOO)",
				"    i := 1",
				"    i := i + 1",
				"    KI: i",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG testMulBrckt$3 varO varOO varOOO\n"+
				"METHOD $\n"+
				"VAR N 1\n"+
				"VAR J 40 ADD 50\n"+
				"VAR K 20\n"+
				"VAR i 1\n"+
				"CALC i i ADD 1\n"+
				"OUT 0 i\n"+
				"FREE N\n"+
				"FREE J\n"+
				"FREE K\n"+
				"FREE varO\n"+
				"FREE varOO\n"+
				"FREE varOOO\n"+
				"FREE i\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
		
	}
	
	@Test
	public void testProgramBuilder_argumentMultiline() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"testMulLine (N = 1",
				"       J = 40,",
				"       K = 20)",
				"    i := 1",
				"    i := i + 1",
				"    KI: i",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG testMulLine$0\n"+
				"METHOD $\n"+
				"VAR N 1\n"+
				"VAR J 40\n"+
				"VAR K 20\n"+
				"VAR i 1\n"+
				"CALC i i ADD 1\n"+
				"OUT 0 i\n"+
				"FREE N\n"+
				"FREE J\n"+
				"FREE K\n"+
				"FREE i\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
		
	}
	
	@Test
	public void testProgramBuilder_argumentMultilineUninitialized() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"testMulLineU (N",
				"       J, O",
				"       K)",
				"    i := 1",
				"    i := i + 1",
				"    KI: i",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG testMulLineU$4 N J O K\n"+
				"METHOD $\n"+
				"VAR i 1\n"+
				"CALC i i ADD 1\n"+
				"OUT 0 i\n"+
				"FREE N\n"+
				"FREE J\n"+
				"FREE O\n"+
				"FREE K\n"+
				"FREE i\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
		
	}
	
	@Test(expected = CompileException.class)
	public void testProgramBuilder_invalidName1() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"1test (N",
				"       J, O",
				"       K)",
				"    i := 1",
				"    i := i + 1",
				"    KI: i",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
	}
	
	@Test(expected = CompileException.class)
	public void testProgramBuilder_invalidName2() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"test$1 (N",
				"       J, O",
				"       K)",
				"    i := 1",
				"    i := i + 1",
				"    KI: i",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
	}
}
