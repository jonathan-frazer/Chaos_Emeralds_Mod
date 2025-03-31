package net.sonicrushxii.chaos_emerald.event_handler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.sonicrushxii.chaos_emerald.capabilities.ChaosEmeraldProvider;
import net.sonicrushxii.chaos_emerald.modded.ModEffects;

public class FallDamageHandler {

    @SubscribeEvent
    public void onLivingFall(LivingFallEvent event) {
        if(event.getEntity() instanceof ServerPlayer player)
        {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap->{
                if(player.hasEffect(ModEffects.FALL_DAMAGE_NEGATE.get()) && player.getEffect(ModEffects.FALL_DAMAGE_NEGATE.get()).getDuration() > 0) {
                    event.setDistance(0.0f);
                    player.removeEffect(ModEffects.FALL_DAMAGE_NEGATE.get());
                }
            });
        }
    }
}