package mrp_v2.randomdimensions;

import mrp_v2.randomdimensions.common.capabilities.CapabilityHandler;
import mrp_v2.randomdimensions.network.Packet;
import mrp_v2.randomdimensions.world.util.WorldUtil;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RandomDimensions.ID)
public class RandomDimensions
{

    public static final String ID = "random" + "dimensions";

    public RandomDimensions()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    /**
     * @param event
     */
    private void commonSetup(FMLCommonSetupEvent event)
    {
        Packet.Handler.createChannel();
        CapabilityHandler.registerCapabilities();
        WorldUtil.init();
    }
}
