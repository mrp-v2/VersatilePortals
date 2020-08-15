package mrp_v2.randomdimensions.common.capabilities;

import mrp_v2.randomdimensions.RandomDimensions;
import mrp_v2.randomdimensions.util.ObjectHolder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
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
        event.addCapability(new ResourceLocation(RandomDimensions.ID, "portaldatacapability"),
                new PortalDataProvider(event));
    }

    public static void registerCapabilities()
    {
        CapabilityManager.INSTANCE.register(IPortalDataCapability.class, ObjectHolder.PORTAL_DATA_STORAGE,
                PortalDataHandler::new);
    }
}
