package mrp_v2.versatileportals.block;

import mrp_v2.versatileportals.block.util.PortalFrameUtil;
import mrp_v2.versatileportals.particles.PortalControllerParticleData;
import mrp_v2.versatileportals.tileentity.PortalControllerTileEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class PortalControllerBlock extends PortalFrameBlock implements IPortalFrame
{
    public static final String ID = "portal_controller";
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final VoxelShape TOP_SIDE = box(0, 13, 0, 16, 16, 16), BOTTOM_SIDE = box(0, 0, 0, 16, 3, 16),
            EAST_SIDE = box(13, 0, 0, 16, 16, 16), WEST_SIDE = box(0, 0, 0, 3, 16, 16), SOUTH_SIDE =
            box(0, 0, 13, 16, 16, 16), NORTH_SIDE = box(0, 0, 0, 16, 16, 3);
    public static final VoxelShape SHAPE_NS = Shapes.or(TOP_SIDE, BOTTOM_SIDE, EAST_SIDE, WEST_SIDE).optimize(),
            SHAPE_EW = Shapes.or(TOP_SIDE, BOTTOM_SIDE, SOUTH_SIDE, NORTH_SIDE).optimize(), SHAPE_UD =
            Shapes.or(EAST_SIDE, WEST_SIDE, SOUTH_SIDE, NORTH_SIDE);

    public PortalControllerBlock()
    {
        super(Properties::noOcclusion);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    public static void animateTick(BlockState stateIn, Level worldIn, BlockPos pos)
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

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving)
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

    public boolean isSideValidForPortal(BlockState state, BlockGetter reader, BlockPos pos, Direction side)
    {
        return state.isFaceSturdy(reader, pos, side);
    }

    @Override public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Override
    public BlockEntity createTileEntity(BlockState state, BlockGetter world)
    {
        return new PortalControllerTileEntity();
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn,
                                 BlockHitResult hit)
    {
        PortalControllerTileEntity portalController = (PortalControllerTileEntity) worldIn.getBlockEntity(pos);
        if (portalController != null)
        {
            if (player instanceof ServerPlayer)
            {
                NetworkHooks.openGui((ServerPlayer) player, portalController, (buffer) ->
                {
                    buffer.writeInt(portalController.getPortalColor());
                    buffer.writeBlockPos(pos);
                });
            }
            return InteractionResult.sidedSuccess(worldIn.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
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

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(AXIS, context.getNearestLookingDirection().getAxis());
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
            ItemStack stack)
    {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (stack.hasCustomHoverName())
        {
            BlockEntity tileEntity = worldIn.getBlockEntity(pos);
            if (tileEntity instanceof PortalControllerTileEntity)
            {
                ((PortalControllerTileEntity) tileEntity).setCustomName(stack.getHoverName());
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(AXIS);
    }
}
