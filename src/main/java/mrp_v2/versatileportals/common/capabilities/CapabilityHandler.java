package mrp_v2.versatileportals.common.capabilities;

import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.world.EventHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@EventBusSubscriber
public class CapabilityHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static Capability<IPortalDataCapability> PORTAL_DATA_CAPABILITY = null;

    public static Capability<IPortalDataCapability> GetPortalDataCapability() {
        if (PORTAL_DATA_CAPABILITY == null) {
            PORTAL_DATA_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
            });
        }
        if (!PORTAL_DATA_CAPABILITY.isRegistered()) {
            LOGGER.debug("Portal Data Capability was retrieved but it isn't registered!");
        }
        return PORTAL_DATA_CAPABILITY;
    }

    @SubscribeEvent
    public static void attachEntityCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        event.addCapability(new ResourceLocation(VersatilePortals.ID, "portaldatacapability"),
                new PortalDataProvider(event));
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(IPortalDataCapability.class);
    }
}
