package net.sonicrushxii.chaos_emerald.network.all;

import net.minecraft.client.KeyMapping;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.sonicrushxii.chaos_emerald.KeyBindings;
import net.sonicrushxii.chaos_emerald.block.ChaosBlockItem;
import net.sonicrushxii.chaos_emerald.capabilities.ChaosEmeraldProvider;
import net.sonicrushxii.chaos_emerald.capabilities.all.ChaosAbilityDetails;
import net.sonicrushxii.chaos_emerald.network.PacketHandler;
import net.sonicrushxii.chaos_emerald.network.common.ChaosBoost;
import net.sonicrushxii.chaos_emerald.network.common.ChaosDimensionChange;
import net.sonicrushxii.chaos_emerald.network.common.ChaosTeleport;
import net.sonicrushxii.chaos_emerald.network.common.TimeStop;

public class KeyPress {
    int keyMapping;

    public KeyPress(KeyMapping keyMapping)
    {
        this.keyMapping = keyMapping.getKey().getValue();
    }

    public KeyPress(FriendlyByteBuf buffer) {
        this.keyMapping = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer){
        buffer.writeInt(this.keyMapping);
    }

    public void handle(CustomPayloadEvent.Context ctx){
        ctx.enqueueWork(
                ()->{
                    ServerPlayer player = ctx.getSender();
                    if(player != null)
                    {
                        //Check if holding Chaos Emerald
                        if(!ChaosBlockItem.isHoldingChaosEmerald(player)) return;

                        player.getCapability(ChaosEmeraldProvider.CHAOS_EMERALD_CAP).ifPresent(chaosEmeraldCap -> {
                            //Set Color
                            ChaosAbilityDetails chaosAbilities = chaosEmeraldCap.chaosAbilityDetails;
                            chaosAbilities.useColor = ChaosBlockItem.getEmeraldColorInHand(player);

                            //Time Stop
                            if (this.keyMapping == KeyBindings.INSTANCE.chaosTimeStop.getKey().getValue() && !player.isShiftKeyDown())
                                TimeStop.keyPress(player);

                            //Chaos Boost
                            else if(this.keyMapping == KeyBindings.INSTANCE.chaosTimeStop.getKey().getValue() && player.isShiftKeyDown())
                                ChaosBoost.keyPress(player);

                            //Teleport
                            else if (this.keyMapping == KeyBindings.INSTANCE.chaosTeleport.getKey().getValue() && !player.isShiftKeyDown())
                                ChaosTeleport.keyPress(player);

                            //Dimension Teleport
                            else if (this.keyMapping == KeyBindings.INSTANCE.chaosTeleport.getKey().getValue() && player.isShiftKeyDown())
                                ChaosDimensionChange.keyPress(player);

                            //Put Color back to normal
                            else chaosAbilities.useColor = Integer.MIN_VALUE;

                            //Sync with all Clients
                            PacketHandler.sendToALLPlayers(new EmeraldDataSyncS2C(player.getId(),chaosEmeraldCap));
                        });

                    }
                });
        ctx.setPacketHandled(true);
    }
}
