package mrp_v2.versatileportals.block;

import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.common.capabilities.IPortalDataCapability;
import mrp_v2.versatileportals.datagen.EN_USTranslationGenerator;
import mrp_v2.versatileportals.particles.PortalParticleData;
import mrp_v2.versatileportals.tileentity.PortalControllerTileEntity;
import mrp_v2.versatileportals.util.Util;
import mrp_v2.versatileportals.world.Teleporter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;
import java.util.function.Function;

public class PortalBlock extends BasicBlock
{
    public static final String ID = "portal";
    public static final TranslationTextComponent invalidControlItemNoKey, invalidControlItemWorldDoesNotExist;

    static
    {
        String stem = String.join(".", "block", VersatilePortals.ID, PortalControllerBlock.ID, "message",
                "invalidControlItem");
        invalidControlItemNoKey = EN_USTranslationGenerator.makeTextTranslation(stem + ".hasNoKey",
                "There is no control item or it is invalid");
        invalidControlItemWorldDoesNotExist = EN_USTranslationGenerator.makeTextTranslation(stem + ".worldDoesNotExist",
                "There is no world matching the control item");
    }

    public PortalBlock()
    {
        this(ID, (properties) -> properties);
    }

    protected PortalBlock(String id, Function<Properties, Properties> propertiesModifier)
    {
        super(id, propertiesModifier.apply(Properties.create(Material.PORTAL)
                .doesNotBlockMovement()
                .hardnessAndResistance(-1.0F)
                .sound(SoundType.GLASS)
                .setLightLevel((i) -> 11)));
        this.setDefaultState(
                this.stateContainer.getBaseState().with(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.X));
    }

    @SuppressWarnings("deprecation") @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
            BlockPos currentPos, BlockPos facingPos)
    {
        Direction.Axis updateAxis = facing.getAxis();
        Direction.Axis thisAxis = stateIn.get(BlockStateProperties.HORIZONTAL_AXIS);
        boolean isUpdateFromOtherAxis = thisAxis != updateAxis && updateAxis.isHorizontal();
        return !isUpdateFromOtherAxis &&
                !facingState.isIn(this) &&
                !(new PortalSize(worldIn, currentPos, thisAxis)).validate() ?
                Blocks.AIR.getDefaultState() :
                super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override public BlockState rotate(BlockState state, Rotation rot)
    {
        switch (rot)
        {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch (state.get(BlockStateProperties.HORIZONTAL_AXIS))
                {
                    case Z:
                        return state.with(BlockStateProperties.HORIZONTAL_AXIS, Axis.X);
                    case X:
                        return state.with(BlockStateProperties.HORIZONTAL_AXIS, Axis.Z);
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
        return Blocks.NETHER_PORTAL.getShape(state, worldIn, pos, context);
    }

    @Override public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        if (!(worldIn instanceof ServerWorld) ||
                entityIn.isPassenger() ||
                entityIn.isBeingRidden() ||
                !entityIn.isNonBoss())
        {
            return;
        }
        if (!entityIn.getBoundingBox().intersects(this.getBoundingBox(state, worldIn, pos)))
        {
            return;
        }
        IPortalDataCapability portalData = Util.getPortalData(entityIn);
        if (portalData.getRemainingPortalCooldown() > 0)
        {
            portalData.setRemainingPortalCooldown(entityIn.getPortalCooldown());
            return;
        }
        if (portalData.incrementInPortalTime() < entityIn.getMaxInPortalTime())
        {
            return;
        }
        PortalSize originPortalSize = new PortalSize(worldIn, pos, state.get(BlockStateProperties.HORIZONTAL_AXIS));
        PortalControllerTileEntity originPortalController = originPortalSize.getPortalController(worldIn).getLeft();
        if (originPortalController == null)
        {
            return;
        }
        ServerWorld originWorld = (ServerWorld) worldIn;
        RegistryKey<World> destinationWorldKey = originPortalController.getTeleportDestination(originWorld);
        if (destinationWorldKey == null)
        {
            if (entityIn instanceof ServerPlayerEntity)
            {
                sendMessage((ServerPlayerEntity) entityIn, invalidControlItemNoKey);
            }
            return;
        }
        ServerWorld destinationWorld = originWorld.getServer().getWorld(destinationWorldKey);
        if (destinationWorld == null)
        {
            if (entityIn instanceof ServerPlayerEntity)
            {
                sendMessage((ServerPlayerEntity) entityIn, invalidControlItemWorldDoesNotExist);
            }
            return;
        }
        entityIn.changeDimension(destinationWorld, new Teleporter(destinationWorld, originWorld, originPortalSize));
    }

    private void sendMessage(ServerPlayerEntity player, ITextComponent message)
    {
        player.func_241151_a_(message, ChatType.GAME_INFO, net.minecraft.util.Util.DUMMY_UUID);
    }

    public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return getShape(state, worldIn, pos, ISelectionContext.dummy()).toBoundingBoxList().get(0).offset(pos);
    }

    @Override @OnlyIn(Dist.CLIENT) public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        for (int i = 0; i < 4; ++i)
        {
            double d0 = pos.getX() + rand.nextDouble();
            double d1 = pos.getY() + rand.nextDouble();
            double d2 = pos.getZ() + rand.nextDouble();
            double d3 = (rand.nextFloat() - 0.5D) * 0.5D;
            double d4 = (rand.nextFloat() - 0.5D) * 0.5D;
            double d5 = (rand.nextFloat() - 0.5D) * 0.5D;
            int j = rand.nextInt(2) * 2 - 1;
            if (stateIn.get(BlockStateProperties.HORIZONTAL_AXIS) == Axis.Z)
            {
                d0 = pos.getX() + 0.5D + 0.25D * j;
                d3 = rand.nextFloat() * 2.0F * j;
            } else
            {
                d2 = pos.getZ() + 0.5D + 0.25D * j;
                d5 = rand.nextFloat() * 2.0F * j;
            }
            worldIn.addParticle(new PortalParticleData(PortalBlock.getColor(stateIn, worldIn, pos)), d0, d1, d2, d3, d4,
                    d5);
        }
    }

    public static int getColor(BlockState blockState, World world, BlockPos pos)
    {
        PortalSize size = new PortalSize(world, pos, blockState.get(BlockStateProperties.HORIZONTAL_AXIS));
        PortalControllerTileEntity portalControllerTE = size.getPortalController(world).getLeft();
        if (portalControllerTE != null)
        {
            return portalControllerTE.getPortalColor();
        }
        return PortalControllerTileEntity.ERROR_PORTAL_COLOR;
    }

    @Override public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state)
    {
        return ItemStack.EMPTY;
    }

    @Override protected void fillStateContainer(Builder<Block, BlockState> builder)
    {
        builder.add(BlockStateProperties.HORIZONTAL_AXIS);
    }
}
