package mrp_v2.versatileportals.client.util;

import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.client.gui.screen.ControlItemEditorScreen;
import mrp_v2.versatileportals.client.gui.screen.PortalControllerScreen;
import mrp_v2.versatileportals.client.particle.PortalControllerParticle;
import mrp_v2.versatileportals.client.particle.PortalParticle;
import mrp_v2.versatileportals.client.renderer.color.ExistingWorldControlItemColorer;
import mrp_v2.versatileportals.client.renderer.color.PortalColorer;
import mrp_v2.versatileportals.client.renderer.tileentity.PortalControllerBlockEntityRenderer;
import mrp_v2.versatileportals.datagen.EN_USTranslationGenerator;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD, modid = VersatilePortals.ID)
public class EventHandler {

    private static CreativeModeTab versatilePortalsTab;

    @SubscribeEvent
    public static void clientSetup(final FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(ObjectHolder.PORTAL_BLOCK.get(), RenderType.translucent());
        VersatilePortals.WORLD_SUPPLIER = () -> Minecraft.getInstance().level;
        BlockEntityRenderers.register(ObjectHolder.PORTAL_CONTROLLER_TILE_ENTITY_TYPE.get(), PortalControllerBlockEntityRenderer::new);
        MenuScreens.register(ObjectHolder.PORTAL_CONTROLLER_CONTAINER_TYPE.get(), PortalControllerScreen::new);
    }

    @SubscribeEvent
    public static void registerBlockColors(final RegisterColorHandlersEvent.Block event) {
        event.getBlockColors()
                .register(PortalColorer.INSTANCE, ObjectHolder.PORTAL_BLOCK.get(),
                        ObjectHolder.PORTAL_FRAME_BLOCK.get(), ObjectHolder.PORTAL_CONTROLLER_BLOCK.get());
    }

    @SubscribeEvent
    public static void registerItemColors(final RegisterColorHandlersEvent.Item event) {
        event.getItemColors()
                .register(ExistingWorldControlItemColorer.INSTANCE, ObjectHolder.EXISTING_WORLD_TELEPORT_ITEM.get());
    }

    @SubscribeEvent
    public static void registerParticles(final RegisterParticleProvidersEvent event) {
        ParticleEngine particleManager = Minecraft.getInstance().particleEngine;
        particleManager.register(ObjectHolder.PORTAL_PARTICLE_TYPE.get(), PortalParticle.Factory::new);
        particleManager
                .register(ObjectHolder.PORTAL_CONTROLLER_PARTICLE_TYPE.get(), PortalControllerParticle.Factory::new);
    }

    @SubscribeEvent
    public static void addTab(CreativeModeTabEvent.Register e) {
        versatilePortalsTab = e.registerCreativeModeTab(new ResourceLocation(VersatilePortals.ID, VersatilePortals.ID), builder -> builder.title(EN_USTranslationGenerator.VERSATILE_PORTAL_CREATIVE_TAB_LABEL).icon(() -> new ItemStack(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get())).title(EN_USTranslationGenerator.VERSATILE_PORTAL_CREATIVE_TAB_LABEL));
    }

    @SubscribeEvent
    public static void addItemsToTabs(CreativeModeTabEvent.BuildContents e) {
        if (e.getTab() == versatilePortalsTab) {
            e.accept(ObjectHolder.EMPTY_EXISTING_WORLD_TELEPORT_ITEM);
            e.accept(ObjectHolder.PORTAL_LIGHTER_ITEM);
            e.accept(ObjectHolder.PORTAL_CONTROLLER_BLOCK_ITEM);
            e.accept(ObjectHolder.PORTAL_FRAME_BLOCK_ITEM);
        }
    }

    public static void OpenControlItemEditorScreen(int initialColor, Player player, InteractionHand hand) {
        Minecraft.getInstance().setScreen(new ControlItemEditorScreen(initialColor, player, hand));
    }
}
