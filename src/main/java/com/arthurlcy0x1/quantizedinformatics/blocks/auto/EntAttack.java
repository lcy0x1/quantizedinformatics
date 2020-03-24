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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntAttack extends BaseBlock {

	public static class Cont extends CTEBlock.CTECont {

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(SIZE));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent) {
			super(Registrar.CTME_ATK, id, inv, ent, 0);
			// TODO Auto-generated constructor stub
		}

	}

	@OnlyIn(Dist.CLIENT)
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

		private PlayerEntity ent;

		public TE() {
			super(Registrar.TETME_ATK, SIZE, EntMachine.TYPE_ATK);
		}

		@Override
		public Container createMenu(int id, PlayerInventory inv, PlayerEntity pe) {
			ent = pe;
			return new Cont(id, inv, this);
		}

		public float getDamage() {
			return 1 << 3 * Math.min(MAX_POWER, getPower());
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("ent_attack");
		}

		public Entity getSource() {
			return ent;
		}

		@Override
		protected void handle(Entity e, Vec3d dir) {
			e.attackEntityFrom(getDamageSource(), getDamage());
		}

		private DamageSource getDamageSource() {
			return new Source(this);
		}

	}

	private static class Source extends DamageSource {

		private final TE ent;

		public Source(TE te) {
			super(Registrar.MODID + ".ent_attack");
			ent = te;
		}

		@Override
		public Entity getTrueSource() {
			return ent.getSource();
		}

	}

	private static final int SIZE = 0, MAX_POWER = 6;

	public EntAttack() {
		super(construct(BlockProp.M_ENT).addImpl((STE) TE::new));
	}

}
