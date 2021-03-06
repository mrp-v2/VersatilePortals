package mrp_v2.versatileportals.block;

import mrp_v2.versatileportals.block.util.PortalFrameUtil;
import mrp_v2.versatileportals.particles.PortalControllerParticleData;
import mrp_v2.versatileportals.tileentity.PortalControllerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
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
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class PortalControllerBlock extends PortalFrameBlock implements IPortalFrame
{
    public static final String ID = "portal_controller";
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final VoxelShape TOP_SIDE = box(0, 13, 0, 16, 16, 16), BOTTOM_SIDE = box(0, 0, 0, 16, 3, 16),
            EAST_SIDE = box(13, 0, 0, 16, 16, 16), WEST_SIDE = box(0, 0, 0, 3, 16, 16), SOUTH_SIDE =
            box(0, 0, 13, 16, 16, 16), NORTH_SIDE = box(0, 0, 0, 16, 16, 3);
    public static final VoxelShape SHAPE_NS = VoxelShapes.or(TOP_SIDE, BOTTOM_SIDE, EAST_SIDE, WEST_SIDE).optimize(),
            SHAPE_EW = VoxelShapes.or(TOP_SIDE, BOTTOM_SIDE, SOUTH_SIDE, NORTH_SIDE).optimize(), SHAPE_UD =
            VoxelShapes.or(EAST_SIDE, WEST_SIDE, SOUTH_SIDE, NORTH_SIDE);

    public PortalControllerBlock()
    {
        super(Properties::noOcclusion);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    public static void animateTick(BlockState stateIn, World worldIn, BlockPos pos)
    {
        PortalControllerParticleData data = new PortalControllerParticleData(PortalFrameUtil.getColor(worldIn, pos));
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + 0.5D;
        double z = pos.getZ() + 0.5D;
        double motion = 0.375D;
        double noMotion = 0.0D;
        Direction.Axis axis = stateIn.getValue(AXIS);
        if (axis != Direction.Axis.X)
        {
            worldIn.addParticle(data, x, y, z, motion, noMotion, noMotion);
            worldIn.addParticle(data, x, y, z, -motion, noMotion, noMotion);
        }
        if (axis != Direction.Axis.Y)
        {
            worldIn.addParticle(data, x, y, z, noMotion, motion, noMotion);
            worldIn.addParticle(data, x, y, z, noMotion, -motion, noMotion);
        }
        if (axis != Direction.Axis.Z)
        {
            worldIn.addParticle(data, x, y, z, noMotion, noMotion, motion);
            worldIn.addParticle(data, x, y, z, noMotion, noMotion, -motion);
        }
    }

    @Override public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.hasTileEntity() && (state.getBlock() != newState.getBlock() || !newState.hasTileEntity()))
        {
            worldIn.getBlockEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                    .ifPresent(itemHandler ->
                    {
                        for (int i = 0; i < itemHandler.getSlots(); i++)
                        {
                            Block.popResource(worldIn, pos, itemHandler.getStackInSlot(i));
                        }
                    });
        }
        super.onRemove(state, worldIn, pos, newState, isMoving);
    }

    public boolean isSideValidForPortal(BlockState state, IBlockReader reader, BlockPos pos, Direction side)
    {
        return state.isFaceSturdy(reader, pos, side);
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
    public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn,
            BlockRayTraceResult hit)
    {
        PortalControllerTileEntity portalController = (PortalControllerTileEntity) worldIn.getBlockEntity(pos);
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
            return ActionResultType.sidedSuccess(worldIn.isClientSide);
        }
        return ActionResultType.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return this.getShape(state);
    }

    private VoxelShape getShape(BlockState state)
    {
        switch (state.getValue(AXIS))
        {
            case X:
                return SHAPE_EW;
            case Y:
                return SHAPE_UD;
            case Z:
                return SHAPE_NS;
            default:
                throw new IllegalStateException();
        }
    }

    @Override public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.defaultBlockState().setValue(AXIS, context.getNearestLookingDirection().getAxis());
    }

    @Override public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack stack)
    {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (stack.hasCustomHoverName())
        {
            TileEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof PortalControllerTileEntity)
            {
                ((PortalControllerTileEntity) tileEntity).setCustomName(stack.getHoverName());
            }
        }
    }

    @Override protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(AXIS);
    }
}
