package mrp_v2.randomdimensions;

import mrp_v2.randomdimensions.common.capabilities.CapabilityHandler;
import mrp_v2.randomdimensions.network.Packet;
import mrp_v2.randomdimensions.util.RegistryHandler;
import mrp_v2.randomdimensions.world.util.WorldUtil;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.function.Supplier;

@Mod(RandomDimensions.ID) public class RandomDimensions
{

    public static final String ID = "random" + "dimensions";

    public static Supplier<World> WORLD_SUPPLIER;

    public RandomDimensions()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        WORLD_SUPPLIER = () -> null;
    }

    /**
     *
     */
    private void commonSetup(FMLCommonSetupEvent event)
    {
        Packet.Handler.createChannel();
        CapabilityHandler.registerCapabilities();
        RegistryHandler.postRegistering();
        WorldUtil.init();
    }
}
