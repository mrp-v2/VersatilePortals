package mrp_v2.versatileportals.util;

import mrp_v2.configurablerecipeslibrary.item.crafting.ConfigurableCraftingRecipe;
import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.config.ServerConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = VersatilePortals.ID, bus = Mod.EventBusSubscriber.Bus.MOD) public class EventHandler
{
    @SubscribeEvent public static void setup(FMLCommonSetupEvent event)
    {
        ConfigurableCraftingRecipe.addConditionMapping(VersatilePortals.ID + ":harder_crafting",
                ServerConfig.SERVER.harderCrafting::get);
    }
}
