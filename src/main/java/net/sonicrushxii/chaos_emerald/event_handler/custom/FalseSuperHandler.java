package net.sonicrushxii.chaos_emerald.event_handler.custom;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.sonicrushxii.chaos_emerald.KeyBindings;
import net.sonicrushxii.chaos_emerald.capabilities.ChaosEmeraldProvider;
import net.sonicrushxii.chaos_emerald.entities.false_super.ChaosSpaz;
import net.sonicrushxii.chaos_emerald.modded.ModEntityTypes;
import net.sonicrushxii.chaos_emerald.modded.ModSounds;
import net.sonicrushxii.chaos_emerald.network.PacketHandler;
import net.sonicrushxii.chaos_emerald.network.all.EmeraldDataSyncS2C;
import net.sonicrushxii.chaos_emerald.network.all.ParticleAuraPacketS2C;
import net.sonicrushxii.chaos_emerald.network.all.PlayerStopSoundPacketS2C;
import net.sonicrushxii.chaos_emerald.network.all.SyncEntityMotionS2C;
import net.sonicrushxii.chaos_emerald.network.transformations.false_super.ActivateFalseSuper;
import net.sonicrushxii.chaos_emerald.potion_effects.AttributeMultipliers;
import org.joml.Vector3f;

import java.util.Objects;

public class FalseSuperHandler
{
    private final static int FALSE_SUPER_DURATION = 60;

