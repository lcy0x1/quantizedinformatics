package com.arthurlcy0x1.quantizedinformatics;

import com.arthurlcy0x1.quantizedinformatics.items.Telescope;

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
			if (i instanceof Telescope)
				event.setNewfov(event.getFov() * Telescope.getItemZoom(is));

		}
	}

}
