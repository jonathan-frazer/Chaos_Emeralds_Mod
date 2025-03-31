package net.sonicrushxii.chaos_emerald.event_handler.custom;

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
import net.sonicrushxii.chaos_emerald.capabilities.EmeraldType;
import net.sonicrushxii.chaos_emerald.capabilities.all.ChaosUseDetails;
import net.sonicrushxii.chaos_emerald.modded.ModEffects;
import net.sonicrushxii.chaos_emerald.modded.ModSounds;
import net.sonicrushxii.chaos_emerald.network.PacketHandler;
import net.sonicrushxii.chaos_emerald.network.all.EmeraldDataSyncS2C;
import net.sonicrushxii.chaos_emerald.network.all.ParticleAuraPacketS2C;
import net.sonicrushxii.chaos_emerald.network.all.SyncEntityMotionS2C;
import net.sonicrushxii.chaos_emerald.network.common.ChaosTeleport;
import net.sonicrushxii.chaos_emerald.network.common.TimeStop;
import org.joml.Vector3f;

import java.util.Objects;

public class ChaosEmeraldHandler
{
    //Time Stop
    public static final byte TIME_STOP_BUILDUP = 20; //In Ticks
    public static final byte TIME_STOP_DURATION = 15; // In Seconds
    public static final byte TIME_STOP_COOLDOWN = 2; // In Seconds

    //Teleport
    public static final byte TELEPORT_BUILDUP = 20; //In Ticks
    public static final byte TELEPORT_DURATION = 10; // In Seconds
    public static final byte TELEPORT_COOLDOWN = 1; // In Seconds

    //Cooldowns
    private static final int AQUA_EMERALD_CD = 1;
    private static final int BLUE_EMERALD_CD = 1;
    private static final int GREEN_EMERALD_CD = 1;
    private static final int GREY_EMERALD_CD = 1;
    private static final int PURPLE_EMERALD_CD = 1;
    private static final int RED_EMERALD_CD = 1;
    private static final int YELLOW_EMERALD_CD = 1;

    //Red Emerald
    private static final int BULLET_FALLTIME = 5;

