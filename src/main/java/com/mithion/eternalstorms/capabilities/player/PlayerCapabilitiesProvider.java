package com.mithion.eternalstorms.capabilities.player;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class PlayerCapabilitiesProvider implements ICapabilitySerializable<INBT> {
	
	@CapabilityInject(PlayerCapabilities.class)
	public static final Capability<PlayerCapabilities> PLAYER_CAP = null;
	private final LazyOptional<PlayerCapabilities> holder = LazyOptional.of(PlayerCapabilities::new);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return PLAYER_CAP.orEmpty(cap, holder);
	}

	@Override
	public INBT serializeNBT() {
		return PLAYER_CAP.getStorage().writeNBT(PLAYER_CAP, holder.orElse(null), null);
	}

	@Override
	public void deserializeNBT(INBT nbt) {
		PLAYER_CAP.getStorage().readNBT(PLAYER_CAP, holder.orElse(null), null, nbt);
	}
}
