package net.sonicrushxii.chaos_emerald.event_handler.custom;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;
import net.sonicrushxii.chaos_emerald.KeyBindings;
import net.sonicrushxii.chaos_emerald.Utilities;
import net.sonicrushxii.chaos_emerald.capabilities.ChaosEmeraldProvider;
import net.sonicrushxii.chaos_emerald.capabilities.superform.SuperFormAbility;
import net.sonicrushxii.chaos_emerald.capabilities.superform.SuperFormProperties;
import net.sonicrushxii.chaos_emerald.modded.ModBlocks;
import net.sonicrushxii.chaos_emerald.network.PacketHandler;
import net.sonicrushxii.chaos_emerald.network.all.EmeraldDataSyncS2C;
import net.sonicrushxii.chaos_emerald.network.all.ParticleAuraPacketS2C;
import net.sonicrushxii.chaos_emerald.network.all.SyncEntityMotionS2C;
import net.sonicrushxii.chaos_emerald.network.transformations.form_hyper.DeactivateHyperForm;
import net.sonicrushxii.chaos_emerald.network.transformations.form_super.*;
import net.sonicrushxii.chaos_emerald.potion_effects.AttributeMultipliers;
import org.joml.Vector3f;

import java.util.Objects;

import static net.sonicrushxii.chaos_emerald.network.transformations.form_hyper.DeactivateHyperForm.spawnItem;

public class SuperFormHandler
{
    public static final int SUPERFORM_DURATION = 300; //SECONDS
    public static final int SUPERFORM_COOLDOWN = 10; //SECONDS

    public static void serverTick(ServerPlayer player, int tick)
    {
        //Server Tick
        player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
            //Super Form Handler
            if (chaosEmeraldCap.superFormTimer != 0)
            {
                //Increase Timer
                chaosEmeraldCap.superFormTimer += 1;

                //Transformation Animation
                if(chaosEmeraldCap.superFormTimer < 0)
                {
                    //Lock in Place
                    player.setDeltaMovement(0,0,0);

                    //Display Particle
                    PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                            ParticleTypes.ELECTRIC_SPARK,
                            player.getX(),player.getY()+player.getEyeHeight()/2,player.getZ(),
                            0.001,0.5F,player.getEyeHeight()/2,0.5F,1,false));

                    //Activation Point
                    if(chaosEmeraldCap.superFormTimer == -4)
                    {
                        //Play the Sound
                        player.level().playSound(null,player.getX(),player.getY(),player.getZ(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.MASTER, 0.75f, 1.0f);

                        //Particle
                        PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                ParticleTypes.FLASH,
                                player.getX(), player.getY() + player.getEyeHeight() / 2, player.getZ(),
                                0.001, 0.01F, 0.01F, 0.01F, 1, true));

                        //Give Potion Effects
                        ActivateSuperForm.giveEffects(player);
                    }

                    //Flight Effects
                    {
                        //Flight
                        player.getAbilities().mayfly = true;
                        player.getAbilities().flying = !player.onGround();
                        player.onUpdateAbilities();
                    }

