package com.arthurlcy0x1.quantizedinformatics.blocks;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.blocks.WireConnect.*;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

import static net.minecraft.state.properties.BlockStateProperties.POWER_0_15;

public class DraftOut extends DIOTerm<DraftOut.TE> {

	public static class Cont extends DIOCont {

		public Cont(int id, PlayerInventory inv) {
			this(id, new IntArray(1));
		}

		protected Cont(int id, IIntArray arr) {
			super(Registrar.CTD_OUT, id, new SignalWriter(1, 0, arr));
		}

	}

	public static class TE extends DTETerm<TE> implements INamedContainerProvider {

		public TE() {
			super(Registrar.TET_OUT, INPUT);
		}

		@Override
		public Container createMenu(int id, PlayerInventory pi, PlayerEntity pl) {
			return new Cont(id, getSignal());
		}

		@Override
		public ITextComponent getDisplayName() {
			return TITLE;
		}

		@Override
		public int[] update(int[] vals) {
			int val = vals[getSignal().getInput(0)];
			int power = val == S_HIGH ? 15 : 0;
			world.setBlockState(pos, getBlockState().with(POWER_0_15, power));
			return vals;
		}

	}

	private static final ITextComponent TITLE = new TranslationTextComponent(
			"quantizedinformatics:container.draft_out");

	public DraftOut() {
		super(TE::new, INPUT);
	}

	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}

	@Override
	public void fillStateContainer(Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(POWER_0_15);
	}

	@Override
	public int getWeakPower(BlockState bs, IBlockReader r, BlockPos pos, Direction d) {
		return bs.get(POWER_0_15);
	}

}
