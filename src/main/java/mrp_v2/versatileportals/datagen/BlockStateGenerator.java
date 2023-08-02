package mrp_v2.versatileportals.datagen;

import mrp_v2.mrplibrary.datagen.providers.BlockStateProvider;
import mrp_v2.versatileportals.block.PortalBlock;
import mrp_v2.versatileportals.block.PortalControllerBlock;
import mrp_v2.versatileportals.block.PortalFrameBlock;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import net.minecraftforge.client.model.generators.ModelBuilder.ElementBuilder;

public class BlockStateGenerator extends BlockStateProvider
{
    public BlockStateGenerator(DataGenerator gen, String modid, ExistingFileHelper exFileHelper)
    {
        super(gen, modid, exFileHelper);
    }

    @Override protected void registerStatesAndModels()
    {
        registerPortalFrame();
        registerPortalController();
        registerPortal();
    }

    private void registerPortal()
    {
        ModelBuilder<BlockModelBuilder> modelBuilder =
                models().getBuilder("block/" + PortalBlock.ID).texture("portal", "block/" + PortalBlock.ID)
                        .texture("particle", "block/" + PortalBlock.ID);
        ModelBuilder<BlockModelBuilder>.ElementBuilder elementBuilder =
                modelBuilder.element().from(6, 0, 0).to(10, 16, 16);
        ModelBuilder<BlockModelBuilder> flatModelBuilder =
                models().getBuilder("block/" + PortalBlock.ID + "_flat").texture("portal", "block/" + PortalBlock.ID)
                        .texture("particle", "block/" + PortalBlock.ID);
        ModelBuilder<BlockModelBuilder>.ElementBuilder flatElementBuilder =
                flatModelBuilder.element().from(0, 6, 0).to(16, 10, 16);
        noCullTintedFace(elementBuilder, Direction.EAST);
        noCullTintedFace(elementBuilder, Direction.WEST);
        noCullTintedFace(flatElementBuilder, Direction.UP);
        noCullTintedFace(flatElementBuilder, Direction.DOWN);
        elementBuilder.texture("#portal");
        flatElementBuilder.texture("#portal");
        getVariantBuilder(ObjectHolder.PORTAL_BLOCK.get()).partialState()
                .with(BlockStateProperties.AXIS, Direction.Axis.X).modelForState().modelFile(modelBuilder).addModel()
                .partialState().with(BlockStateProperties.AXIS, Direction.Axis.Z).modelForState()
                .modelFile(modelBuilder).rotationY(90).addModel().partialState()
                .with(BlockStateProperties.AXIS, Direction.Axis.Y).modelForState().modelFile(flatModelBuilder)
                .addModel();
    }

    private void registerPortalFrame()
    {
        simpleBlock(ObjectHolder.PORTAL_FRAME_BLOCK.get(),
                models().withExistingParent("block/" + PortalFrameBlock.ID, "block")
                        .texture("frame", "block/" + PortalFrameBlock.ID)
                        .texture("particle", "block/" + PortalFrameBlock.ID).element().cube("#frame")
                        .allFaces((dir, face) -> face.tintindex(0)).end());
    }

