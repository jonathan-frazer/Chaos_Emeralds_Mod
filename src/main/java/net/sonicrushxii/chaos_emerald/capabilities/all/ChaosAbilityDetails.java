package net.sonicrushxii.chaos_emerald.capabilities.all;

import net.minecraft.nbt.CompoundTag;

import java.util.Arrays;

public class ChaosAbilityDetails
{
    //Color of the Chaos Emerald Being Used
    public int useColor;

    //Time Stop
    public byte timeStop;
    //Teleport
    public byte teleport;
    //Dimension Teleport
    public byte dimTeleport;
    public String targetDimension;
    public int[] previousDimensionPos;
    //Chaos Boost
    public byte buffBoost;

    public ChaosAbilityDetails()
    {
        useColor = Integer.MIN_VALUE;

        //Timestop info
        timeStop = 0;
        //Teleport Info
        teleport = 0;
        //Dimension Teleport Info
        dimTeleport = 0;
        targetDimension = "minecraft:the_nether";
        previousDimensionPos = new int[5];
        //Chaos Boost
        buffBoost = 0;
    }

    public ChaosAbilityDetails(CompoundTag nbt)
    {
        useColor = nbt.contains("Color")?nbt.getInt("Color"):Integer.MIN_VALUE;

        //Timestop Info
        timeStop = nbt.contains("Timestop")?nbt.getByte("Timestop"):0;

        //Teleport Info
        teleport = nbt.contains("Teleport")?nbt.getByte("Teleport"):0;

        //Dimension Teleport
        dimTeleport = nbt.contains("DimensionTeleport")?nbt.getByte("DimensionTeleport"):0;
        targetDimension = nbt.contains("PreviousDim")?nbt.getString("PreviousDim"):"minecraft:the_nether";
        previousDimensionPos = nbt.contains("PreviousDimPos")?nbt.getIntArray("PreviousDimPos"):new int[5];

        //Chaos Boost
        buffBoost = nbt.contains("ChaosBoost")?nbt.getByte("ChaosBoost"):0;
    }

    public CompoundTag serialize()
    {
        CompoundTag nbt = new CompoundTag();

        if(useColor > -1)                                               nbt.putInt("Color", useColor);

        //Timestop
        if(timeStop != 0)                                               nbt.putByte("Timestop",timeStop);

        //Teleport
        if(teleport != 0)                                               nbt.putByte("Teleport",teleport);

        //Dimension Teleport
        if(dimTeleport != 0)                                            nbt.putByte("DimensionTeleport",dimTeleport);
        if(!targetDimension.equals("minecraft:the_nether"))             nbt.putString("PreviousDim", targetDimension);
        if(Arrays.equals(previousDimensionPos,new int[]{0,0,0,0,0}))    nbt.putIntArray("PreviousDimPos",previousDimensionPos);

        //Chaos Boost
        if(buffBoost != 0)                                              nbt.putByte("ChaosBoost",buffBoost);

        return nbt;
    }

    public boolean stoppingTime()
    {
        return this.timeStop > 0 || this.teleport > 0;
    }

    public boolean abilityInUse()
    {
        return this.stoppingTime() || this.dimTeleport > 0 || this.buffBoost > 0;
    }
}