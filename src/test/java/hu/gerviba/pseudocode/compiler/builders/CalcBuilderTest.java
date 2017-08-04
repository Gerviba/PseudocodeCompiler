package hu.gerviba.pseudocode.compiler.builders;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Ignore;
import org.junit.Test;

import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;

public class CalcBuilderTest {
    
    @Test
    public void testCalcBuilder_func() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program func",
				"    result = 1 + testFunc(10)",
				"    ",
				"    ",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG func$0\n"+
				"METHOD $\n"+
				"CALL testFunc$1 10 TEMP $1\n"+
				"VAR result 1 ADD $1\n"+
				"FREE result\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}
    
    @Test
    public void testCalcBuilder_func2() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program func",
				"    result = 1 + testFunc(10)    (20)",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG func$0\n"+
				"METHOD $\n"+
				"CALL testFunc$2 10 20 TEMP $1\n"+
				"VAR result 1 ADD $1\n"+
				"FREE result\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}
    
    //TODO: funcMinimal (0 args), funcMinimal2 (1 + nélkül)
    
    @Test
    public void testCalcBuilder_funcDef() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program funcDef",
				"    Eljárás methodDef",
				"        KI: \"Ez eljárásban van\"",
				"    Eljárás vége",
				"    ",
				"    result := 1 + methodDef()",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG funcDef$0\n"+
				"METHOD $\n"+
				"CALL methodDef$0 TEMP $1\n"+
				"VAR result 1 ADD $1\n"+
				"FREE result\n"+
				"RETURN\n"+
				"METHOD methodDef$0\n"+
				"OUT 0 \"Ez eljárásban van\"\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}
    
    @Test
    public void testCalcBuilder_funcDef2() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program funcDef2",
				"    Eljárás methodDef",
				"        KI: \"Ez eljárásban van\"",
				"    Eljárás vége",
				"    ",
				"    result := methodDef() + 1",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG funcDef2$0\n"+
				"METHOD $\n"+
				"CALL methodDef$0 TEMP $1\n"+
				"VAR result $1 ADD 1\n"+
				"FREE result\n"+
				"RETURN\n"+
				"METHOD methodDef$0\n"+
				"OUT 0 \"Ez eljárásban van\"\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}

    @Test
    public void testCalcBuilder_funcDef3() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program funcDef3",
				"    Eljárás methodDef",
				"        KI: \"Ez eljárásban van\"",
				"    Eljárás vége",
				"    ",
				"    result := 1 + methodDef() + 1",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG funcDef3$0\n"+
				"METHOD $\n"+
				"CALL methodDef$0 TEMP $1\n"+
				"TEMP $2 1 ADD $1\n"+
				"VAR result $2 ADD 1\n"+
				"FREE result\n"+
				"RETURN\n"+
				"METHOD methodDef$0\n"+
				"OUT 0 \"Ez eljárásban van\"\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}
    
	@Test
    public void testCalcBuilder_eq() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program eqTest",
				"    i := 0",
				"    i := i=1",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG eqTest$0\n"+
				"METHOD $\n"+
				"VAR i 0\n"+
				"CALC i i EQ 1\n"+
				"FREE i\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}

	@Test
    public void testCalcBuilder_neq() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program neqTest",
				"    i := 0",
				"    i := i!=1",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG neqTest$0\n"+
				"METHOD $\n"+
				"VAR i 0\n"+
				"CALC i i NEQ 1\n"+
				"FREE i\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}

	@Test
    public void testCalcBuilder_xor() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program xorTest",
				"    i := 0",
				"    i := i^1",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG xorTest$0\n"+
				"METHOD $\n"+
				"VAR i 0\n"+
				"CALC i i XOR 1\n"+
				"FREE i\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}

	@Test
    public void testCalcBuilder_neg() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program negTest",
				"    i := 0",
				"    i := -i",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG negTest$0\n"+
				"METHOD $\n"+
				"VAR i 0\n"+
				"CALC i i NEG\n"+
				"FREE i\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}
	
	@Test
    public void testCalcBuilder_neqComplex() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program preformat",
				"    i := 0",
				"    i := i<=1*1",
				"    i := i!=1*1",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG preformat$0\n"+
				"METHOD $\n"+
				"VAR i 0\n"+
				"TEMP $1 1 MUL 1\n"+
				"CALC i i LE $1\n"+
				"TEMP $2 1 MUL 1\n"+
				"CALC i i NEQ $2\n"+
				"FREE i\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}
	
	@Ignore(value = "Not a bug [Miért is?]")
	@Test
    public void testCalcBuilder_minusMul() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program preformat",
				"	 i := 0",
				"    i := -1*1",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG preformat$0\n"+
				"METHOD $\n"+
				"VAR i 0\n"+
				"CALC i -1 MUL 1\n"+
				"FREE i\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}
	
