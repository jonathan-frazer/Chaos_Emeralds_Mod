package net.sonicrushxii.chaos_emerald.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.sonicrushxii.chaos_emerald.capabilities.all.ChaosAbilityDetails;
import net.sonicrushxii.chaos_emerald.capabilities.all.PlayerFrozenDetails;
import net.sonicrushxii.chaos_emerald.capabilities.all.form_properties.FormProperties;

public class ChaosEmeraldCap
{
    public FormProperties formProperties = new FormProperties();

    public byte[] chaosCooldownKey = new byte[EmeraldAbility.values().length];

    //Previous Game Mode
    public byte prevGameMode = 0;

    public float atkRotPhaseX = 0.0f;
    public float atkRotPhaseY = 0.0f;

    //Details used for the Frozen Effect
    public PlayerFrozenDetails playerFrozenDetails = new PlayerFrozenDetails();

    //Chaos Emerald Usage
    public ChaosAbilityDetails chaosAbilityDetails = new ChaosAbilityDetails();

    public void copyDeathFrom(ChaosEmeraldCap source)
    {
        //Chaos Emerald Usage
        if(source.chaosCooldownKey.length == 0) this.chaosCooldownKey = new byte[EmeraldAbility.values().length];
        else                                    this.chaosCooldownKey = source.chaosCooldownKey;

        //Previous Gamemode
        this.prevGameMode = source.prevGameMode;

        //Attack Phase Rotation
        this.atkRotPhaseX = source.atkRotPhaseX;
        this.atkRotPhaseY = source.atkRotPhaseY;

        //Chaos Form Properties
        this.formProperties = source.formProperties;

        //Chaos Emerald Ability Usage
        this.chaosAbilityDetails = source.chaosAbilityDetails;
        this.chaosAbilityDetails.previousDimensionPos = new int[5];
    }

    public void copyPerfectFrom(ChaosEmeraldCap source)
    {
        this.copyDeathFrom(source);
        this.playerFrozenDetails = source.playerFrozenDetails;
    }

    public void saveNBTData(CompoundTag nbt)
    {
        //Copy Chaos Cooldown
        if(chaosCooldownKey.length == 0) chaosCooldownKey = new byte[EmeraldAbility.values().length];
        nbt.putByteArray("ChaosEmeraldCooldown", chaosCooldownKey);

        //Previous Gamemode
        nbt.putByte("previousGameMode",prevGameMode);

        //Attack Rotation Phase
        nbt.putFloat("AtkRotPhaseX",this.atkRotPhaseX);
        nbt.putFloat("AtkRotPhaseY",this.atkRotPhaseY);

        //Serialize Player Frozen Details
        nbt.put("PlayerFrozenDetails", playerFrozenDetails.serialize());

        //Serialize Form Abilities
        nbt.put("FormAbilities", formProperties.serialize());

        //Chaos Emerald Abilities
        nbt.put("ChaosAbilities", chaosAbilityDetails.serialize());
    }

    public void loadNBTData(CompoundTag nbt)
    {
        //Load Chaos Emerald Cooldown
        chaosCooldownKey = nbt.getByteArray("ChaosEmeraldCooldown");
        if(chaosCooldownKey.length == 0) chaosCooldownKey = new byte[EmeraldAbility.values().length];

        CompoundTag formDetails = nbt.getCompound("FormAbilities");

        //Previous Gamemode
        this.prevGameMode = nbt.getByte("previousGameMode");

        //Serialize Player Frozen Details
        this.playerFrozenDetails = new PlayerFrozenDetails(nbt.getCompound("PlayerFrozenDetails"));

        //Attack Rotation Phase
        this.atkRotPhaseX = nbt.getFloat("AtkRotPhaseX");
        this.atkRotPhaseY = nbt.getFloat("AtkRotPhaseY");

        //Load Form Properties Tag
        formProperties = new FormProperties(formDetails);

        //Chaos Emerald Abilities
        this.chaosAbilityDetails = new ChaosAbilityDetails(nbt.getCompound("ChaosAbilities"));
    }
}