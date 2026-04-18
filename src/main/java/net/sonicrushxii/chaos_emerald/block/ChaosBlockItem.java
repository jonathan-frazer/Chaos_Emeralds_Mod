package net.sonicrushxii.chaos_emerald.block;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.sonicrushxii.chaos_emerald.event_handler.custom.ChaosEmeraldHandler;
import net.sonicrushxii.chaos_emerald.modded.ModEffects;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.StringTokenizer;

public class ChaosBlockItem extends BlockItem {

    public ChaosBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    // -------------------------------------------------------------------------
    // Curios integration — passive buffs when equipped in the "gem" slot
    // -------------------------------------------------------------------------

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return CuriosApi.createCurioProvider(new ICurio() {

            @Override
            public ItemStack getStack() {
                return stack;
            }

            @Override
            public boolean canEquip(SlotContext slotContext) {
                return "gem".equals(slotContext.identifier());
            }

            @Override
            public void curioTick(SlotContext slotContext) {
                if (!(slotContext.entity() instanceof ServerPlayer player)) return;
                if (player.tickCount % 20 != 0) return;

                switch (SuperBlockItem.emeraldColor(stack)) {
                    case "red_emerald"    -> applyEffect(player, MobEffects.FIRE_RESISTANCE, 0);
                    case "blue_emerald"   -> applyEffect(player, MobEffects.MOVEMENT_SPEED, 0);
                    case "aqua_emerald"   -> applyEffect(player, MobEffects.DOLPHINS_GRACE, 0);
                    case "yellow_emerald" -> applyEffect(player, MobEffects.DAMAGE_RESISTANCE, 0);
                    case "grey_emerald"   -> applyEffect(player, MobEffects.HEALTH_BOOST, 3);
                    case "purple_emerald" -> applyEffect(player, MobEffects.LUCK, 2);
                    case "green_emerald"  -> applyEffect(player, ModEffects.CHAOS_LOOTING.get(), 0);
                }
            }
        });
    }

    private static void applyEffect(ServerPlayer player, MobEffect effect, int amplifier) {
        player.addEffect(new MobEffectInstance(effect, 80, amplifier, false, false, true), player);
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

