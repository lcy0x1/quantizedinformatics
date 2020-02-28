package com.arthurlcy0x1.quantizedinformatics.blocks;

import com.arthurlcy0x1.quantizedinformatics.Registrar;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;

public class RedFnCont extends BaseCont {

	public RedFnCont(int id, PlayerInventory inv) {
		this(id, inv, new Inventory(7));
	}

	protected RedFnCont(int id, PlayerInventory inv, IInventory ent) {
		super(Registrar.CT_REDFN, id, inv, ent);

		// TODO add slots
	}

}
