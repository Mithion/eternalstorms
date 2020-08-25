package com.mithion.eternalstorms.network;

import com.mithion.eternalstorms.capabilities.player.PlayerCapabilities;
import com.mithion.eternalstorms.capabilities.player.PlayerCapabilitiesProvider;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

public class ServerMessageDispatcher {
	/*
	 * Helper to quickly sync the server's player temperature to their client.
	 * Should only be called from serverside.
	 */
	public static void sendTemperatureSyncMessage(ServerPlayerEntity player) {		
		PlayerCapabilities caps = player.getCapability(PlayerCapabilitiesProvider.PLAYER_CAP).orElse(null);
		if (caps == null)
			return;
		
		NetworkManager.network.send(
			PacketDistributor.PLAYER.with(() -> player),
			new SyncTemperatureMessage(caps.getTemperature())			
		);		
	}
}
