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

	private static class RecipeGen {

		private static final String modid = "quantizedinformatics:";
		private static final String path = "./src/main/resources/data/quantizedinformatics/recipes/";
		private static final String shapeless = "{\"group\":\"^g\",\"type\": \"minecraft:crafting_shapeless\",\"ingredients\":[^i],\"result\":^r}";
		private static final String shaped = "{\"group\":\"^g\",\"type\":\"minecraft:crafting_shaped\",\"pattern\":[\"###\",\"###\",\"###\"],\"key\":{\"#\":^i},\"result\":^r}";
		private static final String single = "{\"item\":\"^i\"}";
		private static final String multi = "{\"item\":\"^i\",\"count\":^n}";

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

		private static final String b2i(String metal) {
			String i = single.replaceAll("\\^i", metal + "_block");
			String r = multi.replaceAll("\\^i", metal + "_ingot").replaceAll("\\^n", "9");
			return shapeless.replaceAll("\\^g", modid + "block_to_ingot").replaceAll("\\^i", i).replaceAll("\\^r", r);
		}

		private static final String i2b(String metal) {
			String i = single.replaceAll("\\^i", metal + "_ingot");
			String r = single.replaceAll("\\^i", metal + "_block");
			return shaped.replaceAll("\\^g", modid + "ingot_to_block").replaceAll("\\^i", i).replaceAll("\\^r", r);
		}

		private static final void doPowder(String metal) throws IOException {
			AssetGen.write(path + "z_autogen_" + metal + "_powder_to_tiny.json", p2t(modid + metal));
			AssetGen.write(path + "z_autogen_" + metal + "_tiny_to_powder.json", t2p(modid + metal));
		}

		private static final void doOrePowder(String metal) throws IOException {
			AssetGen.write(path + "z_autogen_" + metal + "_ore_powder_to_tiny.json", op2t(modid + metal));
			AssetGen.write(path + "z_autogen_" + metal + "_ore_tiny_to_powder.json", ot2p(modid + metal));
		}

		private static final void doIngot(String metal) throws IOException {
			AssetGen.write(path + "z_autogen_" + metal + "_ingot_to_nugget.json", i2n(modid + metal));
			AssetGen.write(path + "z_autogen_" + metal + "_nugget_to_ingot.json", n2i(modid + metal));
			AssetGen.write(path + "z_autogen_" + metal + "_block_to_ingot.json", b2i(modid + metal));
			AssetGen.write(path + "z_autogen_" + metal + "_ingot_to_block.json", i2b(modid + metal));
		}

	}

	private static class AssetGen {

		private static final String bs = "./src/main/resources/assets/quantizedinformatics/blockstates/";
		private static final String bm = "./src/main/resources/assets/quantizedinformatics/models/block/";
		private static final String im = "./src/main/resources/assets/quantizedinformatics/models/item/";
		private static final String lt = "./src/main/resources/data/quantizedinformatics/loot_tables/blocks/";
		private static final String bsstr = "{\"variants\":{\"\":{\"model\":\"quantizedinformatics:block/^\"}}}";
		private static final String fbsstr = "{\"variants\":{\"facing=north\":{\"model\":\"quantizedinformatics:block/^\"},\"facing=east\":{\"model\": \"quantizedinformatics:block/^\",\"y\": 90},\"facing=south\": {\"model\":\"quantizedinformatics:block/^\",\"y\": 180},\"facing=west\":{\"model\":\"quantizedinformatics:block/^\",\"y\": 270}}}";
		private static final String bmstr = "{\"parent\":\"block/cube_all\",\"textures\":{\"all\":\"quantizedinformatics:blocks/^\"}}";
		private static final String fbmstr = "{\"parent\":\"block/cube\",\"textures\":{\"north\":\"quantizedinformatics:blocks/^f\",\"up\":\"quantizedinformatics:blocks/^t\",\"down\":\"quantizedinformatics:blocks/^t\",\"south\":\"quantizedinformatics:blocks/^s\",\"west\":\"quantizedinformatics:blocks/^s\",\"east\":\"quantizedinformatics:blocks/^s\",\"particle\":\"quantizedinformatics:blocks/^t\"}}";
		private static final String bimstr = "{\"parent\":\"quantizedinformatics:block/^\"}";
		private static final String imstr = "{\"parent\":\"item/generated\",\"textures\":{\"layer0\":\"quantizedinformatics:items/^\"}}";
		private static final String ltstr = "{\"type\":\"minecraft:block\",\"pools\":[{\"rolls\":1,\"entries\":[{\"type\":\"minecraft:item\",\"name\":\"quantizedinformatics:^\"}],\"conditions\":[{\"condition\":\"minecraft:survives_explosion\"}]}]}";

		private static final String wbmstrc = "{\"parent\": \"quantizedinformatics:block/ab_wire_core\",\"textures\":{\"wire\":\"quantizedinformatics:blocks/^\"}}";
		private static final String wbmstrs = "{\"parent\": \"quantizedinformatics:block/ab_wire_side\",\"textures\":{\"wire\":\"quantizedinformatics:blocks/^\"}}";
		private static final String wimstr = "{\"parent\":\"quantizedinformatics:block/^_core\"}";
		private static final String wbsstr = "{\"multipart\":[{\"when\":{\"OR\":[{\"north\":\"false\"},{\"east\":\"false\"},{\"south\":\"false\"},{\"west\":\"false\"},{\"up\":\"false\"},{\"down\":\"false\"}]},\"apply\":{\"model\":\"quantizedinformatics:block/^_core\"}},{\"when\":{\"north\":\"true\"},\"apply\":{\"model\":\"quantizedinformatics:block/^_side\"}},{\"when\":{\"east\":\"true\"},\"apply\":{\"model\":\"quantizedinformatics:block/^_side\",\"y\":90}},{\"when\":{\"south\":\"true\"},\"apply\":{\"model\":\"quantizedinformatics:block/^_side\",\"y\":180}},{\"when\":{\"west\":\"true\"},\"apply\":{\"model\":\"quantizedinformatics:block/^_side\",\"y\":270}},{\"when\":{\"up\":\"true\"},\"apply\":{\"model\":\"quantizedinformatics:block/^_side\",\"x\":270}},{\"when\":{\"down\":\"true\"},\"apply\":{\"model\":\"quantizedinformatics:block/^_side\",\"x\":90}}]}";

		public static void addBlockAssets(String block) throws IOException {
			write(bs + block + ".json", bsstr.replaceAll("\\^", block));
			write(bm + block + ".json", bmstr.replaceAll("\\^", block));
			write(im + block + ".json", bimstr.replaceAll("\\^", block));
			write(lt + block + ".json", ltstr.replaceAll("\\^", block));
		}

		public static void addDireBlockAssets(String block, String f, String s, String t) throws IOException {
			write(bs + block + ".json", fbsstr.replaceAll("\\^", block));
			write(bm + block + ".json", fbmstr.replaceAll("\\^f", f).replaceAll("\\^s", s).replaceAll("\\^t", t));
			write(im + block + ".json", bimstr.replaceAll("\\^", block));
			write(lt + block + ".json", ltstr.replaceAll("\\^", block));
		}

		public static void addItemAssets(String item) throws IOException {
			write(im + item + ".json", imstr.replaceAll("\\^", item));
		}

		public static void addItemAssets(String item, String res) throws IOException {
			write(im + item + ".json", imstr.replaceAll("\\^", res));
		}

		public static void addWireAssets(String block) throws IOException {
			write(bs + block + ".json", wbsstr.replaceAll("\\^", block));
			write(bm + block + "_core.json", wbmstrc.replaceAll("\\^", block));
			write(bm + block + "_side.json", wbmstrs.replaceAll("\\^", block));
			write(im + block + ".json", imstr.replaceAll("\\^", block));
			write(lt + block + ".json", ltstr.replaceAll("\\^", block));
		}

		private static void write(String name, String cont) throws IOException {
			File f = new File(name);
			if (!f.exists())
				f.createNewFile();
			PrintStream ps = new PrintStream(f);
			ps.println(cont);
			ps.close();
		}

	}

	public static class LogicTest {

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

		String[] type = { "_ingot", "_nugget", "_plate" };

		for (String ali : allv)
			for (int i = 0; i < 4; i++) {
				String alloy = ali + "_" + i;
				RecipeGen.doIngot(alloy);
				for (String s : type) {
					check(alloy + s);
					AssetGen.addItemAssets(alloy + s, ali + s);
				}
			}

		for (String alloy : al) {
			for (String s : type) {
				check(alloy + s);
				AssetGen.addItemAssets(alloy + s);
			}
			RecipeGen.doIngot(alloy);
		}
		for (int i : wire)
			check(metal[i] + "_wire");

	}

	public static void check(String str) {
		if (!new File("./metal_asset/" + str + ".png").exists())
			System.out.println(str);
	}

	public static void addMetals() throws IOException {

		String[] metal = { "iron", "gold", "copper", "silver", "tin", "lead", "tungsten", "aluminum", "nickel",
				"cobalt", "manganese", "titanium", "platinum" };
		int[] ingot = { 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		int[] powder = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11, 12 };
		int[] plate = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
		int[] wire = { 0, 1, 2, 3, 6, 7 };

		String[] ore_metal = { "coal", "iron", "gold", "copper", "tin", "silver", "lead", "uranium", "aluminum",
				"tungsten", "graphite", "borax", "beryllium", "nickel", "manganese", "titanium" };
		String[] blocks = { "pm_transistor", "pmc_pump" };
		String[] dblocks = { "pmg_thermal", "pmg_lava", "pmc_furnace", "pmc_plate", "pmc_wire", "pmc_cut",
				"pmc_centrifuge", "pmc_wash", "pmc_powder", "pmc_electrolysis" };
		String[] items = { "rubber", "elem_alo", "elem_cao", "elem_caco3", "elem_beo", "elem_wo", "elem_tio", "elem_uo",
				"elem_aso", "elem_as", "elem_s", "elem_alo_tiny", "elem_aso_tiny", "elem_beo_tiny", "elem_wo_tiny",
				"elem_tio_tiny", "elem_uo_tiny", "elem_s_tiny" };
		for (int i = 0; i < ore_metal.length; i++) {
			String ore = ore_metal[i];
			if (i > 2) {
				AssetGen.addBlockAssets(ore + "_ore");
			}
			AssetGen.addItemAssets(ore + "_ore_powder");
			AssetGen.addItemAssets(ore + "_ore_powder_clean");
			AssetGen.addItemAssets(ore + "_ore_powder_clean_tiny");
		}
		for (String block : blocks)
			AssetGen.addBlockAssets(block);
		for (String block : dblocks)
			AssetGen.addDireBlockAssets(block, block + "_front", block + "_top", block + "_side");
		for (String item : items)
			AssetGen.addItemAssets(item);
		for (int i : ingot)
			AssetGen.addItemAssets(metal[i] + "_ingot");
		for (int i : ingot)
			AssetGen.addItemAssets(metal[i] + "_nugget");
		for (int i : powder)
			AssetGen.addItemAssets(metal[i] + "_powder");
		for (int i : powder)
			AssetGen.addItemAssets(metal[i] + "_powder_tiny");
		for (int i : plate)
			AssetGen.addItemAssets(metal[i] + "_plate");
		for (int i : wire)
			AssetGen.addItemAssets(metal[i] + "_wire");
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
