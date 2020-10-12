package mrp_v2.versatileportals.datagen;

import mrp_v2.mrp_v2datagenlibrary.datagen.DataGeneratorHelper;
import mrp_v2.mrp_v2datagenlibrary.datagen.LootTables;
import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = VersatilePortals.ID, bus = Mod.EventBusSubscriber.Bus.MOD) public class DataGenHandler
{
    @SubscribeEvent public static void gatherDataEvent(final GatherDataEvent event)
    {
        DataGeneratorHelper helper = new DataGeneratorHelper(event, VersatilePortals.ID);
        if (event.includeServer())
        {
            LootTables lootTables = new LootTables();
            lootTables.addLootTable(ObjectHolder.PORTAL_FRAME_BLOCK, lootTables::registerDropSelfLootTable);
            lootTables.addLootTable(ObjectHolder.PORTAL_CONTROLLER_BLOCK, lootTables::registerDropSelfLootTable);
            helper.addLootTables(lootTables);
            helper.addRecipeGenerator(RecipeGenerator::new);
        }
        if (event.includeClient())
        {
            helper.addBlockStateProvider(BlockStateGenerator::new);
        }
    }
}
