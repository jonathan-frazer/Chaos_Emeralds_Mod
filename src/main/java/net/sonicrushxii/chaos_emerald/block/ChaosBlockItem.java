package net.sonicrushxii.chaos_emerald.block;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.sonicrushxii.chaos_emerald.capabilities.EmeraldType;
import net.sonicrushxii.chaos_emerald.event_handler.custom.ChaosEmeraldHandler;

import java.util.StringTokenizer;

public class ChaosBlockItem extends BlockItem {

    public ChaosBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    public static boolean isHoldingChaosEmerald(ServerPlayer player)
    {
        return (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof ChaosBlockItem || player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof ChaosBlockItem);
    }

    public static int getEmeraldColorInHand(ServerPlayer player)
    {
        Item mainHandItem = player.getItemInHand(InteractionHand.MAIN_HAND).getItem();
        Item offHandItem = player.getItemInHand(InteractionHand.OFF_HAND).getItem();

        //Check MainHand
        if(mainHandItem instanceof ChaosBlockItem chaosEmeraldItem)
            return colorMapping(chaosEmeraldItem);

        //Check OffHand
        if(offHandItem instanceof ChaosBlockItem chaosEmeraldItem)
            return colorMapping(chaosEmeraldItem);

        return Integer.MIN_VALUE;
    }

    public static int colorMapping(ChaosBlockItem chaosEmeraldItem)
    {
        //Make String Tokenizer
        StringTokenizer sg = new StringTokenizer(chaosEmeraldItem.toString(),"/");
        //Ignore First Token
        sg.nextToken();

        //Get Last Token
        return switch (sg.nextToken())
        {
            case "aqua_emerald" -> EmeraldType.AQUA_EMERALD.color();
            case "blue_emerald" -> EmeraldType.BLUE_EMERALD.color();
            case "green_emerald" -> EmeraldType.GREEN_EMERALD.color();
            case "grey_emerald" -> EmeraldType.GREY_EMERALD.color();
            case "purple_emerald" -> EmeraldType.PURPLE_EMERALD.color();
            case "red_emerald" -> EmeraldType.RED_EMERALD.color();
            case "yellow_emerald" -> EmeraldType.YELLOW_EMERALD.color();
            default -> Integer.MIN_VALUE;
        };
    }

    private static boolean useEmerald(String itemString, Level pLevel, Player pPlayer)
    {
        //Make String Tokenizer
        StringTokenizer sg = new StringTokenizer(itemString,"/");
        //Ignore First Token
        sg.nextToken();

        //Get Last Token
        switch(sg.nextToken())
        {
            case "aqua_emerald":
                ChaosEmeraldHandler.aquaEmeraldUse(pLevel, pPlayer);
                break;
            case "blue_emerald":
                ChaosEmeraldHandler.blueEmeraldUse(pLevel, pPlayer);
                break;
            case "green_emerald":
                ChaosEmeraldHandler.greenEmeraldUse(pLevel, pPlayer);
                break;
            case "grey_emerald":
                ChaosEmeraldHandler.greyEmeraldUse(pLevel, pPlayer);
                break;
            case "purple_emerald":
                ChaosEmeraldHandler.purpleEmeraldUse(pLevel, pPlayer);
                break;
            case "red_emerald":
                ChaosEmeraldHandler.redEmeraldUse(pLevel, pPlayer);
                break;
            case "yellow_emerald":
                ChaosEmeraldHandler.yellowEmeraldUse(pLevel, pPlayer);
                break;
            default: return false;
        }
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level world = pContext.getLevel();
        Player player = pContext.getPlayer();

        assert player != null;
        if(!player.isShiftKeyDown())
        {
            //If MainHand Fails check Offhand
            useEmerald(pContext.getItemInHand().getItem().toString(), world, player);
            return InteractionResult.FAIL;
        }

        return super.useOn(pContext);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        //Use corresponding hand
        switch(pUsedHand)
        {
            case MAIN_HAND: useEmerald(pPlayer.getMainHandItem().getItem().toString(), pLevel, pPlayer); break;
            case OFF_HAND: useEmerald(pPlayer.getOffhandItem().getItem().toString(), pLevel, pPlayer); break;
        }

        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
