package mrp_v2.versatileportals.network;

import mrp_v2.versatileportals.VersatilePortals;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {
    public static final String ID = "portal_color";
    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE;
    private static int id = 0;

    public static void createChannel() {
        INSTANCE =
                NetworkRegistry.newSimpleChannel(new ResourceLocation(VersatilePortals.ID, ID), () -> PROTOCOL_VERSION,
                        PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
        INSTANCE.registerMessage(id++, PortalControllerScreenClosedPacket.class,
                PortalControllerScreenClosedPacket::encode, PortalControllerScreenClosedPacket::new,
                PortalControllerScreenClosedPacket::handle);
        INSTANCE.registerMessage(id++, PortalFrameUpdatePacket.class, PortalFrameUpdatePacket::encode,
                PortalFrameUpdatePacket::new, PortalFrameUpdatePacket::handle);
        INSTANCE.registerMessage(id++, ControlItemEditedPacket.class, ControlItemEditedPacket::encode, ControlItemEditedPacket::new,
                ControlItemEditedPacket::handle);
    }
}
