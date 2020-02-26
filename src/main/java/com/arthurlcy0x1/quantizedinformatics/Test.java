package com.arthurlcy0x1.quantizedinformatics;

import com.arthurlcy0x1.quantizedinformatics.logic.LogicDiagram;
import com.arthurlcy0x1.quantizedinformatics.logic.LogicDiagram.GateContainer;
import com.arthurlcy0x1.quantizedinformatics.logic.LogicGate;

public class Test {

	public static void main(String[] args) {
	}

	private static LogicGate alu_0() {
		// AB0123, XY
		LogicDiagram.ParentDiagram diag = new LogicDiagram.ParentDiagram(6, 2);
		GateContainer not0 = diag.addGate(LogicGate.getPrimeGate(LogicGate.NOT, 1));
		GateContainer and0 = diag.addGate(LogicGate.getPrimeGate(LogicGate.AND, 2));
		GateContainer and1 = diag.addGate(LogicGate.getPrimeGate(LogicGate.AND, 2));
		GateContainer and2 = diag.addGate(LogicGate.getPrimeGate(LogicGate.AND, 3));
		GateContainer and3 = diag.addGate(LogicGate.getPrimeGate(LogicGate.AND, 3));
		GateContainer nor0 = diag.addGate(LogicGate.getPrimeGate(LogicGate.NOR, 3));
		GateContainer nor1 = diag.addGate(LogicGate.getPrimeGate(LogicGate.NOR, 2));
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

	private static LogicGate alu_1() {
		LogicDiagram.ParentDiagram diag = new LogicDiagram.ParentDiagram(14, 5);
		LogicGate pre = alu_0();
		GateContainer nm = diag.addGate(LogicGate.getPrimeGate(LogicGate.NOT, 1));
		nm.setInput(0, null, 13);
		GateContainer[] ps = new GateContainer[4];
		for (int i = 0; i < 4; i++) {
			ps[i] = diag.addGate(pre);
			ps[i].setInput(0, null, i);
			ps[i].setInput(1, null, i + 4);
			for (int j = 0; j < 4; j++)
				ps[i].setInput(j + 2, null, j + 8);
			GateContainer xor = diag.addGate(LogicGate.getPrimeGate(LogicGate.XOR, 2));
			xor.setInput(0, ps[i], 0);
			xor.setInput(1, ps[i], 1);
			GateContainer[] and = new GateContainer[i + 1];
			for (int j = 0; j <= i; j++) {
				and[j] = diag.addGate(LogicGate.getPrimeGate(LogicGate.AND, j + 2));
			}
		}

		return diag.toGate();
	}

	private static void xor_0() {
		LogicDiagram.ParentDiagram diag = new LogicDiagram.ParentDiagram(2, 1);
		GateContainer and0 = diag.addGate(LogicGate.getPrimeGate(LogicGate.AND, 2));
		GateContainer and1 = diag.addGate(LogicGate.getPrimeGate(LogicGate.AND, 2));
		GateContainer not0 = diag.addGate(LogicGate.getPrimeGate(LogicGate.NOT, 1));
		GateContainer not1 = diag.addGate(LogicGate.getPrimeGate(LogicGate.NOT, 1));
		GateContainer or0 = diag.addGate(LogicGate.getPrimeGate(LogicGate.OR, 2));
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
		GateContainer and0 = diag.addGate(LogicGate.getPrimeGate(LogicGate.NAND, 2));
		GateContainer and1 = diag.addGate(LogicGate.getPrimeGate(LogicGate.NAND, 2));
		GateContainer not0 = diag.addGate(LogicGate.getPrimeGate(LogicGate.NOT, 1));
		GateContainer not1 = diag.addGate(LogicGate.getPrimeGate(LogicGate.NOT, 1));
		GateContainer or0 = diag.addGate(LogicGate.getPrimeGate(LogicGate.NAND, 2));
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
