package net.sonicrushxii.chaos_emerald.network.all;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateHandItem {

    ItemStack itemStack;
    InteractionHand interactionHand;

    public UpdateHandItem(ItemStack itemStack, InteractionHand interactionHand) {
        this.itemStack = itemStack;
        this.interactionHand = interactionHand;
    }

    public UpdateHandItem(FriendlyByteBuf buffer) {
        this.itemStack = buffer.readItem();
        this.interactionHand = buffer.readEnum(InteractionHand.class);
    }

    public void encode(FriendlyByteBuf buffer){
        buffer.writeItem(this.itemStack);
        buffer.writeEnum(interactionHand);
    }

    public static void performUpdateItem(ServerPlayer pPlayer, ItemStack item, InteractionHand interactionHand)
    {
        switch(interactionHand)
        {
            case MAIN_HAND: pPlayer.setItemSlot(EquipmentSlot.MAINHAND, item); break;
            case OFF_HAND: pPlayer.setItemSlot(EquipmentSlot.OFFHAND,item); break;
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(
                ()->{
                    ServerPlayer player = ctx.get().getSender();
                    if(player != null)
                        performUpdateItem(player,this.itemStack,interactionHand);
                });
        ctx.get().setPacketHandled(true);
    }
}
