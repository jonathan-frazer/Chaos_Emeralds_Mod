package net.sonicrushxii.chaos_emerald.network.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameType;
import net.sonicrushxii.chaos_emerald.capabilities.ChaosEmeraldProvider;
import net.sonicrushxii.chaos_emerald.capabilities.EmeraldAbility;
import net.sonicrushxii.chaos_emerald.capabilities.all.ChaosAbilityDetails;
import net.sonicrushxii.chaos_emerald.event_handler.custom.ChaosEmeraldHandler;
import net.sonicrushxii.chaos_emerald.modded.ModTeleporter;
import net.sonicrushxii.chaos_emerald.network.PacketHandler;
import net.sonicrushxii.chaos_emerald.network.all.EmeraldDataSyncS2C;
import net.sonicrushxii.chaos_emerald.network.all.ParticleAuraPacketS2C;

import java.util.Arrays;
import java.util.Collections;

public class ChaosDimensionChange
{
    public static void keyPress(ServerPlayer player)
    {
        player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap ->
        {
            //Fetch Ability Properties
            ChaosAbilityDetails chaosAbilities = chaosEmeraldCap.chaosAbilityDetails;

            //Activate Teleport
            if (chaosAbilities.dimTeleport == 0 && chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.CHAOS_CONTROL.ordinal()] == 0 && !chaosAbilities.abilityInUse()) {
                chaosAbilities.dimTeleport = 1;
                player.displayClientMessage(Component.translatable("Chaos Control!!").withStyle(Style.EMPTY.withColor(chaosAbilities.useColor)),true);
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

    public static void performDimensionChange(ServerPlayer player)
    {
        player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap ->
        {
            ChaosAbilityDetails chaosAbilities = chaosEmeraldCap.chaosAbilityDetails;

            //Set Target Dimension and Coordinates
            ServerLevel destinationWorld = player.getServer().getLevel(ResourceKey.create(Registries.DIMENSION,
                    new ResourceLocation(chaosAbilities.targetDimension)));

            int[] currentDimPositions = chaosAbilities.previousDimensionPos.clone();

            chaosAbilities.targetDimension = String.valueOf(player.level().dimension().location());
            chaosAbilities.previousDimensionPos[0] = player.getBlockX();
            chaosAbilities.previousDimensionPos[1] = player.getBlockY();
            chaosAbilities.previousDimensionPos[2] = player.getBlockZ();
            chaosAbilities.previousDimensionPos[3] = (int) player.getYRot();
            chaosAbilities.previousDimensionPos[4] = (int) player.getXRot();

            //Particle
            PacketHandler.sendToALLPlayers(new ParticleAuraPacketS2C(
                    ParticleTypes.FLASH,
                    player.getX(),player.getY()+player.getEyeHeight()/2,player.getZ(),
                    0.001,0.01F,0.01F,0.01F,
                    1,true));

            //Change Dimensions
            assert destinationWorld != null;
            player.changeDimension(destinationWorld, new ModTeleporter(new BlockPos(0,0,0), false));
            player.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);

            //Get Slowfalling
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20, 0, false, false, false), player);

            //Teleport to the Position(if one was specified)
            if(!Arrays.equals(currentDimPositions,new int[]{0,0,0,0,0}))
            {
                player.teleportTo(
                        destinationWorld,
                        currentDimPositions[0],
                        currentDimPositions[1],
                        currentDimPositions[2],
                        Collections.emptySet(),
                        currentDimPositions[3],
                        currentDimPositions[4]
                );
                player.connection.send(new ClientboundTeleportEntityPacket(player));
            }
            else
            {
                String command = "spreadplayers 0 0 10 50 under 120 false " + player.getScoreboardName();
                player.server.getCommands().performPrefixedCommand(player.createCommandSourceStack().withSuppressedOutput(), command);
            }

            //Sync Data
            PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(player.getId(),chaosEmeraldCap));
        });
    }
}
