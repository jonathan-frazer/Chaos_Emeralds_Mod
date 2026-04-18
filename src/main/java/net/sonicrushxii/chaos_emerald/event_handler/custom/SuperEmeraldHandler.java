package net.sonicrushxii.chaos_emerald.event_handler.custom;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.registries.ForgeRegistries;
import net.sonicrushxii.chaos_emerald.Utilities;
import net.sonicrushxii.chaos_emerald.capabilities.ChaosEmeraldProvider;
import net.sonicrushxii.chaos_emerald.capabilities.EmeraldType;
import net.sonicrushxii.chaos_emerald.entities.aqua.SuperAquaBubbleEntity;
import net.sonicrushxii.chaos_emerald.entities.blue.IceVerticalSpike;
import net.sonicrushxii.chaos_emerald.entities.green.ChaosDiveRipple;
import net.sonicrushxii.chaos_emerald.entities.purple.SuperChaosSlicer;
import net.sonicrushxii.chaos_emerald.event_handler.WorldLoadHandler;
import net.sonicrushxii.chaos_emerald.modded.ModDimensions;
import net.sonicrushxii.chaos_emerald.modded.ModEffects;
import net.sonicrushxii.chaos_emerald.modded.ModEntityTypes;
import net.sonicrushxii.chaos_emerald.modded.ModTeleporter;
import net.sonicrushxii.chaos_emerald.network.PacketHandler;
import net.sonicrushxii.chaos_emerald.network.all.EmeraldDataSyncS2C;
import net.sonicrushxii.chaos_emerald.network.all.ParticleAuraPacketS2C;
import net.sonicrushxii.chaos_emerald.network.all.SyncEntityMotionS2C;
import net.sonicrushxii.chaos_emerald.network.red.SuperInfernoParticleS2C;
import net.sonicrushxii.chaos_emerald.potion_effects.AttributeMultipliers;
import net.sonicrushxii.chaos_emerald.scheduler.Scheduler;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SuperEmeraldHandler {

    public static byte BUBBLE_BOOST_TIME = 100;                         //Time in Ticks
    public static int AQUA_SUPER_EMERALD_CD = 40;

    public static byte BLUE_EMERALD_CD = 60;

    public static byte CHAOS_DIVE_TIME = 115;                           //Time in Ticks
    public static int GREEN_SUPER_EMERALD_CD = 40;

    public static int CHAOS_REPRIEVE_TIME = 600;                        //Time in Seconds
    public static int GREY_SUPER_EMERALD_CD = 1200;

    public static int PURPLE_SUPER_EMERALD_CD = 45;

    public static int CHAOS_INFERNO_TIME = 300;                         //Time in Ticks
    public static int RED_SUPER_EMERALD_CD = 120;

    public static byte CHAOS_GAMBIT_TIME = 50;                         //Time in Ticks
    public static int YELLOW_SUPER_EMERALD_CD = 1200;

    public static void aquaEmeraldUse(Player pPlayer)
    {
        if(pPlayer instanceof ServerPlayer player)
        {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                if(chaosEmeraldCap.isUsingActiveAbility()) return;

                if (chaosEmeraldCap.superCooldownKey[EmeraldType.AQUA_EMERALD.ordinal()] > 0) {
                    player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(0x00FFFF)), true);
                    return;
                }

                //Super Aqua Emerald
                chaosEmeraldCap.aquaSuperUse = 1;

                //Get Saturation
                player.addEffect(new MobEffectInstance(MobEffects.SATURATION,6,0,false,false,false),player);

                //Add Step Height
                if (!Objects.requireNonNull(player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get())).hasModifier(AttributeMultipliers.BUBBLE_BOOST_STEP))
                    Objects.requireNonNull(player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get())).addTransientModifier(AttributeMultipliers.BUBBLE_BOOST_STEP);

                //Add Speed
                if (!Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).hasModifier(AttributeMultipliers.BUBBLE_BOOST_SPEED))
                    Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).addTransientModifier(AttributeMultipliers.BUBBLE_BOOST_SPEED);

                //Sync To Client
                PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(
                        player.getId(),chaosEmeraldCap
                ));
            });

        }

    }

    public static void blueEmeraldUse(Level pLevel, Player pPlayer)
    {
        if(pPlayer instanceof ServerPlayer player) {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                try {
                    if (chaosEmeraldCap.isUsingActiveAbility()) return;

                    if (chaosEmeraldCap.superCooldownKey[EmeraldType.BLUE_EMERALD.ordinal()] > 0) {
                        player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(0x0000FF)), true);
                        return;
                    }

                    chaosEmeraldCap.superCooldownKey[EmeraldType.BLUE_EMERALD.ordinal()] = BLUE_EMERALD_CD;

                    //If Looking Down don't scale further
                    if (player.getXRot() >= 85.0 && player.getXRot() <= 90.0) {
                        IceVerticalSpike iceSuperSpike = new IceVerticalSpike(ModEntityTypes.ICE_SUPER_SPIKE.get(), pLevel);
                        iceSuperSpike.setPos(new Vec3(player.getX(), player.getY() + 0.1, player.getZ()));
                        iceSuperSpike.setOwner(player.getUUID());
                        iceSuperSpike.setMovementDirection(
                                new Vec3(0, 1, 0)
                                        .add(Utilities.calculateViewVector(0, player.getYRot()).scale(0.4))
                        );

                        Vec3 launchVec = Utilities.calculateViewVector(-65f, player.getYRot()).scale(1.85);
                        player.setDeltaMovement(launchVec);
                        PacketHandler.sendToALLPlayers(new SyncEntityMotionS2C(player.getId(), launchVec));
                        pLevel.addFreshEntity(iceSuperSpike);

                        //Give Padding Effect
                        Scheduler.scheduleTask(()->{
                            MobEffectInstance iceLaunchEffect = new MobEffectInstance(ModEffects.SUPER_ICE_LAUNCH.get(), 200, 0, false, false, false);
                            if (player.hasEffect(ModEffects.SUPER_CHAOS_DIVE.get()))    Objects.requireNonNull(player.getEffect(ModEffects.SUPER_ICE_LAUNCH.get())).update(iceLaunchEffect);
                            else                                                        player.addEffect(iceLaunchEffect, player);
                            player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(),iceLaunchEffect));
                        },2);

                        return;
                    }

                    Vec3 startPos = new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
                    Vec3 motionDir = player.getLookAngle();

                    //Look Forward and Spawn Ice spike over there
                    for (byte i = 0; i < 16; ++i) {
                        Vec3 destPos = startPos.add(motionDir.scale(i));
                        BlockPos pos = Utilities.convertToBlockPos(destPos);
                        List<LivingEntity> targetEntities = pLevel.getEntitiesOfClass(
                                LivingEntity.class, new AABB(destPos.x() - 0.5, destPos.y() - 0.5, destPos.z() - 0.5,
                                        destPos.x() + 0.5, destPos.y() + 0.5, destPos.z() + 0.5),
                                target -> !(target.is(player))
                        );

                        //If Entities are found then target them
                        if (!targetEntities.isEmpty()) {
                            //Grab the first Enemy
                            LivingEntity target = targetEntities.get(0);

                            //Grab
                            List<LivingEntity> nearbyEntities = pLevel.getEntitiesOfClass(
                                    LivingEntity.class, new AABB(target.getX() - 2.5, target.getY() - 2.5, target.getZ() - 2.5,
                                            target.getX() + 2.5, target.getY() + 2.5, target.getZ() + 2.5),
                                    adjacentEnemy -> !(adjacentEnemy.is(player))
                            );
                            for (LivingEntity enemy : nearbyEntities) {
                                IceVerticalSpike iceSuperSpike = new IceVerticalSpike(ModEntityTypes.ICE_SUPER_SPIKE.get(), pLevel);
                                iceSuperSpike.setPos(new Vec3(enemy.getX(), enemy.getY(), enemy.getZ()));
                                iceSuperSpike.setOwner(player.getUUID());
                                iceSuperSpike.setMovementDirection(new Vec3(0, 1, 0)
                                        .add(destPos.subtract(
                                                        new Vec3(
                                                                player.getX(),
                                                                player.getY(),
                                                                player.getZ()
                                                        )
                                                ).normalize().scale(0.4)
                                        )
                                );
                                pLevel.addFreshEntity(iceSuperSpike);
                            }

                            return;
                        }

                        //If Block is found target them
                        if (!Utilities.passableBlocks.contains(ForgeRegistries.BLOCKS.getKey(pLevel.getBlockState(pos).getBlock()) + "")) {
                            IceVerticalSpike iceSuperSpike = new IceVerticalSpike(ModEntityTypes.ICE_SUPER_SPIKE.get(), pLevel);
                            iceSuperSpike.setPos(destPos.add(0, 1, 0));
                            iceSuperSpike.setOwner(player.getUUID());
                            iceSuperSpike.setMovementDirection(new Vec3(0, 1, 0)
                                    .add(destPos.subtract(
                                                    new Vec3(
                                                            player.getX(),
                                                            player.getY(),
                                                            player.getZ()
                                                    )
                                            ).normalize().scale(0.4)
                                    )
                            );
                            pLevel.addFreshEntity(iceSuperSpike);

                            return;
                        }
                    }
                }
                finally {
                    PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(
                            player.getId(),chaosEmeraldCap
                    ));
                }
            });
        }
    }

    public static void greenEmeraldUse(Player pPlayer)
    {
        if(pPlayer instanceof ServerPlayer player) {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                if (chaosEmeraldCap.greenSuperUse > 0 && !player.onGround()) {
                    Vec3 motionDir = Utilities.calculateViewVector(0, player.getYRot()).scale(0.3);
                    player.addDeltaMovement(motionDir);
                    PacketHandler.sendToALLPlayers(new SyncEntityMotionS2C(player.getId(),player.getDeltaMovement().add(motionDir)));
                }

                if (chaosEmeraldCap.isUsingActiveAbility()) return;

                if (chaosEmeraldCap.superCooldownKey[EmeraldType.GREEN_EMERALD.ordinal()] > 0) {
                    player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(0x00FF00)), true);
                    return;
                }

                //Super Green Emerald
                chaosEmeraldCap.greenSuperUse = 1;

                //Give Effect
                MobEffectInstance diveEffect = new MobEffectInstance(ModEffects.SUPER_CHAOS_DIVE.get(), 120, 0, false, false, false);
                if (player.hasEffect(ModEffects.SUPER_CHAOS_DIVE.get()))    Objects.requireNonNull(player.getEffect(ModEffects.SUPER_CHAOS_DIVE.get())).update(diveEffect);
                else                                                        player.addEffect(diveEffect, player);

                Vec3 launchVec = Utilities.calculateViewVector(-65f, player.getYRot()).scale(1.05);
                player.setDeltaMovement(launchVec);
                PacketHandler.sendToALLPlayers(new SyncEntityMotionS2C(player.getId(), launchVec));

                PacketHandler.sendToALLPlayers( new EmeraldDataSyncS2C(
                        pPlayer.getId(),chaosEmeraldCap
                ));
            });
        }
    }

    public static void greyEmeraldUse(Level pLevel, Player pPlayer)
    {
        if(pPlayer instanceof ServerPlayer player)
        {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                if(chaosEmeraldCap.greySuperUse < 0) {
                    pPlayer.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(0xEEEEEE)),true);
                    return;
                }

                ServerLevel destinationWorld = Objects.requireNonNull(pLevel.getServer()).getLevel(ModDimensions.CHAOS_REPRIEVE_LEVEL_KEY); // Target Dimension

                // Optional: play teleport sound
                if (!pLevel.dimension().equals(ModDimensions.CHAOS_REPRIEVE_LEVEL_KEY))
                {
                    //Store Player's original Position
                    chaosEmeraldCap.currentDimension = String.valueOf(player.level().dimension().location());

                    chaosEmeraldCap.greySuperPos[0] = player.getBlockX();
                    chaosEmeraldCap.greySuperPos[1] = player.getBlockY();
                    chaosEmeraldCap.greySuperPos[2] = player.getBlockZ();
                    chaosEmeraldCap.greySuperPos[3] = (int)player.getYRot();
                    chaosEmeraldCap.greySuperPos[4] = (int)player.getXRot();

                    PacketHandler.sendToALLPlayers( new EmeraldDataSyncS2C(
                            pPlayer.getId(),chaosEmeraldCap
                    ));

                    //Cooldown
                    chaosEmeraldCap.greySuperUse = 1;

                    //Change Dimensions
                    assert destinationWorld != null;
                    player.changeDimension(destinationWorld, new ModTeleporter(WorldLoadHandler.reprieveTargetPos, false));
                    player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);

                    //Get Slowfalling
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,20,0,false,false,false),player);

                    //Teleport to the Position
                    player.teleportTo(destinationWorld,
                            WorldLoadHandler.reprieveTargetPos.getX(),
                            WorldLoadHandler.reprieveTargetPos.getY(),
                            WorldLoadHandler.reprieveTargetPos.getZ(),
                            Collections.emptySet(),
                            0, 0);
                    player.connection.send(new ClientboundTeleportEntityPacket(player));
                }
                else
                {
                    //Get Original Dimensions
                    ServerLevel originalWorld = Objects.requireNonNull(player.getServer()).getLevel(ResourceKey.create(Registries.DIMENSION,
                            new ResourceLocation(chaosEmeraldCap.currentDimension)));

                    //Change back to the original world
                    assert originalWorld != null;
                    player.changeDimension(originalWorld, new ModTeleporter(WorldLoadHandler.reprieveTargetPos, false));
                    player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);

                    //Get Slowfalling
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,20,0,false,false,false),player);

                    //Cooldown
                    chaosEmeraldCap.greySuperUse = -GREY_SUPER_EMERALD_CD;

                    //Teleport to the Position
                    player.teleportTo(originalWorld,
                            chaosEmeraldCap.greySuperPos[0],
                            chaosEmeraldCap.greySuperPos[1],
                            chaosEmeraldCap.greySuperPos[2],
                            Collections.emptySet(),
                            chaosEmeraldCap.greySuperPos[3],chaosEmeraldCap.greySuperPos[4]);
                    player.connection.send(new ClientboundTeleportEntityPacket(player));
                }
            });
        }
    }

    public static void purpleEmeraldUse(Player pPlayer)
    {
        if(pPlayer instanceof ServerPlayer player) {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                if (chaosEmeraldCap.isUsingActiveAbility()) return;

                if (chaosEmeraldCap.superCooldownKey[EmeraldType.PURPLE_EMERALD.ordinal()] > 0) {
                    player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(0xFF00FF)), true);
                    return;
                }

                chaosEmeraldCap.purpleSuperUse = 1;

                PacketHandler.sendToALLPlayers( new EmeraldDataSyncS2C(
                        pPlayer.getId(),chaosEmeraldCap
                ));
            });
        }
    }

    public static void redEmeraldUse(Player pPlayer)
    {
        if(pPlayer instanceof ServerPlayer player) {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                if (chaosEmeraldCap.isUsingActiveAbility()) return;

                if (chaosEmeraldCap.superCooldownKey[EmeraldType.RED_EMERALD.ordinal()] > 0) {
                    player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(0xFF00FF)), true);
                    return;
                }

                chaosEmeraldCap.redSuperUse = 1;

                PacketHandler.sendToALLPlayers( new EmeraldDataSyncS2C(
                        pPlayer.getId(),chaosEmeraldCap
                ));
            });
        }
    }

    public static void yellowEmeraldUse(Player pPlayer)
    {
        if(pPlayer instanceof ServerPlayer player) {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                if (chaosEmeraldCap.isUsingActiveAbility()) return;

                if (chaosEmeraldCap.superCooldownKey[EmeraldType.YELLOW_EMERALD.ordinal()] > 0) {
                    player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(0x00FFFF)), true);
                    return;
                }

                //Super Yellow Emerald
                chaosEmeraldCap.yellowSuperUse = 1;

                //Add Slowness
                if (!Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).hasModifier(AttributeMultipliers.GAMBIT_SLOW))
                    Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).addTransientModifier(AttributeMultipliers.GAMBIT_SLOW);

                PacketHandler.sendToALLPlayers( new EmeraldDataSyncS2C(
                        pPlayer.getId(),chaosEmeraldCap
                ));
            });
        }
    }

    public static void serverTick(ServerPlayer player, int tick)
    {
        Level world = player.level();
        player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
            try {

                //Bubble Boost
                {
                    if (chaosEmeraldCap.aquaSuperUse > 0) {
                        //Add Timer
                        chaosEmeraldCap.aquaSuperUse += 1;

                        //Particle
                        PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                ParticleTypes.BUBBLE,
                                player.getX(), player.getY() + player.getEyeHeight() / 2, player.getZ(),
                                0.001, 0.50f, 1.00f, 0.50f, 10,
                                false));
                        PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                new DustParticleOptions(new Vector3f(0f, 1f, 1f), 1.5f),
                                player.getX(), player.getY() + player.getEyeHeight() / 2, player.getZ(),
                                0.001, 0.50f, 1.00f, 0.50f, 5,
                                false));

                        //Spawn Bubbles
                        if (player.isSprinting()) {
                            //Spawn Right Bubble
                            SuperAquaBubbleEntity superRightBubble = new SuperAquaBubbleEntity(ModEntityTypes.AQUA_BOOST_BUBBLE.get(), player.serverLevel());
                            superRightBubble.setPos(new Vec3(player.getX(), player.getY() + player.getEyeHeight() / 2, player.getZ()));
                            superRightBubble.setDuration(40);
                            superRightBubble.setOwner(player.getUUID());
                            superRightBubble.setMovementDirection(player.getLookAngle().scale(Utilities.random.nextFloat(0.5f, 1.5f)).cross(new Vec3(0, 1, 0)));

                            player.level().addFreshEntity(superRightBubble);

                            //Spawn Left Bubble
                            SuperAquaBubbleEntity superLeftBubble = new SuperAquaBubbleEntity(ModEntityTypes.AQUA_BOOST_BUBBLE.get(), player.serverLevel());
                            superLeftBubble.setPos(new Vec3(player.getX(), player.getY() + player.getEyeHeight() / 2, player.getZ()));
                            superLeftBubble.setDuration(40);
                            superLeftBubble.setOwner(player.getUUID());
                            superLeftBubble.setMovementDirection(player.getLookAngle().scale(Utilities.random.nextFloat(0.5f, 1.5f)).cross(new Vec3(0, -1, 0)));

                            player.level().addFreshEntity(superLeftBubble);

                            //Water Boost
                            if (!player.isInWater())
                            {
                                try {
                                    if (Objects.equals(ForgeRegistries.BLOCKS.getKey(world.getBlockState(player.blockPosition().offset(0, -1, 0)).getBlock()), ForgeRegistries.BLOCKS.getKey(Blocks.WATER))) {
                                        //Get Motion
                                        Vec3 playerDirection = Utilities.calculateViewVector(0,player.getYRot());

                                        if (!chaosEmeraldCap.isWaterBoosting) {
                                            Objects.requireNonNull(player.getAttribute(ForgeMod.ENTITY_GRAVITY.get())).setBaseValue(0.0);
                                            chaosEmeraldCap.isWaterBoosting = true;

                                            //Slight upward
                                            playerDirection = Utilities.calculateViewVector(-1,player.getYRot());
                                        }

                                        //Move Forward
                                        player.setDeltaMovement(playerDirection.scale(2.0));
                                        player.connection.send(new ClientboundSetEntityMotionPacket(player));
                                    }
                                } catch (NullPointerException ignored) {
                                }
                            }
                        }
                    }

                    //Undo Water Boost
                    try {
                        if (chaosEmeraldCap.isWaterBoosting)
                            if (!Objects.equals(ForgeRegistries.BLOCKS.getKey(world.getBlockState(player.blockPosition().offset(0, -1, 0)).getBlock()), ForgeRegistries.BLOCKS.getKey(Blocks.WATER))
                                    ||
                                    !(chaosEmeraldCap.aquaSuperUse > 0)
                                    ||
                                    (player.getDeltaMovement().x < 0.5 && player.getDeltaMovement().y < 0.5 && player.getDeltaMovement().z < 0.5)
                                    ||
                                    player.isInWater()) {
                                Objects.requireNonNull(player.getAttribute(ForgeMod.ENTITY_GRAVITY.get())).setBaseValue(0.08);
                                chaosEmeraldCap.isWaterBoosting = false;
                            }
                    } catch (NullPointerException ignored) {
                    }

                    if (chaosEmeraldCap.aquaSuperUse == BUBBLE_BOOST_TIME) {
                        //Reset Timer
                        chaosEmeraldCap.aquaSuperUse = 0;

                        //Remove Step Height
                        if (Objects.requireNonNull(player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get())).hasModifier(AttributeMultipliers.BUBBLE_BOOST_STEP))
                            Objects.requireNonNull(player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get())).removeModifier(AttributeMultipliers.BUBBLE_BOOST_STEP.getId());

                        //Remove Speed
                        if (Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).hasModifier(AttributeMultipliers.BUBBLE_BOOST_SPEED))
                            Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).removeModifier(AttributeMultipliers.BUBBLE_BOOST_SPEED.getId());

                        //Set Cooldown(in Seconds)
                        chaosEmeraldCap.superCooldownKey[EmeraldType.AQUA_EMERALD.ordinal()] = AQUA_SUPER_EMERALD_CD;
                    }
                }

                //Chaos Dive
                {
                    //Duration
                    if (chaosEmeraldCap.greenSuperUse > 0) {
                        //Add Timer
                        chaosEmeraldCap.greenSuperUse += 1;

                        //Particle
                        if(chaosEmeraldCap.greenSuperUse < 38)
                        {
                            PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                    ParticleTypes.HAPPY_VILLAGER,
                                    player.getX(), player.getY() + player.getEyeHeight() / 2, player.getZ(),
                                    0.001, 0.50f, 1.00f, 0.50f, 2,
                                    false));
                            PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                    new DustParticleOptions(new Vector3f(0f, 1f, 0f), 1.5f),
                                    player.getX(), player.getY() + player.getEyeHeight() / 2, player.getZ(),
                                    0.001, 0.50f, 1.00f, 0.50f, 2,
                                    false));
                        }
                        else
                        {
                            PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                    ParticleTypes.HAPPY_VILLAGER,
                                    player.getX(), player.getY() + player.getEyeHeight() / 2, player.getZ(),
                                    0.001, 0.50f, 1.00f, 0.50f, 1,
                                    false));
                        }
                    }

                    //Move to touching the ground later
                    if (chaosEmeraldCap.greenSuperUse > 3 && chaosEmeraldCap.greenSuperUse < 38 && player.onGround())
                        chaosEmeraldCap.greenSuperUse = 38;

                    //Lock Ground Position
                    if (chaosEmeraldCap.greenSuperUse > 3 && player.onGround()) {
                        player.setDeltaMovement(0, player.getDeltaMovement().y(), 0);
                        PacketHandler.sendToALLPlayers(new SyncEntityMotionS2C(player.getId(), new Vec3(0, player.getDeltaMovement().y(), 0)));

                        //Spawn Ripples
                        if((chaosEmeraldCap.greenSuperUse-1) % 20 == 0)
                        {
                            Vec3 spawnPos = new Vec3(player.getX(),
                                    player.getY()-0.1,
                                    player.getZ());
                            world.playSound(null,player.getX(),player.getY(),player.getZ(),
                                    SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.MASTER, 0.4f, 2.0f);
                            ChaosDiveRipple chaosDiveRipple = new ChaosDiveRipple(ModEntityTypes.CHAOS_DIVE_RIPPLE.get(), world);
                            chaosDiveRipple.setPos(spawnPos);
                            chaosDiveRipple.setOwner(player.getUUID());
                            chaosDiveRipple.setLevelGround(player.isShiftKeyDown());
                            chaosDiveRipple.setDuration(50);

                            // Add the entity to the world
                            world.addFreshEntity(chaosDiveRipple);
                        }
                    }

                    if (chaosEmeraldCap.greenSuperUse > CHAOS_DIVE_TIME) {
                        //Reset Timer
                        chaosEmeraldCap.greenSuperUse = 0;

                        //Set Cooldown(in Seconds)
                        chaosEmeraldCap.superCooldownKey[EmeraldType.GREEN_EMERALD.ordinal()] = GREEN_SUPER_EMERALD_CD;

                        //Remove Effect
                        if (player.hasEffect(ModEffects.SUPER_CHAOS_DIVE.get()))
                            player.removeEffect(ModEffects.SUPER_CHAOS_DIVE.get());
                    }
                }

                //Chaos Gambit
                {
                    if (chaosEmeraldCap.yellowSuperUse > 0) {
                        //Add Timer
                        chaosEmeraldCap.yellowSuperUse += 1;

                        //Particle
                        PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                ParticleTypes.ELECTRIC_SPARK,
                                player.getX(), player.getY() + player.getEyeHeight() / 2, player.getZ(),
                                0.001, 0.50f, 1.00f, 0.50f, 4,
                                false));
                        PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                new DustParticleOptions(new Vector3f(1f, 1f, 0f), 1.5f),
                                player.getX(), player.getY() + player.getEyeHeight() / 2, player.getZ(),
                                0.001, 0.50f, 1.00f, 0.50f, 2,
                                false));

                        player.setDeltaMovement(0, 0, 0);
                        PacketHandler.sendToALLPlayers(new SyncEntityMotionS2C(player.getId(), new Vec3(0, 0, 0)));
                    }

                    if (chaosEmeraldCap.yellowSuperUse == 10) {
                        chaosEmeraldCap.atkRotPhaseX = player.getXRot();
                        chaosEmeraldCap.atkRotPhaseY = player.getYRot();
                    }

                    if (chaosEmeraldCap.yellowSuperUse > 10)
                    {
                        Vec3 playerPos = new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
                        Vec3 lookAngle = Utilities.calculateViewVector(chaosEmeraldCap.atkRotPhaseX, chaosEmeraldCap.atkRotPhaseY);

                        for (int i = 1; i <= Math.min(chaosEmeraldCap.yellowSuperUse - 10, 24); i += 1) {
                            Vec3 destPos = playerPos.add(lookAngle.scale(i * 1.8));

                            //Display Particles in Gaussian Distribution
                            PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                    ParticleTypes.END_ROD,
                                    destPos.x, destPos.y, destPos.z,
                                    0.001, 1.95f, 1.75f, 1.95f, 4,
                                    false));
                            PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                    new DustParticleOptions(new Vector3f(1f, 1f, 0f), 2f),
                                    destPos.x, destPos.y, destPos.z,
                                    0.001, 1.95f, 1.75f, 1.95f, 8,
                                    true));

                            for (BlockPos pos : BlockPos.betweenClosed(Utilities.convertToBlockPos(destPos).offset(-1,-1,-1),
                                    Utilities.convertToBlockPos(destPos).offset(1,1,1)))
                            {
                                BlockState blockState = player.level().getBlockState(pos);
                                if(!Utilities.unbreakableBlocks.contains(ForgeRegistries.BLOCKS.getKey(blockState.getBlock())+""))
                                    player.level().destroyBlock(pos, player.isShiftKeyDown());
                            }

                            //Deal Damage
                            for(LivingEntity enemy : world.getEntitiesOfClass(LivingEntity.class, new AABB(
                                    destPos.x+2.75, destPos.y+2.75, destPos.z+2.75,
                                    destPos.x-2.75, destPos.y-2.75, destPos.z-2.75
                                    ), (enemy)->(!enemy.is(player))
                            ))
                            {
                                //Damage
                                enemy.hurt(player.damageSources().playerAttack(player), 13.33f);

                                //Updated Motion
                                Vec3 motionDirection = (destPos).subtract(new Vec3(enemy.getX(),enemy.getY(),enemy.getZ())).scale(0.3);
                                enemy.setDeltaMovement(motionDirection);
                                PacketHandler.sendToALLPlayers(new SyncEntityMotionS2C(enemy.getId(),motionDirection));
                            }
                        }
                    }

                    if (chaosEmeraldCap.yellowSuperUse > CHAOS_GAMBIT_TIME) {
                        //Reset Timer
                        chaosEmeraldCap.yellowSuperUse = 0;

                        //Remove Slowness
                        if (Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).hasModifier(AttributeMultipliers.GAMBIT_SLOW))
                            Objects.requireNonNull(player.getAttribute(Attributes.MOVEMENT_SPEED)).removeModifier(AttributeMultipliers.GAMBIT_SLOW.getId());

                        //Apply Tired Effects
                        MobEffectInstance blindnessEffect = new MobEffectInstance(MobEffects.BLINDNESS, 20, 4, false, false, false);
                        if (player.hasEffect(MobEffects.BLINDNESS))    Objects.requireNonNull(player.getEffect(MobEffects.BLINDNESS)).update(blindnessEffect);
                        else                                           player.addEffect(blindnessEffect, player);

                        MobEffectInstance weaknessEffect = new MobEffectInstance(MobEffects.WEAKNESS, 20, 4, false, false, false);
                        if (player.hasEffect(MobEffects.WEAKNESS))    Objects.requireNonNull(player.getEffect(MobEffects.WEAKNESS)).update(weaknessEffect);
                        else                                           player.addEffect(weaknessEffect, player);

                        MobEffectInstance slownessEffect = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 4, false, false, false);
                        if (player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN))    Objects.requireNonNull(player.getEffect(MobEffects.MOVEMENT_SLOWDOWN)).update(slownessEffect);
                        else                                           player.addEffect(slownessEffect, player);

                        //Set Cooldown(in Seconds)
                        chaosEmeraldCap.superCooldownKey[EmeraldType.YELLOW_EMERALD.ordinal()] = YELLOW_SUPER_EMERALD_CD;
                    }
                }

                //Chaos Slicer
                {
                    if(chaosEmeraldCap.purpleSuperUse > 0)
                    {
                        chaosEmeraldCap.purpleSuperUse += 1;

                        //Throw Slicer Projectile
                        if(chaosEmeraldCap.purpleSuperUse == 2 || chaosEmeraldCap.purpleSuperUse == 12)
                        {
                            SuperChaosSlicer superChaosSlicer = new SuperChaosSlicer(ModEntityTypes.CHAOS_SLICER.get(), player.level());

                            Vec3 sideDir = player.getLookAngle().cross(new Vec3(0,(chaosEmeraldCap.purpleSuperUse==12)?-1:1,0));

                            Vec3 spawnPos = new Vec3(player.getX()+player.getLookAngle().x,
                                    player.getY()+player.getLookAngle().y+1.0,
                                    player.getZ()+player.getLookAngle().z).add(sideDir);

                            //Sound
                            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                                    SoundEvents.EGG_THROW, SoundSource.MASTER, 1.0f, 1.0f);

                            superChaosSlicer.setPos(spawnPos);
                            superChaosSlicer.setDuration(120);
                            superChaosSlicer.setMovementDirection(player.getLookAngle().scale(1.5));
                            superChaosSlicer.setOwner(player.getUUID());
                            superChaosSlicer.setInverted(chaosEmeraldCap.purpleSuperUse==12);

                            // Add the entity to the world
                            player.level().addFreshEntity(superChaosSlicer);
                        }

                        //End Chaos Inferno
                        if(chaosEmeraldCap.purpleSuperUse > 15) {
                            chaosEmeraldCap.purpleSuperUse = 0;
                            //Set Cooldown(in Seconds)
                            chaosEmeraldCap.superCooldownKey[EmeraldType.PURPLE_EMERALD.ordinal()] = PURPLE_SUPER_EMERALD_CD;
                        }
                    }
                }

                //Chaos Inferno
                {
                    if(chaosEmeraldCap.redSuperUse > 0)
                    {
                        chaosEmeraldCap.redSuperUse += 1;

                        PacketHandler.sendToALLPlayers(new SuperInfernoParticleS2C(
                                player.getX(), player.getY()+player.getEyeHeight()/3, player.getZ(), chaosEmeraldCap.redSuperUse
                        ));

                        for(LivingEntity enemy : player.level().getEntitiesOfClass(LivingEntity.class,
                                new AABB(player.getX()+3.0,player.getY()-1.0,player.getZ()+3.0,
                                        player.getX()-3.0,player.getY()+5.0,player.getZ()-3.0),
                                (entity)->!entity.is(player)))
                        {
                            //Damage Enemy
                            enemy.hurt(player.damageSources().playerAttack(player), 1.0f);
                            if(Utilities.random.nextInt(100) < 5)  enemy.setRemainingFireTicks(40);
                        }

                        //Display Particle Every Second
                        if (tick == 0) {
                            PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                                    ParticleTypes.LAVA,
                                    player.getX(), player.getY() + player.getEyeHeight() / 2, player.getZ(),
                                    0.001, 0.5F, player.getEyeHeight() / 2, 0.5F, 3, false));
                        }
                    }

                    //End Chaos Inferno
                    if(chaosEmeraldCap.redSuperUse > CHAOS_INFERNO_TIME) {
                        chaosEmeraldCap.redSuperUse = 0;
                        //Set Cooldown(in Seconds)
                        chaosEmeraldCap.superCooldownKey[EmeraldType.RED_EMERALD.ordinal()] = RED_SUPER_EMERALD_CD;
                    }
                }

                //Chaos Reprieve
                {
                    if(tick == 0)
                    {
                        //Grey Super Use
                        if(chaosEmeraldCap.greySuperUse != 0)   chaosEmeraldCap.greySuperUse += 1;

                        //Go Home after Time
                        if(chaosEmeraldCap.greySuperUse >= CHAOS_REPRIEVE_TIME
                                &&
                                player.serverLevel().dimension().equals(ModDimensions.CHAOS_REPRIEVE_LEVEL_KEY))
                        {
                            //Get Original Dimensions
                            ServerLevel originalWorld = Objects.requireNonNull(player.getServer()).getLevel(ResourceKey.create(Registries.DIMENSION,
                                    new ResourceLocation(chaosEmeraldCap.currentDimension)));

                            //Change back to the original world
                            assert originalWorld != null;
                            player.changeDimension(originalWorld, new ModTeleporter(WorldLoadHandler.reprieveTargetPos, false));
                            player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);

                            //Get Slowfalling
                            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,20,0,false,false,false),player);

                            //Cooldown
                            chaosEmeraldCap.greySuperUse = -GREY_SUPER_EMERALD_CD;

                            //Teleport to the Position
                            player.teleportTo(originalWorld,
                                    chaosEmeraldCap.greySuperPos[0],
                                    chaosEmeraldCap.greySuperPos[1],
                                    chaosEmeraldCap.greySuperPos[2],
                                    Collections.emptySet(),
                                    chaosEmeraldCap.greySuperPos[3],chaosEmeraldCap.greySuperPos[4]);
                            player.connection.send(new ClientboundTeleportEntityPacket(player));
                        }
                    }

                    //Go Home if you fall off
                    if(player.getY() < 0 &&
                            player.serverLevel().dimension().equals(ModDimensions.CHAOS_REPRIEVE_LEVEL_KEY))
                    {
                        //Get Original Dimensions
                        ServerLevel originalWorld = Objects.requireNonNull(player.getServer()).getLevel(ResourceKey.create(Registries.DIMENSION,
                                new ResourceLocation(chaosEmeraldCap.currentDimension)));

                        //Change back to the original world
                        assert originalWorld != null;
                        player.changeDimension(originalWorld, new ModTeleporter(WorldLoadHandler.reprieveTargetPos, false));
                        player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);

                        //Cooldown
                        chaosEmeraldCap.greySuperUse = -GREY_SUPER_EMERALD_CD;

                        //Get Slowfalling
                        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING,20,0,false,false,false),player);

                        //Teleport to the Position
                        player.teleportTo(originalWorld,
                                chaosEmeraldCap.greySuperPos[0],
                                chaosEmeraldCap.greySuperPos[1],
                                chaosEmeraldCap.greySuperPos[2],
                                Collections.emptySet(),
                                chaosEmeraldCap.greySuperPos[3],chaosEmeraldCap.greySuperPos[4]);
                        player.connection.send(new ClientboundTeleportEntityPacket(player));
                    }
                }

            }finally
            {
                // Sync every tick while any super-emerald ability is actively running so
                // that other players see timers ticking and abilities ending in real time.
                // At idle, a once-per-second sync is enough to keep traffic low.
                boolean anyActive = chaosEmeraldCap.aquaSuperUse > 0
                        || chaosEmeraldCap.greenSuperUse > 0
                        || chaosEmeraldCap.yellowSuperUse > 0
                        || chaosEmeraldCap.purpleSuperUse > 0
                        || chaosEmeraldCap.redSuperUse > 0;
                if (tick == 0 || anyActive) {
                    PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(
                            player.getId(), chaosEmeraldCap
                    ));
                }
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientTick(AbstractClientPlayer player, int tick)
    {
    }
}
