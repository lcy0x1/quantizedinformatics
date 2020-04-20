package com.arthurlcy0x1.quantizedinformatics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ResourceManager {

	private static class AssetGen {

		private static final String BS_L, BS_, BS_A, BS_AD, IM_, BM_, BM_M, IM_B, BS_F, BS_W, LT_, IM_W, BM_WS, BM_WC;

		static {
			String path = "./resources/";
			BS_ = readFile(path + "BS/-templates/-.json");
			BS_A = readFile(path + "BS/-templates/-air.json");
			BS_L = readFile(path + "BS/-templates/-lit.json");
			BS_AD = readFile(path + "BS/-templates/-alldire.json");
			BS_F = readFile(path + "BS/-templates/-face.json");
			BS_W = readFile(path + "BS/-templates/-wire.json");

			BM_ = readFile(path + "BM/-templates/-.json");
			BM_M = readFile(path + "BM/-templates/-machine.json");
			BM_WS = readFile(path + "BM/-templates/-wire_side.json");
			BM_WC = readFile(path + "BM/-templates/-wire_core.json");

			IM_ = readFile(path + "IM/-templates/-.json");
			IM_B = readFile(path + "IM/-templates/-block.json");
			IM_W = readFile(path + "IM/-templates/-wire.json");

			LT_ = readFile(path + "BL/-templates/-.json");

		}

		private static void addBlockAssets(String block) throws IOException {
			write(BS + block + ".json", BS_.replaceAll("\\^", block));
			write(BM + block + ".json", BM_.replaceAll("\\^", block));
			write(IM + block + ".json", IM_B.replaceAll("\\^", block));
		}

		private static void addBlockAssetsAir(String block) throws IOException {
			write(BS + block + ".json", BS_A);
		}

		private static void addBlockAssetsAllDire(String block) throws IOException {
			write(BS + block + ".json", BS_AD.replaceAll("\\^", block));
			write(IM + block + ".json", IM_B.replaceAll("\\^", block));
		}

		private static void addBlockAssetsFace(String block) throws IOException {
			write(BS + block + ".json", BS_F.replaceAll("\\^", block));
			write(IM + block + ".json", IM_B.replaceAll("\\^", block));
		}

		private static void addBlockAssetsLit(String block) throws IOException {
			write(BS + block + ".json", BS_L.replaceAll("\\^", block));
			write(BM + block + ".json", BM_.replaceAll("\\^", block));
			write(BM + block + "_a.json", BM_.replaceAll("\\^", block + "_a"));
			write(IM + block + ".json", IM_B.replaceAll("\\^", block));
		}

		private static void addBlockAssetsMachine(String block) throws IOException {
			write(BS + block + ".json", BS_F.replaceAll("\\^", block));
			write(BM + block + ".json", BM_M.replaceAll("\\^", block));
			write(IM + block + ".json", IM_B.replaceAll("\\^", block));
		}

		private static void addBlockAssetsWire(String block) throws IOException {
			write(BS + block + ".json", BS_W.replaceAll("\\^", block));
			write(BM + block + "_core.json", BM_WC.replaceAll("\\^", block));
			write(BM + block + "_side.json", BM_WS.replaceAll("\\^", block));
			write(IM + block + ".json", IM_W.replaceAll("\\^", block));
		}

		private static void addBlockItemAssets(String block) throws IOException {
			write(IM + block + ".json", IM_B.replaceAll("\\^", block));
		}

		private static void addItemAssets(String item) throws IOException {
			write(IM + item + ".json", IM_.replaceAll("\\^", item));
		}

		private static void addItemAssets(String item, String res) throws IOException {
			write(IM + item + ".json", IM_.replaceAll("\\^", res));
		}

		private static void addLootTable(String block) throws IOException {
			write(BL + block + ".json", LT_.replaceAll("\\^", block));
		}

		private static String readFile(String path) {
			List<String> list = null;
			try {
				list = Files.readLines(new File(path), Charset.defaultCharset());
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			}
			String str = "";
			for (String s : list)
				str += s + "\n";
			return str;
		}

		private static void write(String name, String cont) throws IOException {
			File f = new File(name);
			check(f);
			PrintStream ps = new PrintStream(f);
			ps.println(cont);
			ps.close();
		}

	}

	private static class AssetMove {

		private static final Map<String, String> PATHMAP = new HashMap<>();

		static {
			PATHMAP.put("ASSETS", PATH_ASSET);
			PATHMAP.put("BS", BS);
			PATHMAP.put("BM", BM);
			PATHMAP.put("BT", BT);
			PATHMAP.put("BL", BL);
			PATHMAP.put("IM", IM);
			PATHMAP.put("IT", IT);
			PATHMAP.put("R", R);

		}

		public static void organize() throws IOException {
			delete(new File(PATH_ASSET));
			delete(new File(PATH_DATA));
			GUIGen.gen();
			orgImpl("ASSETS");
			orgBlocks();
			orgItems();
			orgImpl("R");
		}

		private static void copyTo(File file, String path) throws IOException {
			File f = new File(path);
			check(f);
			Files.copy(file, f);
		}

		private static void orgBlocks() throws IOException {
			orgImpl("BT");
			Map<String, List<String>> map;
			map = readJson("./resources/BL/-info.json");
			List<String> ignore = map.get("ignore");
			map = readJson("./resources/BS/-info.json");
			List<String> blocks = orgImpl("BS");
			for (String block : blocks)
				AssetGen.addBlockItemAssets(block);
			for (String block : map.get(""))
				AssetGen.addBlockAssets(block);
			for (String block : map.get("air"))
				AssetGen.addBlockAssetsAir(block);
			for (String block : map.get("lit"))
				AssetGen.addBlockAssetsLit(block);
			for (String block : map.get("alldire"))
				AssetGen.addBlockAssetsAllDire(block);
			for (String block : map.get("face"))
				AssetGen.addBlockAssetsFace(block);
			for (String block : map.get("machine"))
				AssetGen.addBlockAssetsMachine(block);
			for (String block : map.get("wire"))
				AssetGen.addBlockAssetsWire(block);
			map.forEach((k, v) -> blocks.addAll(v));
			for (String block : blocks)
				if (!ignore.contains(block))
					AssetGen.addLootTable(block);
			orgImpl("BM");
			orgImpl("BL");
		}

		private static void orgImpl(File file, List<String> list, String path, String str) throws IOException {
			String name = file.getName();
			char ch = name.charAt(0);
			if (ch == '.' || ch == '-')
				return;
			else if (ch >= 'A' && ch <= 'Z') {
				path = PATHMAP.get(name);
				str = "";
			} else if (ch == '@') {
				path += name.substring(1) + "/";
				str = "";
			} else if (ch == '_') {
				str += name;
			} else if (ch >= '0' && ch <= '9' || ch >= 'a' && ch <= 'z') {
				String[] ss = name.split("\\.");
				if (ss[0].endsWith("_"))
					str = ss[0] + str + (ss.length > 1 ? "." + ss[1] : "");
				else
					str = name;
			} else {
				System.out.println("invalid filename: " + path + ", " + str);
			}
			if (file.isDirectory()) {
				for (File fi : file.listFiles())
					orgImpl(fi, list, path, str);
			} else {
				if (list != null)
					list.add(str.split("\\.")[0]);
				copyTo(file, path + str);
			}
		}

		private static List<String> orgImpl(String path) throws IOException {
			List<String> list = new ArrayList<>();
			orgImpl(new File("./resources/" + path + "/"), list, PATHMAP.get(path), "");
			return list;
		}

		private static void orgItems() throws IOException {
			Map<String, List<String>> map = readJson("./resources/IT/-info.json");
			List<String> list = orgImpl("IT");
			for (String item : list) {
				if (!map.get("ignore").contains(item))
					AssetGen.addItemAssets(item);
				RecipeGen.ITEM_KEY.forEach(e -> e.accept(item));
			}
			map = readJson("./resources/IM/-info.json");
			for (Entry<String, List<String>> ent : map.entrySet()) {
				String key = ent.getKey();
				int lo = key.charAt(0) - '0';
				int hi = key.charAt(2) - '0';
				for (String str : ent.getValue())
					for (int i = lo; i <= hi; i++)
						AssetGen.addItemAssets(str.replaceAll("\\^", "_" + i), str.replaceAll("\\^", ""));
			}
			orgImpl("IM");
		}

		private static JsonElement readJsonFile(String path) throws IOException {
			File f = new File(path);
			JsonReader r = new JsonReader(Files.newReader(f, Charset.defaultCharset()));
			JsonElement e = new JsonParser().parse(r);
			r.close();
			return e;
		}

		private static Map<String, List<String>> readJson(String path) throws IOException {
			JsonElement e = readJsonFile(path);
			Map<String, List<String>> ans = new HashMap<>();
			e.getAsJsonObject().entrySet()
					.forEach(ent0 -> ent0.getValue().getAsJsonObject().entrySet().forEach(ent1 -> {
						String key = ent1.getKey();
						List<String> list;
						if (ans.containsKey(key))
							list = ans.get(key);
						else
							ans.put(key, list = new ArrayList<>());
						ent1.getValue().getAsJsonObject().entrySet().forEach(ent2 -> {
							String group = ent2.getKey();
							ent2.getValue().getAsJsonArray().forEach(ent3 -> {
								String name = ent3.getAsString();
								if (name.startsWith("_") || name.startsWith("^"))
									list.add(group + name);
								else if (name.endsWith("_"))
									list.add(name + group);
								else
									list.add(name);
							});
						});
					}));
			return ans;
		}

	}

	private static class RecipeGen {

		private static final List<Consumer<String>> ITEM_KEY = new ArrayList<>();

		private static final String MODID = "quantizedinformatics:";
		private static final String RPATH = "./src/main/resources/data/quantizedinformatics/recipes/";
		private static final String NS, SC, PLA;
		private static final String SI = "{\"item\":\"^i\"}";
		private static final String ML = "{\"item\":\"^i\",\"count\":^n}";

		static {
			NS = AssetGen.readFile("./resources/R/-templates/-single.json");
			SC = AssetGen.readFile("./resources/R/-templates/-merge.json");
			PLA = AssetGen.readFile("./resources/R/-templates/-plate.json");
			ITEM_KEY.add(RecipeGen::genPlate);
			ITEM_KEY.add(RecipeGen::genPowder);
		}

		private static void doMerge(String metal, String lr, String sm, String w0, String w1) {
			String l = rep(SI, "\\^i", metal + lr);
			String s = rep(SI, "\\^i", metal + sm);
			String s9 = rep(ML, "\\^i", metal + sm).replaceAll("\\^n", "9");
			String name, cont;
			name = w0 + "_to_" + w1;
			cont = rep(NS, "\\^g", MODID + name, "\"\\^i\"", l, "\"\\^r\"", s9);
			writeRecipe(name + "_" + metal.substring(21), cont);
			name = w1 + "_to_" + w0;
			cont = rep(SC, "\\^g", MODID + name, "\"\\^i\"", s, "\"\\^r\"", l);
			writeRecipe(name + "_" + metal.substring(21), cont);
		}

		private static final void genPlate(String str) {
			if (str.endsWith("_plate")) {
				String prev = str.substring(0, str.length() - 6);
				String target = prev + "_ingot";
				if (!prev.equals("iron") && !prev.equals("gold"))
					target = MODID + target;
				String ing = MODID + str;
				String cont = rep(PLA, "\"\\^i\"", rep(SI, "\\^i", target), "\"\\^r\"",
						rep(ML, "\\^i", ing, "\\^n", "2"));
				writeRecipe("plate_" + prev, cont);
				if (!prev.equals("iron") && !prev.equals("gold"))
					doMerge(MODID + prev, "_ingot", "_nugget", "ingot", "nugget");
			}
		}

		private static void genPowder(String str) {
			if (str.endsWith("_tiny")) {
				String prev = str.substring(0, str.length() - 5);
				doMerge(MODID + prev, "", "_tiny", "powder", "tiny");
			}
		}

		private static final String rep(String... src) {
			for (int i = 1; i < src.length; i += 2) {
				src[0] = src[0].replaceAll(src[i], src[i + 1]);
			}
			return src[0];
		}

		private static void writeRecipe(String name, String cont) {
			try {
				AssetGen.write(RPATH + "autogen_" + name + ".json", cont);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private static class GUIGen {

		private static final String GUI = "./resources/ASSETS/@textures/@gui/";
		private static final Map<String, Item> ITEM_MAP = new HashMap<>();

		private static class Item {

			private final String name, app;
			private final int w, h, dx, dy;

			private BufferedImage bimg;

			private Item(String str, String appe, JsonObject e) {
				ITEM_MAP.put(appe == null ? str : str + appe, this);
				name = str;
				app = appe;
				w = e.get("w").getAsInt();
				h = e.get("h").getAsInt();
				dx = getInt(e, "dx", 0);
				dy = getInt(e, "dy", 0);
			}

			private BufferedImage getImg() throws IOException {
				if (bimg != null)
					return bimg;
				String path = GUI + "-templates/sprites/" + name;
				if (app != null)
					path += "/" + app;
				path += ".png";
				return bimg = ImageIO.read(new File(path));
			}

			public String toString() {
				return app == null ? name : name + app;
			}

		}

		private static int getInt(JsonObject e, String key, int def) {
			return e.has(key) ? e.get(key).getAsInt() : def;
		}

		private static class Comp {

			private final String name;
			private final Item it;
			private final int x, y, rx, ry;

			private Comp(String str, JsonObject e) {
				name = str;
				it = ITEM_MAP.get(e.get("sprite").getAsString());
				x = e.get("x").getAsInt();
				y = e.get("y").getAsInt();
				rx = getInt(e, "rx", 1);
				ry = getInt(e, "ry", 1);
			}

			private void draw(Graphics g, int cx, int cy) throws IOException {
				for (int i = 0; i < rx; i++)
					for (int j = 0; j < ry; j++)
						g.drawImage(it.getImg(), cx + i * it.w, cy + j * it.h, null);
			}

			private int gety0() {
				return y - it.h / 2;
			}

			private int gety1() {
				return gety0() + ry * it.h;
			}

			public String toString() {
				return name;
			}

		}

		private static void readSprites() throws IOException {
			JsonElement e = AssetMove.readJsonFile(GUI + "-templates/info.json");
			e.getAsJsonObject().entrySet().forEach(ent -> {
				String name = ent.getKey();
				JsonObject o = ent.getValue().getAsJsonObject();
				if (o.has("ids"))
					o.get("ids").getAsJsonArray().forEach(ele -> new Item(name, ele.getAsString(), o));
				else
					new Item(name, null, o);
			});
		}

		private static void gen() throws IOException {
			readSprites();
			File f = new File(GUI + "-templates/container/");
			Item top = ITEM_MAP.get("top");
			Item middle = ITEM_MAP.get("middle");
			Item bottom = ITEM_MAP.get("bottom");
			for (File fi : f.listFiles()) {
				JsonObject e = AssetMove.readJsonFile(fi.getPath()).getAsJsonObject();
				JsonObject out = new JsonObject();
				List<Item> side = new ArrayList<>();
				List<Comp> comp = new ArrayList<>();
				e.get("side").getAsJsonArray().forEach(s -> side.add(ITEM_MAP.get(s.getAsString())));
				for (Entry<String, JsonElement> ent : e.get("comp").getAsJsonObject().entrySet())
					comp.add(new Comp(ent.getKey(), ent.getValue().getAsJsonObject()));
				int y0 = 0, y1 = 0;
				for (Comp c : comp) {
					y0 = Math.min(y0, c.gety0());
					y1 = Math.max(y1, c.gety1());
				}
				out.addProperty("height", top.h + y1 - y0 + bottom.h);
				BufferedImage bimg = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
				Graphics g = bimg.getGraphics();
				g.drawImage(top.getImg(), 0, 0, null);
				for (int i = 0; i < y1 - y0; i++)
					g.drawImage(middle.getImg(), 0, top.h + i, null);
				g.drawImage(bottom.getImg(), 0, top.h + y1 - y0, null);
				JsonObject jarr = new JsonObject();
				for (Comp c : comp) {
					int cx = c.x - c.it.w / 2;
					int cy = c.y - c.it.h / 2 - y0 + top.h;
					c.draw(g, cx, cy);
					JsonObject co = new JsonObject();
					co.addProperty("x", cx + c.it.dx);
					co.addProperty("y", cy + c.it.dy);
					co.addProperty("w", c.it.w);
					co.addProperty("h", c.it.h);
					jarr.add(c.name, co);
				}
				out.add("comp", jarr);
				int dx = 0;
				JsonObject jside = new JsonObject();
				for (Item s : side) {
					JsonObject so = new JsonObject();
					so.addProperty("x", top.w);
					so.addProperty("y", dx);
					so.addProperty("w", s.w);
					so.addProperty("h", s.h);
					jside.add(s.toString(), so);
					g.drawImage(s.getImg(), top.w, dx, null);
					dx += s.h;
				}
				out.add("side", jside);
				g.dispose();
				File fx = new File(GUI + "@container/generated/" + fi.getName().split("\\.")[0] + ".png");
				check(fx);
				ImageIO.write(bimg, "PNG", fx);
				write(GUI + "@coords/" + fi.getName(), out);

			}

		}

		private static void write(String path, JsonObject obj) throws IOException {
			File fy = new File(path);
			check(fy);
			JsonWriter jw = new JsonWriter(Files.newWriter(fy, Charset.defaultCharset()));
			jw.setLenient(true);
			jw.setIndent("\t");
			Streams.write(obj, jw);
			jw.close();
		}

	}

	private static final String PATH_PRE = "./src/main/resources/";
	private static final String PATH_ASSET = PATH_PRE + "assets/quantizedinformatics/";
	private static final String PATH_DATA = PATH_PRE + "data/quantizedinformatics/";

	private static final String BS = PATH_ASSET + "blockstates/";
	private static final String BM = PATH_ASSET + "models/block/";
	private static final String BT = PATH_ASSET + "textures/blocks/";
	private static final String BL = PATH_DATA + "loot_tables/blocks/";
	private static final String IM = PATH_ASSET + "models/item/";
	private static final String IT = PATH_ASSET + "textures/items/";
	private static final String R = PATH_DATA + "recipes/";

	public static void main(String[] strs) throws IOException {
		AssetMove.organize();
	}

	private static void check(File f) throws IOException {
		if (!f.getParentFile().exists())
			f.getParentFile().mkdirs();
		if (!f.exists())
			f.createNewFile();
	}

	private static void delete(File f) {
		if (!f.exists())
			return;
		if (f.isDirectory())
			for (File fi : f.listFiles())
				delete(fi);
		f.delete();
	}

}
