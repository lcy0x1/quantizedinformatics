package com.arthurlcy0x1.quantizedinformatics;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.arthurlcy0x1.quantizedinformatics.utils.logic.Estimator;
import com.arthurlcy0x1.quantizedinformatics.utils.logic.LogicDiagram;
import com.arthurlcy0x1.quantizedinformatics.utils.logic.LogicGate;
import com.arthurlcy0x1.quantizedinformatics.utils.logic.Estimator.EstiResult;
import com.arthurlcy0x1.quantizedinformatics.utils.logic.Estimator.EstiType;
import com.arthurlcy0x1.quantizedinformatics.utils.logic.LogicDiagram.GateContainer;
import com.arthurlcy0x1.quantizedinformatics.utils.logic.LogicDiagram.ParentDiagram;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.SimplexNoiseGenerator;

public class Test {

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

	private static class AssetGen {

		private static void write(String name, String cont) throws IOException {
			File f = new File(name);
			if (!f.exists())
				f.createNewFile();
			PrintStream ps = new PrintStream(f);
			ps.println(cont);
			ps.close();
		}

	}

	private static class RecipeGen {

		private static final String modid = "quantizedinformatics:";
		private static final String path = "./src/main/resources/data/quantizedinformatics/recipes/";
		private static final String shapeless = "{\"group\":\"^g\",\"type\": \"minecraft:crafting_shapeless\",\"ingredients\":[^i],\"result\":^r}";
		private static final String shaped = "{\"group\":\"^g\",\"type\":\"minecraft:crafting_shaped\",\"pattern\":[\"###\",\"###\",\"###\"],\"key\":{\"#\":^i},\"result\":^r}";
		private static final String single = "{\"item\":\"^i\"}";
		private static final String multi = "{\"item\":\"^i\",\"count\":^n}";

		private static final String b2i(String metal) {
			String i = single.replaceAll("\\^i", metal + "_block");
			String r = multi.replaceAll("\\^i", metal + "_ingot").replaceAll("\\^n", "9");
			return shapeless.replaceAll("\\^g", modid + "block_to_ingot").replaceAll("\\^i", i).replaceAll("\\^r", r);
		}

		private static final void doIngot(String metal) throws IOException {
			AssetGen.write(path + "z_autogen_" + metal + "_ingot_to_nugget.json", i2n(modid + metal));
			AssetGen.write(path + "z_autogen_" + metal + "_nugget_to_ingot.json", n2i(modid + metal));
			AssetGen.write(path + "z_autogen_" + metal + "_block_to_ingot.json", b2i(modid + metal));
			AssetGen.write(path + "z_autogen_" + metal + "_ingot_to_block.json", i2b(modid + metal));
		}

		private static final void doOrePowder(String metal) throws IOException {
			AssetGen.write(path + "z_autogen_" + metal + "_ore_powder_to_tiny.json", op2t(modid + metal));
			AssetGen.write(path + "z_autogen_" + metal + "_ore_tiny_to_powder.json", ot2p(modid + metal));
		}

		private static final void doPowder(String metal) throws IOException {
			AssetGen.write(path + "z_autogen_" + metal + "_powder_to_tiny.json", p2t(modid + metal));
			AssetGen.write(path + "z_autogen_" + metal + "_tiny_to_powder.json", t2p(modid + metal));
		}

		private static final String i2b(String metal) {
			String i = single.replaceAll("\\^i", metal + "_ingot");
			String r = single.replaceAll("\\^i", metal + "_block");
			return shaped.replaceAll("\\^g", modid + "ingot_to_block").replaceAll("\\^i", i).replaceAll("\\^r", r);
		}

		private static final String i2n(String metal) {
			String i = single.replaceAll("\\^i", metal + "_ingot");
			String r = multi.replaceAll("\\^i", metal + "_nugget").replaceAll("\\^n", "9");
			return shapeless.replaceAll("\\^g", modid + "ingot_to_nugget").replaceAll("\\^i", i).replaceAll("\\^r", r);
		}

		private static final String n2i(String metal) {
			String i = single.replaceAll("\\^i", metal + "_nugget");
			String r = single.replaceAll("\\^i", metal + "_ingot");
			return shaped.replaceAll("\\^g", modid + "nugget_to_ingot").replaceAll("\\^i", i).replaceAll("\\^r", r);
		}

		private static final String op2t(String metal) {
			String i = single.replaceAll("\\^i", metal + "_ore_clean_powder");
			String r = multi.replaceAll("\\^i", metal + "_ore_clean_powder_tiny").replaceAll("\\^n", "9");
			return shapeless.replaceAll("\\^g", modid + "powder_to_tiny").replaceAll("\\^i", i).replaceAll("\\^r", r);
		}

		private static final String ot2p(String metal) {
			String i = single.replaceAll("\\^i", metal + "_ore_clean_powder_tiny");
			String r = single.replaceAll("\\^i", metal + "_ore_clean_powder");
			return shaped.replaceAll("\\^g", modid + "tiny_to_powder").replaceAll("\\^i", i).replaceAll("\\^r", r);
		}

		private static final String p2t(String metal) {
			String i = single.replaceAll("\\^i", metal + "_powder");
			String r = multi.replaceAll("\\^i", metal + "_powder_tiny").replaceAll("\\^n", "9");
			return shapeless.replaceAll("\\^g", modid + "powder_to_tiny").replaceAll("\\^i", i).replaceAll("\\^r", r);
		}

