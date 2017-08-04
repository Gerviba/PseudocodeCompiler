package hu.gerviba.pseudocode.utils;

import static org.junit.Assert.*;
import static hu.gerviba.pseudocode.utils.FormatUtil.*;

import java.util.Base64;

import org.junit.Test;

import hu.gerviba.pseudocode.lang.primitive.PNumeric;

public class FormatUtilTest {

	@Test
	public void testIsInteger_other() throws Exception {
		assertFalse(isInteger(""));
		assertFalse(isInteger("sajt"));
		assertFalse(isInteger("true"));
	}
	
	@Test
	public void testIsInteger_decimalNumber() throws Exception {
		assertTrue(isInteger("129"));
		assertFalse(isInteger("-"));
		assertFalse(isInteger("1.2"));
		assertTrue(isInteger("0"));
		assertTrue(isInteger("1"));
		assertFalse(isInteger("0f"));
		assertTrue(isInteger(""+Integer.MIN_VALUE));
		assertTrue(isInteger(""+Integer.MAX_VALUE));
		assertTrue(isInteger(""+Long.MIN_VALUE));
		assertTrue(isInteger(""+Long.MAX_VALUE));
	}
	
	@Test
	public void testIsInteger_hexadecimalNumber() throws Exception {
		assertTrue(isInteger("0xABCDEF"));
		assertTrue(isInteger("0X0123456789ABCDEF"));
		assertTrue(isInteger("0x0"));
		assertFalse(isInteger("0xABCP"));
		assertFalse(isInteger("0x"));
		assertTrue(isInteger("0xaAbBcC"));
		assertTrue(isInteger("0x1000"));
		assertFalse(isInteger("0xG21"));
		assertFalse(isInteger("F"));
	}
	
	@Test
	public void testIsInteger_binaryNumber() throws Exception {
		assertTrue(isInteger("0b00100101"));
		assertFalse(isInteger("0b011012"));
		assertTrue(isInteger("0B11110"));
		assertFalse(isInteger("0b"));
	}
	
	@Test
	public void testIsInteger_ocatlNumber() throws Exception {
		assertTrue(isInteger("0667"));
		assertFalse(isInteger("08"));
		assertFalse(isInteger("001"));
		assertTrue(isInteger("00"));
		assertFalse(isInteger("000"));
		assertFalse(isInteger("0000"));
	}
	
	@Test
	public void testIsReal() throws Exception {
		assertTrue(isReal("0.0"));
		assertTrue(isReal("-.0"));
		assertTrue(isReal("-0.0"));
		assertTrue(isReal("-1"));
		assertTrue(isReal("-0"));
		assertTrue(isReal("10.3213"));
		assertTrue(isReal("69.69"));
		assertTrue(isReal("3213.23213"));
		assertTrue(isReal("31321.12e30"));
		assertTrue(isReal("10e1023"));
		assertTrue(isReal(".2e20"));
		assertTrue(isReal("1.0e-10"));
		assertTrue(isReal("6920"));
		assertFalse(isReal("6920e10e10"));
		assertFalse(isReal("1.1.1"));
		assertFalse(isReal("00"));
		assertFalse(isReal("10e--2"));
		assertFalse(isReal("63e10000"));
		assertFalse(isReal("2d213"));
		assertFalse(isReal("."));
		assertFalse(isReal(""));
		assertFalse(isReal(".."));
		assertFalse(isReal(" "));
		assertFalse(isReal("kecske"));
		assertFalse(isReal("-001"));
	}

	@Test
	public void testIsLogical_string() throws Exception {
		assertTrue(isLogical("1"));
		assertTrue(isLogical("0"));
		assertTrue(isLogical("true"));
		assertTrue(isLogical("false"));
		assertTrue(isLogical("igaz"));
		assertTrue(isLogical("hamis"));
		assertTrue(isLogical("True"));
		assertTrue(isLogical("False"));
		assertTrue(isLogical("Igaz"));
		assertTrue(isLogical("Hamis"));
		assertTrue(isLogical("TRUE"));
		assertTrue(isLogical("FALSE"));
		assertTrue(isLogical("IGAZ"));
		assertTrue(isLogical("HAMIS"));
		assertTrue(isLogical("hAmiS"));
		assertFalse(isLogical("igen"));
		assertFalse(isLogical("nem"));
		assertFalse(isLogical("Igen"));
		assertFalse(isLogical("Nem"));
		assertFalse(isLogical("IGEN"));
		assertFalse(isLogical("NEM"));
		assertFalse(isLogical("hAmiSS"));
		assertFalse(isLogical("trrue"));
		assertFalse(isLogical("kacsa"));
		assertFalse(isLogical("ttrue"));
		assertFalse(isLogical("falsetrue"));
		assertFalse(isLogical("2"));
		assertFalse(isLogical("00"));
		assertFalse(isLogical("11"));
		assertFalse(isLogical("-1"));
		assertFalse(isLogical(""));
		assertFalse(isLogical(" "));
	}
	
