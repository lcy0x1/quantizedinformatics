package com.arthurlcy0x1.quantizedinformatics;

import com.arthurlcy0x1.quantizedinformatics.logic.LogicDiagram;
import com.arthurlcy0x1.quantizedinformatics.logic.LogicGate;

public class Test {

	public static void main(String[] args) {
		System.out.println(alu_0());
		xor_0();
		xor_1();
	}

	private static LogicGate alu_0() {
		// AB0123, XY
		LogicDiagram.ParentDiagram diag = new LogicDiagram.ParentDiagram(6, 2);
		LogicDiagram.GateContainer not0 = diag.addGate(LogicGate.getPrimeGate(LogicGate.NOT, 1));
		LogicDiagram.GateContainer and0 = diag.addGate(LogicGate.getPrimeGate(LogicGate.AND, 2));
		LogicDiagram.GateContainer and1 = diag.addGate(LogicGate.getPrimeGate(LogicGate.AND, 2));
		LogicDiagram.GateContainer and2 = diag.addGate(LogicGate.getPrimeGate(LogicGate.AND, 3));
		LogicDiagram.GateContainer and3 = diag.addGate(LogicGate.getPrimeGate(LogicGate.AND, 3));
		LogicDiagram.GateContainer nor0 = diag.addGate(LogicGate.getPrimeGate(LogicGate.NOR, 3));
		LogicDiagram.GateContainer nor1 = diag.addGate(LogicGate.getPrimeGate(LogicGate.NOR, 2));
		not0.setInput(0, null, 1);
		and0.setInput(0, null, 1);
		and0.setInput(1, null, 2);
		and1.setInput(0, not0, 0);
		and1.setInput(1, null, 3);
		and2.setInput(0, null, 0);
		and2.setInput(1, null, 4);
		and2.setInput(2, not0, 0);
		and3.setInput(0, null, 0);
		and3.setInput(1, null, 1);
		and3.setInput(2, null, 5);
		nor0.setInput(0, and0, 0);
		nor0.setInput(1, and1, 0);
		nor1.setInput(0, and2, 0);
		nor1.setInput(1, and3, 0);
		diag.setInput(0, nor0, 0);
		diag.setInput(1, nor1, 0);
		return diag.toGate();
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
	}

}
