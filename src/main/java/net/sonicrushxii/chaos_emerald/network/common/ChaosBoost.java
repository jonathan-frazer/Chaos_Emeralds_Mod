package net.sonicrushxii.chaos_emerald.network.common;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;
import net.sonicrushxii.chaos_emerald.capabilities.ChaosEmeraldProvider;
import net.sonicrushxii.chaos_emerald.capabilities.EmeraldAbility;
import net.sonicrushxii.chaos_emerald.capabilities.all.ChaosAbilityDetails;
import net.sonicrushxii.chaos_emerald.network.PacketHandler;
import net.sonicrushxii.chaos_emerald.network.all.EmeraldDataSyncS2C;

public class ChaosBoost
{
    public static void keyPress(ServerPlayer player)
    {
        player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap ->
        {
            //Fetch Ability Properties
            ChaosAbilityDetails chaosAbilities = chaosEmeraldCap.chaosAbilityDetails;

            //Activate Teleport
            if (chaosAbilities.buffBoost == 0 && chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.CHAOS_CONTROL.ordinal()] == 0 && !chaosAbilities.abilityInUse()) {
                chaosAbilities.buffBoost = 1;
                player.displayClientMessage(Component.translatable("Chaos Boost!").withStyle(Style.EMPTY.withColor(chaosAbilities.useColor)),true);
            }

            //Cooldown not set
            else if(chaosEmeraldCap.chaosCooldownKey[EmeraldAbility.CHAOS_CONTROL.ordinal()] > 0){
                player.displayClientMessage(Component.translatable("That Ability is not Ready Yet").withStyle(Style.EMPTY.withColor(chaosAbilities.useColor)),true);
                chaosAbilities.useColor = Integer.MIN_VALUE;
            }

            //Other Ability Active
            else if(chaosAbilities.abilityInUse())
            {
                player.displayClientMessage(Component.translatable("That Ability cannot be used currently").withStyle(Style.EMPTY.withColor(chaosAbilities.useColor)),true);
            }

            //Error Handling
            else
            {
                player.displayClientMessage(Component.translatable("That Ability cannot be used currently").withStyle(Style.EMPTY.withColor(chaosAbilities.useColor)),true);
                chaosAbilities.useColor = Integer.MIN_VALUE;
            }

            //Sync Data
            PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(player.getId(),chaosEmeraldCap));
        });
    }

    public static void applyBuffs(ServerPlayer player)
    {
        final int SECONDS = 20;
        final int DURATION = 30*SECONDS;

        //Speed
        if(!player.hasEffect(MobEffects.MOVEMENT_SPEED)) player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, DURATION, 2, false, true));
        else player.getEffect(MobEffects.MOVEMENT_SPEED).update(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, DURATION, 2, false, true));

        //Jump
        if(!player.hasEffect(MobEffects.JUMP)) player.addEffect(new MobEffectInstance(MobEffects.JUMP, DURATION, 2, false, true));
        else player.getEffect(MobEffects.JUMP).update(new MobEffectInstance(MobEffects.JUMP, DURATION, 2, false, true));

        //Resistance
        if(!player.hasEffect(MobEffects.DAMAGE_RESISTANCE)) player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, DURATION, 3, false, true));
        else player.getEffect(MobEffects.DAMAGE_RESISTANCE).update(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, DURATION, 3, false, true));

        //Strength
        if(!player.hasEffect(MobEffects.DAMAGE_BOOST)) player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, DURATION, 1, false, true));
        else player.getEffect(MobEffects.DAMAGE_BOOST).update(new MobEffectInstance(MobEffects.DAMAGE_BOOST, DURATION, 1, false, true));

        //Haste
        if(!player.hasEffect(MobEffects.DIG_SPEED)) player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, DURATION, 1, false, true));
        else player.getEffect(MobEffects.DIG_SPEED).update(new MobEffectInstance(MobEffects.DIG_SPEED, DURATION, 1, false, true));
    }
}
