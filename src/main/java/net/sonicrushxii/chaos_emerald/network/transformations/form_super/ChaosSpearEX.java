package net.sonicrushxii.chaos_emerald.network.transformations.form_super;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;
import net.sonicrushxii.chaos_emerald.capabilities.ChaosEmeraldProvider;
import net.sonicrushxii.chaos_emerald.capabilities.superform.SuperFormAbility;
import net.sonicrushxii.chaos_emerald.capabilities.superform.SuperFormProperties;
import net.sonicrushxii.chaos_emerald.entities.form_super.ChaosSpearEXEntity;
import net.sonicrushxii.chaos_emerald.modded.ModEntityTypes;
import net.sonicrushxii.chaos_emerald.network.PacketHandler;
import net.sonicrushxii.chaos_emerald.network.all.EmeraldDataSyncS2C;

import java.util.function.Supplier;

public class ChaosSpearEX
{
    public ChaosSpearEX() {}

    public ChaosSpearEX(FriendlyByteBuf buffer){}

    public void encode(FriendlyByteBuf buffer){}

    public static void spawnChaosSpear(ServerPlayer player, Vec3 spawnPos)
    {
        Level pLevel = player.level();

        //Sound
        pLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.EGG_THROW, SoundSource.MASTER, 1.0f, 1.0f);

        //Spawning
        ChaosSpearEXEntity chaosSpearEXEntity = new ChaosSpearEXEntity(ModEntityTypes.CHAOS_SPEAR_EX.get(), pLevel);

        chaosSpearEXEntity.setPos(spawnPos);
        chaosSpearEXEntity.initializeDuration(120);
        chaosSpearEXEntity.setMovementDirection(player.getLookAngle().scale(1.5));
        chaosSpearEXEntity.setDestroyBlocks(player.isShiftKeyDown());
        chaosSpearEXEntity.setOwner(player.getUUID());

        // Add the entity to the world
        pLevel.addFreshEntity(chaosSpearEXEntity);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(
                ()->{
                    ServerPlayer player = ctx.get().getSender();

                    if(player != null)
                    {
                        Level pLevel = player.level();

                        player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                            SuperFormProperties superFormProperties = (SuperFormProperties) chaosEmeraldCap.formProperties;

                            //Set Cooldown
                            superFormProperties.setCooldown(SuperFormAbility.CHAOS_SPEAR_EX,(byte)10);
                            
                            //Throw Spear
                            {
                                Vec3 spawnPos = new Vec3(player.getX() + player.getLookAngle().x,
                                        player.getY() + player.getLookAngle().y + 1.0,
                                        player.getZ() + player.getLookAngle().z);
                                Vec3 rightAngle = player.getLookAngle().cross(new Vec3(0,1,0));

                                //Spawn Middle
                                spawnChaosSpear(player,spawnPos);
                                //Spawn Left and Right
                                spawnChaosSpear(player,spawnPos.add(rightAngle));
                                spawnChaosSpear(player,spawnPos.add(rightAngle.reverse()));
                            }

                            //Emerald Data
                            PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(
                                    player.getId(),chaosEmeraldCap
                            ));
                        });
                    }
                });
        ctx.get().setPacketHandled(true);
    }
}
