package net.sonicrushxii.chaos_emerald.event_handler;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.sonicrushxii.chaos_emerald.capabilities.ChaosEmeraldProvider;
import net.sonicrushxii.chaos_emerald.event_handler.custom.ChaosEmeraldHandler;
import net.sonicrushxii.chaos_emerald.event_handler.custom.FalseSuperHandler;

public class PlayerTickHandler {
    private static final int TICKS_PER_SEC = 20;
    private static int serverTick = 0;
    public static int clientTick = 0;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END || event.player == null) return;
        if (event.player.level().isClientSide()) onLocalPlayerTick((LocalPlayer)event.player);
        else onServerPlayerTick((ServerPlayer)event.player);

        Player player = event.player;

        //Handle cooldowns for all players
        if(serverTick == 0)
            event.player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                byte[] cooldownKey = chaosEmeraldCap.cooldownKey;
                for(byte i=0;i<cooldownKey.length;++i)
                    cooldownKey[i] = (byte) Math.max(0,cooldownKey[i]-1);
            });
    }

    public void onLocalPlayerTick(LocalPlayer player)
    {
        clientTick = (clientTick+1)%TICKS_PER_SEC;

        //Handles Chaos Emeralds
        ChaosEmeraldHandler.clientTick(player,clientTick);

        //False Super Form
        FalseSuperHandler.clientTick(player,clientTick);
    }

    public void onServerPlayerTick(ServerPlayer player)
    {
        //Handles Tick
        serverTick = (serverTick+1)%TICKS_PER_SEC;

        //Handle Chaos Emeralds
        ChaosEmeraldHandler.serverTick(player,serverTick);

        //False Super Form
        FalseSuperHandler.serverTick(player,serverTick);

    }
}
