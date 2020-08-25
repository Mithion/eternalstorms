package com.mithion.eternalstorms.capabilities.player;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class PlayerCapabilitiesStorage implements IStorage<PlayerCapabilities> {

	@Override
	public INBT writeNBT(Capability<PlayerCapabilities> capability, PlayerCapabilities instance, Direction side) {
		CompoundNBT nbt = new CompoundNBT();
		
		nbt.putFloat("player_temperature", instance.getTemperature());
		
		return nbt;
	}

	@Override
	public void readNBT(Capability<PlayerCapabilities> capability, PlayerCapabilities instance, Direction side,
			INBT nbt) {
		if (nbt instanceof CompoundNBT) {
			CompoundNBT cnbt = (CompoundNBT) nbt;
			if (cnbt.contains("player_temperature"))
				instance.setTemperature(cnbt.getFloat("player_temperature"));
		}
	}

}
