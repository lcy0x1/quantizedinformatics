package com.arthurlcy0x1.quantizedinformatics.blocks.auto;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.Translator;
import com.arthurlcy0x1.quantizedinformatics.blocks.AllDireBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.CTEBlock;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class AutoPipe extends AllDireBlock implements WireConnect {

	public static class Cont extends CTEBlock.CTECont {

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(SIZE));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent) {
			super(Registrar.CTA_PIPE, id, inv, ent, 0);
			// TODO Auto-generated constructor stub
		}

	}

	public static class TE extends CTEBlock.CTETE<TE> {

		public TE() {
			super(Registrar.TETA_PIPE, SIZE);
		}

		@Override
		public Container createMenu(int id, PlayerInventory inv, PlayerEntity pl) {
			return new Cont(id, inv, this);
		}

		@Override
		public ITextComponent getDisplayName() {
			return Translator.getCont("auto_pipe");
		}

	}

	private static final int SIZE = 5;

	public AutoPipe() {
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
