package net.sonicrushxii.chaos_emerald.event_handler;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import net.sonicrushxii.chaos_emerald.ChaosEmerald;
import net.sonicrushxii.chaos_emerald.modded.ModBlocks;

import java.util.Objects;
import java.util.UUID;

/**
 * Handles Curios-slot passive buffs for the seven Super Emeralds.
 *
 * <ul>
 *   <li>Red    → Fire Resistance I</li>
 *   <li>Blue   → Speed I</li>
 *   <li>Aqua   → Dolphin's Grace I</li>
 *   <li>Yellow → Resistance I</li>
 *   <li>Purple → Fortune (+3 Luck attribute)</li>
 *   <li>Grey   → Health Boost IV (+8 max HP)</li>
 *   <li>Green  → Looting +3 (via LootingLevelEvent)</li>
 * </ul>
 */
public class CuriosBonusHandler {

    private static final ResourceLocation CURIO_KEY =
            new ResourceLocation(ChaosEmerald.MOD_ID, "super_emerald_curio");

    // Stable UUID for the Fortune/Luck attribute modifier (same across sessions so
    // the modifier is de-duplicated correctly by the attribute system).
    private static final UUID LUCK_UUID = UUID.fromString("c4e7d3a2-11f8-4b2c-a9e1-7c3f85d20b4e");

    // -------------------------------------------------------------------------
    // Capability attachment
    // -------------------------------------------------------------------------

    @SubscribeEvent
    public void onAttachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        Item item = event.getObject().getItem();
        if (!isSuperEmerald(item)) return;

        ICurio curio = buildCurio(item);
        LazyOptional<ICurio> opt = LazyOptional.of(() -> curio);

        event.addCapability(CURIO_KEY, new ICapabilityProvider() {
            @Override
            public @NotNull <T> LazyOptional<T> getCapability(
                    @NotNull Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
                Capability<ICurio> curiosCap = CuriosCapability.ITEM;
                if (curiosCap != null) return curiosCap.orEmpty(cap, opt);
                return LazyOptional.empty();
            }
        });
    }

    // -------------------------------------------------------------------------
    // Looting (Green) — handled via event so it applies on mob kill, not in tick
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static boolean isSuperEmerald(Item item) {
        return item == ModBlocks.AQUA_SUPER_EMERALD.get().asItem()
                || item == ModBlocks.BLUE_SUPER_EMERALD.get().asItem()
                || item == ModBlocks.GREEN_SUPER_EMERALD.get().asItem()
                || item == ModBlocks.GREY_SUPER_EMERALD.get().asItem()
                || item == ModBlocks.PURPLE_SUPER_EMERALD.get().asItem()
                || item == ModBlocks.RED_SUPER_EMERALD.get().asItem()
                || item == ModBlocks.YELLOW_SUPER_EMERALD.get().asItem();
    }

    /**
     * Builds the {@link ICurio} implementation for {@code item}.
     * <p>
     * Purple uses {@link #getAttributeModifiers} to give a static Luck bonus (Fortune
     * analogue); all other effect-based emeralds refresh a potion effect in
     * {@link #curioTick} once per second so the effect never expires while equipped.
     * Green has no tick action — its bonus is applied in {@link #onLootingLevel}.
     */
    private static ICurio buildCurio(Item item) {
        return new ICurio() {

            @Override
            public void curioTick(SlotContext slotContext) {
                if (!(slotContext.entity() instanceof ServerPlayer player)) return;
                // Refresh once per second.
                if (player.tickCount % 20 != 0) return;

                if (item == ModBlocks.RED_SUPER_EMERALD.get().asItem()) {
                    applyEffect(player, MobEffects.FIRE_RESISTANCE, 40, 0);
                } else if (item == ModBlocks.BLUE_SUPER_EMERALD.get().asItem()) {
                    applyEffect(player, MobEffects.MOVEMENT_SPEED, 40, 0);
                } else if (item == ModBlocks.AQUA_SUPER_EMERALD.get().asItem()) {
                    applyEffect(player, MobEffects.DOLPHINS_GRACE, 40, 0);
                } else if (item == ModBlocks.YELLOW_SUPER_EMERALD.get().asItem()) {
                    applyEffect(player, MobEffects.DAMAGE_RESISTANCE, 40, 0);
                } else if (item == ModBlocks.GREY_SUPER_EMERALD.get().asItem()) {
                    applyEffect(player, MobEffects.HEALTH_BOOST, 40, 3); // level 4 = +8 HP
                }
                // Purple → attribute modifier handled below; Green → LootingLevelEvent.
            }

            @Override
            public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
                    SlotContext slotContext, UUID uuid) {
                Multimap<Attribute, AttributeModifier> map = LinkedHashMultimap.create();
                if (item == ModBlocks.PURPLE_SUPER_EMERALD.get().asItem()) {
                    // +3 Luck — Fortune analogue; affects loot tables via LUCK context param.
                    map.put(Attributes.LUCK,
                            new AttributeModifier(LUCK_UUID,
                                    "chaos_emerald.fortune", 3.0,
                                    AttributeModifier.Operation.ADDITION));
                }
                return map;
            }
        };
    }

    /** Refreshes a potion effect, updating the existing instance if already present. */
    private static void applyEffect(ServerPlayer player, MobEffect effect, int duration, int amplifier) {
        MobEffectInstance existing = player.getEffect(effect);
        MobEffectInstance inst = new MobEffectInstance(effect, duration, amplifier, false, false, false);
        if (existing != null) existing.update(inst);
        else player.addEffect(inst, player);
    }
}
