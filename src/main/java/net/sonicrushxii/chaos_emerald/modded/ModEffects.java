package net.sonicrushxii.chaos_emerald.modded;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sonicrushxii.chaos_emerald.ChaosEmerald;
import net.sonicrushxii.chaos_emerald.potion_effects.*;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS
            = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, ChaosEmerald.MOD_ID);

    public static final RegistryObject<MobEffect> CHAOS_BIND = MOB_EFFECTS.register(
            "chaos_bind",()->((new ChaosBindEffect(MobEffectCategory.HARMFUL,0x00FFFF))
                    .addAttributeModifier(Attributes.ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-987689B5BE5", -10.0, AttributeModifier.Operation.ADDITION)
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, "22653B89-116E-49DC-9B6B-9877489B5BE5", -10.0, AttributeModifier.Operation.ADDITION)
                    .addAttributeModifier(ForgeMod.ENTITY_GRAVITY.get(), "12AEAA34-359B-1198-935C-987861020334", -1.0, AttributeModifier.Operation.MULTIPLY_TOTAL)
    ));
    public static final RegistryObject<MobEffect> CHAOS_DASH_ATTACK = MOB_EFFECTS.register(
            "chaos_dash",()->((new ChaosDashEffect(MobEffectCategory.BENEFICIAL,0x00FF00))));
    public static final RegistryObject<MobEffect> CHAOS_FLAME_JUMP = MOB_EFFECTS.register(
            "chaos_flame_jump",()->((new ChaosFlameEffect(MobEffectCategory.BENEFICIAL,0xFF0000))));
    public static final RegistryObject<MobEffect> SUPER_ICE_LAUNCH = MOB_EFFECTS.register(
            "super_ice_launch",()->((new SuperIcePadEffect(MobEffectCategory.BENEFICIAL,0x0000FF))));
    public static final RegistryObject<MobEffect> SUPER_FALLDMG_EFFECT = MOB_EFFECTS.register(
            "super_falldmg_effect",()->((new SuperFormPadEffect(MobEffectCategory.BENEFICIAL,0xFFFF00))));

    public static final RegistryObject<MobEffect> SUPER_CHAOS_DIVE = MOB_EFFECTS.register(
            "super_chaos_dive",()->((new SuperDivePadEffect(MobEffectCategory.BENEFICIAL,0x00FF00))));

    public static final RegistryObject<MobEffect> HYPER_FALLDMG_EFFECT = MOB_EFFECTS.register(
            "hyper_falldmg_effect",()->((new HyperFormPadEffect(MobEffectCategory.BENEFICIAL,0xFFFF00))));

    public static final RegistryObject<MobEffect> CHAOS_LOOTING = MOB_EFFECTS.register(
            "chaos_looting", () -> new ChaosLootingEffect(MobEffectCategory.BENEFICIAL, 0x00CC44));

    public static void register(IEventBus eventBus)
    {
        MOB_EFFECTS.register(eventBus);
    }
}
