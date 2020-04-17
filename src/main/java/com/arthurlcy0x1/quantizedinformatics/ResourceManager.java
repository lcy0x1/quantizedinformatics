package com.arthurlcy0x1.quantizedinformatics;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

public class ResourceManager {

	private static class AssetGen {

		private static final String BS_L, BS_, BS_A, BS_AD, IM_, BM_, BM_M, IM_B, BS_F, BS_W, LT_, IM_W, BM_WS, BM_WC;

		static {
			String path = "./resources/";
			BS_ = readFile(path + "BS/-.json");
			BS_A = readFile(path + "BS/-air.json");
			BS_L = readFile(path + "BS/-lit.json");
			BS_AD = readFile(path + "BS/-alldire.json");
			BS_F = readFile(path + "BS/-face.json");
			BS_W = readFile(path + "BS/-wire.json");

			BM_ = readFile(path + "BM/-.json");
			BM_M = readFile(path + "BM/-machine.json");
			BM_WS = readFile(path + "BM/-wire_side.json");
			BM_WC = readFile(path + "BM/-wire_core.json");

			IM_ = readFile(path + "IM/-.json");
			IM_B = readFile(path + "IM/-block.json");
			IM_W = readFile(path + "IM/-wire.json");

			LT_ = readFile(path + "BL/-.json");

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

	public static void main(String[] strs) throws IOException {
		organize();
	}

	public static void organize() throws IOException {
		delete(new File(PATH_ASSET));
		delete(new File(PATH_DATA));
		orgImpl("ASSETS");
		orgBlocks();
		orgItems();
		orgImpl("R");
	}

	private static void check(File f) throws IOException {
		if (!f.getParentFile().exists())
			f.getParentFile().mkdirs();
		if (!f.exists())
			f.createNewFile();
	}

	private static void copyTo(File file, String path) throws IOException {
		File f = new File(path);
		check(f);
		Files.copy(file, f);
	}

	private static void delete(File f) {
		if (!f.exists())
			return;
		if (f.isDirectory())
			for (File fi : f.listFiles())
				delete(fi);
		f.delete();
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
		for (String item : orgImpl("IT"))
			if (!map.get("ignore").contains(item))
				AssetGen.addItemAssets(item);
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

	private static Map<String, List<String>> readJson(String path) throws IOException {
		File f = new File(path);
		JsonReader r = new JsonReader(Files.newReader(f, Charset.defaultCharset()));
		JsonElement e = new JsonParser().parse(r);
		r.close();
		Map<String, List<String>> ans = new HashMap<>();
		e.getAsJsonObject().entrySet().forEach(ent0 -> ent0.getValue().getAsJsonObject().entrySet().forEach(ent1 -> {
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
