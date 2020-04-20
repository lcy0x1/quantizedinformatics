package com.arthurlcy0x1.quantizedindustry;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Slot;
import net.minecraft.resources.IResource;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

public class SpriteManager {

	public static interface SlotFactory<T extends Slot> {

		public T getSlot(int x, int y);

	}

	public static class Rect {

		public static final Rect ZERO = new Rect(0, 0, 0, 0);

		public final int x, y, w, h;

		private Rect(int x0, int y0, int w0, int h0) {
			x = x0;
			y = y0;
			w = w0;
			h = h0;
		}

	}

	@OnlyIn(Dist.CLIENT)
	public class ScreenRenderer {

		private final int x, y, w, h;
		private final ContainerScreen<?> scr;

		private ScreenRenderer(ContainerScreen<?> scrIn) {
			x = scrIn.getGuiLeft();
			y = scrIn.getGuiTop();
			w = scrIn.getXSize();
			h = scrIn.getYSize();
			scr = scrIn;
		}

		public void start() {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			scr.getMinecraft().getTextureManager().bindTexture(texture);
			scr.blit(x, y, 0, 0, w, h);
		}

		public void draw(String c, String s) {
			Rect cr = getComp(c);
			Rect sr = getSide(s);
			scr.blit(x + cr.x, y + cr.y, sr.x, sr.y, sr.w, sr.h);
		}

		public void drawLeftRight(String c, String s, double per) {
			Rect cr = getComp(c);
			Rect sr = getSide(s);
			int dw = (int) Math.round(sr.w * per);
			scr.blit(x + cr.x, y + cr.y, sr.x, sr.y, dw, sr.h);
		}

		public void drawBottomUp(String c, String s, double per) {
			Rect cr = getComp(c);
			Rect sr = getSide(s);
			int dh = (int) Math.round(sr.h * per);
			scr.blit(x + cr.x, y + cr.y + sr.h - dh, sr.x, sr.y, sr.w, dh);
		}

	}

	private final String name;
	private final Map<String, Rect> side = new HashMap<>();
	private final Map<String, Rect> comp = new HashMap<>();
	private final ResourceLocation coords, texture;

	private int height = 0;
	private boolean loaded = false;

	public SpriteManager(String mod, String str) {
		name = mod + ":" + str;
		coords = new ResourceLocation(mod, "/textures/gui/coords/" + str + ".json");
		texture = new ResourceLocation(mod, "/textures/gui/container/" + str + ".png");
		check();
	}

	private void check() {
		if (!loaded)
			DistExecutor.runWhenOn(Dist.CLIENT, () -> this::load);
	}

	private void load() {
		try {
			IResource r = Minecraft.getInstance().getResourceManager().getResource(coords);
			JsonObject jo = JSONUtils.fromJson(new InputStreamReader(r.getInputStream()));
			height = JSONUtils.getInt(jo, "height");
			JSONUtils.getJsonObject(jo, "side").entrySet().forEach(ent -> {
				JsonObject co = ent.getValue().getAsJsonObject();
				int x = JSONUtils.getInt(co, "x");
				int y = JSONUtils.getInt(co, "y");
				int w = JSONUtils.getInt(co, "w");
				int h = JSONUtils.getInt(co, "h");
				side.put(ent.getKey(), new Rect(x, y, w, h));
			});
			JSONUtils.getJsonObject(jo, "comp").entrySet().forEach(ent -> {
				JsonObject co = ent.getValue().getAsJsonObject();
				int x = JSONUtils.getInt(co, "x");
				int y = JSONUtils.getInt(co, "y");
				int w = JSONUtils.getInt(co, "w");
				int h = JSONUtils.getInt(co, "h");
				comp.put(ent.getKey(), new Rect(x, y, w, h));
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		loaded = true;
	}

	@OnlyIn(Dist.CLIENT)
	public ScreenRenderer getRenderer(ContainerScreen<?> blit) {
		check();
		return new ScreenRenderer(blit);
	}

	public <T extends Slot> T getSlot(String key, SlotFactory<T> fac) {
		check();
		Rect c = getComp(key);
		return fac.getSlot(c.x, c.y);
	}

	public Rect getComp(String key) {
		check();
		return comp.containsKey(key) ? comp.get(key) : Rect.ZERO;
	}

	public boolean within(String key, double x, double y) {
		check();
		Rect c = getComp(key);
		return x > c.x && x < c.x + c.w && y > c.y && y < c.y + c.h;
	}

	public Rect getSide(String key) {
		check();
		return side.containsKey(key) ? side.get(key) : Rect.ZERO;
	}

	public int getHeight() {
		check();
		return height;
	}

	public int getPIH() {
		check();
		return height - 83;
	}

	public String toString() {
		return name;
	}

}
