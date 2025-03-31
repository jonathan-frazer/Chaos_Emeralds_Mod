package net.sonicrushxii.chaos_emerald.event_handler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.sonicrushxii.chaos_emerald.ChaosEmerald;
import net.sonicrushxii.chaos_emerald.capabilities.ChaosEmeraldProvider;
import net.sonicrushxii.chaos_emerald.capabilities.EmeraldAbility;
import net.sonicrushxii.chaos_emerald.event_handler.client_specific.ClientTickHandler;
import net.sonicrushxii.chaos_emerald.event_handler.custom.ChaosEmeraldHandler;

public class PlayerTickHandler {

    private static final int TICKS_PER_SEC = 20;
    public static int serverTick = 0;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent.Pre event)
    {
        if (FMLEnvironment.dist == Dist.CLIENT && event.player.level().isClientSide())
            ClientTickHandler.clientPlayerTick(event.player);
        else
            onServerPlayerTick((ServerPlayer)event.player);

        Player player = event.player;

        //Handle cooldowns for all players
        if(serverTick == 0)
            event.player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                for(byte i = 0; i< EmeraldAbility.values().length; ++i) {
                    chaosEmeraldCap.chaosCooldownKey[i] = (byte) Math.max(0, chaosEmeraldCap.chaosCooldownKey[i] - 1);
                }
            });
    }

    public void onServerPlayerTick(ServerPlayer player)
    {
        //Handles Tick
        serverTick = (serverTick+1)%TICKS_PER_SEC;

        //Chaos Emerald Handling
        ChaosEmeraldHandler.serverTick(player,serverTick);
    }
}
