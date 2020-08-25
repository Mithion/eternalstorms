package com.mithion.eternalstorms;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mithion.eternalstorms.hud.GuiInit;
import com.mithion.eternalstorms.hud.HudRenderer;

@Mod("eternalstorms")
public class EternalStorms
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    
    public static final String MODID = "eternalstorms";
    final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    //register HUD overlay renderers
    public EternalStorms() {
    	DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
    		MinecraftForge.EVENT_BUS.register(HudRenderer.class);
    		modEventBus.register(GuiInit.class);
    	});
    }
}
