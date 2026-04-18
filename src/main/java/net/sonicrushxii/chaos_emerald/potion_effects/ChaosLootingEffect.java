package net.sonicrushxii.chaos_emerald.potion_effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Visual indicator effect for the Green Chaos/Super Emerald Curios passive.
 * The actual Looting +3 bonus is applied in CuriosBonusHandler via LootingLevelEvent.
 */
public class ChaosLootingEffect extends MobEffect {
    public ChaosLootingEffect(MobEffectCategory category, int color) {
        super(category, color);
    }
}
