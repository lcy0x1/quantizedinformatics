package com.arthurlcy0x1.quantizedinformatics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

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

	public static void addBlocks() throws IOException {
		String[] list = { "quantum_fog", "craft_frame", "craft_3d", "oxidation_furnace", "reduction_furnace",
				"draft_wire", "draft_center", "draft_gate", "draft_in", "draft_out", "draft_listener", "auto_craft",
				"recipe_maker", "pipe_body", "pipe_head", "pipe_core" };
		String path = "./src/main/resources/data/quantizedinformatics/loot_tables/blocks/";
		String pre = "{\"type\": \"minecraft:block\",\"pools\": [{\"rolls\": 1,\"entries\": [{\"type\": \"minecraft:item\",\"name\": \"quantizedinformatics:";
		String post = "\"}],\"conditions\": [{\"condition\": \"minecraft:survives_explosion\"}]}]}";
		for (String b : list) {
			String name = path + b + ".json";
			File f = new File(name);
			if (!f.exists())
				f.createNewFile();
			PrintStream ps = new PrintStream(f);
			ps.println(pre + b + post);
			ps.close();
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
					group = Registrar.MODID + ":" + group;
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

	public static void generateItemModel() throws IOException {
		String[] ss0 = { "red", "mos", "imp" };
		String[] ss1 = { "empty", "dirty", "buff", "not", "nand", "nor", "and", "or", "xor" };
		String path = "./src/main/resources/assets/quantizedinformatics/models/item/";
		String pre = "{\n\t\"parent\": \"item/generated\",\n\t\"textures\": {\n\t\"layer0\": \"quantizedinformatics:items/";
		String post = "\"\n\t}\n}";
		for (String s0 : ss0)
			for (String s1 : ss1) {
				String name = "gate_" + s0 + "_" + s1;
				File f = new File(path + name + ".json");
				if (!f.exists())
					f.createNewFile();
				PrintStream ps = new PrintStream(f);
				ps.println(pre + name + post);
				ps.close();
			}
	}

	public static void main(String[] args) throws IOException {
		System.out.println(1 << (2 ^ 1));

	}

	public static double normal(double x, double mean, double stdev) {
		double n = (x - mean) / stdev;
		return Math.exp(-0.5 * n * n) / stdev / Math.sqrt(2 * Math.PI);
	}

	public static void recolor() throws IOException {
		File fp = new File("./src/main/resources/assets/quantizedinformatics/textures/items/elem_p.png");
		File fb = new File("./src/main/resources/assets/quantizedinformatics/textures/items/elem_si.png");
		BufferedImage bimg = ImageIO.read(fp);
		int w = bimg.getWidth();
		int h = bimg.getHeight();
		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++) {
				int p = bimg.getRGB(i, j);
				int b = p & 255;
				int g = p >> 8 & 255;
				int r = p >> 16 & 255;
				int a = p >> 24;
				p = a << 24 | b << 16 | r << 8 | g;
				bimg.setRGB(i, j, p);
			}
		ImageIO.write(bimg, "PNG", fb);

	}

	public static void renameItemTexture() throws IOException {
		String[] ss0 = { "red", "mos", "imp" };
		String[] ss1 = { "empty", "dirty", "buff", "not", "nand", "nor", "and", "or", "xor" };
		String path = "./src/main/resources/assets/quantizedinformatics/textures/items/";

		for (String s0 : ss0)
			for (String s1 : ss1) {
				String n0 = "gate" + s0 + s1;
				String n1 = "gate_" + s0 + "_" + s1;
				File f = new File(path + n0 + ".png");
				f.renameTo(new File(path + n1 + ".png"));

			}
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

	private static ParentDiagram alu_0() {
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

}