		private static final String t2p(String metal) {
			String i = single.replaceAll("\\^i", metal + "_powder_tiny");
			String r = single.replaceAll("\\^i", metal + "_powder");
			return shaped.replaceAll("\\^g", modid + "tiny_to_powder").replaceAll("\\^i", i).replaceAll("\\^r", r);
		}

	}

	public static void addGroup() throws IOException {
		File f = new File("./src/main/resources/data/quantizedinformatics/recipes/");
		Set<String> set = new HashSet<>();
		for (File fi : f.listFiles())
			if (fi.getName().endsWith(".json")) {
				String name = fi.getName();
				List<String> bs = Files.readAllLines(fi.toPath());
				String type = bs.get(2).trim().substring(9);
				set.add(type);
				String group;
				if (type.startsWith("m"))
					if (name.startsWith("gate_red_empty"))
						group = "gate_red_empty";
					else if (name.startsWith("gate_mos_empty"))
						group = "gate_mos_empty";
					else if (name.startsWith("gate_red"))
						group = "gate_red";
					else if (name.startsWith("gate_mos"))
						group = "gate_mos";
					else if (name.startsWith("gate_imp"))
						group = "gate_imp";
					else if (name.startsWith("gate"))
						group = "gate_other";
					else if (name.startsWith("weapon"))
						group = "weapon";
					else if (name.startsWith("draft"))
						group = "draft";
					else if (name.startsWith("pipe"))
						group = "pipe";
					else if (name.startsWith("soul"))
						group = "soul";
					else if (name.startsWith("maxwell_electric") || name.startsWith("maxwell_magnetic"))
						group = "maxwell";
					else
						group = null;
				else
					group = "none";
				if (group == null)
					group = "";
				else
					group = AbReg.MODID + ":" + group;
				PrintStream ps = new PrintStream(fi);
				ps.println(bs.get(0));

				ps.println("\t\"group\": \"" + group + "\",");
				for (int i = 2; i < bs.size(); i++)
					ps.println(bs.get(i));
				ps.close();
			}
		for (String s : set)
			System.out.println(s);
	}

	public static void addRecipe() throws IOException {
		String path = "./src/main/resources/data/quantizedinformatics/recipes/oxi_iron_";
		String s0 = "{\n\t\"type\": \"quantizedinformatics:oxidation\",\n\t\"time\": 200,\n\t\"in_main\": {\n\t\"item\": \"iron_";
		String s1 = "\"\n\t},\n\t\"out_main\": \"quantizedinformatics:elem_feo\",\n\t\"out_main_count\": ";
		String s3 = "\n}";
		String[] tools = { "pickaxe", "shovel", "axe", "hoe", "sword", "helmet", "chestplate", "leggings", "boots",
				"horse_armor" };
		int[] cost = { 3, 1, 3, 2, 2, 5, 8, 7, 4, 12 };
		for (int i = 0; i < tools.length; i++) {
			String cont = s0 + tools[i] + s1 + cost[i] + s3;
			String name = path + tools[i] + ".json";
			File f = new File(name);
			if (!f.exists())
				f.createNewFile();
			PrintStream ps = new PrintStream(f);
			ps.println(cont);
			ps.close();
		}
	}

	public static void check(String str) {
		if (!new File("./metal_asset/" + str + ".png").exists())
			System.out.println(str);
	}

	public static void main(String[] args) throws IOException {
		String[] metal = { "iron", "gold", "copper", "silver", "tin", "lead", "tungsten", "aluminum", "nickel",
				"cobalt", "manganese", "titanium", "platinum" };

		String[] allv = { "bronze", "steel", "al_alloy" };
		String[] al = { "bronze_c", "steel_c", "steel_n", "pb_alloy", "w_alloy", "ti_alloy", "co_alloy", "adv_alloy" };

		int[] ingot = { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		int[] powder = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12 };
		int[] plate = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		int[] wire = { 0, 1, 2, 3, 6, 7 };

		String[] ore_metal = { "coal", "iron", "gold", "copper", "tin", "silver", "lead", "uranium", "aluminum",
				"tungsten", "borax", "beryllium", "nickel", "manganese", "titanium" };

		for (int i = 0; i < ore_metal.length; i++) {
			String ore = ore_metal[i];
			check(ore + "_ore_powder");
			check(ore + "_ore_powder_clean");
			check(ore + "_ore_powder_clean_tiny");
			RecipeGen.doOrePowder(ore);
		}
		for (int i : ingot) {
			check(metal[i] + "_ingot");
			check(metal[i] + "_nugget");
			RecipeGen.doIngot(metal[i]);
		}
		for (int i : powder) {
			check(metal[i] + "_powder");
			check(metal[i] + "_powder_tiny");
			RecipeGen.doPowder(metal[i]);
		}
		for (int i : plate)
			check(metal[i] + "_plate");

		for (String ali : allv)
			for (int i = 0; i < 4; i++) {
				String alloy = ali + "_" + i;
				RecipeGen.doIngot(alloy);
			}

		for (String alloy : al) {
			RecipeGen.doIngot(alloy);
		}
		for (int i : wire)
			check(metal[i] + "_wire");

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
