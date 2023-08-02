package mrp_v2.versatileportals.common.capabilities;

import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber public class CapabilityHandler
{
    @CapabilityInject(IPortalDataCapability.class) public static final Capability<IPortalDataCapability>
            PORTAL_DATA_CAPABILITY = null;

    @SubscribeEvent public static void attachEntityCapabilities(final AttachCapabilitiesEvent<Entity> event)
    {
        event.addCapability(new ResourceLocation(VersatilePortals.ID, "portaldatacapability"),
                new PortalDataProvider(event));
    }

    public static void registerCapabilities()
    {
        CapabilityManager.INSTANCE.register(IPortalDataCapability.class, ObjectHolder.PORTAL_DATA_STORAGE,
                PortalDataHandler::new);
    }
}
