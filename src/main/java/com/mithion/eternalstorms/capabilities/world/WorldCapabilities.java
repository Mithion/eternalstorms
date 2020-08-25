package com.mithion.eternalstorms.capabilities.world;

import net.minecraft.util.math.vector.Vector3f;

public class WorldCapabilities {
	private Vector3f windDirection;
	private float windStrength;
	
	public static float BASE_WATER_TEMP = 10f;	
	public static float BASE_AIR_TEMP = 15f;	
	public static int OPTIMAL_TEMP_Y_LEVEL = 60; //deviating from here (up or down) will start to drop the temperature
	public static float WET_TEMP_ADJUSTMENT = 15; //this is subtracted from a block's temperature if the air is wet
	public static float WIND_EFFECTIVENESS = 0.05f; //how much does wind dir and strength affect player movement
	
	public WorldCapabilities() {
		windDirection = new Vector3f(1, 0, 0);
	}
	
	public Vector3f getWindDirection() { return windDirection; }
	public void setWindDirection(Vector3f direction) { this.windDirection = direction; }
	
	public float getWindStrength() { return this.windStrength; }
	public void setWindStrength(float strength) { this.windStrength = strength; }
}
