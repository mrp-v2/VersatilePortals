package mrp_v2.versatileportals.block;

import mrp_v2.versatileportals.block.util.PortalSize;
import mrp_v2.versatileportals.particles.PortalParticleData;
import mrp_v2.versatileportals.tileentity.PortalControllerTileEntity;
import mrp_v2.versatileportals.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;
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

    @SuppressWarnings("deprecation") @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
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
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
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

    @Override public void entityInside(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        if (!entityIn.isPassenger() && !entityIn.isVehicle() && entityIn.canChangeDimensions())
        {
            Util.getPortalData(entityIn).setPortal(pos);
        }
    }

    @Override @OnlyIn(Dist.CLIENT) public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
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

    public static int getColor(BlockState blockState, IBlockReader world, BlockPos pos)
    {
        PortalSize size = new PortalSize(world, pos, blockState.getValue(BlockStateProperties.AXIS));
        PortalControllerTileEntity portalControllerTE = size.getPortalController(world).getLeft();
        if (portalControllerTE != null)
        {
            return portalControllerTE.getPortalColor();
        }
        return PortalControllerTileEntity.ERROR_PORTAL_COLOR;
    }

    @Override public ItemStack getCloneItemStack(IBlockReader worldIn, BlockPos pos, BlockState state)
    {
        return ItemStack.EMPTY;
    }

    @Override protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.AXIS);
    }
}
