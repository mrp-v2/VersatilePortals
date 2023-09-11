package mrp_v2.versatileportals.datagen;

import mrp_v2.mrplibrary.datagen.DataGeneratorHelper;
import mrp_v2.versatileportals.VersatilePortals;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VersatilePortals.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenHandler {
    @SubscribeEvent
    public static void gatherDataEvent(final GatherDataEvent event) {
        DataGeneratorHelper helper = new DataGeneratorHelper(event, VersatilePortals.ID);
        helper.addLootTables(new LootTableProvider.SubProviderEntry(LootTables::new, LootContextParamSets.BLOCK));
        helper.addRecipeProvider(RecipeGenerator::new);
        helper.addTextureProvider(TextureGenerator::new);
        helper.addParticleProvider(ParticleGenerator::new);
        helper.addBlockStateProvider(BlockStateGenerator::new);
        helper.addItemModelProvider(ItemModelGenerator::new);
        helper.addLanguageProvider(EN_USTranslationGenerator::new);
        helper.addBlockTagsProvider(BlockTagsGenerator::new);
    }
}
