package net.sonicrushxii.chaos_emerald.timehandler;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.sonicrushxii.chaos_emerald.capabilities.ChaosEmeraldProvider;
import net.sonicrushxii.chaos_emerald.capabilities.all.ChaosAbilityDetails;

import java.util.concurrent.atomic.AtomicBoolean;

public class TimeHandler {

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent.Post event)
    {
        // Get the server instance
        MinecraftServer server = event.getServer(); // Get the level (world) you want to execute the command in

        //Iterate through every dimension
        for(ServerLevel world : server.getAllLevels())
        {
            AtomicBoolean foundTimeStopper = new AtomicBoolean(false);
            for(Entity entity : world.getAllEntities())
            {
                //Player Interactions
                if (entity instanceof Player player)
                {
                    player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                        //Fetch Ability Properties
                        ChaosAbilityDetails chaosAbility = chaosEmeraldCap.chaosAbilityDetails;

                        if(chaosAbility.stoppingTime()) {
                            world.tickRateManager().setFrozen(true);
                            foundTimeStopper.set(true);
                        }
                    });
                    if(foundTimeStopper.get()) break;
                }
            }

            //If someone  is found Then prevent the world from returning to normal time
            if(foundTimeStopper.get())
                break;

            world.tickRateManager().setFrozen(false);
        }
    }
}