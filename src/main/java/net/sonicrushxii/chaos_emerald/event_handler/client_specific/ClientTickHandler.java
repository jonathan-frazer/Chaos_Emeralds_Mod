package net.sonicrushxii.chaos_emerald.event_handler.client_specific;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.sonicrushxii.chaos_emerald.event_handler.custom.*;

public class ClientTickHandler {

    public static int clientTick = 0;
    private static final int TICKS_PER_SEC = 20;

    public static void clientPlayerTick(Player pPlayer)
    {
        // Only process the local player. PlayerTickEvent fires for every player
        // entity visible on this client, so without this guard the tick counter
        // would advance N times per game tick and ability packets would be sent
        // N times per key press with N nearby players.
        if (pPlayer != net.minecraft.client.Minecraft.getInstance().player) return;

        //Local Player Tick
        if(pPlayer instanceof AbstractClientPlayer player)
        {
            clientTick = (clientTick+1)%TICKS_PER_SEC;

            //Handles Chaos Emeralds
            ChaosEmeraldHandler.clientTick(player,clientTick);

            //False Super Form
            FalseSuperHandler.clientTick(player);

            //Handle Super Emeralds
            SuperEmeraldHandler.clientTick(player,clientTick);

            //Super Form
            SuperFormHandler.clientTick(player);

            //Hyper Form
            HyperFormHandler.clientTick(player);
        }
    }
}
