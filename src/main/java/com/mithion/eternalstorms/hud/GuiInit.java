package com.mithion.eternalstorms.hud;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class GuiInit {
	
	@SubscribeEvent	
	public static void onClientSetupEvent(FMLClientSetupEvent event) {
		//initialize the HudRenderer class (it's already registered to the Forge bus so we do it here I guess
		HudRenderer.instance = new HudRenderer();
	}
	
}
