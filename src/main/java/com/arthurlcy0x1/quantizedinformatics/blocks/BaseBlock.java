package com.arthurlcy0x1.quantizedinformatics.blocks;

import static net.minecraft.state.properties.BlockStateProperties.POWER_0_15;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.arthurlcy0x1.quantizedinformatics.logic.LogicRE;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class BaseBlock extends Block {

	public static class BlockImplementor {

		private final Properties props;
		private final List<IState> stateList = new ArrayList<IState>();

		private IRotMir rotmir;
		private IFace face;
		private ITE ite;
		private IClick click;
		private ILight light;
		private IPower power;

		public BlockImplementor(Properties p) {
			props = p;
		}

		public BlockImplementor addImpl(IImpl impl) {
			if (impl instanceof IState)
				stateList.add((IState) impl);
			if (impl instanceof STE)
				impl = new TEPvd((STE) impl);
			for (Field f : getClass().getDeclaredFields())
				if (IImpl.class.isAssignableFrom(f.getType()) && f.getType().isAssignableFrom(impl.getClass()))
					try {
						f.setAccessible(true);
						if (f.get(this) != null)
							throw new LogicRE("implementation conflict");
						f.set(this, impl);
					} catch (Exception e) {
						throw new LogicRE("security error");
					}
			return this;
		}

		public BlockImplementor addImpls(IImpl... impls) {
			for (IImpl impl : impls)
				addImpl(impl);
			return this;
		}

	}

	public static interface IClick extends IImpl {

		public ActionResultType onClick(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h);

	}

	public static interface IImpl {
	}

	public static interface ILight extends IImpl {

		public int getLightValue(BlockState bs);

	}

	public static interface IState extends IImpl {

		public void fillStateContainer(Builder<Block, BlockState> builder);

	}

	public static interface STE extends IImpl, Supplier<TileEntity> {

		@Override
		public TileEntity get();

	}

	private static class AllDireBlock implements BaseBlock.IFace, BaseBlock.IState {

		private AllDireBlock() {
		}

		@Override
		public void fillStateContainer(Builder<Block, BlockState> builder) {
			builder.add(FACING);
		}

		@Override
		public BlockState getStateForPlacement(BlockState def, BlockItemUseContext context) {
			return def.with(FACING, context.getFace().getOpposite());
		}

	}

	private static class HorizontalBlock implements IRotMir, IState, IFace {

		private HorizontalBlock() {
		}

		@Override
		public void fillStateContainer(Builder<Block, BlockState> builder) {
			builder.add(HORIZONTAL_FACING);
		}

		@Override
		public BlockState getStateForPlacement(BlockState def, BlockItemUseContext context) {
			return def.with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
		}

		@Override
		public BlockState mirror(BlockState state, Mirror mirrorIn) {
			return state.rotate(mirrorIn.toRotation(state.get(HORIZONTAL_FACING)));
		}

		@Override
		public BlockState rotate(BlockState state, Rotation rot) {
			return state.with(HORIZONTAL_FACING, rot.rotate(state.get(HORIZONTAL_FACING)));
		}
	}

	private static interface IFace extends IImpl {

		public BlockState getStateForPlacement(BlockState def, BlockItemUseContext context);

	}

	private static interface IPower extends IImpl {

		public int getWeakPower(BlockState bs, IBlockReader r, BlockPos pos, Direction d);

	}

	private static interface IRotMir extends IImpl {

		public BlockState mirror(BlockState state, Mirror mirrorIn);

		public BlockState rotate(BlockState state, Rotation rot);
	}

	private static interface ITE extends IImpl {

		public TileEntity createTileEntity(BlockState state, IBlockReader world);

	}

	private static class Power implements IState, IPower {

		private Power() {
		}

		@Override
		public void fillStateContainer(Builder<Block, BlockState> builder) {
			builder.add(POWER_0_15);
		}

		@Override
		public int getWeakPower(BlockState bs, IBlockReader r, BlockPos pos, Direction d) {
			return bs.get(POWER_0_15);
		}

	}

	private static class TEPvd implements ITE, IClick {

		private final Supplier<? extends TileEntity> f;

		private TEPvd(Supplier<? extends TileEntity> sup) {
			f = sup;
		}

		@Override
		public TileEntity createTileEntity(BlockState state, IBlockReader world) {
			return f.get();
		}

		@Override
		public ActionResultType onClick(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h) {
			if (w.isRemote)
				return ActionResultType.SUCCESS;
			TileEntity te = w.getTileEntity(pos);
			if (te instanceof INamedContainerProvider)
				pl.openContainer((INamedContainerProvider) te);
			return ActionResultType.SUCCESS;
		}

	}

	public static final Power POW = new Power();
	public static final AllDireBlock ALD = new AllDireBlock();
	public static final HorizontalBlock HOR = new HorizontalBlock();

	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

	private static BlockImplementor TEMP;

	public static BlockImplementor construct(BlockProp bb) {
		return new BlockImplementor(bb.getProps());
	}

	private static Properties handler(BlockImplementor bi) {
		if (TEMP != null)
			throw new LogicRE("concurrency error");
		TEMP = bi;
		return bi.props;
	}

	private BlockImplementor impl;

	public BaseBlock(BlockImplementor bimpl) {
		super(handler(bimpl));
	}

	@Override
	public final boolean canProvidePower(BlockState bs) {
		return impl.power != null;
	}

	@Override
	public final TileEntity createTileEntity(BlockState state, IBlockReader world) {
		if (impl.ite != null)
			return impl.ite.createTileEntity(state, world);
		return null;
	}

	@Override
	public final ActionResultType func_225533_a_(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h,
			BlockRayTraceResult r) {
		return onClick(bs, w, pos, pl, h);
	}

	@Override
	public final int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		if (impl.ite == null)
			return 0;
		TileEntity te = worldIn.getTileEntity(pos);
		return te == null ? 0 : Container.calcRedstone(te);
	}

	@Override
	public final int getLightValue(BlockState bs) {
		return impl.light == null ? lightValue : impl.light.getLightValue(bs);
	}

	@Override
	public final BlockState getStateForPlacement(BlockItemUseContext context) {
		if (impl.face == null)
			return getDefaultState();
		return impl.face.getStateForPlacement(getDefaultState(), context);
	}

	@Override
	public final int getWeakPower(BlockState bs, IBlockReader r, BlockPos pos, Direction d) {
		return impl.power == null ? 0 : impl.power.getWeakPower(bs, r, pos, d);
	}

	@Override
	public final boolean hasTileEntity(BlockState state) {
		return impl.ite != null;
	}

	@Override
	public final BlockState mirror(BlockState state, Mirror mirrorIn) {
		if (impl.rotmir != null)
			return impl.rotmir.mirror(state, mirrorIn);
		return state;
	}

	public final ActionResultType onClick(BlockState bs, World w, BlockPos pos, PlayerEntity pl, Hand h) {
		return impl.click == null ? ActionResultType.PASS : impl.click.onClick(bs, w, pos, pl, h);
	}

	@Override
	public final void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		if (impl.ite != null && state.getBlock() != newState.getBlock()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity != null) {
				if (tileentity instanceof IInventory) {
					InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileentity);
					worldIn.updateComparatorOutputLevel(pos, this);

				}
				worldIn.removeTileEntity(pos);
			}
		}
	}

	@Override
	public final BlockState rotate(BlockState state, Rotation rot) {
		if (impl.rotmir != null)
			return impl.rotmir.rotate(state, rot);
		return state;
	}

	protected void addImpls(BlockImplementor impl) {
	}

	@Override
	protected final void fillStateContainer(Builder<Block, BlockState> builder) {
		impl = TEMP;
		TEMP = null;
		addImpls(impl);
		for (IState is : impl.stateList)
			is.fillStateContainer(builder);
	}

}
