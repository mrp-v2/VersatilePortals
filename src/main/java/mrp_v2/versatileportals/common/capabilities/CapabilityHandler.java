package mrp_v2.versatileportals.common.capabilities;

import mrp_v2.versatileportals.VersatilePortals;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class CapabilityHandler {
    private static Capability<IPortalDataCapability> PORTAL_DATA_CAPABILITY = null;

    public static Capability<IPortalDataCapability> GetPortalDataCapability() {
        if (PORTAL_DATA_CAPABILITY == null) {
            PORTAL_DATA_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
            });
        }
        return PORTAL_DATA_CAPABILITY;
    }

    @SubscribeEvent
    public static void attachEntityCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        event.addCapability(new ResourceLocation(VersatilePortals.ID, "portaldatacapability"),
                new PortalDataProvider(event));
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) // might need to be registered on other event bus
    {
        event.register(IPortalDataCapability.class);
    }
}
