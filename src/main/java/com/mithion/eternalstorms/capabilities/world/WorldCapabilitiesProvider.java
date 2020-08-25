package com.mithion.eternalstorms.capabilities.world;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class WorldCapabilitiesProvider implements ICapabilitySerializable<INBT> {

	@CapabilityInject(WorldCapabilities.class)
	public static final Capability<WorldCapabilities> WORLD_CAP = null;
	private final LazyOptional<WorldCapabilities> holder = LazyOptional.of(WorldCapabilities::new);
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return WORLD_CAP.orEmpty(cap, holder);
	}

	@Override
	public INBT serializeNBT() {
		return WORLD_CAP.getStorage().writeNBT(WORLD_CAP, holder.orElse(null), null);
	}

	@Override
	public void deserializeNBT(INBT nbt) {
		WORLD_CAP.getStorage().readNBT(WORLD_CAP, holder.orElse(null), null, nbt);
	}

}