	@Test
    public void testCalcBuilder_preformat() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program preformat",
				"    i := 0",
				"    i := i+1",
				"    i := i-1",
				"    i := i/1",
				"    i := i*1",
				"    i := i%1",
				"    i := i=1",
				"    i := i>=1",
				"    i := i<=1",
				"    i := i!=1",
				"    i := i>>1",
				"    i := i>>>1",
				"    i := i<<1",
				"    i := i<1",
				"    i := i>1",
				"    i := i&1",
				"    i := i&&1",
				"    i := i|1",
				"    i := i||1",
				"    i := i^1",
				"    i := -i",
				"    i := ~i",
				"    i := igaz",
				"    i := !i",
				"    i := -1",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG preformat$0\n"+
				"METHOD $\n"+
				"VAR i 0\n"+
				"CALC i i ADD 1\n"+
				"CALC i i SUB 1\n"+
				"CALC i i DIV 1\n"+
				"CALC i i MUL 1\n"+
				"CALC i i MOD 1\n"+
				"CALC i i EQ 1\n"+
				"CALC i i GE 1\n"+
				"CALC i i LE 1\n"+
				"CALC i i NEQ 1\n"+
				"CALC i i SL 1\n"+
				"CALC i i CL 1\n"+
				"CALC i i SR 1\n"+
				"CALC i i LT 1\n"+
				"CALC i i GT 1\n"+
				"CALC i i BAND 1\n"+
				"CALC i i AND 1\n"+
				"CALC i i BOR 1\n"+
				"CALC i i OR 1\n"+
				"CALC i i XOR 1\n"+
				"CALC i i NEG\n"+
				"CALC i i BNEG\n"+
				"CALC i igaz\n"+
				"CALC i i NOT\n"+
				"CALC i -1\n"+
				"FREE i\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}
    
	@Test
    public void testCalcBuilder_preformatComplex() throws Exception {
		LinkedList<String> preformattedCode = new LinkedList<>(Arrays.asList(
				"# @Debug OFF",
				"Program preformat",
				"    i := 0",
				"    i := i+1*1",
				"    i := i-1*1",
				"    i := i/1*1",
				"    i := i*1*1",
				"    i := i%1*1",
				"    i := i=1*1",
				"    i := i>=1*1",
				"    i := i<=1*1",
				"    i := i!=1*1",
				"    i := i>>1*1",
				"    i := i>>>1*1",
				"    i := i<<1*1",
				"    i := i<1*1",
				"    i := i>1*1",
				"    i := i&1*1",
				"    i := i&&1*1",
				"    i := i|1*1",
				"    i := i||1*1",
				"    i := i^1*1",
				"    i := -i*1",
				"    i := ~i*1",
				"    i := igaz",
				"    i := !i*1",
				"    i := -1*1",
				"Program vége"
			));
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.loadLines(preformattedCode).loadDirectives().initHeader().startCompile();
		
		assertEquals(
				"PROG preformat$0\n"+
				"METHOD $\n"+
				"VAR i 0\n"+
				"TEMP $1 1 MUL 1\n"+
				"CALC i i ADD $1\n"+
				"TEMP $2 1 MUL 1\n"+
				"CALC i i SUB $2\n"+
				"TEMP $3 i DIV 1\n"+
				"CALC i $3 MUL 1\n"+
				"TEMP $4 i MUL 1\n"+
				"CALC i $4 MUL 1\n"+
				"TEMP $5 i MOD 1\n"+
				"CALC i $5 MUL 1\n"+
				"TEMP $6 1 MUL 1\n"+
				"CALC i i EQ $6\n"+
				"TEMP $7 1 MUL 1\n"+
				"CALC i i GE $7\n"+
				"TEMP $8 1 MUL 1\n"+
				"CALC i i LE $8\n"+
				"TEMP $9 1 MUL 1\n"+
				"CALC i i NEQ $9\n"+
				"TEMP $10 i SL 1\n"+
				"CALC i $10 MUL 1\n"+
				"TEMP $11 i CL 1\n"+
				"CALC i $11 MUL 1\n"+
				"TEMP $12 i SR 1\n"+
				"CALC i $12 MUL 1\n"+
				"TEMP $13 1 MUL 1\n"+
				"CALC i i LT $13\n"+
				"TEMP $14 1 MUL 1\n"+
				"CALC i i GT $14\n"+
				"TEMP $15 1 MUL 1\n"+
				"CALC i i BAND $15\n"+
				"TEMP $16 1 MUL 1\n"+
				"CALC i i AND $16\n"+
				"TEMP $17 1 MUL 1\n"+
				"CALC i i BOR $17\n"+
				"TEMP $18 1 MUL 1\n"+
				"CALC i i OR $18\n"+
				"TEMP $19 1 MUL 1\n"+
				"CALC i i XOR $19\n"+
				"TEMP $20 i NEG\n"+
				"CALC i $20 MUL 1\n"+
				"TEMP $21 i BNEG\n"+
				"CALC i $21 MUL 1\n"+
				"CALC i igaz\n"+
				"TEMP $22 i NOT\n"+
				"CALC i $22 MUL 1\n"+
				"TEMP $23 1 NEG\n"+
				"CALC i $23 MUL 1\n"+
				"FREE i\n"+
				"RETURN\n"+
				"END 0",
				cc.getCompiledBody());
	}
	
}
