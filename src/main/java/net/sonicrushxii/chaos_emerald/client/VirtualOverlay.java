package net.sonicrushxii.chaos_emerald.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.sonicrushxii.chaos_emerald.ChaosEmerald;
import net.sonicrushxii.chaos_emerald.Utilities;
import net.sonicrushxii.chaos_emerald.capabilities.ChaosEmeraldProvider;
import net.sonicrushxii.chaos_emerald.capabilities.all.ChaosAbilityDetails;
import net.sonicrushxii.chaos_emerald.event_handler.custom.ChaosEmeraldHandler;
import org.joml.Vector3f;

public class VirtualOverlay {

    private static final float OVERLAY_MAX_DECAY_FRACTION = 0.95F;

    private static final ResourceLocation EMPTY_SLOT = new ResourceLocation(ChaosEmerald.MOD_ID,
            "textures/custom_gui/empty_slot.png");

    private static final ResourceLocation CHAOS_OVERLAY_INNER = new ResourceLocation(ChaosEmerald.MOD_ID,
            "textures/custom_gui/chaos_overlay_inner.png");
    private static final ResourceLocation CHAOS_OVERLAY_OUTER = new ResourceLocation(ChaosEmerald.MOD_ID,
            "textures/custom_gui/chaos_overlay_outer.png");

    // Register the Main Overlay
    public static final IGuiOverlay CHAOS_ABILITY_HUD = ((ForgeGui gui, GuiGraphics guiComponent, float partialTick, int screenWidth, int screenHeight) -> {
        AbstractClientPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
            ChaosAbilityDetails chaosAbility = chaosEmeraldCap.chaosAbilityDetails;
            if(chaosAbility.useColor > -1 && (chaosAbility.abilityInUse()))
                renderChaosOverlay(player,gui,guiComponent,partialTick,screenWidth,screenHeight);
        });
    });

    public static void renderChaosOverlay(AbstractClientPlayer player, ForgeGui gui, GuiGraphics guiComponent, float partialTick, int screenWidth, int screenHeight) {
        player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
            ChaosAbilityDetails chaosAbility = chaosEmeraldCap.chaosAbilityDetails;

            // Calculate gradient based on the time spent in Timestop/Teleport
            float overlayGradient = (chaosAbility.teleport > 0)
                    ? OVERLAY_MAX_DECAY_FRACTION - ((float) (chaosAbility.teleport) / (ChaosEmeraldHandler.TELEPORT_DURATION)) * OVERLAY_MAX_DECAY_FRACTION
                    : OVERLAY_MAX_DECAY_FRACTION - ((float) (chaosAbility.timeStop) / (ChaosEmeraldHandler.TIME_STOP_DURATION)) * OVERLAY_MAX_DECAY_FRACTION;

            // Calculate Dimensions
            final int[] textureDimensions = {screenWidth, screenHeight};
            int x = 0;
            int y = 0;

            // Enable blending
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            // Modify shader color based on the current Chaos Color
            Vector3f colorComponent = Utilities.hexToVector3f(chaosAbility.useColor);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(colorComponent.x, colorComponent.y, colorComponent.z, (1F - OVERLAY_MAX_DECAY_FRACTION) + overlayGradient);

            // Draw Outer Overlay
            RenderSystem.setShaderTexture(0, CHAOS_OVERLAY_OUTER);
            guiComponent.blit(
                    CHAOS_OVERLAY_OUTER,
                    x, y, 0, 0,
                    textureDimensions[0], textureDimensions[1], textureDimensions[0], textureDimensions[1]
            );

            // Reset shader color before drawing next element
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            // Draw Inner Overlay
            RenderSystem.setShaderTexture(0, CHAOS_OVERLAY_INNER);
            guiComponent.blit(
                    CHAOS_OVERLAY_INNER,
                    x, y, 0, 0,
                    textureDimensions[0], textureDimensions[1], textureDimensions[0], textureDimensions[1]
            );

            // Reset OpenGL states
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS);
        });
    }

}