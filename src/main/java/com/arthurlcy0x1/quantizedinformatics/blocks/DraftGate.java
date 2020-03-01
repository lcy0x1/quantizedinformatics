package com.arthurlcy0x1.quantizedinformatics.blocks;

import org.apache.logging.log4j.LogManager;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.items.LogicDraft;

import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

public class DraftGate extends CTEBlock<DraftGate.TE> implements DraftBlock {

	public static class Cont extends CTECont {

		protected static class ChipSlot extends Slot {

			public ChipSlot(IInventory inv, int ind, int x, int y) {
				super(inv, ind, x, y);
			}

			@Override
			public boolean isItemValid(ItemStack is) {
				return is.getItem() instanceof LogicDraft;
			}

		}

		public Cont(int id, PlayerInventory inv) {
			this(id, inv, new Inventory(1));
		}

		protected Cont(int id, PlayerInventory inv, IInventory ent) {
			super(Registrar.CT_GATE, id, inv, ent, 0);// TODO height
			this.addSlot(new ChipSlot(slots, 0, 0, 0));

		}

	}

	public static class Panel extends ContainerScreen<DraftGate.Cont> {

		public Panel(DraftGate.Cont cont, PlayerInventory inv, ITextComponent tit) {
			super(cont, inv, tit);
			this.init();
		}

		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
			// TODO draw gui

		}

	}

	public static class TE extends CTEBlock.CTETE<TE> {

		public TE() {
			super(Registrar.TET_GATE, 1);
			LogManager.getLogger().warn("draft gate entity active");
		}

		public TE(BlockState bs, IBlockReader w) {
			this();
		}

		@Override
		public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_,
				PlayerEntity p_createMenu_3_) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ITextComponent getDisplayName() {
			return TITLE;
		}

		@Override
		public int getInventoryStackLimit() {
			return 1;
		}

		@Override
		public boolean isItemValidForSlot(int index, ItemStack stack) {
			return stack.getItem() instanceof LogicDraft;
		}

	}

	private static final ITextComponent TITLE = new TranslationTextComponent(
			"quantizedinformatics::container.draft_gate");

	public DraftGate() {
		super(TE::new);
	}

	@Override
	public int type() {
		return GATE;
	}
}
