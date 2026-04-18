package net.sonicrushxii.chaos_emerald.event_handler.custom;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.MinecraftServer;
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
import net.sonicrushxii.chaos_emerald.capabilities.ChaosEmeraldCap;
import net.sonicrushxii.chaos_emerald.capabilities.ChaosEmeraldProvider;
import net.sonicrushxii.chaos_emerald.capabilities.EmeraldType;
import net.sonicrushxii.chaos_emerald.entities.blue.IceHorizontalSpike;
import net.sonicrushxii.chaos_emerald.entities.yellow.ChaosSpear;
import net.sonicrushxii.chaos_emerald.modded.ModDimensions;
import net.sonicrushxii.chaos_emerald.modded.ModEffects;
import net.sonicrushxii.chaos_emerald.modded.ModEntityTypes;
import net.sonicrushxii.chaos_emerald.network.PacketHandler;
import net.sonicrushxii.chaos_emerald.network.all.EmeraldDataSyncS2C;
import net.sonicrushxii.chaos_emerald.network.all.ParticleAuraPacketS2C;
import net.sonicrushxii.chaos_emerald.network.all.SyncEntityMotionS2C;
import net.sonicrushxii.chaos_emerald.network.grey.SyncDigPacketS2C;
import net.sonicrushxii.chaos_emerald.network.red.FireSyncPacketS2C;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

public class ChaosEmeraldHandler {

    // Cooldowns (in ticks)
    private static final int AQUA_EMERALD_CD   = 40;
    private static final int BLUE_EMERALD_CD   = 25;
    private static final int GREEN_EMERALD_CD  = 7;
    private static final int GREY_EMERALD_CD   = 30;
    private static final int PURPLE_EMERALD_CD = 30;
    private static final int RED_EMERALD_CD    = 15;
    private static final int YELLOW_EMERALD_CD = 10;

    // Cost: 1 hunger point per ability use (spec: "Costs 1 hunger to use each to prevent spam")
    private static final int ABILITY_HUNGER_COST = 1;

