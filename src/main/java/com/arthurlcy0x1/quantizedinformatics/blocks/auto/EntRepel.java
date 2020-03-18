package com.arthurlcy0x1.quantizedinformatics.blocks.auto;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.BaseBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;

public class EntRepel extends BaseBlock {

	public static class Cont extends CTEBlock.CTECont {

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(SIZE));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent) {
			super(Registrar.CTME_REP, id, inv, ent, 0);
			// TODO Auto-generated constructor stub
		}

	}

	public static class Scr extends CTEBlock.CTEScr<Cont> {

		public Scr(Cont cont, PlayerInventory inv, ITextComponent text) {
			super(cont, inv, text, 0);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			// TODO Auto-generated method stub

		}

	}

	public static class TE extends EntMachine.EMTE<TE> {

		public TE() {
			super(Registrar.TETME_REP, SIZE, EntMachine.TYPE_DEF);
		}

		@Override
		public Container createMenu(int id, PlayerInventory inv, PlayerEntity pe) {
			return new Cont(id, inv, this);
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("ent_attack");
		}

		public double getMaxVec() {
			return SPEED_FAC * (1 << 3 * Math.min(MAX_SPEED, getPower()));
		}

		@Override
		protected void handle(Entity e, Vec3d dir) {
			double r = getRadius();
			double dis = dir.length();
			if (dis > 0.5) {
				dir = dir.scale((r - dis) / dis / r);
				e.addVelocity(dir.x, dir.y, dir.z);
			}
		}

	}

	public static DamageSource DNGSRC = new DamageSource(Registrar.MODID + ".ent_attack");

	private static final int SIZE = 0, MAX_SPEED = 6;
	private static final double SPEED_FAC = 0.01;

	public EntRepel() {
		super(construct(BlockProp.M_ENT).addImpl((STE) TE::new));
	}

}
