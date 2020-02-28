package com.arthurlcy0x1.quantizedinformatics.blocks;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;

public class OxiFnCont extends BaseCont {

	public OxiFnCont(int id, PlayerInventory inv) {
		this(id, inv, new Inventory(7));
	}

	protected OxiFnCont(int id, PlayerInventory inv, IInventory ent) {
		super(Registrar.CT_OXIFN, id, inv, ent);

		// TODO add slots
	}

}
