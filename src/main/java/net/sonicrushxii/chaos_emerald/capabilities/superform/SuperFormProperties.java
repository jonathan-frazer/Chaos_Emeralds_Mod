package net.sonicrushxii.chaos_emerald.capabilities.superform;

import net.minecraft.nbt.CompoundTag;
import net.sonicrushxii.chaos_emerald.capabilities.all.FormProperties;

public class SuperFormProperties extends FormProperties
{
    private final byte[] abilityCooldowns;

    public SuperFormProperties()
    {
        abilityCooldowns = new byte[SuperFormAbility.values().length];
    }

    public SuperFormProperties(CompoundTag nbt)
    {
        byte[] loaded = nbt.getByteArray("AbilityCooldowns");
        // Guard against a missing or wrong-length array (e.g. first load after
        // a new ability is added to the enum) to prevent ArrayIndexOutOfBounds.
        if (loaded.length == SuperFormAbility.values().length)
            abilityCooldowns = loaded;
        else
            abilityCooldowns = new byte[SuperFormAbility.values().length];
    }

    @Override
    public CompoundTag serialize()
    {
        CompoundTag nbt = new CompoundTag();

        nbt.putByteArray("AbilityCooldowns",abilityCooldowns);
        return nbt;
    }

    //Cooldown Manager
    public byte[] getAllCooldowns() {return abilityCooldowns;}
    public byte getCooldown(SuperFormAbility ability){return abilityCooldowns[ability.ordinal()];}
    public void setCooldown(SuperFormAbility ability, byte seconds){abilityCooldowns[ability.ordinal()] = seconds;}
}
