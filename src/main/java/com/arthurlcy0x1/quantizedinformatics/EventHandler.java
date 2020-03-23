package com.arthurlcy0x1.quantizedinformatics;

import com.arthurlcy0x1.quantizedinformatics.items.EntityCannon;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler {

	@SubscribeEvent
	public void zoom(FOVUpdateEvent event) {
		ItemStack is = event.getEntity().getActiveItemStack();
		if (is != null) {
			Item i = is.getItem();
			if (i instanceof EntityCannon)
				event.setNewfov(event.getFov() * (((EntityCannon) i).getZoom(event.getEntity())));

		}
	}

}
