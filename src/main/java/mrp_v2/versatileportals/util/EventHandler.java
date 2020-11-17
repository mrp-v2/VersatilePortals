package mrp_v2.versatileportals.util;

import mrp_v2.configurablerecipeslibrary.item.crafting.ConfigurableCraftingRecipe;
import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.config.ServerConfig;
import mrp_v2.versatileportals.datagen.RecipeGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = VersatilePortals.ID, bus = Mod.EventBusSubscriber.Bus.MOD) public class EventHandler
{
    @SubscribeEvent public static void setup(FMLCommonSetupEvent event)
    {
        ConfigurableCraftingRecipe.addConditionMapping(RecipeGenerator.HARDER_CRAFTING_ID,
                ServerConfig.SERVER.harderCrafting::get);
    }
}
