package com.arthurlcy0x1.quantizedinformatics;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import com.arthurlcy0x1.quantizedinformatics.utils.logic.Estimator;
import com.arthurlcy0x1.quantizedinformatics.utils.logic.LogicDiagram;
import com.arthurlcy0x1.quantizedinformatics.utils.logic.LogicGate;
import com.google.gson.JsonObject;
import com.arthurlcy0x1.quantizedinformatics.utils.logic.Estimator.EstiResult;
import com.arthurlcy0x1.quantizedinformatics.utils.logic.Estimator.EstiType;
import com.arthurlcy0x1.quantizedinformatics.utils.logic.LogicDiagram.GateContainer;
import com.arthurlcy0x1.quantizedinformatics.utils.logic.LogicDiagram.ParentDiagram;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.SimplexNoiseGenerator;

public class Test {

	public static class ClassTest {

		public static void main() {
		}

	}

	public static class LogicTest {

		public static ParentDiagram alu_0() {
			// AB0123, XY
			ParentDiagram diag = new ParentDiagram(6, 2);
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
			return diag;// .toGate();
		}

		public static LogicGate alu_1() {
			LogicDiagram.ParentDiagram diag = new LogicDiagram.ParentDiagram(14, 5);
			LogicGate pre = alu_0().toGate();
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

		public static void xor_0() {
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

		public static void xor_1() {
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

	public static void check(String str) {
		if (!new File("./metal_asset/" + str + ".png").exists())
			System.out.println(str);
	}

	public static void main(String[] args) throws IOException {
		JsonObject e = new JsonObject();
		System.out.println(e);
	}

	public static void testEsti() {
		Vec3d target = new Vec3d(0, 10, 0);
		new Estimator(0.04, 0.02, Vec3d.ZERO, 3, 80, target, Vec3d.ZERO).getAnswer();
		long t0 = System.nanoTime();
		Estimator est = new Estimator(0.04, 0.02, Vec3d.ZERO, 3, 80, target, Vec3d.ZERO);
		EstiResult er = est.getAnswer();
		long t1 = System.nanoTime();
		System.out.println("time: " + (t1 - t0) / 1000);
		System.out.println((er.getType() == EstiType.ZERO) + ", " + er.getVec() + ", " + er.getT());
		System.out.println("deviation: " + est.getX0(er.getA(), er.getT()) + ", " + est.getY0(er.getA(), er.getT()));

	}

	public static void testSimplex() {
		SimplexNoiseGenerator rand = new SimplexNoiseGenerator(new Random());
		double min = 0, dv = 1.0 / 32;
		int n0 = -1000, n1 = 1000;
		for (int i = n0; i < n1; i++)
			for (int j = n0; j < n1; j++) {
				double val = rand.getValue(i, j);
				double v0 = rand.getValue(i + dv, j);
				double v1 = rand.getValue(i - dv, j);
				double v2 = rand.getValue(i, j + dv);
				double v3 = rand.getValue(i, j - dv);
				min = Math.max(min, Math.abs(val - v0));
				min = Math.max(min, Math.abs(val - v1));
				min = Math.max(min, Math.abs(val - v2));
				min = Math.max(min, Math.abs(val - v3));
			}
		System.out.println(min);
	}

}
