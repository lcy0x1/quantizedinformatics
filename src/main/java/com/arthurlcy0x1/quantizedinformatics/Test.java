package com.arthurlcy0x1.quantizedinformatics;

import com.arthurlcy0x1.quantizedinformatics.logic.LogicDiagram;
import com.arthurlcy0x1.quantizedinformatics.logic.LogicGate;

public class Test {

	public static void main(String[] args) {
		xor_0();
		xor_1();
		LogicGate gate = LogicGate.getPrimeGate(LogicGate.XOR, 2);
		System.out.println(gate);
		System.out.println(gate.map);

	}

	private static void xor_0() {
		LogicDiagram.ParentDiagram diag = new LogicDiagram.ParentDiagram(2, 1);
		LogicDiagram.GateContainer and0 = diag.addGate(LogicGate.getPrimeGate(LogicGate.AND, 2));
		LogicDiagram.GateContainer and1 = diag.addGate(LogicGate.getPrimeGate(LogicGate.AND, 2));
		LogicDiagram.GateContainer not0 = diag.addGate(LogicGate.getPrimeGate(LogicGate.NOT, 1));
		LogicDiagram.GateContainer not1 = diag.addGate(LogicGate.getPrimeGate(LogicGate.NOT, 1));
		LogicDiagram.GateContainer or0 = diag.addGate(LogicGate.getPrimeGate(LogicGate.OR, 2));
		not0.setInput(0, null, 0);
		not1.setInput(0, null, 1);
		and0.setInput(0, null, 0);
		and0.setInput(1, not1, 0);
		and1.setInput(0, null, 1);
		and1.setInput(1, not0, 0);
		or0.setInput(0, and0, 0);
		or0.setInput(1, and1, 0);
		diag.setInput(0, or0, 0);
		LogicGate gate = diag.toGate();
		System.out.println(gate);
		System.out.println(gate.map);
	}

	private static void xor_1() {
		LogicDiagram.ParentDiagram diag = new LogicDiagram.ParentDiagram(2, 1);
		LogicDiagram.GateContainer and0 = diag.addGate(LogicGate.getPrimeGate(LogicGate.NAND, 2));
		LogicDiagram.GateContainer and1 = diag.addGate(LogicGate.getPrimeGate(LogicGate.NAND, 2));
		LogicDiagram.GateContainer not0 = diag.addGate(LogicGate.getPrimeGate(LogicGate.NOT, 1));
		LogicDiagram.GateContainer not1 = diag.addGate(LogicGate.getPrimeGate(LogicGate.NOT, 1));
		LogicDiagram.GateContainer or0 = diag.addGate(LogicGate.getPrimeGate(LogicGate.NAND, 2));
		not0.setInput(0, null, 0);
		not1.setInput(0, null, 1);
		and0.setInput(0, null, 0);
		and0.setInput(1, not1, 0);
		and1.setInput(0, null, 1);
		and1.setInput(1, not0, 0);
		or0.setInput(0, and0, 0);
		or0.setInput(1, and1, 0);
		diag.setInput(0, or0, 0);
		LogicGate gate = diag.toGate();
		System.out.println(gate);
		System.out.println(gate.map);
	}

}
