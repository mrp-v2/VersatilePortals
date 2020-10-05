package mrp_v2.customteleporters;

import mrp_v2.customteleporters.client.util.RegistryHandler;
import mrp_v2.customteleporters.common.capabilities.CapabilityHandler;
import mrp_v2.customteleporters.network.Packet;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Supplier;

@Mod(CustomTeleporters.ID) public class CustomTeleporters
{
    public static final String ID = "custom" + "teleporters";
    public static Supplier<World> WORLD_SUPPLIER;
    public static String DISPLAY_NAME = "Custom Teleporters";

    static
    {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> RegistryHandler::init);
    }

    public CustomTeleporters()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        WORLD_SUPPLIER = () -> null;
    }

    private void commonSetup(FMLCommonSetupEvent event)
    {
        Packet.Handler.createChannel();
        CapabilityHandler.registerCapabilities();
    }
}
