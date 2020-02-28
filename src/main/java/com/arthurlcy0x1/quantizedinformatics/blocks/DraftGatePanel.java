package com.arthurlcy0x1.quantizedinformatics.blocks;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class DraftGatePanel extends ContainerScreen<DraftGateCont> {

	public DraftGatePanel(DraftGateCont cont, PlayerInventory inv, ITextComponent tit) {
		super(cont, inv, tit);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		// TODO draw gui
	}

}
