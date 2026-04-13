package net.sonicrushxii.chaos_emerald.event_handler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.CombatRules;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.sonicrushxii.chaos_emerald.capabilities.ChaosEmeraldProvider;
import net.sonicrushxii.chaos_emerald.modded.ModEffects;
import net.sonicrushxii.chaos_emerald.network.PacketHandler;
import net.sonicrushxii.chaos_emerald.network.aqua.BindEffectSyncPacketS2C;

public class DamageHandler {
    @SubscribeEvent
    public void onPlayerDamaged(LivingAttackEvent event)
    {
        /** Entity: Attack*/
        LivingEntity enemy = event.getEntity();
        if(enemy.hasEffect(ModEffects.CHAOS_BIND.get()) && enemy.getEffect(ModEffects.CHAOS_BIND.get()).getDuration() > 0)
        {
            event.setCanceled(true);
            enemy.removeEffect(ModEffects.CHAOS_BIND.get());
            PacketHandler.sendToALLPlayers(new BindEffectSyncPacketS2C(enemy.getId(),0));
        }

        /** Player Attacked*/
        if(event.getEntity() instanceof ServerPlayer player)
        {
            //Heavily Reduce Damage if using Chaos Blast
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                //Green Emerald
                if(player.hasEffect(ModEffects.CHAOS_DASH_ATTACK.get()) && player.getEffect(ModEffects.CHAOS_DASH_ATTACK.get()).getDuration() > 0
                        && !(event.getSource().getEntity() instanceof Player) && !event.getSource().isIndirect())
                    event.setCanceled(true);

                //Purple Emerald
                if(chaosEmeraldCap.purpleChaosUse > 1 && !(event.getSource().getEntity() instanceof LivingEntity))
                    event.setCanceled(true);

                //False Super Form - (Turn Invulnerable while transforming)
                if(chaosEmeraldCap.falseChaosSpaz < 0)
                    event.setCanceled(true);

                //False Super Form - (Turn Invulnerable while transforming)
                if(chaosEmeraldCap.superFormTimer < 0)
                    event.setCanceled(true);

                //Super Aqua Emerald
                if ((chaosEmeraldCap.aquaSuperUse > 0 && player.isSprinting()) && !(event.getSource().getEntity() instanceof Player)
                        && !event.getSource().isIndirect() )
                    event.setCanceled(true);

            });
        }

        /** Player: Attacker*/
        try{
            if(event.getSource().getEntity() instanceof ServerPlayer player)
            {

            }
        }catch(NullPointerException ignored){}
    }

    /**
     * Defense pierce — players in Super or Hyper form bypass 50 % of the target's armor.
     *
     * LivingHurtEvent fires BEFORE armor absorption, so event.getAmount() is the raw
     * incoming damage.  The vanilla formula is:
     *   afterArmor = CombatRules.getDamageAfterAbsorb(raw, armor, toughness)
     *
     * To make armor only absorb 50 % of its normal contribution we boost the raw
     * damage before the engine applies armor, using the approximation:
     *   newRaw = raw * (raw + afterArmor) / (2 * afterArmor)
     *
     * Under the linear-reduction assumption (constant armor factor), this yields:
     *   CombatRules.getDamageAfterAbsorb(newRaw, armor, toughness)
     *       ≈ (raw + afterArmor) / 2   [halfway between full-armor and no-armor]
     */
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event)
    {
        if (!(event.getSource().getEntity() instanceof ServerPlayer attacker)) return;

        attacker.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(cap -> {
            if (cap.superFormTimer <= 0 && cap.hyperFormTimer <= 0) return;

            LivingEntity target = event.getEntity();
            float raw      = event.getAmount();
            float armor    = (float) target.getAttributeValue(Attributes.ARMOR);
            float toughness = (float) target.getAttributeValue(Attributes.ARMOR_TOUGHNESS);

            if (armor <= 0) return; // nothing to pierce

            float withArmor = CombatRules.getDamageAfterAbsorb(raw, armor, toughness);
            if (withArmor >= raw) return; // armor absorbed nothing (extremely high toughness edge-case)

            // Boost raw so after-armor damage lands halfway between full-armor and no-armor.
            float newRaw = raw * (raw + withArmor) / (2.0f * withArmor);
            event.setAmount(newRaw);
        });
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event)
    {
        //Reset all form Timers
        if(event.getEntity() instanceof ServerPlayer player)
        {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                chaosEmeraldCap.falseSuperTimer = 0;
                chaosEmeraldCap.superFormTimer = 0;
                chaosEmeraldCap.hyperFormTimer = 0;
            });
        }
    }
}
