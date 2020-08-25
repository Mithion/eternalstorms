package com.mithion.eternalstorms.capabilities.world;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class WorldCapabilitiesStorage implements IStorage<WorldCapabilities>{

	@Override
	public INBT writeNBT(Capability<WorldCapabilities> capability, WorldCapabilities instance, Direction side) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putFloat("wind_dir_x", instance.getWindDirection().getX());
		nbt.putFloat("wind_dir_y", instance.getWindDirection().getY());
		nbt.putFloat("wind_dir_z", instance.getWindDirection().getZ());
		nbt.putFloat("wind_strength", instance.getWindStrength());
		return nbt;
	}

	@Override
	public void readNBT(Capability<WorldCapabilities> capability, WorldCapabilities instance, Direction side, INBT nbt) {
		if (nbt instanceof CompoundNBT) {
			CompoundNBT cnbt = (CompoundNBT) nbt; 
			if (cnbt.contains("wind_dir_x") && cnbt.contains("wind_dir_y") && cnbt.contains("wind_dir_z"))
				instance.setWindDirection(new Vector3f(cnbt.getFloat("wind_dir_x"), cnbt.getFloat("wind_dir_y"), cnbt.getFloat("wind_dir_z")));
			
			if (cnbt.contains("wind_strength"))
				instance.setWindStrength(cnbt.getFloat("wind_strength"));
		}
	}
	
}
