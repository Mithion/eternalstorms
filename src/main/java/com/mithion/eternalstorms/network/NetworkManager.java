package com.mithion.eternalstorms.network;

import com.mithion.eternalstorms.EternalStorms;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod.EventBusSubscriber(modid = EternalStorms.MODID, bus = Bus.MOD)
public class NetworkManager {
	static final String PROTOCOL_VERSION = "1";

	static final SimpleChannel network = NetworkRegistry.newSimpleChannel(new ResourceLocation(EternalStorms.MODID, "main"),
			() -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	
	@SubscribeEvent
	public static void setup(final FMLCommonSetupEvent event) {
		int packet_id = 1;
		
		//--------------------------------------
		//  Server -> Client Messages
		//--------------------------------------
		network.registerMessage(packet_id++, SyncTemperatureMessage.class, SyncTemperatureMessage::encode, SyncTemperatureMessage::decode,
				ClientMessageHandler::handleSyncTemperatureMessage);
	}
}
