package mrp_v2.randomdimensions.client.util;

import mrp_v2.randomdimensions.RandomDimensions;
import mrp_v2.randomdimensions.client.gui.screen.PortalControllerScreen;
import mrp_v2.randomdimensions.client.particle.PortalParticle;
import mrp_v2.randomdimensions.client.renderer.color.PortalColorer;
import mrp_v2.randomdimensions.util.ObjectHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD, modid = RandomDimensions.ID)
public class RegistryHandler
{

    static
    {
        ScreenManager.registerFactory(ObjectHolder.PORTAL_CONTROLLER_CONTAINER_TYPE, PortalControllerScreen::new);
    }

    @SubscribeEvent public static void registerBlockColors(final ColorHandlerEvent.Block event)
    {
        event.getBlockColors()
             .register(PortalColorer.INSTANCE, ObjectHolder.PORTAL_BLOCK, ObjectHolder.INDESTRUCTIBLE_PORTAL_BLOCK,
                     ObjectHolder.PORTAL_FRAME_BLOCK, ObjectHolder.PORTAL_CONTROLLER_BLOCK,
                     ObjectHolder.INDESTRUCTIBLE_PORTAL_FRAME_BLOCK);
    }

    @SuppressWarnings("resource") @SubscribeEvent
    public static void registerParticles(final ParticleFactoryRegisterEvent event)
    {
        Minecraft.getInstance().particles.registerFactory(ObjectHolder.PORTAL_PARTICLE_TYPE,
                PortalParticle.Factory::new);
    }
}
