package com.mithion.eternalstorms.hud;

import org.lwjgl.opengl.GL11;

import com.mithion.eternalstorms.capabilities.player.PlayerCapabilities;
import com.mithion.eternalstorms.capabilities.player.PlayerCapabilitiesProvider;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HudRenderer extends AbstractGui{	
	public static HudRenderer instance;
	
	private Minecraft mc;
	static final float scaleFactor = 0.75f;
	static final int yPos = 10;
	static final int xPos = 5;
	
	public HudRenderer() {
		this.mc = Minecraft.getInstance();
	}
	
	@SubscribeEvent(receiveCanceled = true)
	public static void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
		ClientPlayerEntity entityPlayerSP = instance.mc.player;
		if (entityPlayerSP == null)
			return; // just in case
		if (event.getType() == ElementType.HOTBAR) {
			// render along with the hotbar
			instance.renderHUD(event.getMatrixStack(), event.getWindow().getScaledWidth(), event.getWindow().getScaledHeight());
		}
	}
	
	public void renderHUD(MatrixStack matrixStack, int screenWidth, int screenHeight) {
		PlayerEntity player = mc.player;
		FontRenderer fr = mc.fontRenderer;

		PlayerCapabilities caps = player.getCapability(PlayerCapabilitiesProvider.PLAYER_CAP).orElse(null);
		if (caps == null)
			return;
		
		
		GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);		
		GL11.glPushMatrix();

		//GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);		
		GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
		
		String s = new TranslationTextComponent("hud.temperature", caps.getTemperature()).getString();			
		func_238471_a_(matrixStack, fr, s, xPos + fr.getStringWidth(s) / 2, yPos, 14737632);
		
		s = new TranslationTextComponent("hud.wet", player.isWet()).getString();		
		func_238471_a_(matrixStack, fr, s, xPos + fr.getStringWidth(s) / 2, yPos + 10, 14737632);
		
		GL11.glPopMatrix();
		GL11.glPopAttrib();
	}
}
