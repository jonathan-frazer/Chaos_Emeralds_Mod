package net.sonicrushxii.chaos_emerald.network.common;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.sonicrushxii.chaos_emerald.capabilities.ChaosEmeraldProvider;
import net.sonicrushxii.chaos_emerald.capabilities.EmeraldAbility;
import net.sonicrushxii.chaos_emerald.capabilities.all.ChaosAbilityDetails;
import net.sonicrushxii.chaos_emerald.event_handler.custom.ChaosEmeraldHandler;
import net.sonicrushxii.chaos_emerald.modded.ModEffects;
import net.sonicrushxii.chaos_emerald.modded.ModSounds;
import net.sonicrushxii.chaos_emerald.network.PacketHandler;
import net.sonicrushxii.chaos_emerald.network.all.EmeraldDataSyncS2C;
import net.sonicrushxii.chaos_emerald.network.all.ParticleAuraPacketS2C;
import net.sonicrushxii.chaos_emerald.potion_effects.PlayerTimeFreeze;

import java.util.concurrent.atomic.AtomicBoolean;

public class TimeStop {

    public static void keyPress(ServerPlayer player) {
        player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap ->
        {
            //Fetch Ability Properties
            ChaosAbilityDetails chaosAbilities = chaosEmeraldCap.chaosAbilityDetails;

            //Activate Time Stop
            if (chaosAbilities.timeStop == 0 && chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.CHAOS_CONTROL.ordinal()] == 0 && !chaosAbilities.abilityInUse()) {
                chaosAbilities.timeStop = -ChaosEmeraldHandler.TIME_STOP_BUILDUP;
                player.displayClientMessage(Component.translatable("Chaos Control!").withStyle(Style.EMPTY.withColor(chaosAbilities.useColor)),true);
            }
            //Deactivate Time Stop
            else if (chaosAbilities.timeStop > 0) {
                endTimeStop(player);
            }
            //Cooldown not set
            else if(chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.CHAOS_CONTROL.ordinal()] > 0){
                player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(chaosAbilities.useColor)),true);
                chaosAbilities.useColor = Integer.MIN_VALUE;
            }
            //Other Ability Active
            else if(chaosAbilities.abilityInUse())
            {
                player.displayClientMessage(Component.translatable("That Ability cannot be used currently").withStyle(Style.EMPTY.withColor(chaosAbilities.useColor)),true);
            }
            //Error Handling
            else
            {
                player.displayClientMessage(Component.translatable("That Ability cannot be used currently").withStyle(Style.EMPTY.withColor(chaosAbilities.useColor)),true);
                chaosAbilities.useColor = Integer.MIN_VALUE;
            }

            //Sync Data
            PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(player.getId(),chaosEmeraldCap));
        });
    }

    public static void startTimeStop(ServerPlayer player)
    {
        //Freeze World
        TickRateManager tickRateManager = player.serverLevel().tickRateManager();
        tickRateManager.setFrozen(true);

        //Cure Frozen if you are
        PlayerTimeFreeze.removeEffect(player);

        //Freeze & Blind Any Players around
        for(LivingEntity targetPlayer: player.level().getEntitiesOfClass(Player.class,
                new AABB(player.getX()+36,player.getY()+36,player.getZ()+36,
                        player.getX()-36,player.getY()-36,player.getZ()-36),
                (enemy)->!enemy.is(player)
            )
        )
        {
            targetPlayer.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap ->
            {
                ChaosAbilityDetails chaosAbility = chaosEmeraldCap.chaosAbilityDetails;
                //If they are using a Chaos Ability don't freeze or blind them
                if(chaosAbility.teleport > 0 || chaosAbility.timeStop > 0) return;

                MobEffectInstance freezeEffect = new MobEffectInstance(ModEffects.PLAYER_TIME_FREEZE.get(), ChaosEmeraldHandler.TIME_STOP_DURATION*20, 0, false, false, false);
                if(targetPlayer.hasEffect(ModEffects.PLAYER_TIME_FREEZE.get()))
                    targetPlayer.getEffect(ModEffects.PLAYER_TIME_FREEZE.get()).update(freezeEffect);
                else
                    targetPlayer.addEffect(freezeEffect,player);

                MobEffectInstance blindnessEffect = new MobEffectInstance(MobEffects.BLINDNESS, ChaosEmeraldHandler.TIME_STOP_DURATION*20, 3, false, false, false);
                if(targetPlayer.hasEffect(MobEffects.BLINDNESS))
                    targetPlayer.getEffect(MobEffects.BLINDNESS).update(blindnessEffect);
                else
                    targetPlayer.addEffect(blindnessEffect,player);
            });
        }
    }

    public static void endTimeStop(ServerPlayer player)
    {
        player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap ->
        {
            //Fetch Ability Properties
            ChaosAbilityDetails chaosAbilities = chaosEmeraldCap.chaosAbilityDetails;

            //Reset Data
            chaosAbilities.timeStop = 0;

            //Particle Effects
            player.level().playSound(null,player.getX(),player.getY(),player.getZ(), ModSounds.CHAOS_CONTROL_TIME_RESUME.get(), SoundSource.MASTER, 1.0f, 1.0f);

            //Particle
            PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                    ParticleTypes.FLASH,
                    player.getX(),player.getY()+player.getEyeHeight()/2,player.getZ(),
                    0.001,0.01F,0.01F,0.01F,
                    1,true));

            //Reset World
            player.serverLevel().tickRateManager().setFrozen(false);

            //Find all Players around
            //Check for any time Stoppers
            AtomicBoolean noTimeStoppers = new AtomicBoolean(true);
            for(LivingEntity targetPlayer: player.level().getEntitiesOfClass(Player.class,
                    new AABB(player.getX()+128,player.getY()+128,player.getZ()+128,
                            player.getX()-128,player.getY()-128,player.getZ()-128),(enemy)->!enemy.is(player)))
            {
                targetPlayer.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap1 ->
                {
                    ChaosAbilityDetails chaosAbility = chaosEmeraldCap1.chaosAbilityDetails;
                    //If they are using a Chaos Ability don't freeze or blind them
                    if(chaosAbility.stoppingTime()) {
                        noTimeStoppers.set(false);
                    }
                });

                //If a Timestopper is found exit
                if(!noTimeStoppers.get()) break;
            }

            //If there are no Timestoppers then return everyone to normal
            if(noTimeStoppers.get()) {
                for (LivingEntity targetPlayers : player.level().getEntitiesOfClass(Player.class,
                        new AABB(player.getX() + 128, player.getY() + 128, player.getZ() + 128,
                                player.getX() - 128, player.getY() - 128, player.getZ() - 128), (enemy) -> !enemy.is(player))) {
                    //Check if any are Frozen, then Remove Effect
                    if (targetPlayers.hasEffect(ModEffects.PLAYER_TIME_FREEZE.get()))
                        targetPlayers.removeEffect(ModEffects.PLAYER_TIME_FREEZE.get());

                    //Check if any are Blinded, then Remove Effect
                    if (targetPlayers.hasEffect(MobEffects.BLINDNESS))
                        targetPlayers.removeEffect(MobEffects.BLINDNESS);
                }
            }
            else
            {
                MobEffectInstance freezeEffect = new MobEffectInstance(ModEffects.PLAYER_TIME_FREEZE.get(), ChaosEmeraldHandler.TIME_STOP_DURATION*20, 0, false, false, false);
                if(player.hasEffect(ModEffects.PLAYER_TIME_FREEZE.get()))
                    player.getEffect(ModEffects.PLAYER_TIME_FREEZE.get()).update(freezeEffect);
                else
                    player.addEffect(freezeEffect,player);

                MobEffectInstance blindnessEffect = new MobEffectInstance(MobEffects.BLINDNESS, ChaosEmeraldHandler.TIME_STOP_DURATION*20, 3, false, false, false);
                if(player.hasEffect(MobEffects.BLINDNESS))
                    player.getEffect(MobEffects.BLINDNESS).update(blindnessEffect);
                else
                    player.addEffect(blindnessEffect,player);
            }

            //Reset Color
            chaosAbilities.useColor = Integer.MIN_VALUE;

            //Cooldown
            chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.CHAOS_CONTROL.ordinal()] = ChaosEmeraldHandler.TIME_STOP_COOLDOWN;
        });


    }
}
