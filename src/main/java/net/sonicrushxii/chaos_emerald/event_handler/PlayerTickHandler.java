package net.sonicrushxii.chaos_emerald.event_handler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.sonicrushxii.chaos_emerald.capabilities.ChaosEmeraldProvider;
import net.sonicrushxii.chaos_emerald.capabilities.EmeraldType;
import net.sonicrushxii.chaos_emerald.event_handler.client_specific.ClientTickHandler;
import net.sonicrushxii.chaos_emerald.event_handler.custom.*;

public class PlayerTickHandler {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END || event.player == null) return;
        if (FMLEnvironment.dist == Dist.CLIENT && event.player.level().isClientSide())
            ClientTickHandler.clientPlayerTick(event.player);
        else
            onServerPlayerTick((ServerPlayer) event.player);
    }

    public void onServerPlayerTick(ServerPlayer player)
    {
        // Per-player tick counter (0-19, cycles every second).
        // Using player.tickCount ensures each player has an independent counter,
        // fixing the multiplayer bug where a shared static counter would advance
        // N times per game tick with N online players.
        int tick = player.tickCount % 20;

        // Decrement cooldowns every server tick (cooldown values are in ticks).
        // Previously gated on serverTick==0, which caused cooldowns to drain
        // at 1/20th the correct rate per additional online player.
        player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
            for (int i = 0; i < EmeraldType.values().length; ++i) {
                chaosEmeraldCap.chaosCooldownKey[i] = Math.max(0, chaosEmeraldCap.chaosCooldownKey[i] - 1);
                chaosEmeraldCap.superCooldownKey[i] = Math.max(0, chaosEmeraldCap.superCooldownKey[i] - 1);
            }
        });

        // Handle Chaos Emeralds
        ChaosEmeraldHandler.serverTick(player, tick);

        // False Super Form
        FalseSuperHandler.serverTick(player, tick);

        // Handle Super Emeralds
        SuperEmeraldHandler.serverTick(player, tick);

        // Super Form
        SuperFormHandler.serverTick(player, tick);

        // Hyper Form
        HyperFormHandler.serverTick(player, tick);
    }
}
