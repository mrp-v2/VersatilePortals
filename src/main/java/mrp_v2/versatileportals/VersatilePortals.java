package mrp_v2.versatileportals;

import mrp_v2.versatileportals.common.capabilities.CapabilityHandler;
import mrp_v2.versatileportals.network.PacketHandler;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

@Mod(VersatilePortals.ID) public class VersatilePortals
{
    public static final String ID = "versatile" + "portals";
    public static final String DISPLAY_NAME = "Versatile Portals";
    public static final Logger LOGGER = LogManager.getLogger();
    public static Supplier<World> WORLD_SUPPLIER;

    public VersatilePortals()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        ObjectHolder.registerListeners(FMLJavaModLoadingContext.get().getModEventBus());
        WORLD_SUPPLIER = () -> null;
    }

    private void commonSetup(FMLCommonSetupEvent event)
    {
        PacketHandler.createChannel();
        CapabilityHandler.registerCapabilities();
    }
}
