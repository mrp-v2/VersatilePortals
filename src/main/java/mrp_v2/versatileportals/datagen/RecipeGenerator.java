package mrp_v2.versatileportals.datagen;

import mrp_v2.mrplibrary.datagen.providers.RecipeProvider;
import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class RecipeGenerator extends RecipeProvider
{
    protected RecipeGenerator(DataGenerator dataGeneratorIn, String modId)
    {
        super(dataGeneratorIn, modId);
    }

    @Override protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer)
    {
        makePortalFrameRecipe(consumer);
        makePortalControllerRecipe(consumer);
        makeEmptyExistingWorldControlRecipe(consumer);
        makePortalLighterRecipe(consumer);
    }

    private void makePortalFrameRecipe(Consumer<IFinishedRecipe> consumer)
    {
        ShapedRecipeBuilder recipeBuilder = ShapedRecipeBuilder.shaped(ObjectHolder.PORTAL_FRAME_BLOCK.get());
        recipeBuilder.pattern("LLL");
        recipeBuilder.pattern("LOL");
        recipeBuilder.pattern("LLL");
        recipeBuilder.define('L', Tags.Items.GEMS_LAPIS);
        recipeBuilder.define('O', Tags.Items.OBSIDIAN);
        recipeBuilder.unlockedBy("has_obsidian", has(Tags.Items.OBSIDIAN));
        recipeBuilder.unlockedBy("has_lapis", has(Tags.Items.GEMS_LAPIS));
        recipeBuilder.save(consumer);
    }

    private void makePortalControllerRecipe(Consumer<IFinishedRecipe> consumer)
    {
        ShapedRecipeBuilder recipeBuilder = ShapedRecipeBuilder.shaped(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get());
        recipeBuilder.pattern("FBF");
        recipeBuilder.pattern("BRB");
        recipeBuilder.pattern("FEF");
        recipeBuilder.define('F', ObjectHolder.PORTAL_FRAME_BLOCK.get());
        recipeBuilder.define('B', Tags.Items.BOOKSHELVES);
        recipeBuilder.define('R', Tags.Items.DUSTS_REDSTONE);
        recipeBuilder.define('E', Blocks.ENCHANTING_TABLE);
        recipeBuilder.unlockedBy("has_portal_frame", has(ObjectHolder.PORTAL_FRAME_BLOCK.get()));
        recipeBuilder.save(consumer);
    }

    private void makeEmptyExistingWorldControlRecipe(Consumer<IFinishedRecipe> consumer)
    {
        // normal recipe
        ShapelessRecipeBuilder recipeBuilder =
                ShapelessRecipeBuilder.shapeless(ObjectHolder.EMPTY_EXISTING_WORLD_TELEPORT_ITEM.get());
        recipeBuilder.requires(Ingredient.of(Tags.Items.GEMS_LAPIS));
        recipeBuilder.requires(Items.CLOCK);
        recipeBuilder.requires(Items.MAP);
        recipeBuilder.requires(Items.COMPASS);
        recipeBuilder.requires(Tags.Items.DUSTS_REDSTONE);
        recipeBuilder.unlockedBy("has_portal_controller", has(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get()));
        recipeBuilder.save(consumer);
        // recipe from existing world control
        recipeBuilder = ShapelessRecipeBuilder.shapeless(ObjectHolder.EMPTY_EXISTING_WORLD_TELEPORT_ITEM.get());
        recipeBuilder.requires(ObjectHolder.EXISTING_WORLD_TELEPORT_ITEM.get());
        recipeBuilder.unlockedBy("has_portal_controller", has(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get()));
        recipeBuilder.save(consumer, new ResourceLocation(VersatilePortals.ID, "empty_control_from_existing"));
    }

    private void makePortalLighterRecipe(Consumer<IFinishedRecipe> consumer)
    {
        ShapelessRecipeBuilder recipeBuilder = ShapelessRecipeBuilder.shapeless(ObjectHolder.PORTAL_LIGHTER_ITEM.get());
        recipeBuilder.requires(Items.FLINT_AND_STEEL);
        recipeBuilder.requires(Tags.Items.GEMS_LAPIS);
        recipeBuilder.unlockedBy("has_portal_controller", has(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get()));
        recipeBuilder.save(consumer);
    }
}