    // -------------------------------------------------------------------------
    // Core helper — handles all common guard logic so each ability only contains
    // its unique behaviour.  Pass cooldown=0 for multi-tick "active" abilities
    // (grey/purple) whose cooldown is applied later by serverTick().
    // -------------------------------------------------------------------------
    private static void useAbility(
            Player pPlayer,
            EmeraldType type,
            int cooldown,
            BiConsumer<ServerPlayer, ChaosEmeraldCap> action
    ) {
        if (!(pPlayer instanceof ServerPlayer player)) return;

        player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(cap -> {
            if (cap.isUsingActiveAbility()) return;

            if (cap.chaosCooldownKey[type.ordinal()] > 0) {
                player.displayClientMessage(
                    Component.literal("That ability is not ready yet.")
                        .withStyle(Style.EMPTY.withColor(type.chatColor)), true);
                return;
            }

            if (!consumeHunger(player)) return;

            action.accept(player, cap);

            // Instant abilities set their cooldown immediately; active abilities
            // (grey/purple) set theirs when the multi-tick sequence finishes.
            if (cooldown > 0) cap.chaosCooldownKey[type.ordinal()] = cooldown;

            PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(player.getId(), cap));
        });
    }

    /** Returns true and deducts hunger if the player can afford the ability cost. */
    private static boolean consumeHunger(ServerPlayer player) {
        int food = player.getFoodData().getFoodLevel();
        if (food < ABILITY_HUNGER_COST) {
            player.displayClientMessage(
                Component.literal("Not enough hunger to use that ability.")
                    .withStyle(Style.EMPTY.withColor(0xFF5500)), true);
            return false;
        }
        player.getFoodData().setFoodLevel(food - ABILITY_HUNGER_COST);
        return true;
    }

    // -------------------------------------------------------------------------
    // Ability implementations
    // -------------------------------------------------------------------------

    public static void aquaEmeraldUse(Level pLevel, Player pPlayer) {
        useAbility(pPlayer, EmeraldType.AQUA_EMERALD, AQUA_EMERALD_CD, (player, cap) -> {
            Level level = player.level();

            Vec3 lookAngle = player.getLookAngle().scale(2);
            Vec3 displayPos = new Vec3(
                    player.getX() + lookAngle.x(),
                    player.getY() + lookAngle.y() + player.getEyeHeight(),
                    player.getZ() + lookAngle.z()
            );

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.MASTER, 0.75f, 0.75f);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.MASTER, 0.75f, 0.75f);

            PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(ParticleTypes.BUBBLE,
                    displayPos.x, displayPos.y, displayPos.z, 0.01,
                    3.5f, 3.5f, 3.5f, 30, false));
            PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(ParticleTypes.ENCHANTED_HIT,
                    displayPos.x, displayPos.y, displayPos.z, 0.01,
                    3.5f, 3.5f, 3.5f, 100, true));

            Vec3 knockDir = player.getLookAngle().scale(0.2);
            int count = 0;
            for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                    new AABB(displayPos.x() + 3.5, displayPos.y() + 3.5, displayPos.z() + 3.5,
                             displayPos.x() - 3.5, displayPos.y() - 3.5, displayPos.z() - 3.5),
                    (enemy) -> !enemy.is(player))
            ) {
                MobEffectInstance bindEffect = new MobEffectInstance(ModEffects.CHAOS_BIND.get(), 200, 0, false, false, false);
                if (target.hasEffect(ModEffects.CHAOS_BIND.get()))
                    Objects.requireNonNull(target.getEffect(ModEffects.CHAOS_BIND.get())).update(bindEffect);
                else
                    target.addEffect(bindEffect, target);
                player.connection.send(new ClientboundUpdateMobEffectPacket(target.getId(), bindEffect));
                target.addDeltaMovement(new Vec3(knockDir.x, 0.15, knockDir.z));
                if (++count > 4) break;
            }
        });
    }

    public static void blueEmeraldUse(Level pLevel, Player pPlayer) {
        useAbility(pPlayer, EmeraldType.BLUE_EMERALD, BLUE_EMERALD_CD, (player, cap) -> {
            Level level = player.level();

            Vec3 spawnPos = new Vec3(
                    player.getX() + player.getLookAngle().x,
                    player.getY() + player.getLookAngle().y + 1.0,
                    player.getZ() + player.getLookAngle().z);

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.EGG_THROW, SoundSource.MASTER, 1.0f, 1.0f);

            IceHorizontalSpike spike = new IceHorizontalSpike(ModEntityTypes.ICE_CHAOS_SPIKE.get(), level);
            spike.setPos(spawnPos);
            spike.setMovementDirection(player.getLookAngle());
            spike.setOwner(player.getUUID());
            level.addFreshEntity(spike);
        });
    }

    public static void greenEmeraldUse(Level pLevel, Player pPlayer) {
        useAbility(pPlayer, EmeraldType.GREEN_EMERALD, GREEN_EMERALD_CD, (player, cap) -> {
            Level level = player.level();

            Vec3 currentPos = new Vec3(player.getX(), player.getY(), player.getZ());
            Vec3 lookAngle = player.getLookAngle();
            LivingEntity tpTarget = null;

            for (int i = 0; i < 10; ++i) {
                currentPos = currentPos.add(lookAngle);
                AABB boundingBox = new AABB(
                        currentPos.x() + 3, currentPos.y() + 3, currentPos.z() + 3,
                        currentPos.x() - 3, currentPos.y() - 3, currentPos.z() - 3);
                List<LivingEntity> nearby = level.getEntitiesOfClass(
                        LivingEntity.class, boundingBox,
                        (enemy) -> !enemy.is(player) && enemy.isAlive());
                if (!nearby.isEmpty()) {
                    tpTarget = Collections.min(nearby, (e1, e2) -> (int)(
                            e1.distanceToSqr(player.getX(), player.getY(), player.getZ()) -
                            e2.distanceToSqr(player.getX(), player.getY(), player.getZ())));
                    break;
                }
            }

            if (tpTarget == null) return;

            Vec3 targetPos = new Vec3(tpTarget.getX(), tpTarget.getY(), tpTarget.getZ());
            level.playSound(null, currentPos.x(), currentPos.y(), currentPos.z(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.MASTER, 0.75f, 0.75f);

            Vec3 direction = currentPos.subtract(targetPos).normalize();
            Vec3 newPlayerPos = targetPos.add(direction);
            float[] yawPitch = Utilities.getYawPitchFromVec(targetPos.subtract(newPlayerPos));
            player.teleportTo(player.serverLevel(), newPlayerPos.x, newPlayerPos.y + 0.2, newPlayerPos.z,
                    Collections.emptySet(), yawPitch[0], yawPitch[1]);

            player.setDeltaMovement(0, 0.6, 0);
            PacketHandler.sendToALLPlayers(new SyncEntityMotionS2C(player.getId(), player.getDeltaMovement()));
            tpTarget.setDeltaMovement(direction.reverse().scale(1.5));
            tpTarget.hurt(level.damageSources().playerAttack(player), 4);
            tpTarget.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 110, 1, false, true, false), player);
            tpTarget.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 110, 1, false, true, false), player);

            PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                    new DustParticleOptions(new Vector3f(0, 1, 0), 2),
                    newPlayerPos.x(), newPlayerPos.y() + 1, newPlayerPos.z(),
                    0.01, 1.5f, 1.5f, 1.5f, 100, false));

            level.playSound(null, newPlayerPos.x(), newPlayerPos.y(), newPlayerPos.z(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.MASTER, 0.75f, 0.75f);

            MobEffectInstance chaosDashEffect = new MobEffectInstance(ModEffects.CHAOS_DASH_ATTACK.get(), 40, 0, false, false, false);
            if (player.hasEffect(ModEffects.CHAOS_DASH_ATTACK.get()))
                Objects.requireNonNull(player.getEffect(ModEffects.CHAOS_DASH_ATTACK.get())).update(chaosDashEffect);
            else
                player.addEffect(chaosDashEffect);
        });
    }

    // Grey is an active (multi-tick) ability — cooldown is set in serverTick() when it ends.
    public static void greyEmeraldUse(Level pLevel, Player pPlayer) {
        useAbility(pPlayer, EmeraldType.GREY_EMERALD, 0, (player, cap) -> {
            if (cap.greyChaosUse == 0) cap.greyChaosUse = 1;
        });
    }

    // Purple is an active (multi-tick) ability — cooldown is set in serverTick() when it ends.
    public static void purpleEmeraldUse(Level pLevel, Player pPlayer) {
        useAbility(pPlayer, EmeraldType.PURPLE_EMERALD, 0, (player, cap) -> {
            Objects.requireNonNull(player.getAttribute(ForgeMod.ENTITY_GRAVITY.get())).setBaseValue(0.0);
            Objects.requireNonNull(player.getAttribute(Attributes.KNOCKBACK_RESISTANCE)).setBaseValue(1.0);
            player.setDeltaMovement(0, 0.17, 0);
            PacketHandler.sendToALLPlayers(new SyncEntityMotionS2C(player.getId(), player.getDeltaMovement()));
            if (cap.purpleChaosUse == 0) cap.purpleChaosUse = 1;
        });
    }

    public static void redEmeraldUse(Level pLevel, Player pPlayer) {
        useAbility(pPlayer, EmeraldType.RED_EMERALD, RED_EMERALD_CD, (player, cap) -> {
            Level level = player.level();
            BlockPos playerPos = player.blockPosition();

            player.setDeltaMovement(0, 1.0, 0);
            PacketHandler.sendToALLPlayers(new SyncEntityMotionS2C(player.getId(), player.getDeltaMovement()));

            PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                    ParticleTypes.FLAME,
                    player.getX(), player.getY() + 0.35, player.getZ(),
                    0.001, 5f, 0.25f, 5f, 500, true));

            level.playSound(null, playerPos.getX(), playerPos.getY(), playerPos.getZ(),
                    SoundEvents.FIRECHARGE_USE, SoundSource.MASTER, 0.75f, 0.75f);

            final int fireBlocks = 24, radius = 6;
            for (int i = 0; i < fireBlocks; ++i) {
                BlockPos pos = new BlockPos(
                        playerPos.getX() + Utilities.random.nextInt(-radius, radius),
                        playerPos.getY() + Utilities.random.nextInt(-radius, radius),
                        playerPos.getZ() + Utilities.random.nextInt(-radius, radius));
                for (byte h = -3; h <= 3; ++h) {
                    if (Utilities.passableBlocks.contains(ForgeRegistries.BLOCKS.getKey(level.getBlockState(pos.offset(0, h, 0)).getBlock()) + "")
                            && !Utilities.passableBlocks.contains(ForgeRegistries.BLOCKS.getKey(level.getBlockState(pos.offset(0, h - 1, 0)).getBlock()) + "")) {
                        level.setBlock(pos.offset(0, h, 0), Blocks.FIRE.defaultBlockState(), 3);
                        PacketHandler.sendToALLPlayers(new FireSyncPacketS2C(pos.offset(0, h, 0)));
                        break;
                    }
                }
            }

            AABB boundingBox = new AABB(
                    player.getX() + radius, player.getY() + 1, player.getZ() + radius,
                    player.getX() - radius, player.getY() - 2, player.getZ() - radius);
            for (LivingEntity enemy : level.getEntitiesOfClass(LivingEntity.class, boundingBox, (e) -> !e.is(player))) {
                enemy.hurt(level.damageSources().playerAttack(player), 10);
                enemy.setSecondsOnFire(10);
            }

            MobEffectInstance chaosFlameJumpEffect = new MobEffectInstance(ModEffects.CHAOS_FLAME_JUMP.get(), 40, 0, false, false, false);
            if (player.hasEffect(ModEffects.CHAOS_FLAME_JUMP.get()))
                Objects.requireNonNull(player.getEffect(ModEffects.CHAOS_FLAME_JUMP.get())).update(chaosFlameJumpEffect);
            else
                player.addEffect(chaosFlameJumpEffect, player);

            if (player.hasEffect(MobEffects.FIRE_RESISTANCE))
                Objects.requireNonNull(player.getEffect(MobEffects.FIRE_RESISTANCE)).update(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0, false, false, false));
            else
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0, false, false, false), player);
        });
    }

    public static void yellowEmeraldUse(Level pLevel, Player pPlayer) {
        useAbility(pPlayer, EmeraldType.YELLOW_EMERALD, YELLOW_EMERALD_CD, (player, cap) -> {
            Level level = player.level();

            Vec3 spawnPos = new Vec3(
                    player.getX() + player.getLookAngle().x,
                    player.getY() + player.getLookAngle().y + 1.0,
                    player.getZ() + player.getLookAngle().z);

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.EGG_THROW, SoundSource.MASTER, 1.0f, 1.0f);

            ChaosSpear chaosSpear = new ChaosSpear(ModEntityTypes.CHAOS_SPEAR.get(), level);
            chaosSpear.setPos(spawnPos);
            chaosSpear.initializeDuration(120);
            chaosSpear.setMovementDirection(player.getLookAngle());
            chaosSpear.setDestroyBlocks(player.isShiftKeyDown());
            chaosSpear.setOwner(player.getUUID());
            level.addFreshEntity(chaosSpear);
        });
    }

    // -------------------------------------------------------------------------
    // Server tick — handles multi-tick active abilities (grey dig, purple blast)
    // -------------------------------------------------------------------------

    public static void serverTick(ServerPlayer player, int serverTick) {
        Level world = player.level();

        // Grey Emerald (active dig ability)
        player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(cap -> {
            // Ability finished — wait for landing before resetting
            if (cap.greyChaosUse == -1 && player.onGround()) {
                cap.greyChaosUse = 0;
                PacketHandler.sendToALLPlayers(new SyncDigPacketS2C(player.getId(), cap.greyChaosUse, player.getDeltaMovement()));
            }

            if (cap.greyChaosUse > 0) {
                if (!world.dimension().equals(ModDimensions.CHAOS_REPRIEVE_LEVEL_KEY))
                    cap.greyChaosUse += 1;
                else
                    cap.greyChaosUse = (serverTick % 2 == 0) ? (byte)(cap.greyChaosUse + 1) : cap.greyChaosUse;

                Vec3 lookAngle = player.getLookAngle();
                BlockPos playerPos = new BlockPos(
                        (int)(player.getX() + lookAngle.x()),
                        (int)(player.getY() + lookAngle.y()),
                        (int)(player.getZ() + lookAngle.z()));
                final int radius = 1;

                PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                        new DustParticleOptions(new Vector3f(0.9f, 0.9f, 0.9f), 2),
                        player.getX(), player.getY() + 1, player.getZ(),
                        0.01, 1.5f, 1.5f, 1.5f, 20, false));

                for (LivingEntity enemy : world.getEntitiesOfClass(LivingEntity.class, new AABB(
                        player.getX() + lookAngle.x() - 1.5, player.getY() + lookAngle.y() - 1.5, player.getZ() + lookAngle.z() - 1.5,
                        player.getX() + lookAngle.x() + 1.5, player.getY() + lookAngle.y() + 1.5, player.getZ() + lookAngle.z() + 1.5),
                        (enemy) -> !enemy.is(player))) {
                    enemy.hurt(world.damageSources().playerAttack(player), 4);
                }

                if (!world.dimension().equals(ModDimensions.CHAOS_REPRIEVE_LEVEL_KEY)) {
                    BlockPos start = playerPos.offset(-radius, -(radius + 1), -radius);
                    BlockPos end = playerPos.offset(radius, radius + 2, radius);
                    for (BlockPos pos : BlockPos.betweenClosed(start, end)) {
                        BlockState blockState = player.level().getBlockState(pos);
                        if (!Utilities.unbreakableBlocks.contains(ForgeRegistries.BLOCKS.getKey(blockState.getBlock()) + ""))
                            player.level().destroyBlock(pos, player.isShiftKeyDown());
                    }
                }

                player.setDeltaMovement(lookAngle.scale(1));
                PacketHandler.sendToALLPlayers(new SyncDigPacketS2C(player.getId(), cap.greyChaosUse, player.getDeltaMovement()));
            }

            if (cap.greyChaosUse == 40) {
                cap.greyChaosUse = -1;
                cap.chaosCooldownKey[EmeraldType.GREY_EMERALD.ordinal()] = GREY_EMERALD_CD;
                PacketHandler.sendToALLPlayers(new SyncDigPacketS2C(player.getId(), cap.greyChaosUse, player.getDeltaMovement()));
            }
        });

        // Purple Emerald (active blast ability)
        player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(cap -> {
            // Ability finished — wait for landing before resetting
            if (cap.purpleChaosUse == -1 && player.onGround()) {
                cap.purpleChaosUse = 0;
                // No sync needed here; state == 0 is the default and a final
                // EmeraldDataSyncS2C is sent below when purpleChaosUse was non-zero.
            }

            if (cap.purpleChaosUse > 0) {
                PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                        new DustParticleOptions(new Vector3f(0.8f, 0.0f, 1f), 1),
                        player.getX(), player.getY() + 1, player.getZ(),
                        0.01, 1f, 1f, 1f, 30, false));
                cap.purpleChaosUse += 1;

                // Perform blast at tick 20
                if (cap.purpleChaosUse == 20) {
                    CommandSourceStack css = player.createCommandSourceStack().withPermission(4).withSuppressedOutput();
                    MinecraftServer server = player.serverLevel().getServer();
                    server.getCommands().performPrefixedCommand(css,
                        "summon firework_rocket ~ ~ ~ {Life:0,LifeTime:0,FireworksItem:{id:\"firework_rocket\",Count:1,tag:{Fireworks:{Explosions:[{Type:1,Flicker:1b,Colors:[I;12779775,16777215],FadeColors:[I;16711680,0]}]}}}}");

                    AABB boundingBox = new AABB(
                            player.getX() + 6, player.getY() + 6, player.getZ() + 6,
                            player.getX() - 6, player.getY() - 6, player.getZ() - 6);
                    for (LivingEntity enemy : world.getEntitiesOfClass(LivingEntity.class, boundingBox, (e) -> !e.is(player)))
                        enemy.hurt(world.damageSources().playerAttack(player), 7);

                    player.setDeltaMovement(0, 0, 0);
                    PacketHandler.sendToALLPlayers(new SyncEntityMotionS2C(player.getId(), player.getDeltaMovement()));
                }

                // End blast at tick 30
                if (cap.purpleChaosUse > 30) {
                    Objects.requireNonNull(player.getAttribute(ForgeMod.ENTITY_GRAVITY.get())).setBaseValue(0.08);
                    Objects.requireNonNull(player.getAttribute(Attributes.KNOCKBACK_RESISTANCE)).setBaseValue(0.0);
                    cap.purpleChaosUse = -1;
                    cap.chaosCooldownKey[EmeraldType.PURPLE_EMERALD.ordinal()] = PURPLE_EMERALD_CD;
                }

                // Only sync while the ability is actively running (avoids 20 broadcasts/sec at idle)
                PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(player.getId(), cap));
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientTick(AbstractClientPlayer player, int clientTick) {
    }
}
