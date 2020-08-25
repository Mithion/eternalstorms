package com.mithion.eternalstorms.capabilities;

import com.mithion.eternalstorms.EternalStorms;
import com.mithion.eternalstorms.capabilities.player.PlayerCapabilities;
import com.mithion.eternalstorms.capabilities.player.PlayerCapabilitiesFactory;
import com.mithion.eternalstorms.capabilities.player.PlayerCapabilitiesStorage;
import com.mithion.eternalstorms.capabilities.world.WorldCapabilities;
import com.mithion.eternalstorms.capabilities.world.WorldCapabilitiesFactory;
import com.mithion.eternalstorms.capabilities.world.WorldCapabilitiesStorage;

import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = EternalStorms.MODID, bus=Bus.MOD)
public class CapabilityModInit {
	@SubscribeEvent
	public static void setup(final FMLCommonSetupEvent event) {
		//register capabilities so they exist
		CapabilityManager.INSTANCE.register(PlayerCapabilities.class, new PlayerCapabilitiesStorage(), new PlayerCapabilitiesFactory());
		CapabilityManager.INSTANCE.register(WorldCapabilities.class, new WorldCapabilitiesStorage(), new WorldCapabilitiesFactory());
	}
}
