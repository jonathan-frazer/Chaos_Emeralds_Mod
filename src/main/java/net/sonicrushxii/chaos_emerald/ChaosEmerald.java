package net.sonicrushxii.chaos_emerald;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.SlotTypeMessage;
import net.sonicrushxii.chaos_emerald.client.VirtualSlotData;
import net.sonicrushxii.chaos_emerald.entities.all.PointRenderer;
import net.sonicrushxii.chaos_emerald.entities.aqua.ChaosBubbleModel;
import net.sonicrushxii.chaos_emerald.entities.aqua.SuperAquaRenderer;
import net.sonicrushxii.chaos_emerald.entities.false_super.ChaosSpazModel;
import net.sonicrushxii.chaos_emerald.entities.false_super.ChaosSpazRenderer;
import net.sonicrushxii.chaos_emerald.entities.form_hyper.SuperEmeraldModel;
import net.sonicrushxii.chaos_emerald.entities.form_hyper.SuperEmeraldRenderer;
import net.sonicrushxii.chaos_emerald.entities.form_super.*;
import net.sonicrushxii.chaos_emerald.entities.green.ChaosDivePlayerModel;
import net.sonicrushxii.chaos_emerald.entities.yellow.ChaosGambitPlayerModel;
import net.sonicrushxii.chaos_emerald.entities.yellow.SuperEmeraldRenderModel;
import net.sonicrushxii.chaos_emerald.event_handler.*;
import net.sonicrushxii.chaos_emerald.modded.*;
import net.sonicrushxii.chaos_emerald.network.PacketHandler;
import net.sonicrushxii.chaos_emerald.scheduler.Scheduler;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ChaosEmerald.MOD_ID)
public class ChaosEmerald
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "chaos_emerald";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void initializeMod(ChaosEmerald thisMod,FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(thisMod::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(thisMod);
        MinecraftForge.EVENT_BUS.register(new PlayerTickHandler());
        MinecraftForge.EVENT_BUS.register(new DamageHandler());
        MinecraftForge.EVENT_BUS.register(new LoginHandler());
        MinecraftForge.EVENT_BUS.register(new InteractionHandler());
        MinecraftForge.EVENT_BUS.register(new FallDamageHandler());
        MinecraftForge.EVENT_BUS.register(new ArmorRestrictionHandler());
        MinecraftForge.EVENT_BUS.register(new DeathEventHandler());
        MinecraftForge.EVENT_BUS.register(new Scheduler());
        MinecraftForge.EVENT_BUS.register(new CuriosBonusHandler());

        // Register the item to a creative tab
        modEventBus.addListener(thisMod::addCreative);
        modEventBus.addListener(thisMod::enqueueIMC);

        //Mod Stuff
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        ModEffects.register(modEventBus);
        ModSounds.register(modEventBus);
    }

    public ChaosEmerald(FMLJavaModLoadingContext context) {
        initializeMod(this,context);
    }

    public ChaosEmerald() {
        initializeMod(this,FMLJavaModLoadingContext.get());
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        event.enqueueWork(PacketHandler::register);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // Register the custom "gem" Curios slot so it appears in the Curios GUI.
        // Size 1 means one super emerald can be equipped at a time.
        // The data-driven JSON (data/chaos_emerald/curios/slots/gem.json) is the
        // primary declaration; this IMC call ensures Curios registers it even if
        // the data loader hasn't fired yet.
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> new SlotTypeMessage.Builder("gem")
                        .size(1)
                        .icon(new net.minecraft.resources.ResourceLocation(MOD_ID, "slot/gem"))
                        .build());
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            //Entity Setup
            //Chaos Emerald
            EntityRenderers.register(ModEntityTypes.CHAOS_EMERALD_ENTITY.get(), ChaosEmeraldRenderer::new);

            //Aqua Chaos Emerald
            EntityRenderers.register(ModEntityTypes.ICE_CHAOS_SPIKE.get(), PointRenderer::new);
            EntityRenderers.register(ModEntityTypes.ICE_SUPER_SPIKE.get(), PointRenderer::new);

            //Yellow Chaos Emerald
            EntityRenderers.register(ModEntityTypes.CHAOS_SPEAR.get(), PointRenderer::new);

            //False Super Form
            EntityRenderers.register(ModEntityTypes.FALSE_SUPER_CHAOS_SPAZ.get(), ChaosSpazRenderer::new);

            //Super Form
            EntityRenderers.register(ModEntityTypes.CHAOS_SPEAR_EX.get(), PointRenderer::new);
            EntityRenderers.register(ModEntityTypes.PORTAL_RING.get(), PortalRingRenderer::new);

            //Super Emerald
            EntityRenderers.register(ModEntityTypes.SUPER_EMERALD_ENTITY.get(), SuperEmeraldRenderer::new);
            EntityRenderers.register(ModEntityTypes.EMERALD_TRANSFORMER.get(), PointRenderer::new);

            //Aqua Super Emerald
            EntityRenderers.register(ModEntityTypes.AQUA_BOOST_BUBBLE.get(), SuperAquaRenderer::new);

            //Green Super Emerald
            EntityRenderers.register(ModEntityTypes.CHAOS_DIVE_RIPPLE.get(), PointRenderer::new);

            //Purple Super Emerald
            EntityRenderers.register(ModEntityTypes.CHAOS_SLICER.get(), PointRenderer::new);

            //Hyper Form
            EntityRenderers.register(ModEntityTypes.SUPER_CHAOS_SPEAR_EX.get(), PointRenderer::new);

            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }

        @SubscribeEvent
        public static void registerGUIOverlays(RegisterGuiOverlaysEvent event) {
            event.registerBelow(VanillaGuiOverlay.DEBUG_TEXT.id(),
                    "ability_hud",
                    VirtualSlotData.ABILITY_HUD);
        }

        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event){
            event.register(KeyBindings.INSTANCE.transformButton);
            event.register(KeyBindings.INSTANCE.useAbility1);
            event.register(KeyBindings.INSTANCE.useAbility2);
            event.register(KeyBindings.INSTANCE.useAbility3);
            event.register(KeyBindings.INSTANCE.useAbility4);
            event.register(KeyBindings.INSTANCE.useAbility5);
            event.register(KeyBindings.INSTANCE.useAbility6);
            event.register(KeyBindings.INSTANCE.doubleJump);
        }

        @SubscribeEvent
        public static void registerModelLayer(EntityRenderersEvent.RegisterLayerDefinitions event)
        {
            //Chaos Emerald
            event.registerLayerDefinition(ChaosEmeraldModel.LAYER_LOCATION,ChaosEmeraldModel::createBodyLayer);

            //Chaos Bubble
            event.registerLayerDefinition(ChaosBubbleModel.LAYER_LOCATION,ChaosBubbleModel::createBodyLayer);

            //Chaos Spaz
            event.registerLayerDefinition(ChaosSpazModel.LAYER_LOCATION,ChaosSpazModel::createBodyLayer);

            //Super Form
                //Flight Model
                event.registerLayerDefinition(SuperFormFlightModel.LAYER_LOCATION,SuperFormFlightModel::createBodyLayer);
                //Chaos Portal Ring Model
                event.registerLayerDefinition(PortalRingModel.LAYER_LOCATION,PortalRingModel::createBodyLayer);

            //Super Emerald
            event.registerLayerDefinition(SuperEmeraldModel.LAYER_LOCATION,SuperEmeraldModel::createBodyLayer);

            //Chaos Dive
            event.registerLayerDefinition(ChaosDivePlayerModel.LAYER_LOCATION,ChaosDivePlayerModel::createBodyLayer);

            //Chaos Gambit
                // Player Model
                event.registerLayerDefinition(ChaosGambitPlayerModel.LAYER_LOCATION,ChaosGambitPlayerModel::createBodyLayer);
                // Emerald Model
                event.registerLayerDefinition(SuperEmeraldRenderModel.LAYER_LOCATION,SuperEmeraldRenderModel::createBodyLayer);
        }
    }
}
