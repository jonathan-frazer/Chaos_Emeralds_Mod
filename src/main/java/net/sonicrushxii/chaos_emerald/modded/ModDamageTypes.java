package net.sonicrushxii.chaos_emerald.modded;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.sonicrushxii.chaos_emerald.ChaosEmerald;

public class ModDamageTypes {

    /**
     * Damage type used for all direct ability hits (Chaos + Super Emerald attacks,
     * Super/Hyper form attacks).  The DamageHandler listens for the attacker having
     * this type on their source and applies the 50 % armor-pierce formula.
     */
    public static final ResourceKey<DamageType> ABILITY_PIERCE = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            new ResourceLocation(ChaosEmerald.MOD_ID, "ability_pierce")
    );

    /** Creates a DamageSource attributed to {@code attacker} using the ability-pierce type. */
    public static DamageSource abilityPierce(Level level, Player attacker) {
        return new DamageSource(
                level.registryAccess()
                     .registryOrThrow(Registries.DAMAGE_TYPE)
                     .getHolderOrThrow(ABILITY_PIERCE),
                attacker
        );
    }
}
