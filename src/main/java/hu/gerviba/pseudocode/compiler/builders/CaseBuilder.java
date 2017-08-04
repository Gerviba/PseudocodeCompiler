package hu.gerviba.pseudocode.compiler.builders;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 
 * <h3>Blueprint:</h3>
 * 
 * PseudoCode:
 * 
 * <pre>
 * var := "v2"
 * switch(var) {
 *     case v1: //v1, v2 és v3 konstans
 *         KI: "V1"
 *     break;
 *     case v2:
 *     case v3:
 *         scopevar := "V2 vagy V3"
 *         KI: scopevar
 *     break;
 *     default:
 *         KI: "Defa"
 *     break;
 * }
 * </pre>
 * 
 * PseudoCode(Pre)Compiled: // A labelek sorokat fognak tartalmazni és a kulcsszavak ID-re lesznek kicserélve
 * <pre>
 * VAR var "v2"
 * IF var v1 NEXT GOTO ENDOF1				// case v1
 * OUT 0 "V1"								// > kiírás: "V1"
 * GOTO SWITCHBUTTOM						// > break
 * LABEL ENDOF1								// case v2 előtt
 * IF var v2 GOTO STARTOF2 GOTO ENDOF2		// case v2
 * IF var v3 NEXT GOTO ENDOF2				// case v3
 * LABEL STARTOF2							// :
 * VAR scopevar "V2 vagy V3"				// > scopevar = "V2 vagy V3"
 * OUT 0 "V2 vagy V3"						// > kiírás: scopevar
 * FREE scopevar							// > változó felszabadítása
 * GOTO SWITCHBUTTOM						// > break
 * LABEL ENDOF2								// default
 * OUT 0 "Defa"								// > kiírás: "Defa"
 * LABEL SWITCHBUTTOM 						// A break ide fog vezetni
 * </pre>
 */

public class CaseBuilder extends Builder {
	
	protected CaseBuilder(CompilerCore core) {
		super(core, BuilderType.CASE);
	}

	@Override
	protected void build() {
		
	}

	@Override
	public ArrayList<String> getEndPatterns() {
		return new ArrayList<>(Arrays.asList(""));
	}
	
	
	
}
