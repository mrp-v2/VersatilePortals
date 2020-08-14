package mrp_v2.randomdimensions.world.util;

import com.google.common.collect.Lists;
import mrp_v2.randomdimensions.util.Util;
import net.minecraft.block.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.NoteBlockInstrument;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.IPlantable;
import org.apache.logging.log4j.LogManager;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class WorldUtil
{
    private static final List<Block> invalidBlocks = Lists.newArrayList();
    private static final List<PropertyParameter<?>> propertyParameters = Lists.newArrayList();
    private static final List<Class> invalidBlockSupertypes = Lists.newArrayList(
            IPlantable.class,
            AbstractPlantBlock.class,
            AbstractButtonBlock.class,
            AbstractCoralPlantBlock.class,
            AbstractRailBlock.class,
            AirBlock.class,
            AnvilBlock.class,
            BambooBlock.class,
            BambooSaplingBlock.class,
            CarpetBlock.class,
            ChainBlock.class,
            ChorusFlowerBlock.class,
            ChorusPlantBlock.class,
            CocoaBlock.class,
            DoorBlock.class,
            DragonEggBlock.class,
            EndPortalFrameBlock.class,
            EndRodBlock.class,
            FarmlandBlock.class,
            FenceBlock.class,
            FenceGateBlock.class,
            FireBlock.class,
            FlowerPotBlock.class,
            FrostedIceBlock.class,
            LadderBlock.class,
            LanternBlock.class,
            LeverBlock.class,
            NetherPortalBlock.class,
            ObserverBlock.class,
            PaneBlock.class,
            PistonHeadBlock.class,
            PressurePlateBlock.class,
            WallBlock.class,
            WebBlock.class,
            WeightedPressurePlateBlock.class,
            RedstoneDiodeBlock.class,
            RedstoneTorchBlock.class,
            RedstoneWireBlock.class,
            SnowBlock.class,
            SoulFireBlock.class,
            StructureVoidBlock.class,
            TorchBlock.class,
            TripWireBlock.class,
            TripWireHookBlock.class,
            TurtleEggBlock.class,
            VineBlock.class
    );
    private static final List<Function<Block, Boolean>> blockValidityCheckers = Lists.newArrayList(
            block ->
            {
                if (invalidBlocks.contains(block))
                {
                    return false;
                }
                for (Class c : invalidBlockSupertypes)
                {
                    if (c.isInstance(block))
                    {
                        return false;
                    }
                }
                return true;
            }
    );
    private static final List<Function<BlockState, Boolean>> blockStateValidityCheckers = Lists.newArrayList(
            blockState -> !blockState.hasTileEntity(),
            blockState -> blockState.getFluidState().isEmpty(),
            blockState ->
            {
                Block block = blockState.getBlock();
                for (Function<Block, Boolean> blockValidityFunction : blockValidityCheckers)
                {
                    if (!blockValidityFunction.apply(block))
                    {
                        return false;
                    }
                }
                return true;
            },
            blockState ->
            {
                for (PropertyParameter propertyParameter : propertyParameters)
                {
                    if (propertyParameter.isInvalid(blockState))
                    {
                        return false;
                    }
                }
                return true;
            }
    );
    private static List<BlockState> validWorldGenerationBlockStates;

    public static void init()
    {
        if (validWorldGenerationBlockStates != null)
        {
            LogManager.getLogger().warn("Tried to init but init has already happened! This is probably a bug.");
        } else
        {
            invalidBlocks.addAll(Lists.newArrayList(
                    Blocks.BARRIER,
                    Blocks.BEDROCK
            ));
            propertyParameters.addAll(Lists.newArrayList(
                    new PropertyParameter.ValuesOrMissing<>(BlockStateProperties.PERSISTENT, true, true),
                    new PropertyParameter.ValuesOrMissing<>(BlockStateProperties.OPEN, true, false),
                    new PropertyParameter.ValuesOrMissing<>(BlockStateProperties.POWERED, true, false),
                    new PropertyParameter.ValuesOrMissing<>(BlockStateProperties.DISTANCE_1_7, true, 7),
                    new PropertyParameter.ValuesOrMissing<>(BlockStateProperties.BITES_0_6,
                            Util.makeArray(Blocks.CAKE),
                            true,
                            0),
                    new PropertyParameter.ValuesOrMissing<>(BlockStateProperties.NOTE_BLOCK_INSTRUMENT,
                            Util.makeArray(Blocks.NOTE_BLOCK),
                            true,
                            NoteBlockInstrument.HARP),
                    new PropertyParameter.ValuesOrMissing<>(BlockStateProperties.EXTENDED,
                            Util.makeArray(Blocks.PISTON, Blocks.STICKY_PISTON),
                            true,
                            false),
                    new PropertyParameter.ValuesOrMissing<>(BlockStateProperties.LIT, true, false),
                    new PropertyParameter.ValuesOrMissing<>(BlockStateProperties.BOTTOM,
                            Util.makeArray(Blocks.SCAFFOLDING),
                            true,
                            false), new PropertyParameter.ValuesOrMissing<>(BlockStateProperties.POWER_0_15,
                            Util.makeArray(Blocks.TARGET), true, 0),
                    new PropertyParameter.ValuesOrMissing<>(BlockStateProperties.UNSTABLE, Util.makeArray(Blocks.TNT),
                            true, false)));
        }
        validWorldGenerationBlockStates = StreamSupport.stream(Registry.BLOCK.spliterator(), false)
                                                       .flatMap((block) -> block.getStateContainer()
                                                                                .getValidStates()
                                                                                .stream())
                                                       .filter(WorldUtil::isValidForWorldGeneration)
                                                       .collect(Collectors.toList());
        List<String> strings = Lists.newArrayList();
        validWorldGenerationBlockStates.forEach((blockState -> strings.add("\n" + blockState.toString())));
        strings.sort(String::compareToIgnoreCase);
        StringBuilder stringBuilder = new StringBuilder(strings.size() + " valid world generation blocks found: ");
        strings.forEach(stringBuilder::append);
        LogManager.getLogger().debug(stringBuilder.toString());
    }

    private static boolean isValidForWorldGeneration(BlockState blockState)
    {
        for (Function<BlockState, Boolean> validityChecker : blockStateValidityCheckers)
        {
            if (!validityChecker.apply(blockState))
            {
                return false;
            }
        }
        return true;
    }

    public static List<BlockState> getValidWorldGenerationBlockStates()
    {
        return validWorldGenerationBlockStates;
    }

    /**
     * Adds blocks to the list of blocks that won't be used to generate worlds.
     */
    public static void addInvalidBlocks(Block... invalidBlocks)
    {
        WorldUtil.invalidBlocks.addAll(Arrays.asList(invalidBlocks));
    }

    /**
     * Adds classes to the list of classes that blocks cannot be an instance of to be used to generate worlds.
     */
    public static void addInvalidBlockSupertypes(Class... invalidBlockSupertypes)
    {
        WorldUtil.invalidBlockSupertypes.addAll(Arrays.asList(invalidBlockSupertypes));
    }

    /**
     * Adds functions that decide whether a block is suitable for world generation.
     */
    public static void addBlockValidityCheckers(Function<Block, Boolean>... blockValidityCheckers)
    {
        WorldUtil.blockValidityCheckers.addAll(Arrays.asList(blockValidityCheckers));
    }

    /**
     * Adds functions that decide whether a <c>BlockState</c> is suitable for world generation.
     */
    public static void addBlockStateValidityCheckers(Function<BlockState, Boolean>... blockStateValidityCheckers)
    {
        WorldUtil.blockStateValidityCheckers.addAll(Arrays.asList(blockStateValidityCheckers));
    }
}
