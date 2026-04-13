package net.sonicrushxii.chaos_emerald.block;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.sonicrushxii.chaos_emerald.event_handler.custom.ChaosEmeraldHandler;

import java.util.StringTokenizer;

public class ChaosBlockItem extends BlockItem {
    public ChaosBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
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
                ChaosEmeraldHandler.aquaEmeraldUse(pLevel,pPlayer);
                break;
            case "blue_emerald":
                ChaosEmeraldHandler.blueEmeraldUse(pLevel,pPlayer);
                break;
            case "green_emerald":
                ChaosEmeraldHandler.greenEmeraldUse(pLevel,pPlayer);
                break;
            case "grey_emerald":
                ChaosEmeraldHandler.greyEmeraldUse(pLevel,pPlayer);
                break;
            case "purple_emerald":
                ChaosEmeraldHandler.purpleEmeraldUse(pLevel,pPlayer);
                break;
            case "red_emerald":
                ChaosEmeraldHandler.redEmeraldUse(pLevel,pPlayer);
                break;
            case "yellow_emerald":
                ChaosEmeraldHandler.yellowEmeraldUse(pLevel,pPlayer);
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
        if(!player.isShiftKeyDown()){
            useEmerald(pContext.getItemInHand().getItem().toString(), world, player);
            // Return SUCCESS (not FAIL) so that Minecraft does NOT also call use() as a
            // fallback — FAIL leaves the action unconsumed, causing the ability to fire
            // twice per right-click (once from useOn and once from use).
            return InteractionResult.SUCCESS;
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

