package net.sonicrushxii.chaos_emerald.event_handler.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.sonicrushxii.chaos_emerald.Utilities;
import net.sonicrushxii.chaos_emerald.capabilities.ChaosEmeraldProvider;
import net.sonicrushxii.chaos_emerald.capabilities.EmeraldAbility;
import net.sonicrushxii.chaos_emerald.capabilities.all.ChaosAbilityDetails;
import net.sonicrushxii.chaos_emerald.modded.ModEffects;
import net.sonicrushxii.chaos_emerald.modded.ModSounds;
import net.sonicrushxii.chaos_emerald.network.PacketHandler;
import net.sonicrushxii.chaos_emerald.network.all.EmeraldDataSyncS2C;
import net.sonicrushxii.chaos_emerald.network.all.ParticleAuraPacketS2C;
import net.sonicrushxii.chaos_emerald.network.all.SyncEntityMotionS2C;
import net.sonicrushxii.chaos_emerald.network.common.ChaosBoost;
import net.sonicrushxii.chaos_emerald.network.common.ChaosDimensionChange;
import net.sonicrushxii.chaos_emerald.network.common.ChaosTeleport;
import net.sonicrushxii.chaos_emerald.network.common.TimeStop;

import java.util.Arrays;
import java.util.Collections;

public class ChaosEmeraldHandler
{
    //Time Stop
    public static final byte TIME_STOP_BUILDUP = 20; //In Ticks
    public static final byte TIME_STOP_DURATION = 15; // In Seconds
    public static final byte TIME_STOP_COOLDOWN = 2; // In Seconds

    //Chaos Boost
    public static final byte CHAOS_BOOST_BUILDUP = 10; //In Ticks
    public static final byte CHAOS_BOOST_COOLDOWN = 5; // In Seconds

    //Teleport
    public static final byte TELEPORT_BUILDUP = 20; //In Ticks
    public static final byte TELEPORT_DURATION = 10; // In Seconds
    public static final byte TELEPORT_COOLDOWN = 1; // In Seconds

    //Dimension Teleport
    public static final byte DIMENSION_TP_BUILDUP = 10; //In Ticks
    public static final byte DIMENSION_TP_COOLDOWN = 5; // In Seconds

    //Cooldowns
    private static final int AQUA_EMERALD_CD = 1;
    private static final int BLUE_EMERALD_CD = 1;
    private static final int GREEN_EMERALD_CD = 1;
    private static final int GREY_EMERALD_CD = 1;
    private static final int PURPLE_EMERALD_CD = 1;
    private static final int RED_EMERALD_CD = 1;
    private static final int YELLOW_EMERALD_CD = 1;

    //Red Emerald
    private static final int BULLET_FALLOFF = 5;

