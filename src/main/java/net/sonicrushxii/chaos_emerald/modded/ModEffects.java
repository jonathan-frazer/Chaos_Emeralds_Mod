package net.sonicrushxii.chaos_emerald.modded;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sonicrushxii.chaos_emerald.ChaosEmerald;
import net.sonicrushxii.chaos_emerald.potion_effects.FallDamageNegation;
import net.sonicrushxii.chaos_emerald.potion_effects.PlayerTimeFreeze;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS
            = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ChaosEmerald.MOD_ID);

    public static final RegistryObject<MobEffect> PLAYER_TIME_FREEZE = MOB_EFFECTS.register(
            "player_time_freeze",()->((new PlayerTimeFreeze(MobEffectCategory.HARMFUL,0xFF0000)
                    .addAttributeModifier(ForgeMod.ENTITY_GRAVITY.get(), "A1B2C3D4-E5F6-7890-ABCD-EF1234567890", -1.0, AttributeModifier.Operation.MULTIPLY_TOTAL)))
    );
    public static final RegistryObject<MobEffect> FALL_DAMAGE_NEGATE = MOB_EFFECTS.register(
            "fall_damage_negate",()->((new FallDamageNegation(MobEffectCategory.BENEFICIAL,0xFFFFFF))
            ));
    public static void register(IEventBus eventBus)
    {
        MOB_EFFECTS.register(eventBus);
    }
}