	@Test
	public void testIsLogical_PNumeric() throws Exception {
		assertTrue(isLogical(new PNumeric(1)));
		assertTrue(isLogical(new PNumeric(0)));
		assertTrue(isLogical(new PNumeric("0")));
		assertTrue(isLogical(new PNumeric(1.1D)));
		assertTrue(isLogical(new PNumeric("1.1")));
		assertTrue(isLogical(new PNumeric(1.4)));
		assertTrue(isLogical(new PNumeric("0.0")));
		assertFalse(isLogical(new PNumeric("-0.52")));
		assertFalse(isLogical(new PNumeric("2")));
		assertFalse(isLogical(new PNumeric("42")));
	}
	
	@Test
	public void testIsTrue() throws Exception {
		assertTrue(isTrue("1"));
		assertFalse(isTrue("0"));
		assertTrue(isTrue("true"));
		assertFalse(isTrue("false"));
		assertTrue(isTrue("igaz"));
		assertFalse(isTrue("hamis"));
		assertTrue(isTrue("igen"));
		assertFalse(isTrue("nem"));
		assertTrue(isTrue("True"));
		assertFalse(isTrue("False"));
		assertTrue(isTrue("Igaz"));
		assertFalse(isTrue("Hamis"));
		assertTrue(isTrue("Igen"));
		assertFalse(isTrue("Nem"));
		assertTrue(isTrue("TRUE"));
		assertFalse(isTrue("FALSE"));
		assertTrue(isTrue("IGAZ"));
		assertFalse(isTrue("HAMIS"));
		assertTrue(isTrue("IGEN"));
		assertFalse(isTrue("NEM"));
		assertTrue(isTrue("iGaZ"));
		assertFalse(isTrue("hAmiSS"));
		assertFalse(isTrue("trrue"));
		assertFalse(isTrue("kacsa"));
		assertFalse(isTrue("ttrue"));
		assertFalse(isTrue("falsetrue"));
		assertFalse(isTrue("2"));
		assertFalse(isTrue("00"));
		assertFalse(isTrue("11"));
		assertFalse(isTrue("-1"));
		assertFalse(isTrue(""));
		assertFalse(isTrue(" "));
	}

	@Test
	public void testIsDirective() throws Exception {
		assertTrue(isDirective("# @Debug ON"));
		assertTrue(isDirective("# @Author Gerviba"));
		assertTrue(isDirective("# @Version 1.0.3210b"));
		assertTrue(isDirective("# @Description "
				+ Base64.getEncoder().encodeToString("Ez egy teszt leírás.<br>Tartalmazhat sortörést.".getBytes())));
		assertTrue(isDirective("# @CompileInfo ALL"));
		assertTrue(isDirective("# @CompileInfo\tALL"));
		assertTrue(isDirective("# @PSRE-Min 1.0.0"));
		assertTrue(isDirective("# @Meta KULCS = " 
				+ Base64.getEncoder().encodeToString("Érték".getBytes())));
		assertTrue(isDirective("#@Meta KULCS = HAL"));
		assertTrue(isDirective("#@Meta KULCS = HAL LAL"));
		assertTrue(isDirective("#@Kulcs ERTEK"));
		assertTrue(isDirective("#\t@Kulcs ERTEK"));
		assertTrue(isDirective("#   @abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789   ERTEK/=+==   "));
		assertTrue(isDirective("#   @abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789   ERTEK/=+==\t "));
		assertTrue(isDirective(" \t   \t #\t @abcdefghijklmnopqrstuvwx-asd   1.=   "));
		assertTrue(isDirective("# @abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789AAA BSC="));
		assertFalse(isDirective(" @abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789AAA BSC="));
		assertFalse(isDirective("mnopqrstuvwxyzABC"));
		assertFalse(isDirective("a b c d"));
		assertFalse(isDirective("Program p1 ()"));
	}
	
