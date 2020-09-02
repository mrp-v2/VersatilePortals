package mrp_v2.randomdimensions.util;

import mrp_v2.configurablerecipeslibrary.item.crafting.ConfigurableCraftingRecipe;
import mrp_v2.randomdimensions.RandomDimensions;
import mrp_v2.randomdimensions.config.ServerConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = RandomDimensions.ID, bus = Mod.EventBusSubscriber.Bus.MOD) public class EventHandler
{
    @SubscribeEvent public static void setup(FMLCommonSetupEvent event)
    {
        ConfigurableCraftingRecipe.addConditionMapping(RandomDimensions.ID + ":harder_crafting",
                ServerConfig.SERVER.harderCrafting::get);
    }
}