                    //Return Gravity
                    Objects.requireNonNull(player.getAttribute(ForgeMod.ENTITY_GRAVITY.get())).setBaseValue(0.08);
                }

                //Activate Super Form
                if(chaosEmeraldCap.superFormTimer == 0)
                {
                    chaosEmeraldCap.superFormTimer = 1;
                    //Change Data
                    chaosEmeraldCap.formProperties = new SuperFormProperties();
                }

                //Super Form Duration
                if(chaosEmeraldCap.superFormTimer > 0)
                {
                    //Display Particle Every Tick
                    PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                            new DustParticleOptions(new Vector3f(1.0F, 1.0F, 0.0F),1.0F),
                            player.getX(),player.getY()+player.getEyeHeight()/3,player.getZ(),
                            0.001,0.5F,player.getEyeHeight()/3,0.5F,2,true));

                    //Handle Flight
                    {
                        if (player.getAbilities().flying && player.isSprinting()) {
                            //Move in Direction you are looking
                            Vec3 lookAngle = player.getLookAngle().scale(1.75);
                            player.setDeltaMovement(lookAngle);
                            PacketHandler.sendToALLPlayers(new SyncEntityMotionS2C(player.getId(), lookAngle));
                        }
                        //Re-enable Flight if it isn't enabled
                        if (tick == 0 && !player.getAbilities().mayfly) {
                            player.getAbilities().mayfly = true;
                            player.onUpdateAbilities();
                        }

                        //Brakes
                        if (!player.isSprinting() && player.getAbilities().flying) {
                            Vec3 movementSpeed = player.getDeltaMovement();
                            double movementCoefficient = Math.abs(movementSpeed.x) + Math.abs(movementSpeed.y) + Math.abs(movementSpeed.z);

                            if (movementCoefficient > 1.5) {
                                Vec3 motionSlow = movementSpeed.scale(0.1);
                                player.setDeltaMovement(motionSlow);
                                PacketHandler.sendToALLPlayers(new SyncEntityMotionS2C(player.getId(), motionSlow));
                            }
                        }
                    }

                    //Water Running
                    {
                        if (player.isSprinting() && !player.isInWater())
                        {
                            try {
                                if (Objects.equals(ForgeRegistries.BLOCKS.getKey(player.level().getBlockState(player.blockPosition().offset(0, -1, 0)).getBlock()), ForgeRegistries.BLOCKS.getKey(Blocks.WATER))) {
                                    //Get Motion
                                    Vec3 playerDirection = Utilities.calculateViewVector(0,player.getYRot());

                                    if (!chaosEmeraldCap.isWaterBoosting) {
                                        Objects.requireNonNull(player.getAttribute(ForgeMod.ENTITY_GRAVITY.get())).setBaseValue(0.0);
                                        chaosEmeraldCap.isWaterBoosting = true;

                                        //Slight upward
                                        playerDirection = Utilities.calculateViewVector(-1,player.getYRot());
                                    }

                                    //Move Forward
                                    player.setDeltaMovement(playerDirection.scale(3.0));
                                    player.connection.send(new ClientboundSetEntityMotionPacket(player));
                                }
                            } catch (NullPointerException ignored) {}
                        }
                    }

                    //Give the Player the Effects every Second
                    if(tick == 0)
                    {
                        //Display The Particle
                        PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                ParticleTypes.END_ROD,
                                player.getX(),player.getY()+player.getEyeHeight()/2,player.getZ(),
                                0.001,0.5F,player.getEyeHeight()/2,0.5F,3,false));

                        ActivateSuperForm.giveEffects(player);
                    }

                    //Sprint Boost
                    if(player.isSprinting()) {
                        if (!Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).hasModifier(AttributeMultipliers.SUPER_BOOST_SPEED))
                            Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).addTransientModifier(AttributeMultipliers.SUPER_BOOST_SPEED);
                    }
                    else {
                        if(Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).hasModifier(AttributeMultipliers.SUPER_BOOST_SPEED))
                            Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).removeModifier(AttributeMultipliers.SUPER_BOOST_SPEED);
                    }

                }

                //Super Form End
                if(chaosEmeraldCap.superFormTimer > 20*SUPERFORM_DURATION)
                    DeactivateSuperForm.performDeactivateSuper(player);

                //Super Form Interrupt
                if(player.deathTime != 0)
                {
                    spawnItem(player.serverLevel(),player.blockPosition(),new ItemStack(ModBlocks.AQUA_CHAOS_EMERALD.get().asItem()));
                    spawnItem(player.serverLevel(),player.blockPosition(),new ItemStack(ModBlocks.BLUE_CHAOS_EMERALD.get().asItem()));
                    spawnItem(player.serverLevel(),player.blockPosition(),new ItemStack(ModBlocks.GREEN_CHAOS_EMERALD.get().asItem()));
                    spawnItem(player.serverLevel(),player.blockPosition(),new ItemStack(ModBlocks.GREY_CHAOS_EMERALD.get().asItem()));
                    spawnItem(player.serverLevel(),player.blockPosition(),new ItemStack(ModBlocks.PURPLE_CHAOS_EMERALD.get().asItem()));
                    spawnItem(player.serverLevel(),player.blockPosition(),new ItemStack(ModBlocks.RED_CHAOS_EMERALD.get().asItem()));
                    spawnItem(player.serverLevel(),player.blockPosition(),new ItemStack(ModBlocks.YELLOW_CHAOS_EMERALD.get().asItem()));

                    DeactivateHyperForm.performDeactivateHyper(player);
                    chaosEmeraldCap.hyperFormCooldown = 0;
                }
            }

            //Decrement Super Cooldown
            if(tick == 0)
            {
                //Give Back Chaos Emeralds
                if(chaosEmeraldCap.superFormCooldown == 1)  DeactivateSuperForm.giveBackChaosEmeralds(player);
                chaosEmeraldCap.superFormCooldown = Math.max(chaosEmeraldCap.superFormCooldown - 1, 0);

                //Cooldown Management
                try
                {
                    SuperFormProperties superFormProperties = (SuperFormProperties) chaosEmeraldCap.formProperties;
                    {
                        byte[] allCooldowns = superFormProperties.getAllCooldowns();
                        for (int i = 0; i < allCooldowns.length; ++i) {
                            if (allCooldowns[i] != (byte) -1)
                                allCooldowns[i] = (byte) Math.max(0, allCooldowns[i] - 1);
                        }
                    }
                }catch (ClassCastException ignored) {}
            }

            // Sync data to all clients. During the transformation animation the timer
            // changes every tick and must be kept in sync; once the form is active,
            // syncing once per second (tick==0) is sufficient and avoids flooding the
            // network with 20 identical packets per second per player.
            if (chaosEmeraldCap.superFormTimer < 0 || tick == 0) {
                PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(
                        player.getId(),chaosEmeraldCap
                ));
            }
        });
    }

    public static void clientTick(AbstractClientPlayer player)
    {
        //Sends a Packet To Activate Super form if you have all Seven Emeralds.
        player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
            //Transform
            if(KeyBindings.INSTANCE.transformButton.isDown() && chaosEmeraldCap.superFormTimer == 0 && chaosEmeraldCap.superFormCooldown == 0 && !player.isShiftKeyDown())
            {
                if(ActivateSuperForm.hasAllChaosEmeralds(player) && ActivateSuperForm.isPlayerNotWearingArmor(player)) {
                    PacketHandler.sendToServer(new ActivateSuperForm());
                    player.setDeltaMovement(0,0,0);
                }
            }

            //DeTransform
            if(KeyBindings.INSTANCE.transformButton.isDown() && (chaosEmeraldCap.superFormTimer > 0 && chaosEmeraldCap.superFormTimer < SUPERFORM_DURATION*20) && chaosEmeraldCap.superFormCooldown == 0)
                PacketHandler.sendToServer(new DeactivateSuperForm());

            //Lock Position
            if(chaosEmeraldCap.superFormTimer < 0)  player.setDeltaMovement(0,0,0);

            //Super Form Duration
            if(chaosEmeraldCap.superFormTimer > 0)
            {
                SuperFormProperties superFormProperties = (SuperFormProperties) chaosEmeraldCap.formProperties;

                if (KeyBindings.INSTANCE.useAbility1.isDown() && superFormProperties.getCooldown(SuperFormAbility.CHAOS_SPEAR_EX) == 0)
                    PacketHandler.sendToServer(new ChaosSpearEX());

                if (KeyBindings.INSTANCE.useAbility2.isDown() && superFormProperties.getCooldown(SuperFormAbility.CHAOS_CONTROL_EX) == 0)
                    PacketHandler.sendToServer(new ChaosControlEX());

                if (KeyBindings.INSTANCE.useAbility3.isDown() && superFormProperties.getCooldown(SuperFormAbility.CHAOS_PORTAL) == 0)
                    PacketHandler.sendToServer(new ChaosPortal());
            }
        });
    }
}