	@Test
	public void testIsBlankLine() throws Exception {
		assertTrue(isBlankLine(""));
		assertTrue(isBlankLine(" "));
		assertTrue(isBlankLine("\t"));
		assertTrue(isBlankLine("  "));
		assertTrue(isBlankLine("\t\t"));
		assertTrue(isBlankLine(" \t \t\t   "));
		assertTrue(isBlankLine("\t  \t  "));
		assertFalse(isBlankLine("\t\tasd"));
		assertFalse(isBlankLine("  Program  "));
	}

	@Test
	public void testIsSimple() throws Exception {
		assertTrue(isSimple("1"));
		assertTrue(isSimple("123"));
		assertTrue(isSimple("532.85"));
		assertTrue(isSimple("\"teszt szöveg\""));
		assertTrue(isSimple("\"teszt + szöveg\""));
		assertTrue(isSimple("igaz"));
		assertTrue(isSimple("hamis"));
		assertTrue(isSimple("true"));
		assertTrue(isSimple("false"));
		assertTrue(isSimple("_._"));
		assertTrue(isSimple("változó"));
		assertTrue(isSimple("VÁLTOZÓ"));
		assertTrue(isSimple("öüóőúéáűíÖÜÓŐÚÉÁŰÍ"));
		assertTrue(isSimple("-10"));
		assertTrue(isSimple("-10.42"));
		assertFalse(isSimple("-bool"));
		assertFalse(isSimple("+40"));
		assertFalse(isSimple("10 + 20"));
		assertFalse(isSimple("10 20"));
		assertFalse(isSimple("\"20\" 30\""));		
	}
	
	@Test
	public void testTrim() throws Exception {
		assertEquals(trim("     void     "), "void");
		assertEquals(trim("\tvoid\t"), "void");
		assertEquals(trim("  \t  \t\t void  \t  \t\t "), "void");
		assertEquals(trim("  \t  \t\t vo  \t id  \t  \t\t "), "vo  \t id");
	}
	
	@Test
	public void testVariableName() throws Exception {
		assertTrue(isVariableName("i"));
		assertTrue(isVariableName("i2"));
		assertTrue(isVariableName("var"));
		assertTrue(isVariableName("var_name"));
		assertTrue(isVariableName("éáű"));
		assertTrue(isVariableName("ÍŰÁÚŐÓÜÖ"));
		assertTrue(isVariableName("_2"));
		assertTrue(isVariableName("___"));
		assertFalse(isVariableName("2i"));
		assertFalse(isVariableName(" i"));
		assertFalse(isVariableName("~i"));
		assertFalse(isVariableName("-s"));
		assertFalse(isVariableName("var+iable"));
	}
	
	@Test
	public void testArrayNameDefinitionName() throws Exception {
		assertTrue(isArrayNameDefinition("array(10)"));
		assertTrue(isArrayNameDefinition("D12[)"));
		assertTrue(isArrayNameDefinition("D12(j + 10, i)"));
		assertFalse(isArrayNameDefinition("2i()"));
		assertFalse(isArrayNameDefinition(" i()"));
		assertFalse(isArrayNameDefinition("~i(102)"));
		assertFalse(isArrayNameDefinition("-s(43, 00)"));
		assertFalse(isArrayNameDefinition("var+iable"));
	}
	
	@Test
	public void testReplaceNotInString() throws Exception {
		assertEquals("AAAA\"BBBB\"CCCC", replaceNotInString("AAAA\"BBBB\"CCCC", "D", "-"));
		assertEquals("i == 3", replaceNotInString("i = 3", "=", "=="));
		assertEquals("i == 3 + \" adsd s = asds \"", replaceNotInString("i = 3 + \" adsd s = asds \"", "=", "=="));
	}
	
	@Test
	public void testIsValidOperation() throws Exception {
		assertTrue(isValidOperation("a()"));
		assertTrue(isValidOperation("a[]"));
		assertTrue(isValidOperation("alpha(beta)"));
		assertTrue(isValidOperation("alpha(10) := 30"));
		assertTrue(isValidOperation("alpha(10) = 30"));
		assertTrue(isValidOperation("alpha(beta)   "));
		assertTrue(isValidOperation("sajt(sajt(sajt()))"));
		assertTrue(isValidOperation("teve[sajt()]"));
		assertTrue(isValidOperation("level1(level2(), level2())"));
		assertFalse(isValidOperation("a()b()"));
		assertFalse(isValidOperation("a() b()"));
		assertFalse(isValidOperation("sajt(sajt(sajt())"));
		assertFalse(isValidOperation("()"));
		assertFalse(isValidOperation("alpha(beta)as"));
		assertFalse(isValidOperation("(10)"));
	}
	
}
