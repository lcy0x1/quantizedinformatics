package com.arthurlcy0x1.quantizedinformatics.blocks.auto;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.AllDireBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.Wire;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
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

public class PipeCore extends Block implements WireConnect {

	public static class Cont extends CTEBlock.CTECont {

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(SIZE));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent) {
			super(Registrar.CTAP_CORE, id, inv, ent, 92);
			for (int i = 0; i < SIZE; i++)
				addSlot(new CondsSlot(ent, i, 44 + i % 5 * 18, 30 + i / 5 * 31, TE::isValid, 1));
		}

	}

	public static class Scr extends CTEBlock.CTEScr<Cont> {

		private static final ResourceLocation GUI = new ResourceLocation(Registrar.MODID,
				"textures/gui/container/pipe_core.png");

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
			s = Translator.getContText("pipe_core.cpu");
			font.drawString(s, xSize / 2 - font.getStringWidth(s) / 2, 19.0F, COLOR);
			s = Translator.getContText("pipe_core.ram");
			font.drawString(s, xSize / 2 - font.getStringWidth(s) / 2, 50.0F, COLOR);
		}

	}

	public static class TE extends CTEBlock.CTETE<TE> implements ITickableTileEntity {

		private static final double DEF_SPEED = 0.04;

		public static boolean isValid(int index, ItemStack is) {
			return false;// TODO
		}

		private double prog = 0;

		public TE() {
			super(Registrar.TETAP_CORE, SIZE);
		}

		@Override
		public Container createMenu(int id, PlayerInventory inv, PlayerEntity pl) {
			return new Cont(id, inv, this);
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("pipe_core");
		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack is) {
			return isValid(index, is);
		}

		@Override
		public void read(CompoundNBT tag) {
			super.read(tag);
			prog = tag.getDouble("progress");
		}

		@Override
		public void tick() {
			if (this.world.isRemote)
				return;
			BlockPos[] ps = Wire.queryPipe(world, pos);
			if (ps == null)
				return;
			PipeHead.TE[] head = new PipeHead.TE[ps.length];
			IInventory[] inv = new IInventory[ps.length];
			Direction[] dire = new Direction[ps.length];
			int count = 0;
			for (int i = 0; i < ps.length; i++) {
				TileEntity te = world.getTileEntity(ps[i]);
				if (te != null && te instanceof PipeHead.TE) {
					head[i] = (PipeHead.TE) te;
					BlockState bs = world.getBlockState(ps[i]);
					if (bs.getBlock() instanceof PipeHead) {
						dire[i] = bs.get(AllDireBlock.FACING);
						TileEntity cont = world.getTileEntity(ps[i].offset(dire[i]));
						if (cont instanceof IInventory) {
							inv[i] = (IInventory) cont;
							count++;
						}
					}
				}
			}
			if (count == 0)
				return;
			prog += DEF_SPEED + getSpeed() / count;
			int cap = (int) prog;
			if (cap == 0)
				return;
			prog -= cap;
			for (int i = 0; i < ps.length; i++) {
				if (head[i] == null || inv[i] == null)
					continue;
				int icap = cap;
				for (int j = 0; j < ps.length; j++) {
					if (icap == 0)
						break;
					if (head[j] == null || inv[j] == null)
						continue;
					icap = transfer(head, inv, dire, i, j, icap);
				}
			}

		}

		@Override
		public CompoundNBT write(CompoundNBT tag) {
			super.write(tag);
			tag.putDouble("progress", prog);
			return tag;
		}

		private double getSpeed() {
			return 0;// TODO
		}

		private int transfer(PipeHead.TE[] te, IInventory[] inv, Direction[] dir, int src, int dst, int cap) {
			PipeHead.TE ts = te[src];
			PipeHead.TE td = te[dst];
			IInventory is = inv[src];
			IInventory id = inv[dst];
			Direction ds = dir[src].getOpposite();
			Direction dd = dir[dst].getOpposite();
			if (is == id && !(is instanceof ISidedInventory))
				return cap;
			for (int i = 0; i < is.getSizeInventory(); i++) {
				if (cap == 0)
					return 0;
				ItemStack i0 = is.getStackInSlot(i).copy();
				if (i0.isEmpty())
					continue;
				if (!ts.canExtract(i0) || !td.canInsert(i0))
					continue;
				if (is instanceof ISidedInventory) {
					ISidedInventory isi = (ISidedInventory) is;
					if (!isi.canExtractItem(i, i0, ds))
						continue;
					if (is == id && isi.canInsertItem(i, i0, dd))
						continue;
				}
				int c0 = i0.getCount();
				int c1 = Math.min(c0, cap);
				i0.setCount(c1);
				ItemStack i1 = HopperTileEntity.putStackInInventoryAllSlots(null, id, i0.copy(), dd);
				int c2 = i1.getCount();
				int tra = c1 - c2;
				cap -= tra;
				i0.setCount(c2 + c0 - c1);
				is.setInventorySlotContents(i, i0);
				if (tra > 0) {
					is.markDirty();
					id.markDirty();
				}
			}
			return cap;
		}
	}

	private static final int SIZE = 5;

	public PipeCore() {
		super(Block.Properties.create(Material.ROCK));
	}

	@Override
	public boolean canConnectFrom(int type, BlockState b, Direction d) {
		return type == PIPE;
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
