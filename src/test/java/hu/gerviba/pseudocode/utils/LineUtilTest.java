package hu.gerviba.pseudocode.utils;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.LinkedList;

import org.junit.Test;

import debugutil.Penetration;
import hu.gerviba.pseudocode.CoreProcessor;
import hu.gerviba.pseudocode.compiler.builders.CompilerCore;
import hu.gerviba.pseudocode.compiler.modifiers.CompileMode;
import hu.gerviba.pseudocode.compiler.units.RelativeLine;
import hu.gerviba.pseudocode.exceptions.CompileException;

public class LineUtilTest {

	@Test
	public void testGetLineParts() throws Exception {
		assertEquals(Arrays.asList(), 
		              LineUtil.getLineCommands("", newCompilerCoreMultiLineComment(false)));
		assertEquals(Arrays.asList(" "), 
		              LineUtil.getLineCommands("    //", newCompilerCoreMultiLineComment(false)));
		assertEquals(Arrays.asList(" "), 
		              LineUtil.getLineCommands("    //sdf", newCompilerCoreMultiLineComment(false)));
		assertEquals(Arrays.asList("i := i + 1"), 
		              LineUtil.getLineCommands("i := i      + 1", newCompilerCoreMultiLineComment(false)));
		assertEquals(Arrays.asList("i := i + 1", " j := j + 3"), 
		              LineUtil.getLineCommands("i := i + 1; j := j + 3", newCompilerCoreMultiLineComment(false)));
		assertEquals(Arrays.asList(), 
		              LineUtil.getLineCommands("i := i + 1; j := j \t +    3", newCompilerCoreMultiLineComment(true)));
		assertEquals(Arrays.asList(), 
		              LineUtil.getLineCommands("i := i + 1; j := j + 3*/", newCompilerCoreMultiLineComment(true)));
		assertEquals(Arrays.asList("j := j + 3"), 
		              LineUtil.getLineCommands("i := i + 1; */j := j + 3", newCompilerCoreMultiLineComment(true)));
		assertEquals(Arrays.asList("j := j + 3", " k := 4"), 
		              LineUtil.getLineCommands("i := i + 1; */j := j + 3; k := 4;", newCompilerCoreMultiLineComment(true)));
		assertEquals(Arrays.asList("i := i + 1", " j := j + 34", " k := 3"), 
		              LineUtil.getLineCommands("i := i + 1; j := j + 3/*sajt*/4; k := 3", newCompilerCoreMultiLineComment(false)));
		assertEquals(Arrays.asList("i := i + 1", " j := j + 34", " k := 3"), 
		              LineUtil.getLineCommands("i := i + 1; j := j + 34; k := 3/*82", newCompilerCoreMultiLineComment(false)));
		assertEquals(Arrays.asList(" j := j + 34", " k := 3"), 
		              LineUtil.getLineCommands("i := i + 1;*/ j := j + 34; k := 3/*82", newCompilerCoreMultiLineComment(true)));
		assertEquals(Arrays.asList("i := i + 1", " j := j * 3"), 
		              LineUtil.getLineCommands("i := i + 1; j := j * 3", newCompilerCoreMultiLineComment(false)));
		assertEquals(Arrays.asList("i := i + 1", " j := j / 3"), 
		              LineUtil.getLineCommands("i := i + 1; j := j / 3", newCompilerCoreMultiLineComment(false)));
		assertEquals(Arrays.asList("i := i + 1"), 
		              LineUtil.getLineCommands("i := i + 1//4; j := j * 3", newCompilerCoreMultiLineComment(false)));
	    assertEquals(Arrays.asList("i := \"They\\\"re /*\" + 20"), 
		              LineUtil.getLineCommands("i := \"They\\\"re /*\" + 20", newCompilerCoreMultiLineComment(false)));
		assertEquals(Arrays.asList("i := 30 + 10 - 74 * 30 % 30 << 10 >> 54 >>> 1 <= 30 => 20 == 30 != 90 & 10 && 20 | 30 || 40 < 10 > 20"), 
		              LineUtil.getLineCommands("i := 30+10-74*30%30<<10>>54>>>1<=30=>20==30!=90&10&&20|30||40<10>20", newCompilerCoreMultiLineComment(false)));
		assertEquals(Arrays.asList("i := !30 ~20 - -10 + -40"), 
		              LineUtil.getLineCommands("i := !30 ~20--10+-40", newCompilerCoreMultiLineComment(false)));
		assertEquals(Arrays.asList("i := !func() && f != 2"), 
	              LineUtil.getLineCommands("i := NEM func() ÉS f != 2", newCompilerCoreMultiLineComment(false)));
	}
	
	@Test(expected=CompileException.class)
	public void testGetLineParts_illegalOperator() throws Exception {
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.setMultilineCommentStarted(false);
		Penetration.setValue("lines", cc, new LinkedList<RelativeLine>(Arrays.asList(new RelativeLine(0, "test"))));
		LineUtil.getLineCommands("i := i + 1;*/ j := j + 3", cc);
	}
	
	private static CompilerCore newCompilerCoreMultiLineComment(boolean multiLineComment) {
		CompilerCore cc = new CompilerCore(CompileMode.SEMI_COMPRESSED);
		cc.setMultilineCommentStarted(multiLineComment);
		return cc;
	}
	
	@Test
	public void testSplitLine() throws Exception {
		CoreProcessor core = new CoreProcessor() {
			@Override
			public CompileMode getCompileMode() {
				return CompileMode.SEMI_COMPRESSED;
			}
		};
		
		assertEquals(LineUtil.splitLine("CALC i i ADD 1", core), Arrays.asList("CALC", "i", "i", "ADD", "1"));
		assertEquals(LineUtil.splitLine("VAR i \"asd\"", core), Arrays.asList("VAR", "i", "\"asd\""));
		assertEquals(LineUtil.splitLine("VAR i \"teszt space\"", core), Arrays.asList("VAR", "i", "\"teszt space\""));
		assertEquals(LineUtil.splitLine("VAR i \"teszt \\\"escape\\\"\"", core), Arrays.asList("VAR", "i", "\"teszt \\\"escape\\\"\""));
	}
	
}
