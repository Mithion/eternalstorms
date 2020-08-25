package com.mithion.eternalstorms;

import java.util.HashMap;
import java.util.Random;

import com.mithion.eternalstorms.capabilities.player.PlayerCapabilities;
import com.mithion.eternalstorms.capabilities.player.PlayerCapabilitiesProvider;
import com.mithion.eternalstorms.capabilities.world.WorldCapabilities;
import com.mithion.eternalstorms.capabilities.world.WorldCapabilitiesProvider;
import com.mithion.eternalstorms.damage.ColdDamage;
import com.mithion.eternalstorms.network.ServerMessageDispatcher;

import net.minecraft.block.BlastFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.server.SEntityVelocityPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE)
public class ModEventHandler {
	
	private static final Random str_rand = new Random();
	private static final Random pow_rand = new Random();
	
	static HashMap<BlockState, Float> STATED_TEMPERATURE_MODIFIERS;
	static HashMap<Block, Float> TEMPERATURE_MODIFIERS;
	static HashMap<Item, Float> HANDHELD_MODIFIERS;
	
	//static initializers.  
	//TODO: This would be great to convert to data driven (JSON parsed) for pack makers.
	//TODO: Should also probably add IMC support so other mods can add these directly.
	static {
		 TEMPERATURE_MODIFIERS = new HashMap<>();
		 STATED_TEMPERATURE_MODIFIERS = new HashMap<>();
		 HANDHELD_MODIFIERS = new HashMap<>();
		 
		 //add blocks that modify temperature only when in a given state
		 STATED_TEMPERATURE_MODIFIERS.put(Blocks.FURNACE.getDefaultState().with(FurnaceBlock.LIT, true), 7f);
		 STATED_TEMPERATURE_MODIFIERS.put(Blocks.BLAST_FURNACE.getDefaultState().with(BlastFurnaceBlock.LIT, true), 7f);
		 
		 //add blocks that modify temperature in any state
		 TEMPERATURE_MODIFIERS.put(Blocks.TORCH, 1f);
		 TEMPERATURE_MODIFIERS.put(Blocks.CAMPFIRE, 25f);
		 TEMPERATURE_MODIFIERS.put(Blocks.LAVA, 10f);
		 TEMPERATURE_MODIFIERS.put(Blocks.MAGMA_BLOCK, 3f);
		 TEMPERATURE_MODIFIERS.put(Blocks.SNOW, -1f);
		 TEMPERATURE_MODIFIERS.put(Blocks.SNOW_BLOCK, -3f);
		 TEMPERATURE_MODIFIERS.put(Blocks.ICE, -3f);
		 
		 //add items here that modify temperature when held
		 HANDHELD_MODIFIERS.put(Items.TORCH, 10f);
		 HANDHELD_MODIFIERS.put(Items.LAVA_BUCKET, 10f);
		 HANDHELD_MODIFIERS.put(Item.BLOCK_TO_ITEM.get(Blocks.SNOW_BLOCK), -5f);
		 HANDHELD_MODIFIERS.put(Item.BLOCK_TO_ITEM.get(Blocks.ICE),-10f);
	}
	
