package com.arthurlcy0x1.quantizedinformatics;

import com.arthurlcy0x1.quantizedinformatics.items.SoulItem.SoulMarker;
import com.arthurlcy0x1.quantizedinformatics.items.Telescope;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler {

	@SubscribeEvent
	public void zoom(FOVUpdateEvent event) {
		ItemStack is = event.getEntity().getActiveItemStack();
		if (!is.isEmpty()) {
			Item i = is.getItem();
			if (i instanceof Telescope)
				event.setNewfov(event.getFov() * Telescope.getItemZoom(is));

		}
	}

	@SubscribeEvent
	public void interact(EntityInteract ei) {
		ItemStack is = ei.getItemStack();
		if (!is.isEmpty()) {
			Item i = is.getItem();
			if (i instanceof SoulMarker) {
				Entity ent = ei.getTarget();
				if (ent != null) {
					((SoulMarker) i).interact(is, ei.getPlayer(), ent);
					ei.setCanceled(true);
					ei.setCancellationResult(ActionResultType.SUCCESS);
				}
			}
		}
	}

}
