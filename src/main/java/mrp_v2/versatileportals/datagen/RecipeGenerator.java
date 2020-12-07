package mrp_v2.versatileportals.datagen;

import mrp_v2.mrplibrary.datagen.ConfigurableShapedRecipeBuilder;
import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

public class RecipeGenerator extends mrp_v2.mrplibrary.datagen.RecipeProvider
{
    public static final String HARDER_CRAFTING_ID = VersatilePortals.ID + ":harder_crafting";

    protected RecipeGenerator(DataGenerator dataGeneratorIn, String modId)
    {
        super(dataGeneratorIn, modId);
    }

    @Override protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
    {
        makePortalFrameRecipe(consumer);
        makePortalControllerRecipe(consumer);
        makeEmptyExistingWorldControlRecipe(consumer);
        makePortalLighterRecipe(consumer);
    }

    private void makePortalFrameRecipe(Consumer<IFinishedRecipe> consumer)
    {
        ConfigurableShapedRecipeBuilder recipeBuilder =
                ConfigurableShapedRecipeBuilder.configurableShapedRecipe(ObjectHolder.PORTAL_FRAME_BLOCK.get());
        recipeBuilder.patternLine("LLL");
        recipeBuilder.patternLine("LOL");
        recipeBuilder.patternLine("LLL");
        recipeBuilder.key('L', Tags.Items.GEMS_LAPIS);
        recipeBuilder.key('O', Tags.Items.OBSIDIAN);
        recipeBuilder.addCriterion("has_obsidian", hasItem(Tags.Items.OBSIDIAN));
        recipeBuilder.addCriterion("has_lapis", hasItem(Tags.Items.GEMS_LAPIS));
        recipeBuilder.addOverride(HARDER_CRAFTING_ID).override(Ingredient.fromTag(Tags.Items.GEMS_LAPIS),
                Ingredient.fromTag(Tags.Items.STORAGE_BLOCKS_LAPIS)).end();
        recipeBuilder.build(consumer);
    }

    private void makePortalControllerRecipe(Consumer<IFinishedRecipe> consumer)
    {
        ConfigurableShapedRecipeBuilder recipeBuilder =
                ConfigurableShapedRecipeBuilder.configurableShapedRecipe(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get());
        recipeBuilder.patternLine("FBF");
        recipeBuilder.patternLine("BRB");
        recipeBuilder.patternLine("FEF");
        recipeBuilder.key('F', ObjectHolder.PORTAL_FRAME_BLOCK.get());
        recipeBuilder.key('B', Tags.Items.BOOKSHELVES);
        recipeBuilder.key('R', Tags.Items.DUSTS_REDSTONE);
        recipeBuilder.key('E', Blocks.ENCHANTING_TABLE);
        recipeBuilder.addCriterion("has_portal_frame", hasItem(ObjectHolder.PORTAL_FRAME_BLOCK.get()));
        recipeBuilder.addOverride(HARDER_CRAFTING_ID).override(Ingredient.fromTag(Tags.Items.DUSTS_REDSTONE),
                Ingredient.fromTag(Tags.Items.STORAGE_BLOCKS_REDSTONE)).end();
        recipeBuilder.build(consumer);
    }

    private void makeEmptyExistingWorldControlRecipe(Consumer<IFinishedRecipe> consumer)
    {
        // normal recipe
        ShapelessRecipeBuilder recipeBuilder =
                ShapelessRecipeBuilder.shapelessRecipe(ObjectHolder.EMPTY_EXISTING_WORLD_TELEPORT_ITEM.get());
        recipeBuilder.addIngredient(Ingredient.fromTag(Tags.Items.GEMS_LAPIS));
        recipeBuilder.addIngredient(Items.CLOCK);
        recipeBuilder.addIngredient(Items.MAP);
        recipeBuilder.addIngredient(Items.COMPASS);
        recipeBuilder.addIngredient(Tags.Items.DUSTS_REDSTONE);
        recipeBuilder.addCriterion("has_portal_controller", hasItem(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get()));
        recipeBuilder.build(consumer);
        // recipe from existing world control
        recipeBuilder = ShapelessRecipeBuilder.shapelessRecipe(ObjectHolder.EMPTY_EXISTING_WORLD_TELEPORT_ITEM.get());
        recipeBuilder.addIngredient(ObjectHolder.EXISTING_WORLD_TELEPORT_ITEM.get());
        recipeBuilder.addCriterion("has_portal_controller", hasItem(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get()));
        recipeBuilder.build(consumer, new ResourceLocation(VersatilePortals.ID, "empty_control_from_existing"));
    }

    private void makePortalLighterRecipe(Consumer<IFinishedRecipe> consumer)
    {
        ShapelessRecipeBuilder recipeBuilder =
                ShapelessRecipeBuilder.shapelessRecipe(ObjectHolder.PORTAL_LIGHTER_ITEM.get());
        recipeBuilder.addIngredient(Items.FLINT_AND_STEEL);
        recipeBuilder.addIngredient(Tags.Items.GEMS_LAPIS);
        recipeBuilder.addCriterion("has_portal_controller", hasItem(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get()));
        recipeBuilder.build(consumer);
    }
}
