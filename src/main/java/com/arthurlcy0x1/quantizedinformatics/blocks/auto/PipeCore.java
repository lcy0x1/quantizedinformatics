package com.arthurlcy0x1.quantizedinformatics.blocks.auto;

import java.util.ArrayList;
import java.util.List;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.BaseBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.BlockProp;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.Wire;
import com.arthurlcy0x1.quantizedinformatics.blocks.other.WireConnect;
import com.arthurlcy0x1.quantizedinformatics.items.logic.ALUItem;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PipeCore extends BaseBlock implements WireConnect {

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

	@OnlyIn(Dist.CLIENT)
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
			cstr(Translator.getContText("pipe_core.cpu"), 19);
			cstr(Translator.getContText("pipe_core.ram"), 50);
		}

	}

	public static class TE extends CTEBlock.CTETE<TE> implements ITickableTileEntity {

		private static class Triplet {

			private PipeHead.TE te;
			private IInventory inv;
			private Direction dire;

			private Triplet(PipeHead.TE phte, Direction dir, IInventory i) {
				te = phte;
				dire = dir;
				inv = i;
			}

		}

		private static final double DEF_SPEED = 0.04;

		public static boolean isValid(int index, ItemStack is) {
			if (index < 5)
				return is.getItem() instanceof ALUItem;
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
			List<Triplet> list = new ArrayList<>();
			for (int i = 0; i < ps.length; i++) {
				TileEntity te = world.getTileEntity(ps[i]);
				if (te == null || !(te instanceof PipeHead.TE))
					continue;
				PipeHead.TE phte = (PipeHead.TE) te;
				BlockState bs = world.getBlockState(ps[i]);
				if (bs.getBlock() != Registrar.BAP_HEAD)
					continue;
				Direction dir = bs.get(FACING);
				BlockPos tar = ps[i].offset(dir);
				TileEntity cont = world.getTileEntity(tar);
				if (cont != null && cont instanceof IInventory) {
					IInventory inv = (IInventory) cont;
					list.add(new Triplet(phte, dir, inv));
				} else {
					BlockState tbs = world.getBlockState(tar);
					Block b = tbs.getBlock();
					if (!(b instanceof WireConnect))
						continue;
					WireConnect wc = (WireConnect) b;
					if (!wc.canConnectFrom(SPIPE, tbs, dir.getOpposite()))
						continue;
					BlockPos[] subs = Wire.querySubPipe(world, ps[i]);
					if (subs == null)
						continue;
					for (BlockPos sp : subs) {
						BlockState sbs = world.getBlockState(sp);
						if (sbs.getBlock() != Registrar.BAP_SHEAD)
							continue;
						Direction sdir = sbs.get(FACING);
						BlockPos star = sp.offset(sdir);
						TileEntity scont = world.getTileEntity(star);
						if (scont == null || !(scont instanceof IInventory))
							continue;
						IInventory inv = (IInventory) scont;
						list.add(new Triplet(phte, sdir, inv));

					}
				}
			}
			if (list.size() == 0)
				return;
			prog += DEF_SPEED + getSpeed() / list.size();
			int cap = (int) prog;
			if (cap == 0)
				return;
			prog -= cap;
			for (Triplet i : list) {
				int icap = cap;
				for (Triplet j : list) {
					if (icap == 0)
						break;
					icap = transfer(i, j, icap);
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
			int ans = 0;
			for (int i = 0; i < 5; i++)
				ans += ALUItem.getSpeed(getStackInSlot(i));
			return ans;
		}

		private int transfer(Triplet src, Triplet dst, int cap) {
			PipeHead.TE ts = src.te;
			PipeHead.TE td = dst.te;
			IInventory is = src.inv;
			IInventory id = dst.inv;
			Direction ds = src.dire.getOpposite();
			Direction dd = dst.dire.getOpposite();
			if (is == id && !(is instanceof ISidedInventory))
				return cap;
			for (int i = 0; i < is.getSizeInventory(); i++) {
				if (cap == 0)
					return 0;
				ItemStack i0 = is.getStackInSlot(i).copy();
				if (i0.isEmpty())
					continue;
				int inst = td.canInsert(i0, id, dd);
				if (!ts.canExtract(i0) || inst == 0)
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
				if (inst > 0)
					c1 = Math.min(inst, c1);
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

	private static final int SIZE = 10;

	public PipeCore() {
		super(construct(BlockProp.M_PIPE).addImpl((STE) TE::new));
	}

	@Override
	public boolean canConnectFrom(int type, BlockState b, Direction d) {
		return type == PIPE;
	}

}