    public static void aquaEmeraldUse(Level pLevel, Player pPlayer) {
        if (pPlayer instanceof ServerPlayer player) {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                ChaosAbilityDetails chaosAbility = chaosEmeraldCap.chaosAbilityDetails;
                if (chaosAbility.abilityInUse()) return;

                if (chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.AQUA_EMERALD.ordinal()] > 0) {
                    player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(EmeraldAbility.AQUA_EMERALD.color())), true);
                    return;
                }

                //Set Cooldown(in Seconds)
                chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.AQUA_EMERALD.ordinal()] = AQUA_EMERALD_CD;

                PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(
                        player.getId(), chaosEmeraldCap
                ));
            });
        }
    }

    public static void blueEmeraldUse(Level pLevel, Player pPlayer) {
        if (pPlayer instanceof ServerPlayer player) {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                ChaosAbilityDetails chaosAbility = chaosEmeraldCap.chaosAbilityDetails;
                if (chaosAbility.abilityInUse()) return;

                if (chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.BLUE_EMERALD.ordinal()] > 0) {
                    player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(EmeraldAbility.BLUE_EMERALD.color())), true);
                    return;
                }

                //Set Cooldown(in Seconds)
                chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.BLUE_EMERALD.ordinal()] = BLUE_EMERALD_CD;

                PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(
                        player.getId(), chaosEmeraldCap
                ));
            });
        }
    }

    public static void greenEmeraldUse(Level pLevel, Player pPlayer) {
        if (pPlayer instanceof ServerPlayer player) {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                ChaosAbilityDetails chaosAbility = chaosEmeraldCap.chaosAbilityDetails;
                if (chaosAbility.abilityInUse()) return;

                if (chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.GREEN_EMERALD.ordinal()] > 0) {
                    player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(EmeraldAbility.GREEN_EMERALD.color())), true);
                    return;
                }

                chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.GREEN_EMERALD.ordinal()] = GREEN_EMERALD_CD;

                PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(
                        player.getId(), chaosEmeraldCap
                ));
            });
        }
    }

    public static void greyEmeraldUse(Level pLevel, Player pPlayer) {
        if (pPlayer instanceof ServerPlayer player) {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                ChaosAbilityDetails chaosAbility = chaosEmeraldCap.chaosAbilityDetails;
                if (chaosAbility.abilityInUse()) return;

                if (chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.GREY_EMERALD.ordinal()] > 0) {
                    player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(EmeraldAbility.GREY_EMERALD.color())), true);
                    return;
                }

                chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.GREY_EMERALD.ordinal()] = GREY_EMERALD_CD;

                PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(
                        player.getId(), chaosEmeraldCap
                ));
            });
        }
    }

    public static void purpleEmeraldUse(Level pLevel, Player pPlayer) {
        if (pPlayer instanceof ServerPlayer player) {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                ChaosAbilityDetails chaosAbility = chaosEmeraldCap.chaosAbilityDetails;
                if (chaosAbility.abilityInUse()) return;

                if (chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.PURPLE_EMERALD.ordinal()] > 0) {
                    player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(EmeraldAbility.PURPLE_EMERALD.color())), true);
                    return;
                }

                chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.PURPLE_EMERALD.ordinal()] = PURPLE_EMERALD_CD;

                PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(
                        player.getId(), chaosEmeraldCap
                ));
            });
        }
    }

    public static void redEmeraldUse(Level pLevel, Player pPlayer) {
        if (pPlayer instanceof ServerPlayer player) {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                ChaosAbilityDetails chaosAbility = chaosEmeraldCap.chaosAbilityDetails;
                if (chaosAbility.abilityInUse()) return;

                //Cancel Ability
                if (chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.RED_EMERALD.ordinal()] > 0) {
                    player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(EmeraldAbility.RED_EMERALD.color())), true);
                    return;
                }

                //Dash
                Vec3 vec3 = player.getLookAngle().scale(1.5F);
                player.setDeltaMovement(vec3);
                PacketHandler.sendToALLPlayers(new SyncEntityMotionS2C(pPlayer.getId(),pPlayer.getDeltaMovement()));
                pLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
                PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                        ParticleTypes.FLAME,
                        pPlayer.getX(), pPlayer.getY()+pPlayer.getEyeHeight()/2, pPlayer.getZ(),
                        0.001, 0.25f, 0.25f, 0.25f, 100,
                        true)
                );
                //Negate Fall Damage
                MobEffectInstance freezeEffect = new MobEffectInstance(ModEffects.FALL_DAMAGE_NEGATE.get(), BULLET_FALLOFF*20, 0, false, false, false);
                if(player.hasEffect(ModEffects.PLAYER_TIME_FREEZE.get()))
                    player.getEffect(ModEffects.PLAYER_TIME_FREEZE.get()).update(freezeEffect);
                else
                    player.addEffect(freezeEffect,player);

                //Set Cooldown(in Seconds)
                chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.RED_EMERALD.ordinal()] = RED_EMERALD_CD;

                PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(
                        player.getId(), chaosEmeraldCap
                ));
            });
        }
    }

    public static void yellowEmeraldUse(Level pLevel, Player pPlayer) {
        if (pPlayer instanceof ServerPlayer player) {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                ChaosAbilityDetails chaosAbility = chaosEmeraldCap.chaosAbilityDetails;
                if (chaosAbility.abilityInUse()) return;

                if (chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.YELLOW_EMERALD.ordinal()] > 0) {
                    player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(EmeraldAbility.YELLOW_EMERALD.color())), true);
                    return;
                }

                chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.YELLOW_EMERALD.ordinal()] = YELLOW_EMERALD_CD;

                PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(
                        player.getId(), chaosEmeraldCap
                ));
            });
        }
    }
    
            
    public static void serverTick(ServerPlayer player, int tick)
    {
        Level world = player.level();

        player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap ->
        {
            ChaosAbilityDetails chaosAbilities = chaosEmeraldCap.chaosAbilityDetails;
            //Time Stop
            {
                //Buildup
                if(chaosAbilities.timeStop < 0)
                {
                    chaosAbilities.timeStop += 1;

                    //Particle
                    {
                        PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                ParticleTypes.ELECTRIC_SPARK,
                                player.getX(), player.getY() + player.getEyeHeight() / 2, player.getZ(),
                                0.001, 0.55F, player.getEyeHeight() / 2, 0.55F,
                                5, false));
                        //Particle
                        PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                new DustParticleOptions(Utilities.hexToVector3f(chaosAbilities.useColor), 1.5f),
                                player.getX(), player.getY() + player.getEyeHeight() / 2, player.getZ(),
                                0.001, 0.55F, player.getEyeHeight() / 2, 0.55F,
                                1, true)
                        );
                    }

                    //On Use
                    if(chaosAbilities.timeStop == 1-TIME_STOP_BUILDUP)
                    {
                        //Play Sound
                        world.playSound(null,player.getX(),player.getY(),player.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.MASTER, 1.0f, 1.0f);

                        //Slow Down Player
                        MobEffectInstance slowEffect = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, TIME_STOP_BUILDUP-1, 2, false, false, false);
                        if(player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN))
                            player.getEffect(MobEffects.MOVEMENT_SLOWDOWN).update(slowEffect);
                        else
                            player.addEffect(slowEffect);

                        //Particle
                        PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                ParticleTypes.FLASH,
                                player.getX(),player.getY()+player.getEyeHeight()/2,player.getZ(),
                                0.001,0.01F,0.01F,0.01F,
                                1,true));
                    }

                    //Stopping Time
                    if(chaosAbilities.timeStop == 0)
                    {
                        //Blind
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, false, false));

                        //Play Sound
                        world.playSound(null,player.getX(),player.getY(),player.getZ(), ModSounds.CHAOS_CONTROL_TIME_STOP.get(), SoundSource.MASTER, 1.0f, 1.0f);

                        //Set Data
                        chaosAbilities.timeStop = 1;

                        //Actual Time Stop
                        TimeStop.startTimeStop(player);
                    }

                }

                //Increment Timer
                if (chaosAbilities.timeStop > 0)
                {
                    //Per Tick

                    //Per Second
                    if (tick == 0)
                        chaosAbilities.timeStop += 1;

                    //End Ability
                    if (chaosAbilities.timeStop > TIME_STOP_DURATION)
                        TimeStop.endTimeStop(player);
                }
            }

            //Chaos Boost
            {
                //Duration
                if(chaosAbilities.buffBoost > 0)
                {
                    chaosAbilities.buffBoost += 1;

                    //Particle
                    {
                        PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                ParticleTypes.ELECTRIC_SPARK,
                                player.getX(), player.getY() + player.getEyeHeight() / 2, player.getZ(),
                                0.001, 0.55F, player.getEyeHeight() / 2, 0.55F,
                                5, false));
                        //Particle
                        PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                new DustParticleOptions(Utilities.hexToVector3f(chaosAbilities.useColor), 1.5f),
                                player.getX(), player.getY() + player.getEyeHeight() / 2, player.getZ(),
                                0.001, 0.55F, player.getEyeHeight() / 2, 0.55F,
                                1, true)
                        );
                    }
                }


                //Play Sound
                if(chaosAbilities.buffBoost == 2)
                    world.playSound(null,player.getX(),player.getY(),player.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.MASTER, 1.0f, 1.0f);


                //Ending
                if(chaosAbilities.buffBoost > CHAOS_BOOST_BUILDUP)
                {
                    //Reset Counter
                    chaosAbilities.buffBoost = 0;
                    chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.CHAOS_CONTROL.ordinal()] = CHAOS_BOOST_COOLDOWN;

                    //Apply the Buffs
                    ChaosBoost.applyBuffs(player);

                    //Play Sound
                    world.playSound(null,player.getX(),player.getY(),player.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.MASTER, 1.0f, 1.0f);
                }

            }

            //Teleport
            {
                //Buildup
                if(chaosAbilities.teleport < 0)
                {
                    chaosAbilities.teleport += 1;

                    //Particle
                    {
                        PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                ParticleTypes.ELECTRIC_SPARK,
                                player.getX(), player.getY() + player.getEyeHeight() / 2, player.getZ(),
                                0.001, 0.55F, player.getEyeHeight() / 2, 0.55F,
                                5, false));
                        //Particle
                        PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                new DustParticleOptions(Utilities.hexToVector3f(chaosAbilities.useColor), 1.5f),
                                player.getX(), player.getY() + player.getEyeHeight() / 2, player.getZ(),
                                0.001, 0.55F, player.getEyeHeight() / 2, 0.55F,
                                1, true)
                        );
                    }

                    //On Use
                    if(chaosAbilities.teleport == 1-TELEPORT_BUILDUP)
                    {
                        //Play Sound
                        world.playSound(null,player.getX(),player.getY(),player.getZ(), ModSounds.CHAOS_CONTROL_TELEPORT_START.get(), SoundSource.MASTER, 1.0f, 1.0f);

                        //Slow Down Player
                        MobEffectInstance slowEffect = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, TELEPORT_BUILDUP-1, 2, false, false, false);
                        if(player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN))
                            player.getEffect(MobEffects.MOVEMENT_SLOWDOWN).update(slowEffect);
                        else
                            player.addEffect(slowEffect);

                        //Particle
                        PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                ParticleTypes.FLASH,
                                player.getX(),player.getY()+player.getEyeHeight()/2,player.getZ(),
                                0.001,0.01F,0.01F,0.01F,
                                1,true));
                    }

                    //Teleporting
                    if(chaosAbilities.teleport == 0)
                    {
                        //Blind
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20, 0, false, false));

                        //Set Data
                        chaosAbilities.teleport = 1;

                        //Actual Time Stop
                        ChaosTeleport.startTeleport(player);
                    }

                }

                //Increment Timer
                if (chaosAbilities.teleport > 0)
                {
                    //Per Tick

                    //Per Second
                    if (tick == 0)
                        chaosAbilities.teleport += 1;

                    //End Ability
                    if (chaosAbilities.teleport > TELEPORT_DURATION)
                        ChaosTeleport.endTeleport(player);
                }
            }

            //Dimension Teleport
            {
                //Ability Duration
                if(chaosAbilities.dimTeleport > 0)
                {
                    chaosAbilities.dimTeleport += 1;

                    //Particle
                    {
                        PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                ParticleTypes.ELECTRIC_SPARK,
                                player.getX(), player.getY() + player.getEyeHeight() / 2, player.getZ(),
                                0.001, 0.55F, player.getEyeHeight() / 2, 0.55F,
                                5, false));
                        //Particle
                        PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                new DustParticleOptions(Utilities.hexToVector3f(chaosAbilities.useColor), 1.5f),
                                player.getX(), player.getY() + player.getEyeHeight() / 2, player.getZ(),
                                0.001, 0.55F, player.getEyeHeight() / 2, 0.55F,
                                1, true)
                        );
                    }
                }
                //Play Sound
                if(chaosAbilities.dimTeleport == 2)
                    world.playSound(null,player.getX(),player.getY(),player.getZ(), ModSounds.CHAOS_CONTROL_TELEPORT_START.get(), SoundSource.MASTER, 1.0f, 0.9f);

                //End Ability
                if(chaosAbilities.dimTeleport > DIMENSION_TP_BUILDUP)
                {
                    //Set Data
                    chaosAbilities.dimTeleport = 0;
                    chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.CHAOS_CONTROL.ordinal()] = DIMENSION_TP_COOLDOWN;

                    //Change Dimensions
                    ChaosDimensionChange.performDimensionChange(player);
                }
            }

            //Chaos Color Abilities
            {
                //Red Emerald
                {}
            }

            PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(player.getId(),chaosEmeraldCap));
        });
    }
}
