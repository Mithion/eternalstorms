package com.mithion.eternalstorms.capabilities;

import com.mithion.eternalstorms.EternalStorms;
import com.mithion.eternalstorms.capabilities.player.PlayerCapabilitiesProvider;
import com.mithion.eternalstorms.capabilities.world.WorldCapabilitiesProvider;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(bus = Bus.FORGE)
public class CapabilityForgeInit {
	
	public static final ResourceLocation PLAYER_CAP = new ResourceLocation(EternalStorms.MODID, "player_cap");
	public static final ResourceLocation WORLD_CAP = new ResourceLocation(EternalStorms.MODID, "world_cap");
	
	@SubscribeEvent
	public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> event) {
		//don't attach player intended capabilities to non-players.		
		if (!(event.getObject() instanceof PlayerEntity))
			return;

		event.addCapability(PLAYER_CAP, new PlayerCapabilitiesProvider());
	}

	@SubscribeEvent
	public static void attachWorldCapability(AttachCapabilitiesEvent<World> event) {
		//not necessary to attach the capability to client worlds
		if (!(event.getObject() instanceof ServerWorld))
			return;

		event.addCapability(WORLD_CAP, new WorldCapabilitiesProvider());
	}
}
