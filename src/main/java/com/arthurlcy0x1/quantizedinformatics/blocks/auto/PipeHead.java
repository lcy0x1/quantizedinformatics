package com.arthurlcy0x1.quantizedinformatics.blocks.auto;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.AllDireBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class PipeHead extends AllDireBlock implements WireConnect {

	public static class Cont extends CTEBlock.CTECont {

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(SIZE));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent) {
			super(Registrar.CTAP_HEAD, id, inv, ent, 92);
			for (int i = 0; i < SIZE; i++)
				this.addSlot(new SizeSlot(ent, i, 44 + i % 5 * 18, 30 + i / 5 * 31, 1));
		}

	}

	public static class Scr extends CTEBlock.CTEScr<Cont> {

		private static final ResourceLocation GUI = new ResourceLocation(Registrar.MODID,
				"textures/gui/container/pipe_head.png");

		public Scr(Cont cont, PlayerInventory inv, ITextComponent text) {
			super(cont, inv, text, 174);
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.minecraft.getTextureManager().bindTexture(GUI);
			int i = guiLeft;
			int j = guiTop;
			blit(i, j, 0, 0, xSize, ySize);

		}

		@Override
		protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
			super.drawGuiContainerForegroundLayer(mouseX, mouseY);
			String s;
			s = Translator.getContText("pipe_head.extract");
			font.drawString(s, xSize / 2 - font.getStringWidth(s) / 2, 19.0F, COLOR);
			s = Translator.getContText("pipe_head.insert");
			font.drawString(s, xSize / 2 - font.getStringWidth(s) / 2, 50.0F, COLOR);
		}

	}

	public static class TE extends CTEBlock.CTETE<TE> {

		public TE() {
			super(Registrar.TETAP_HEAD, SIZE);
		}

		@Override
		public Container createMenu(int id, PlayerInventory inv, PlayerEntity pl) {
			return new Cont(id, inv, this);
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("pipe_head");
		}

		protected boolean canExtract(ItemStack is) {
			if (is.isEmpty())
				return false;
			for (int i = 0; i < 5; i++) {
				ItemStack in = getStackInSlot(i);
				if (!in.isEmpty() && Container.areItemsAndTagsEqual(is, in))
					return true;
			}
			return false;
		}

		protected boolean canInsert(ItemStack is) {
			if (is.isEmpty())
				return false;
			for (int i = 5; i < 10; i++) {
				ItemStack in = getStackInSlot(i);
				if (!in.isEmpty() && Container.areItemsAndTagsEqual(is, in))
					return true;
			}
			return false;
		}

	}

	private static final int SIZE = 10;

	public PipeHead() {
		super(Block.Properties.create(Material.ROCK));
	}

	@Override
	public boolean canConnectFrom(int type, BlockState b, Direction d) {
		return type == PIPE && b.get(FACING) == d.getOpposite();
	}

	@Override
	public TE createTileEntity(BlockState state, IBlockReader world) {
		return new TE();
	}

	@Override
	public ActionResultType func_225533_a_(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h,
			BlockRayTraceResult r) {
		return onClick(bs, w, pos, pl, h);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	public ActionResultType onClick(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h) {
		if (w.isRemote)
			return ActionResultType.SUCCESS;
		TileEntity te = w.getTileEntity(pos);
		if (te instanceof INamedContainerProvider)
			pl.openContainer((INamedContainerProvider) te);
		return ActionResultType.SUCCESS;
	}

}