    public static void serverTick(ServerPlayer player, int serverTick)
    {
        //Server Tick
        player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
            //False Super Handler
            if(chaosEmeraldCap.falseSuperTimer != 0)
            {
                //Increase Timer
                chaosEmeraldCap.falseSuperTimer += 1;

                //False Super Activation
                if(chaosEmeraldCap.falseSuperTimer <= -40)
                {
                    //Add Hover
                    Vec3 motionDir = new Vec3(0,0,0);
                    player.setDeltaMovement(motionDir);
                    PacketHandler.sendToALLPlayers(new SyncEntityMotionS2C(player.getId(),motionDir));
                }
                if(chaosEmeraldCap.falseSuperTimer > -40 && chaosEmeraldCap.falseSuperTimer < -20)
                {
                    //Add Hover
                    Vec3 motionDir = new Vec3(0,0.05,0);
                    player.setDeltaMovement(motionDir);
                    PacketHandler.sendToALLPlayers(new SyncEntityMotionS2C(player.getId(),motionDir));
                }

                if(chaosEmeraldCap.falseSuperTimer < 0)
                {
                    //Display Particle Every Tick
                    PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                            new DustParticleOptions(new Vector3f(0.0F, 0.839F, 0.624F),1.5F),
                            player.getX(),player.getY()+player.getEyeHeight()/2,player.getZ(),
                            0.001,0.5F,player.getEyeHeight()/2,0.5F,2,true));
                    PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                            ParticleTypes.END_ROD,
                            player.getX(),player.getY()+player.getEyeHeight()/2,player.getZ(),
                            0.001,0.5F,player.getEyeHeight()/2,0.5F,1,false));
                }

                //Activate False Super
                if(chaosEmeraldCap.falseSuperTimer == -20)
                {
                    //Set Motion to 0
                    Vec3 motionDir = new Vec3(0,0.0,0);
                    player.setDeltaMovement(motionDir);
                    PacketHandler.sendToALLPlayers(new SyncEntityMotionS2C(player.getId(),motionDir));

                    //Particle Flash
                    PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                            ParticleTypes.FLASH,
                            player.getX(),player.getY()+player.getEyeHeight()/2,player.getZ(),
                            0.001,0.01F,player.getEyeHeight()/2,0.01F,1,true));

                    //Give Effects
                    {
                        //Speed
                        if(!player.hasEffect(MobEffects.MOVEMENT_SPEED)) player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, false));
                        else Objects.requireNonNull(player.getEffect(MobEffects.MOVEMENT_SPEED)).update(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, false));

                        //Jump
                        if(!player.hasEffect(MobEffects.JUMP)) player.addEffect(new MobEffectInstance(MobEffects.JUMP, -1, 1, false, false));
                        else Objects.requireNonNull(player.getEffect(MobEffects.JUMP)).update(new MobEffectInstance(MobEffects.JUMP, -1, 1, false, false));

                        //Haste
                        if(!player.hasEffect(MobEffects.DIG_SPEED)) player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, -1, 1, false, false));
                        else Objects.requireNonNull(player.getEffect(MobEffects.DIG_SPEED)).update(new MobEffectInstance(MobEffects.DIG_SPEED, -1, 1, false, false));

                        //Strength
                        if(!player.hasEffect(MobEffects.DAMAGE_BOOST)) player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 1, false, false));
                        else Objects.requireNonNull(player.getEffect(MobEffects.DAMAGE_BOOST)).update(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 1, false, false));

                        //Resistance
                        if(!player.hasEffect(MobEffects.DAMAGE_RESISTANCE)) player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 1, false, false));
                        else Objects.requireNonNull(player.getEffect(MobEffects.DAMAGE_RESISTANCE)).update(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 1, false, false));

                        //Add Step Height
                        if (!Objects.requireNonNull(player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get())).hasModifier(AttributeMultipliers.FALSE_SUPER_STEP_ADDITION))
                            Objects.requireNonNull(player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get())).addTransientModifier(AttributeMultipliers.FALSE_SUPER_STEP_ADDITION);
                    }
                }
                //Return Gravity After a Sec
                if(chaosEmeraldCap.falseSuperTimer == 0)
                {
                    //Change Data
                    chaosEmeraldCap.falseSuperTimer = 1;

                    //Return Gravity
                    Objects.requireNonNull(player.getAttribute(ForgeMod.ENTITY_GRAVITY.get())).setBaseValue(0.08);
                }

                //False Super Dur
                if(chaosEmeraldCap.falseSuperTimer > 0)
                {
                    //Display Particle Every Tick
                    PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                            new DustParticleOptions(new Vector3f(0.1137F, 0.9490F, 0.4471F),1.0F),
                            player.getX(),player.getY()+player.getEyeHeight()/2,player.getZ(),
                            0.001,0.5F,player.getEyeHeight()/2,0.5F,2,true));

                    //Give the Player the Effects every Second
                    if(serverTick == 0)
                    {
                        //Display The Particle
                        PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                ParticleTypes.END_ROD,
                                player.getX(),player.getY()+player.getEyeHeight()/2,player.getZ(),
                                0.001,0.5F,player.getEyeHeight()/2,0.5F,3,false));

                        //Speed
                        if(!player.hasEffect(MobEffects.MOVEMENT_SPEED)) player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, false));
                        else Objects.requireNonNull(player.getEffect(MobEffects.MOVEMENT_SPEED)).update(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, -1, 1, false, false));

                        //Jump
                        if(!player.hasEffect(MobEffects.JUMP)) player.addEffect(new MobEffectInstance(MobEffects.JUMP, -1, 1, false, false));
                        else Objects.requireNonNull(player.getEffect(MobEffects.JUMP)).update(new MobEffectInstance(MobEffects.JUMP, -1, 1, false, false));

                        //Haste
                        if(!player.hasEffect(MobEffects.DIG_SPEED)) player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, -1, 1, false, false));
                        else Objects.requireNonNull(player.getEffect(MobEffects.DIG_SPEED)).update(new MobEffectInstance(MobEffects.DIG_SPEED, -1, 1, false, false));

                        //Strength
                        if(!player.hasEffect(MobEffects.DAMAGE_BOOST)) player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 1, false, false));
                        else Objects.requireNonNull(player.getEffect(MobEffects.DAMAGE_BOOST)).update(new MobEffectInstance(MobEffects.DAMAGE_BOOST, -1, 1, false, false));

                        //Resistance
                        if(!player.hasEffect(MobEffects.DAMAGE_RESISTANCE)) player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 1, false, false));
                        else Objects.requireNonNull(player.getEffect(MobEffects.DAMAGE_RESISTANCE)).update(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, -1, 1, false, false));

                        //Add Step Height
                        if (!Objects.requireNonNull(player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get())).hasModifier(AttributeMultipliers.FALSE_SUPER_STEP_ADDITION))
                            Objects.requireNonNull(player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get())).addTransientModifier(AttributeMultipliers.FALSE_SUPER_STEP_ADDITION);
                    }

                    //Chaos Spaz
                    if(chaosEmeraldCap.falseChaosSpaz > 0)
                    {
                        chaosEmeraldCap.falseChaosSpaz += 1;

                        //Throw Spaz Projectile
                        if(chaosEmeraldCap.falseChaosSpaz == 22 || chaosEmeraldCap.falseChaosSpaz == 38 || chaosEmeraldCap.falseChaosSpaz == 56)
                        {
                            ChaosSpaz chaosSpaz = new ChaosSpaz(ModEntityTypes.FALSE_SUPER_CHAOS_SPAZ.get(), player.level());

                            Vec3 spawnPos = new Vec3(player.getX()+player.getLookAngle().x,
                                    player.getY()+player.getLookAngle().y+1.0,
                                    player.getZ()+player.getLookAngle().z);

                            chaosSpaz.setPos(spawnPos);
                            chaosSpaz.setDuration(120);
                            chaosSpaz.setMovementDirection(player.getLookAngle());
                            chaosSpaz.setDestroyBlocks(player.isShiftKeyDown());
                            //Increase Damage Based on Form
                            {
                                if(chaosEmeraldCap.hyperFormTimer > 0)      chaosSpaz.setSpazDamage(60.0F);
                                else if(chaosEmeraldCap.superFormTimer > 0) chaosSpaz.setSpazDamage(24.0F);
                                else                                        chaosSpaz.setSpazDamage(6.0F);
                            }
                            chaosSpaz.setOwner(player.getUUID());

                            // Add the entity to the world
                            player.level().addFreshEntity(chaosSpaz);
                        }

                        //Freezing Motion
                        {
                            //Set Motion to Zero
                            Vec3 motionDir = new Vec3(0,0,0);
                            player.setDeltaMovement(motionDir);
                            PacketHandler.sendToALLPlayers(new SyncEntityMotionS2C(player.getId(),motionDir));
                        }

                        //End Chaos Spaz
                        if(chaosEmeraldCap.falseChaosSpaz > 80)
                            chaosEmeraldCap.falseSuperTimer = FALSE_SUPER_DURATION*20 + 1; //Set Timer to Time out
                    }

                    //End The Form
                    if(chaosEmeraldCap.falseSuperTimer > FALSE_SUPER_DURATION*20)
                    {
                        //Set Timer to 0
                        chaosEmeraldCap.falseSuperTimer = 0;

                        //Reset Chaos Spaz
                        if(chaosEmeraldCap.falseChaosSpaz > 0)
                        {
                            Objects.requireNonNull(player.getAttribute(ForgeMod.ENTITY_GRAVITY.get())).setBaseValue(0.08);  //Return Gravity
                            chaosEmeraldCap.falseChaosSpaz = 0;                                     //Reset Timer
                            PacketHandler.sendToPlayer(player, new PlayerStopSoundPacketS2C(
                                            ModSounds.FALSE_CHAOS_SPAZ.get().getLocation()
                                    )
                            );                                                                      //Stop Sounds
                        }

                        //Return Step Height back to normal
                        if (Objects.requireNonNull(player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get())).hasModifier(AttributeMultipliers.FALSE_SUPER_STEP_ADDITION))
                            Objects.requireNonNull(player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get())).removeModifier(AttributeMultipliers.FALSE_SUPER_STEP_ADDITION);

                        //Remove Effects
                        {
                            player.removeEffect(MobEffects.MOVEMENT_SPEED);     //Speed
                            player.removeEffect(MobEffects.JUMP);               //Jump
                            player.removeEffect(MobEffects.DIG_SPEED);          //Haste
                            player.removeEffect(MobEffects.DAMAGE_BOOST);       //Strength
                            player.removeEffect(MobEffects.DAMAGE_RESISTANCE);  //Resistance
                        }

                        //Slowfalling
                        if(!player.hasEffect(MobEffects.SLOW_FALLING)) player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 0, false, false));
                        else Objects.requireNonNull(player.getEffect(MobEffects.SLOW_FALLING)).update(new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 0, false, false));
                    }
                }
            }
            // Sync every tick during transformation animation; once per second otherwise.
            if (chaosEmeraldCap.falseSuperTimer < 0 || serverTick == 0) {
                PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(
                        player.getId(),chaosEmeraldCap
                ));
            }
        });
    }

    public static void clientTick(AbstractClientPlayer player)
    {
        //Master Emerald - Key Press Use
        //Sends a Packet To Activate Super form if you have all Seven Emeralds.
        player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
            //Transform
            if (KeyBindings.INSTANCE.transformButton.isDown() && chaosEmeraldCap.falseSuperTimer == 0 && !player.isShiftKeyDown()) {
                PacketHandler.sendToServer(new ActivateFalseSuper());
            }
        });
    }
}
