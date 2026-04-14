package net.sonicrushxii.chaos_emerald.event_handler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;
import net.sonicrushxii.chaos_emerald.modded.ModBlocks;

/**
 * Handles Curios-slot passive buffs that cannot live on the item class.
 *
 * Green Super Emerald — Looting +3 on mob kill.
 * All other buffs (Fire Res, Speed, Dolphin's Grace, Resistance, Health Boost,
 * Fortune) are implemented in {@link net.sonicrushxii.chaos_emerald.block.SuperBlockItem}
 * via {@code ICurioItem}.
 */
public class CuriosBonusHandler {

    @SubscribeEvent
    public void onLootingLevel(LootingLevelEvent event) {
        if (!(event.getDamageSource().getEntity() instanceof ServerPlayer player)) return;

        CuriosApi.getCuriosHelper().getEquippedCurios(player).ifPresent(handler -> {
            for (int i = 0; i < handler.getSlots(); i++) {
                if (handler.getStackInSlot(i).is(ModBlocks.GREEN_SUPER_EMERALD.get().asItem())) {
                    event.setLootingLevel(event.getLootingLevel() + 3);
                    return;
                }
            }
        });
    }
}
