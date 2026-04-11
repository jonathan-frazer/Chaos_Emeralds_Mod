package net.sonicrushxii.chaos_emerald.event_handler;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sonicrushxii.chaos_emerald.ChaosEmerald;
import net.sonicrushxii.chaos_emerald.capabilities.ChaosEmeraldProvider;
import net.sonicrushxii.chaos_emerald.modded.ModBlocks;
import net.sonicrushxii.chaos_emerald.network.transformations.form_hyper.DeactivateHyperForm;

import static net.sonicrushxii.chaos_emerald.network.transformations.form_hyper.DeactivateHyperForm.spawnItem;

@Mod.EventBusSubscriber(modid = ChaosEmerald.MOD_ID)
public class DeathEventHandler {
    @SubscribeEvent
    public void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event)
    {
        if(event.getObject() instanceof Player){
            //Add Other Capabilities from here
            if(!event.getObject().getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).isPresent()){
                event.addCapability(new ResourceLocation(ChaosEmerald.MOD_ID, "properties"), new ChaosEmeraldProvider());
            }
        }
    }

    @SubscribeEvent
    public void onPlayerCloned(PlayerEvent.Clone event){
        if(event.isWasDeath()){
            // Copy capability data from the original (dead) player to the new
            // player instance. Previously both getCapability calls used
            // getOriginal(), so the new player never actually received the data.
            event.getOriginal().getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(oldStore->{
                event.getEntity().getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(newStore->{
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent event) {
        // Check if the entity that died is a Player
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                //Hyper Form Interrupt
                if(chaosEmeraldCap.hyperFormTimer != 0)
                {
                    spawnItem(player.serverLevel(),player.blockPosition(),new ItemStack(ModBlocks.AQUA_SUPER_EMERALD.get().asItem()));
                    spawnItem(player.serverLevel(),player.blockPosition(),new ItemStack(ModBlocks.BLUE_SUPER_EMERALD.get().asItem()));
                    spawnItem(player.serverLevel(),player.blockPosition(),new ItemStack(ModBlocks.GREEN_SUPER_EMERALD.get().asItem()));
                    spawnItem(player.serverLevel(),player.blockPosition(),new ItemStack(ModBlocks.GREY_SUPER_EMERALD.get().asItem()));
                    spawnItem(player.serverLevel(),player.blockPosition(),new ItemStack(ModBlocks.PURPLE_SUPER_EMERALD.get().asItem()));
                    spawnItem(player.serverLevel(),player.blockPosition(),new ItemStack(ModBlocks.RED_SUPER_EMERALD.get().asItem()));
                    spawnItem(player.serverLevel(),player.blockPosition(),new ItemStack(ModBlocks.YELLOW_SUPER_EMERALD.get().asItem()));
                }

                //Super Form Interrupt
                if(chaosEmeraldCap.superFormTimer != 0)
                {
                    spawnItem(player.serverLevel(),player.blockPosition(),new ItemStack(ModBlocks.AQUA_CHAOS_EMERALD.get().asItem()));
                    spawnItem(player.serverLevel(),player.blockPosition(),new ItemStack(ModBlocks.BLUE_CHAOS_EMERALD.get().asItem()));
                    spawnItem(player.serverLevel(),player.blockPosition(),new ItemStack(ModBlocks.GREEN_CHAOS_EMERALD.get().asItem()));
                    spawnItem(player.serverLevel(),player.blockPosition(),new ItemStack(ModBlocks.GREY_CHAOS_EMERALD.get().asItem()));
                    spawnItem(player.serverLevel(),player.blockPosition(),new ItemStack(ModBlocks.PURPLE_CHAOS_EMERALD.get().asItem()));
                    spawnItem(player.serverLevel(),player.blockPosition(),new ItemStack(ModBlocks.RED_CHAOS_EMERALD.get().asItem()));
                    spawnItem(player.serverLevel(),player.blockPosition(),new ItemStack(ModBlocks.YELLOW_CHAOS_EMERALD.get().asItem()));
                }
            });

        }
    }
}