    public static void aquaEmeraldUse(Level pLevel, Player pPlayer)
    {
        if(pPlayer instanceof ServerPlayer player)
        {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                if(chaosEmeraldCap.isUsingActiveAbility()) return;

                if(chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.AQUA_EMERALD.ordinal()] > 0) {
                    player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(EmeraldAbility.AQUA_EMERALD.color())),true);
                    return;
                }

                //Set Cooldown(in Seconds)
                chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.AQUA_EMERALD.ordinal()] = AQUA_EMERALD_CD;

                PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(
                        player.getId(),chaosEmeraldCap
                ));
            });
        }
    }

    public static void blueEmeraldUse(Level pLevel, Player pPlayer)
    {
        if(pPlayer instanceof ServerPlayer player)
        {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                if(chaosEmeraldCap.isUsingActiveAbility()) return;

                if(chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.BLUE_EMERALD.ordinal()] > 0) {
                    player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(EmeraldAbility.BLUE_EMERALD.color())),true);
                    return;
                }

                //Set Cooldown(in Seconds)
                chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.BLUE_EMERALD.ordinal()] = BLUE_EMERALD_CD;

                PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(
                        player.getId(),chaosEmeraldCap
                ));
            });
        }
    }

    public static void greenEmeraldUse(Level pLevel, Player pPlayer)
    {
        if(pPlayer instanceof ServerPlayer player)
        {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                if(chaosEmeraldCap.isUsingActiveAbility()) return;

                if(chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.GREEN_EMERALD.ordinal()] > 0) {
                    player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(EmeraldAbility.GREEN_EMERALD.color())),true);
                    return;
                }

                //Set Cooldown(in Seconds)
                chaosEmeraldCap.chaosCooldownKey[EmeraldType.GREEN_EMERALD.ordinal()] = GREEN_EMERALD_CD;

                PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(
                        player.getId(),chaosEmeraldCap
                ));
            });
        }
    }

    public static void greyEmeraldUse(Level pLevel, Player pPlayer)
    {
        if(pPlayer instanceof ServerPlayer player)
        {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                if(chaosEmeraldCap.isUsingActiveAbility()) return;

                if(chaosEmeraldCap.chaosCooldownKey[EmeraldType.GREY_EMERALD.ordinal()] > 0) {
                    player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(EmeraldType.GREY_EMERALD.color())),true);
                    return;
                }

                //Set Cooldown(in Seconds)
                chaosEmeraldCap.chaosCooldownKey[EmeraldType.GREY_EMERALD.ordinal()] = GREY_EMERALD_CD;

                PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(
                        player.getId(),chaosEmeraldCap
                ));
            });
        }
    }

    public static void purpleEmeraldUse(Level pLevel, Player pPlayer)
    {
        if(pPlayer instanceof ServerPlayer player)
        {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                if(chaosEmeraldCap.isUsingActiveAbility()) return;

                if(chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.PURPLE_EMERALD.ordinal()] > 0) {
                    player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(EmeraldAbility.PURPLE_EMERALD.color())),true);
                    return;
                }

                //Set Cooldown(in Seconds)
                chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.PURPLE_EMERALD.ordinal()] = PURPLE_EMERALD_CD;

                PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(
                        player.getId(),chaosEmeraldCap
                ));
            });
        }
    }

    public static void redEmeraldUse(Level pLevel, Player pPlayer)
    {
        if(pPlayer instanceof ServerPlayer player)
        {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                if(chaosEmeraldCap.isUsingActiveAbility()) return;

                if(chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.RED_EMERALD.ordinal()] > 0) {
                    player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(EmeraldAbility.RED_EMERALD.color())),true);
                    return;
                }

                //Jump
                Vec3 viewVec = player.getViewVector(1.0F);
                player.setDeltaMovement(viewVec.x * 2.0D, viewVec.y * 2.0D, viewVec.z * 2.0D);
                PacketHandler.sendToALLPlayers(new SyncEntityMotionS2C(player.getId(),player.getDeltaMovement()));
                pLevel.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0f, 1.0f);

                //Setup Timer
                chaosEmeraldCap.chaosUseDetails.redEmerald = 1;

                //Particle
                PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(ParticleTypes.FLAME,
                        player.getX(), player.getY(), player.getZ(),
                        0.01, 1.5f, 1.5f, 1.5f,
                        100, false));

                //Set Cooldown(in Seconds)
                chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.RED_EMERALD.ordinal()] = RED_EMERALD_CD;

                PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(
                        player.getId(),chaosEmeraldCap
                ));
            });
        }
    }

    public static void yellowEmeraldUse(Level pLevel, Player pPlayer)
    {
        if(pPlayer instanceof ServerPlayer player)
        {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                if(chaosEmeraldCap.isUsingActiveAbility()) return;

                if(chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.YELLOW_EMERALD.ordinal()] > 0) {
                    player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(EmeraldAbility.YELLOW_EMERALD.color())),true);
                    return;
                }

                //Set Cooldown(in Seconds)
                chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.YELLOW_EMERALD.ordinal()] = YELLOW_EMERALD_CD;

                PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(
                        player.getId(),chaosEmeraldCap
                ));
            });
        }
    }


    public static void serverTick(ServerPlayer player, int tick)
    {
        Level world = player.level();

        player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap ->
        {
            //Fetch Ability Properties
            ChaosUseDetails chaosAbilities = chaosEmeraldCap.chaosUseDetails;

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
                    if(chaosAbilities.timeStop == 1- TIME_STOP_BUILDUP)
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

            //Red Emerald
            if(chaosAbilities.redEmerald > 0)
            {
                //Increment Timer
                chaosAbilities.redEmerald += 1;

                if(chaosAbilities.redEmerald > 5){
                    //Negate Fall Damage
                    MobEffectInstance fallDamageNegation = new MobEffectInstance(ModEffects.FALL_DAMAGE_NEGATE.get(), BULLET_FALLTIME*20, 0, false, false, false);
                    if(player.hasEffect(ModEffects.FALL_DAMAGE_NEGATE.get()))
                        Objects.requireNonNull(player.getEffect(ModEffects.FALL_DAMAGE_NEGATE.get())).update(fallDamageNegation);
                    else
                        player.addEffect(fallDamageNegation,player);
                    chaosAbilities.redEmerald = 0;
                }
            }

            PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(player.getId(),chaosEmeraldCap));
        });
    }
}