	@SubscribeEvent
	public static void onWorldTick(WorldTickEvent event) {
		if (event.world.isRemote) return;
		//ensure it is always raining.  Were this to be made configurable, this is the place to disable it with an if statement.
		forceRain((ServerWorld) event.world);
		//update wind speed
		updateWind((ServerWorld) event.world);
	}
	
	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent event) {
		//don't do anything on client worlds
		if (event.player.world.isRemote)
			return;
		
		//only do things every 1/2 second
		if (event.player.world.getGameTime() % 10 == 0) {
			//resolve capabilities
			PlayerCapabilities caps = event.player.getCapability(PlayerCapabilitiesProvider.PLAYER_CAP).orElse(null);
			if (caps == null)
				return;
			
			//calculate the block temperature
			BlockPos playerPos = new BlockPos(event.player.getPosX(), event.player.getPosY(), event.player.getPosZ());
			float posTemp = calculateTemp(playerPos, event.player.world);
			float curTemp = caps.getTemperature();
			
			//gradually approach the target temperature
			float change = 0.05f;
			if (event.player.isWet()) {
				//change more quickly when wet...this will go up faster too but meh.
				change += 0.1f;
				//adjust temp down inherently when wet
				posTemp -= WorldCapabilities.WET_TEMP_ADJUSTMENT;
			}
			
			//on fire, temp goes up fast
			if (event.player.getFireTimer() > 0) {
				posTemp += 50;
			}
						
			//check surrounding blocks and held items for adjustments
			posTemp += getTempFromSurroundings(event.player, event.player.world);
			posTemp += getTempFromHeldItems(event.player);
			
			//TODO: equipped items (goal is to have them normalize the temp to a comfortable range)
			
			//calculate delta and shift
			float delta = curTemp - posTemp;
			if (Math.abs(delta) < 0.1f)
				curTemp = posTemp;
			else
				curTemp -= delta * change;
						
			if (curTemp < 0) //too cold!!
				event.player.attackEntityFrom(ColdDamage.INSTANCE, Math.max(1, curTemp / -10f));
			else if (curTemp > 60) //too hot!!
				event.player.attackEntityFrom(DamageSource.ON_FIRE, Math.max(1, (curTemp-60) / 10f));
						
			//notify client if it changed
			if (caps.getTemperature() != curTemp) {
				//save the value
				caps.setTemperature(curTemp);
				ServerMessageDispatcher.sendTemperatureSyncMessage((ServerPlayerEntity) event.player);
			}
			
			//apply wind to the player
			//this needs to be sync'd to the player but need to find a good way to do it efficiently
			//right now it's too janky
			//applyWindToPlayer((ServerPlayerEntity)event.player, caps, (ServerWorld)event.player.world);
		}
	}
	
	private static void applyWindToPlayer(ServerPlayerEntity player, PlayerCapabilities capabilities, ServerWorld world) {
		WorldCapabilities world_caps = world.getCapability(WorldCapabilitiesProvider.WORLD_CAP).orElse(null);
		if (world_caps == null)
			return;
		
		//if the block can't see the sky, the player is underground/under a tree/in a structure/etc
		//this essentially simulates the ability to "take cover" without too much processing.
		if (world.canBlockSeeSky(new BlockPos(player.getPosX(), player.getPosY(), player.getPosZ()))) {		
			Vector3f wind_dir = world_caps.getWindDirection(); //this is already normalized and scaled to wind strength in tick, so can just get it
			Vector3d delta = player.getMotion().subtract(new Vector3d(wind_dir));
			player.setMotion(player.getMotion().add(delta.scale(0.05f)));
			player.velocityChanged = true;
			player.connection.sendPacket(new SEntityVelocityPacket(player));
		}
		
	}
	
	//looks at surrounding blocks and calculates temperature adjustments
	private static float getTempFromSurroundings(PlayerEntity player, World world) {
		HashMap<Block, Integer> APPLIEDCHANGES = new HashMap<Block, Integer>();
		
		BlockPos base = new BlockPos(player.getPosX(), player.getPosY(), player.getPosZ());
		float adjustment = 0f;
		for (int i = -2; i <= 2; ++i) {
			for (int j = -2; j <= 2; ++j) {
				for (int k = -2; k <= 2; ++k) {
					BlockPos offset = new BlockPos(i,j,k).add(base);
					BlockState check = world.getBlockState(offset);
					
					int numChanges = APPLIEDCHANGES.containsKey(check.getBlock()) ? APPLIEDCHANGES.get(check.getBlock()) : 0;
					
					if (numChanges > 3) continue; //TODO: maybe allow this as a per-block number?
					
					if (STATED_TEMPERATURE_MODIFIERS.containsKey(check)) {
						adjustment += STATED_TEMPERATURE_MODIFIERS.get(check);
						APPLIEDCHANGES.put(check.getBlock(), numChanges+1);
					} else if (TEMPERATURE_MODIFIERS.containsKey(check.getBlock())) {
						adjustment += TEMPERATURE_MODIFIERS.get(check.getBlock());
						APPLIEDCHANGES.put(check.getBlock(), numChanges+1);
					}
				}
			}
		}
		
		return adjustment;
	}
	
	//looks at mainhand/offhand items and calculates adjustments
	private static float getTempFromHeldItems(PlayerEntity player) {
		float adjustment = 0f;
		
		Item main_hand_item = player.getHeldItem(Hand.MAIN_HAND).getItem();
		Item off_hand_item = player.getHeldItem(Hand.OFF_HAND).getItem();
		if (HANDHELD_MODIFIERS.containsKey(main_hand_item))
			adjustment += HANDHELD_MODIFIERS.get(main_hand_item);
		if (HANDHELD_MODIFIERS.containsKey(off_hand_item))
			adjustment += HANDHELD_MODIFIERS.get(off_hand_item);
		
		return adjustment;
	}
	
	//helper method to calculate the ambient temperature of a block position in the world
	private static float calculateTemp(BlockPos pos, World world) {
		if (world.hasWater(pos))
			return calculateWaterTemp(pos, world);
		return calculateAirTemp(pos, world);
	}
	
	//calculates temperature for air
	private static float calculateAirTemp(BlockPos pos, World world) {
		Biome biome = world.getBiome(pos);
		float biomeTemp = biome.getTemperature(pos);
		float baseTemp = biomeTemp * WorldCapabilities.BASE_AIR_TEMP;
		
		if (biomeTemp < 0.15f) //same as snow renderer
			baseTemp -= WorldCapabilities.WET_TEMP_ADJUSTMENT;
		
		float optimalDelta = (float) Math.pow((WorldCapabilities.OPTIMAL_TEMP_Y_LEVEL - pos.getY()) / 8, 2);
		baseTemp -= (optimalDelta / 3);
		
		return baseTemp;
	}
	
	//calculates temperature for water
	private static float calculateWaterTemp(BlockPos pos, World world) {
		float temp = WorldCapabilities.BASE_WATER_TEMP;

		//adjust for y-coord (59 and lower drops a degree per pos)
		temp += Math.min(0, pos.getY() - 60);
		
		//adjust per water block above
		int y = pos.getY() + 1;
		BlockState state = world.getBlockState(pos); 
		while (y < 60 && state.getBlock() == Blocks.WATER) {
			temp--;
			y++;
		}
		
		return temp;
	}
	
	//updates wind speed
	private static void updateWind(ServerWorld world) {
		WorldCapabilities worldCap = world.getCapability(WorldCapabilitiesProvider.WORLD_CAP).orElse(null);
		if (worldCap == null || world.getGameTime() % 20 != 0)
			return;
		
		//wind direction rotation in degrees
		double windDirDeltaDegrees = str_rand.nextGaussian();
		Quaternion q = new Quaternion(worldCap.getWindDirection(), (float) windDirDeltaDegrees, true);
		worldCap.getWindDirection().transform(q);
		worldCap.getWindDirection().normalize();
		
		//wind strength adjustment
		worldCap.setWindStrength(clamp((float) (worldCap.getWindStrength() + pow_rand.nextGaussian()), 0.1f, 1f));
		
		worldCap.getWindDirection().mul(worldCap.getWindStrength());
	}
	
	//helper method to clamp a value
	private static float clamp(float val, float min, float max) {
		return val < min ? min : val > max ? max : val;
	}
	
	//forces rain every few minutes to ensure it keeps going
	private static void forceRain(ServerWorld world) {
		if (world.getGameTime() % 4000 == 0 || !world.isRaining())	
			world.func_241113_a_(0, 6000, true, true);
	}
}
