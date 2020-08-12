package mrp_v2.randomdimensions.block;

import mrp_v2.randomdimensions.inventory.PortalControllerItemStackHandler;
import mrp_v2.randomdimensions.particles.PortalControllerParticleData;
import mrp_v2.randomdimensions.tileentity.PortalControllerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Random;

public class PortalControllerBlock extends PortalFrameBlock
{
    public static final String ID = "portal_controller";
    private static final VoxelShape SHAPE = VoxelShapes.or(makeCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D),
            makeCuboidShape(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 3.0, 16.0D),
            makeCuboidShape(3.0D, 13.0D, 0.0D, 13.0D, 16.0D, 16.0D)).simplify();

    public PortalControllerBlock()
    {
        super(ID, Properties::notSolid);
        this.setDefaultState(
                this.stateContainer.getBaseState().with(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.X));
    }

    @Override public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Override public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new PortalControllerTileEntity();
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        super.onReplaced(state, worldIn, pos, newState, isMoving);
        if (!state.isIn(newState.getBlock()))
        {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof PortalControllerTileEntity)
            {
                LazyOptional<IItemHandler> itemHandler =
                        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
                if (itemHandler.isPresent())
                {
                    InventoryHelper.dropInventoryItems(worldIn, pos,
                            (PortalControllerItemStackHandler) itemHandler.orElse(
                                    new PortalControllerItemStackHandler(null)));
                }
            }
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
            Hand handIn, BlockRayTraceResult hit)
    {
        PortalControllerTileEntity portalController = (PortalControllerTileEntity) worldIn.getTileEntity(pos);
        if (portalController != null)
        {
            if (player instanceof ServerPlayerEntity)
            {
                NetworkHooks.openGui((ServerPlayerEntity) player, portalController, (buffer) ->
                {
                    buffer.writeInt(portalController.getPortalColor());
                    buffer.writeBlockPos(pos);
                });
            }
            return ActionResultType.func_233537_a_(worldIn.isRemote);
        }
        return ActionResultType.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    @Override @OnlyIn(Dist.CLIENT) public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        PortalControllerTileEntity controller = getPortalController(worldIn, pos);
        if (controller == null)
        {
            return;
        }
        worldIn.addParticle(new PortalControllerParticleData(controller.getPortalColor()), pos.getX() + 0.5D,
                pos.getY() + 0.5D, pos.getZ() + 0.5D, 0.0D, 0.1D, 0.0D);
    }

    @Nullable private static PortalControllerTileEntity getPortalController(World world, BlockPos pos)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null)
        {
            return null;
        }
        return tileEntity instanceof PortalControllerTileEntity ? (PortalControllerTileEntity) tileEntity : null;
    }

    @Override public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getDefaultState()
                   .with(BlockStateProperties.HORIZONTAL_AXIS,
                           context.getPlacementHorizontalFacing().rotateY().getAxis());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack stack)
    {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        if (stack.hasDisplayName())
        {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof PortalControllerTileEntity)
            {
                ((PortalControllerTileEntity) tileEntity).setCustomName(stack.getDisplayName());
            }
        }
    }

    @Override protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.HORIZONTAL_AXIS);
    }
}
