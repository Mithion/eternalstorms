package com.mithion.eternalstorms.network;

import java.util.Optional;
import java.util.function.Supplier;

import com.mithion.eternalstorms.EternalStorms;
import com.mithion.eternalstorms.capabilities.player.PlayerCapabilities;
import com.mithion.eternalstorms.capabilities.player.PlayerCapabilitiesProvider;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClientMessageHandler {
	
	/*
	 * Handles a SyncTemperatureMessage when received by the client.
	 */
	public static void handleSyncTemperatureMessage(final SyncTemperatureMessage message, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
		
		Optional<ClientWorld> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
		if (!clientWorld.isPresent()) {
			EternalStorms.LOGGER.error("SyncTemperatureMessage context could not provide a ClientWorld");
			return;
		}
		
		ctx.enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			PlayerCapabilities caps = mc.player.getCapability(PlayerCapabilitiesProvider.PLAYER_CAP).orElse(null);
			if (caps == null) return;
			
			caps.setTemperature(message.getTemperature());
		});
		
		ctx.setPacketHandled(true);
	}
	
}
