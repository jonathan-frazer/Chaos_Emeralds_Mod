package net.sonicrushxii.chaos_emerald.block;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import net.sonicrushxii.chaos_emerald.event_handler.custom.ChaosEmeraldHandler;
import net.sonicrushxii.chaos_emerald.event_handler.custom.SuperEmeraldHandler;

import java.util.StringTokenizer;
import java.util.UUID;

public class SuperBlockItem extends BlockItem {

    private static final UUID LUCK_UUID = UUID.fromString("c4e7d3a2-11f8-4b2c-a9e1-7c3f85d20b4e");

    public SuperBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    // -------------------------------------------------------------------------
    // Curios integration — recommended Forge approach via initCapabilities
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

                switch (emeraldColor(stack)) {
                    case "red_emerald"    -> applyEffect(player, MobEffects.FIRE_RESISTANCE, 80, 0);
                    case "blue_emerald"   -> applyEffect(player, MobEffects.MOVEMENT_SPEED, 80, 0);
                    case "aqua_emerald"   -> applyEffect(player, MobEffects.DOLPHINS_GRACE, 80, 0);
                    case "yellow_emerald" -> applyEffect(player, MobEffects.DAMAGE_RESISTANCE, 80, 0);
                    case "grey_emerald"   -> applyEffect(player, MobEffects.HEALTH_BOOST, 80, 3);
                    // purple → attribute modifier below; green → LootingLevelEvent in CuriosBonusHandler
                }
            }

            @Override
            public Multimap<Attribute, AttributeModifier> getAttributeModifiers(
                    SlotContext slotContext, UUID uuid) {
                Multimap<Attribute, AttributeModifier> map = LinkedHashMultimap.create();
                if ("purple_emerald".equals(emeraldColor(stack))) {
                    map.put(Attributes.LUCK,
                            new AttributeModifier(LUCK_UUID,
                                    "chaos_emerald.fortune", 3.0,
                                    AttributeModifier.Operation.ADDITION));
                }
                return map;
            }
        });
    }

    // -------------------------------------------------------------------------
    // Right-click ability use
    // -------------------------------------------------------------------------

    private static boolean isCtrlHeld() {
        Minecraft mc = Minecraft.getInstance();
        return InputConstants.isKeyDown(mc.getWindow().getWindow(), InputConstants.KEY_LCONTROL)
                || InputConstants.isKeyDown(mc.getWindow().getWindow(), InputConstants.KEY_RCONTROL);
    }

    private static void useEmerald(String itemString, Level pLevel, Player pPlayer) {
        StringTokenizer sg = new StringTokenizer(itemString, "/");
        sg.nextToken();
        final boolean isCtrlDown = FMLEnvironment.dist == Dist.CLIENT && isCtrlHeld();
        switch (sg.nextToken()) {
            case "aqua_emerald"   -> { if (isCtrlDown) ChaosEmeraldHandler.aquaEmeraldUse(pLevel, pPlayer);   else SuperEmeraldHandler.aquaEmeraldUse(pPlayer); }
            case "blue_emerald"   -> { if (isCtrlDown) ChaosEmeraldHandler.blueEmeraldUse(pLevel, pPlayer);   else SuperEmeraldHandler.blueEmeraldUse(pLevel, pPlayer); }
            case "green_emerald"  -> { if (isCtrlDown) ChaosEmeraldHandler.greenEmeraldUse(pLevel, pPlayer);  else SuperEmeraldHandler.greenEmeraldUse(pPlayer); }
            case "grey_emerald"   -> { if (isCtrlDown) SuperEmeraldHandler.greyEmeraldUse(pLevel, pPlayer);   else ChaosEmeraldHandler.greyEmeraldUse(pLevel, pPlayer); }
            case "purple_emerald" -> { if (isCtrlDown) ChaosEmeraldHandler.purpleEmeraldUse(pLevel, pPlayer); else SuperEmeraldHandler.purpleEmeraldUse(pPlayer); }
            case "red_emerald"    -> { if (isCtrlDown) ChaosEmeraldHandler.redEmeraldUse(pLevel, pPlayer);    else SuperEmeraldHandler.redEmeraldUse(pPlayer); }
            case "yellow_emerald" -> { if (isCtrlDown) ChaosEmeraldHandler.yellowEmeraldUse(pLevel, pPlayer); else SuperEmeraldHandler.yellowEmeraldUse(pPlayer); }
        }
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        Player player = pContext.getPlayer();
        assert player != null;
        if (!player.isShiftKeyDown()) {
            useEmerald(pContext.getItemInHand().getItem().toString(), pContext.getLevel(), player);
            return InteractionResult.SUCCESS;
        }
        return super.useOn(pContext);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, InteractionHand pUsedHand) {
        switch (pUsedHand) {
            case MAIN_HAND -> useEmerald(pPlayer.getMainHandItem().getItem().toString(), pLevel, pPlayer);
            case OFF_HAND  -> useEmerald(pPlayer.getOffhandItem().getItem().toString(), pLevel, pPlayer);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /** Returns the color suffix (e.g. "red_emerald") from this item's registry path. */
    static String emeraldColor(ItemStack stack) {
        ResourceLocation key = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (key == null) return "";
        String path = key.getPath(); // "super_emerald/red_emerald"
        int slash = path.indexOf('/');
        return slash >= 0 ? path.substring(slash + 1) : path;
    }

    private static void applyEffect(ServerPlayer player, MobEffect effect, int duration, int amplifier) {
        player.addEffect(new MobEffectInstance(effect, duration, amplifier, false, false, false), player);
    }
}
