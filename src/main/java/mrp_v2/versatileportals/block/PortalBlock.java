package mrp_v2.versatileportals.block;

import mrp_v2.versatileportals.block.util.PortalSize;
import mrp_v2.versatileportals.blockentity.PortalControllerBlockEntity;
import mrp_v2.versatileportals.particles.PortalParticleData;
import mrp_v2.versatileportals.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Function;

public class PortalBlock extends Block
{
    public static final String ID = "portal";
    public static final VoxelShape Y_AABB = box(0, 6, 0, 16, 10, 16);
    public static final VoxelShape Z_AABB = box(0, 0, 6, 16, 16, 10);
    public static final VoxelShape X_AABB = box(6, 0, 0, 10, 16, 16);

    public PortalBlock()
    {
        this((properties) -> properties);
    }

    protected PortalBlock(Function<Properties, Properties> propertiesModifier)
    {
        super(propertiesModifier
                .apply(Properties.of(Material.PORTAL).noCollission().strength(-1.0F).sound(SoundType.GLASS)
                        .lightLevel((state) -> 11)));
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.AXIS, Direction.Axis.X));
    }

    public static int getColor(BlockState blockState, BlockGetter world, BlockPos pos)
    {
        PortalSize size = new PortalSize(world, pos, blockState.getValue(BlockStateProperties.AXIS));
        PortalControllerBlockEntity portalControllerTE = size.getPortalController(world).getLeft();
        if (portalControllerTE != null)
        {
            return portalControllerTE.getPortalColor();
        }
        return PortalControllerBlockEntity.ERROR_PORTAL_COLOR;
    }

    @Override public BlockState rotate(BlockState state, Rotation rot)
    {
        switch (rot)
        {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch (state.getValue(BlockStateProperties.AXIS))
                {
                    case Z:
                        return state.setValue(BlockStateProperties.AXIS, Axis.X);
                    case X:
                        return state.setValue(BlockStateProperties.AXIS, Axis.Z);
                    default:
                        return state;
                }
            default:
                return state;
        }
    }

    @SuppressWarnings("deprecation") @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn,
            BlockPos currentPos, BlockPos facingPos)
    {
        Direction.Axis updateAxis = facing.getAxis();
        Direction.Axis thisAxis = stateIn.getValue(BlockStateProperties.AXIS);
        boolean isUpdateFromOtherAxis = thisAxis == updateAxis;
        return !isUpdateFromOtherAxis && !facingState.is(this) &&
                !(new PortalSize(worldIn, currentPos, thisAxis)).isValidAndHasCorrectPortalBlockCount() ?
                Blocks.AIR.defaultBlockState() :
                super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @SuppressWarnings("deprecation") @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        switch (state.getValue(BlockStateProperties.AXIS))
        {
            case X:
                return X_AABB;
            case Y:
                return Y_AABB;
            case Z:
                return Z_AABB;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn)
    {
        if (!entityIn.isPassenger() && !entityIn.isVehicle() && entityIn.canChangeDimensions())
        {
            Util.getPortalData(entityIn).setPortal(pos);
        }
    }

    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand)
    {
        for (int i = 0; i < 4; ++i)
        {
            double x = pos.getX() + rand.nextDouble();
            double y = pos.getY() + rand.nextDouble();
            double z = pos.getZ() + rand.nextDouble();
            double xSpeed = (rand.nextFloat() - 0.5D) * 0.5D;
            double ySpeed = (rand.nextFloat() - 0.5D) * 0.5D;
            double zSpeed = (rand.nextFloat() - 0.5D) * 0.5D;
            // Either 1 or -1
            int j = rand.nextInt(2) * 2 - 1;
            switch (stateIn.getValue(BlockStateProperties.AXIS))
            {
                case X:
                    x = pos.getX() + 0.5D + 0.25D * j;
                    xSpeed = rand.nextFloat() * 2.0F * j;
                    break;
                case Y:
                    y = pos.getY() + 0.5D + 0.25D * j;
                    ySpeed = rand.nextFloat() * 2.0F * j;
                    break;
                case Z:
                    z = pos.getZ() + 0.5D + 0.25D * j;
                    zSpeed = rand.nextFloat() * 2.0F * j;
                    break;
            }
            worldIn.addParticle(new PortalParticleData(PortalBlock.getColor(stateIn, worldIn, pos),
                    stateIn.getValue(BlockStateProperties.AXIS) == Axis.Y), x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter worldIn, BlockPos pos, BlockState state)
    {
        return ItemStack.EMPTY;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.AXIS);
    }
}