    private void registerPortalController()
    {
        ModelBuilder<BlockModelBuilder> modelBuilder =
                models().withExistingParent("block/" + PortalControllerBlock.ID, "block");
        modelBuilder.ao(false);
        modelBuilder.texture("base", "block/" + PortalFrameBlock.ID);
        modelBuilder.texture("end", "block/" + PortalControllerBlock.ID);
        modelBuilder.texture("particle", "block/" + PortalFrameBlock.ID);
        ModelBuilder<BlockModelBuilder>.ElementBuilder elementBuilder =
                modelBuilder.element().from(0, 0, 0).to(16, 16, 16);
        sameCullTintedFace(elementBuilder, Direction.UP);
        sameCullTintedFace(elementBuilder, Direction.DOWN);
        sameCullTintedFace(elementBuilder, Direction.EAST);
        sameCullTintedFace(elementBuilder, Direction.WEST);
        elementBuilder = elementBuilder.texture("#base").end().element().from(0, 3, 0).to(3, 13, 16);
        sameCullTintedFace(elementBuilder, Direction.NORTH);
        sameCullTintedFace(elementBuilder, Direction.SOUTH);
        noCullTintedFace(elementBuilder, Direction.EAST);
        elementBuilder = elementBuilder.texture("#end").end().element().from(13, 3, 0).to(16, 13, 16);
        sameCullTintedFace(elementBuilder, Direction.NORTH);
        sameCullTintedFace(elementBuilder, Direction.SOUTH);
        noCullTintedFace(elementBuilder, Direction.WEST);
        elementBuilder = elementBuilder.texture("#end").end().element().from(3, 13, 0).to(13, 16, 16);
        sameCullTintedFace(elementBuilder, Direction.NORTH);
        sameCullTintedFace(elementBuilder, Direction.SOUTH);
        noCullTintedFace(elementBuilder, Direction.DOWN);
        elementBuilder = elementBuilder.texture("#end").end().element().from(3, 0, 0).to(13, 3, 16);
        sameCullTintedFace(elementBuilder, Direction.NORTH);
        sameCullTintedFace(elementBuilder, Direction.SOUTH);
        noCullTintedFace(elementBuilder, Direction.UP);
        elementBuilder = elementBuilder.texture("#end").end().element().from(0, 0, 0).to(3, 3, 16);
        sameCullTintedFace(elementBuilder, Direction.NORTH);
        sameCullTintedFace(elementBuilder, Direction.SOUTH);
        elementBuilder = elementBuilder.texture("#end").end().element().from(0, 13, 0).to(3, 16, 16);
        sameCullTintedFace(elementBuilder, Direction.NORTH);
        sameCullTintedFace(elementBuilder, Direction.SOUTH);
        elementBuilder = elementBuilder.texture("#end").end().element().from(13, 0, 0).to(16, 3, 16);
        sameCullTintedFace(elementBuilder, Direction.NORTH);
        sameCullTintedFace(elementBuilder, Direction.SOUTH);
        elementBuilder = elementBuilder.texture("#end").end().element().from(13, 13, 0).to(16, 16, 16);
        sameCullTintedFace(elementBuilder, Direction.NORTH);
        sameCullTintedFace(elementBuilder, Direction.SOUTH);
        elementBuilder.texture("#end").end();
        axisBlock(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get(), modelBuilder, false);
    }

    private <T extends ModelBuilder<T>> ModelBuilder<T>.ElementBuilder noCullTintedFace(
            ModelBuilder<T>.ElementBuilder builder, Direction face)
    {
        return builder.face(face).tintindex(0).end();
    }

    private void axisBlock(Block block, ModelFile model, boolean defaultStateIsX)
    {
        getVariantBuilder(block).partialState().with(BlockStateProperties.AXIS, Direction.Axis.X).modelForState()
                .modelFile(model).rotationY(defaultStateIsX ? 0 : 90).addModel().partialState()
                .with(BlockStateProperties.AXIS, Direction.Axis.Z).modelForState().modelFile(model)
                .rotationY(defaultStateIsX ? 90 : 0).addModel().partialState()
                .with(BlockStateProperties.AXIS, Direction.Axis.Y).modelForState().modelFile(model).rotationX(90)
                .addModel();
    }

    private <T extends ModelBuilder<T>> ModelBuilder<T>.ElementBuilder sameCullTintedFace(
            ModelBuilder<T>.ElementBuilder builder, Direction face)
    {
        return tintedFace(builder, face, face);
    }

    private <T extends ModelBuilder<T>> ModelBuilder<T>.ElementBuilder tintedFace(
            ModelBuilder<T>.ElementBuilder builder, Direction face, Direction cullface)
    {
        return builder.face(face).cullface(cullface).tintindex(0).end();
    }
}

