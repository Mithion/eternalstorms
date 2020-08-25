package com.mithion.eternalstorms.network;

import com.mithion.eternalstorms.EternalStorms;

import net.minecraft.network.PacketBuffer;

public class SyncTemperatureMessage {
	private float temperature;
	public SyncTemperatureMessage(float temperature) {
		this.temperature = temperature;
	}
	
	public float getTemperature() {
		return this.temperature;
	}
	
	public void setTemperatore(float temperature) {
		this.temperature = temperature;
	}
	
	public static final SyncTemperatureMessage decode(PacketBuffer buf) {
		SyncTemperatureMessage msg = new SyncTemperatureMessage(0);
		try {
			msg.setTemperatore(buf.readFloat());
		}catch (IllegalArgumentException | IndexOutOfBoundsException e) {
			EternalStorms.LOGGER.error("Exception while reading SyncTemperatureMessage: " + e);
			return msg;
		}
		
		return msg;
	}
	
	public static final void encode(final SyncTemperatureMessage msg, PacketBuffer buf)
	{
		buf.writeFloat(msg.getTemperature());
	}
}
