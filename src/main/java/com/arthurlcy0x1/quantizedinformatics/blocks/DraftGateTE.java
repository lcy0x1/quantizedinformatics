package com.arthurlcy0x1.quantizedinformatics.blocks;

import org.apache.logging.log4j.LogManager;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import com.arthurlcy0x1.quantizedinformatics.items.DraftLogicGate;
import com.arthurlcy0x1.quantizedinformatics.items.LogicDraft;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class DraftGateTE extends TileEntity implements IInventory, INamedContainerProvider {

	private static final ITextComponent title = new TranslationTextComponent(
			"quantizedinformatics::container.draftgate");

	private final Inventory inv = new Inventory(1);

	public DraftGateTE() {
		super(Registrar.TET_GATE);
		LogManager.getLogger().warn("draft gate entity active");
	}

	@Override
	public void clear() {
		inv.clear();
	}

	@Override
	public Container createMenu(int type, PlayerInventory inv, PlayerEntity ent) {
		return new DraftGateCont(type, inv, this);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return inv.decrStackSize(index, count);
	}

	@Override
	public ITextComponent getDisplayName() {
		return title;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public int getSizeInventory() {
		return inv.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inv.getStackInSlot(index);
	}

	@Override
	public boolean isEmpty() {
		return inv.isEmpty();
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return stack.getItem() instanceof LogicDraft;
	}

	@Override
	public boolean isUsableByPlayer(PlayerEntity player) {
		return inv.isUsableByPlayer(player);// TODO
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return inv.removeStackFromSlot(index);
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inv.setInventorySlotContents(index, stack);
	}

}
