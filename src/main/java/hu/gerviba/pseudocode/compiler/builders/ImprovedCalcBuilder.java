package hu.gerviba.pseudocode.compiler.builders;

import java.util.LinkedList;
import java.util.List;

import hu.gerviba.pseudocode.compiler.units.Operator;

@Deprecated
public class ImprovedCalcBuilder {

	enum OperationElementType {
		NULL,
		VALUE,
		ARM;
	}
	
	class OperationElement {
		
		OperationElementType leftType;
		OperationElement leftArm;
		LinkedList<String> leftValue;
		
		Operator op;

		OperationElementType rightType;
		OperationElement rightArm;
		LinkedList<String> rightValue;
		
	}

	public ImprovedCalcBuilder calc(String preformatted) {
		return null;
	}
	
	public List<String> getResult() {
		return null;
	}
	
}
