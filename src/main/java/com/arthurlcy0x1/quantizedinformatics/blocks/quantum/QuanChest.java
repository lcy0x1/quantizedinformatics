package com.arthurlcy0x1.quantizedinformatics.blocks.quantum;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class QuanChest extends QuanBlock {

	public static class Cont extends CTEBlock.CTECont {

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(27));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent) {
			super(Registrar.CTQ_CHEST, id, inv, ent, 84);
			for (int i = 0; i < 3; i++)
				for (int j = 0; j < 9; j++)
					addSlot(new Slot(ent, i * 9 + j, 8 + j * 18, 18 + i * 18));
		}

	}

	public static class Scr extends CTEBlock.CTEScr<Cont> {

		private static final ResourceLocation GUI = new ResourceLocation(Registrar.MODID,
				"textures/gui/container/quantum_world_chest.png");

		public Scr(Cont cont, PlayerInventory inv, ITextComponent text) {
			super(cont, inv, text, 166);
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.minecraft.getTextureManager().bindTexture(GUI);
			int i = guiLeft;
			int j = guiTop;
			blit(i, j, 0, 0, xSize, ySize);
		}

	}

	public static class TE extends CTEBlock.CTETE<QuanChest.TE> {

		public TE() {
			super(Registrar.TETQ_CHEST, 27);
		}

		@Override
		public Container createMenu(int wid, PlayerInventory pi, PlayerEntity pl) {
			return new Cont(wid, pi, this);
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("quantum_world_chest");
		}

	}

	public QuanChest() {
		super(construct(BlockProp.QW_BREAK).addImpl((STE) TE::new));
	}

}
