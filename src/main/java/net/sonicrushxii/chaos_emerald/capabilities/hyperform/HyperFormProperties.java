package net.sonicrushxii.chaos_emerald.capabilities.hyperform;

import net.minecraft.nbt.CompoundTag;
import net.sonicrushxii.chaos_emerald.capabilities.all.FormProperties;
import net.sonicrushxii.chaos_emerald.capabilities.superform.SuperFormAbility;

public class HyperFormProperties extends FormProperties
{
    private final byte[] abilityCooldowns;
    public int chaosBlastEXTimer;
    public boolean hasHyperDoubleJump;

    public HyperFormProperties()
    {
        abilityCooldowns = new byte[HyperFormAbility.values().length];
        chaosBlastEXTimer = 0;
        hasHyperDoubleJump = true;
    }

    public HyperFormProperties(CompoundTag nbt)
    {
        byte[] loaded = nbt.getByteArray("AbilityCooldowns");
        // Guard against a missing or wrong-length array.
        if (loaded.length == HyperFormAbility.values().length)
            abilityCooldowns = loaded;
        else
            abilityCooldowns = new byte[HyperFormAbility.values().length];
        chaosBlastEXTimer = nbt.getInt("ChaosBlastEXTimer");
        hasHyperDoubleJump = nbt.getBoolean("hasDoubleJump");
    }

    @Override
    public CompoundTag serialize()
    {
        CompoundTag nbt = new CompoundTag();

        nbt.putByteArray("AbilityCooldowns",abilityCooldowns);
        nbt.putInt("ChaosBlastEXTimer",chaosBlastEXTimer);
        nbt.putBoolean("hasDoubleJump",hasHyperDoubleJump);
        return nbt;
    }

    //Cooldown Manager
    public byte[] getAllCooldowns() {return abilityCooldowns;}
    public byte getCooldown(HyperFormAbility ability){return abilityCooldowns[ability.ordinal()];}
    public void setCooldown(HyperFormAbility ability, byte seconds){abilityCooldowns[ability.ordinal()] = seconds;}
}
