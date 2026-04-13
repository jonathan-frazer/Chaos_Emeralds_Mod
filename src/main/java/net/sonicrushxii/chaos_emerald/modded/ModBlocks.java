package net.sonicrushxii.chaos_emerald.modded;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sonicrushxii.chaos_emerald.ChaosEmerald;
import net.sonicrushxii.chaos_emerald.block.*;

import java.util.StringTokenizer;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ChaosEmerald.MOD_ID);
    // Hardness 3600000F (not -1F) so PistonStructureResolver.canMoveBlock() allows pistons to push
    // these blocks. Hardness -1F is the vanilla "immovable" sentinel. getDestroyProgress() is
    // overridden to 0F in ChaosEmeraldBlock so they still cannot be mined in survival.
    public static final RegistryObject<Block> AQUA_CHAOS_EMERALD = registerBlock("chaos_emerald/aqua_emerald",
            ()->new ChaosEmeraldBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(3600000.0F, 3600000.0F)
                    .noLootTable()
                    .noOcclusion()
                    .lightLevel((pProperites) -> {
                        return 13;
                    })));
    public static final RegistryObject<Block> BLUE_CHAOS_EMERALD = registerBlock("chaos_emerald/blue_emerald",
            ()->new ChaosEmeraldBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE)
                    .strength(3600000.0F, 3600000.0F)
                    .noLootTable()
                    .noOcclusion()
                    .lightLevel((pProperites) -> {
                        return 13;
                    })));
    public static final RegistryObject<Block> GREEN_CHAOS_EMERALD = registerBlock("chaos_emerald/green_emerald",
            ()->new ChaosEmeraldBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN)
                    .strength(3600000.0F, 3600000.0F)
                    .noLootTable()
                    .noOcclusion()
                    .lightLevel((pProperites) -> {
                        return 13;
                    })));
    public static final RegistryObject<Block> GREY_CHAOS_EMERALD = registerBlock("chaos_emerald/grey_emerald",
            ()->new ChaosEmeraldBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(3600000.0F, 3600000.0F)
                    .noLootTable()
                    .noOcclusion()
                    .lightLevel((pProperites) -> {
                        return 13;
                    })));
    public static final RegistryObject<Block> PURPLE_CHAOS_EMERALD = registerBlock("chaos_emerald/purple_emerald",
            ()->new ChaosEmeraldBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE)
                    .strength(3600000.0F, 3600000.0F)
                    .noLootTable()
                    .noOcclusion()
                    .lightLevel((pProperites) -> {
                        return 13;
                    })));
    public static final RegistryObject<Block> RED_CHAOS_EMERALD = registerBlock("chaos_emerald/red_emerald",
            ()->new ChaosEmeraldBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED)
                    .strength(3600000.0F, 3600000.0F)
                    .noLootTable()
                    .noOcclusion()
                    .lightLevel((pProperites) -> {
                        return 13;
                    })));
    public static final RegistryObject<Block> YELLOW_CHAOS_EMERALD = registerBlock("chaos_emerald/yellow_emerald",
            ()->new ChaosEmeraldBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW)
                    .strength(3600000.0F, 3600000.0F)
                    .noLootTable()
                    .noOcclusion()
                    .lightLevel((pProperites) -> {
                        return 13;
                    })));
    public static final RegistryObject<Block> CHAOS_NETHER_PORTAL_BLOCK = BLOCKS.register("nether_portal_block",
            () -> new ChaosNetherPortalBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).noCollission()
                    .strength(-1.0F, 3600000.0F)
                    .lightLevel((pProperites) -> {
                        return 10;
                    })));

    public static final RegistryObject<Block> AQUA_SUPER_EMERALD = registerBlock("super_emerald/aqua_emerald",
            ()->new SuperEmeraldBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .strength(3600000.0F, 3600000.0F)
                    .noLootTable()
                    .noOcclusion()
                    .lightLevel((pProperites) -> {
                        return 14;
                    })));
    public static final RegistryObject<Block> BLUE_SUPER_EMERALD = registerBlock("super_emerald/blue_emerald",
            ()->new SuperEmeraldBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE)
                    .strength(3600000.0F, 3600000.0F)
                    .noLootTable()
                    .noOcclusion()
                    .lightLevel((pProperites) -> {
                        return 14;
                    })));
    public static final RegistryObject<Block> GREEN_SUPER_EMERALD = registerBlock("super_emerald/green_emerald",
            ()->new SuperEmeraldBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN)
                    .strength(3600000.0F, 3600000.0F)
                    .noLootTable()
                    .noOcclusion()
                    .lightLevel((pProperites) -> {
                        return 14;
                    })));
    public static final RegistryObject<Block> GREY_SUPER_EMERALD = registerBlock("super_emerald/grey_emerald",
            ()->new SuperEmeraldBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .strength(3600000.0F, 3600000.0F)
                    .noLootTable()
                    .noOcclusion()
                    .lightLevel((pProperites) -> {
                        return 14;
                    })));
    public static final RegistryObject<Block> PURPLE_SUPER_EMERALD = registerBlock("super_emerald/purple_emerald",
            ()->new SuperEmeraldBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE)
                    .strength(3600000.0F, 3600000.0F)
                    .noLootTable()
                    .noOcclusion()
                    .lightLevel((pProperites) -> {
                        return 14;
                    })));
    public static final RegistryObject<Block> RED_SUPER_EMERALD = registerBlock("super_emerald/red_emerald",
            ()->new SuperEmeraldBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED)
                    .strength(3600000.0F, 3600000.0F)
                    .noLootTable()
                    .noOcclusion()
                    .lightLevel((pProperites) -> {
                        return 14;
                    })));
    public static final RegistryObject<Block> YELLOW_SUPER_EMERALD = registerBlock("super_emerald/yellow_emerald",
            ()->new SuperEmeraldBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW)
                    .strength(3600000.0F, 3600000.0F)
                    .noLootTable()
                    .noOcclusion()
                    .lightLevel((pProperites) -> {
                        return 14;
                    })));
    public static final RegistryObject<Block> CHAOS_END_PORTAL_BLOCK = BLOCKS.register("end_portal_block",
            () -> new ChaosEndPortalBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).noCollission()
                    .strength(-1.0F, 3600000.0F)
                    .lightLevel((pProperites) -> {
                        return 10;
                    })));

    public static final RegistryObject<Block> MASTER_EMERALD = registerBlock("master_emerald",
            ()->new MasterEmeraldBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .strength(-1.0F, 3600000.0F)
                    .noLootTable()
                    .noOcclusion()
                    .lightLevel((pProperites) -> {
                        return 15;
                    })));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block)
    {
        RegistryObject<T> toReturn = BLOCKS.register(name,block);
        StringTokenizer sg = new StringTokenizer(name,"/");

        String token = sg.nextToken();
        switch(token)
        {
            case "chaos_emerald":   registerChaosBlockItem(name,toReturn); break;
            case "super_emerald":   registerSuperBlockItem(name,toReturn); break;
            case "master_emerald":  registerMasterBlockItem(name,toReturn); break;
            default:                registerBlockItem(name,toReturn); break;
        }

        return toReturn;
    }

    private static <T extends Block>RegistryObject<Item> registerChaosBlockItem(String name, RegistryObject<T> block)
    {return ModItems.ITEMS.register(name, () -> new ChaosBlockItem(block.get(),new Item.Properties().stacksTo(1).fireResistant()));}

    private static <T extends Block>RegistryObject<Item> registerSuperBlockItem(String name, RegistryObject<T> block)
    {return ModItems.ITEMS.register(name, () -> new SuperBlockItem(block.get(),new Item.Properties().stacksTo(1).fireResistant()));}

    private static <T extends Block>RegistryObject<Item> registerMasterBlockItem(String name, RegistryObject<T> block)
    {return ModItems.ITEMS.register(name, () -> new MasterBlockItem(block.get(),new Item.Properties().stacksTo(1).fireResistant()));}

    private static <T extends Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block)
    {return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),new Item.Properties()));}

    public static void register(IEventBus eventBus)
    {
        BLOCKS.register(eventBus);
    }

}
