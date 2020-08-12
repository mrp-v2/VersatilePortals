package mrp_v2.randomdimensions.block;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import mrp_v2.randomdimensions.common.capabilities.IPlayerPortalDataCapability;
import mrp_v2.randomdimensions.common.capabilities.IPortalDataCapability;
import mrp_v2.randomdimensions.particles.PortalParticleData;
import mrp_v2.randomdimensions.tileentity.PortalControllerTileEntity;
import mrp_v2.randomdimensions.util.ObjectHolder;
import mrp_v2.randomdimensions.util.Util;
import mrp_v2.randomdimensions.world.Teleporter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPattern.PatternHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

public class PortalBlock extends BasicBlock
{

    public static final String ID = "portal";

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

    public static boolean trySpawnPortal(World world, BlockPos pos)
    {
        if (world.func_234923_W_() == World.field_234919_h_ || world.func_234923_W_() == World.field_234920_i_)
        {
            return false;
        }
        Size size = canMakePortal(world, pos);
        if (size != null)
        {
            size.placePortalBlocks(world);
            return true;
        }
        return false;
    }

    @Nullable public static Size canMakePortal(World world, BlockPos pos)
    {
        Size size = new Size(world, pos, Direction.Axis.X);
        if (size.isValid() && size.portalBlockCount == 0)
        {
            return size;
        }
        size = new Size(world, pos, Direction.Axis.Z);
        return size.isValid() && size.portalBlockCount == 0 ? size : null;
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
                !(new Size(worldIn, currentPos, thisAxis)).isValidWithSizeAndCount() ?
                Blocks.AIR.getDefaultState() :
                super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @SuppressWarnings("deprecation") @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return Blocks.NETHER_PORTAL.getShape(state, worldIn, pos, context);
    }

    @Override public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        if (worldIn instanceof ServerWorld &&
                !entityIn.isPassenger() &&
                !entityIn.isBeingRidden() &&
                entityIn.isNonBoss())
        {
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
            if (entityIn instanceof ServerPlayerEntity)
            {
                IPlayerPortalDataCapability playerPortalData = Util.getPlayerPortalData((PlayerEntity) entityIn);
                if (playerPortalData.incrementInPortalTime() < entityIn.getMaxInPortalTime())
                {
                    return;
                }
            }
            RegistryKey<World> registryKey = World.field_234918_g_;
            if (worldIn.func_234923_W_() == World.field_234918_g_)
            {
                PortalControllerTileEntity controller =
                        new Size(worldIn, pos, state.get(BlockStateProperties.HORIZONTAL_AXIS)).getPortalController(
                                worldIn).getLeft();
                if (controller == null)
                {
                    return;
                }
                registryKey = controller.getTeleportDestination();
            }
            if (registryKey == null)
            {
                return;
            }
            ServerWorld serverWorld = ((ServerWorld) worldIn).getServer().getWorld(registryKey);
            if (serverWorld == null)
            {
                return;
            }
            PatternHelper patternHelper = createPatternHelper(worldIn, pos);
            double d0 = patternHelper.getForwards().getAxis() == Direction.Axis.X ?
                    (double) patternHelper.getFrontTopLeft().getZ() :
                    (double) patternHelper.getFrontTopLeft().getX();
            double d1 = MathHelper.clamp(Math.abs(MathHelper.func_233020_c_(
                    (patternHelper.getForwards().getAxis() == Direction.Axis.X ?
                            entityIn.getPosZ() :
                            entityIn.getPosX()) -
                            (patternHelper.getForwards().rotateY().getAxisDirection() ==
                                    Direction.AxisDirection.NEGATIVE ? 1 : 0), d0, d0 - patternHelper.getWidth())),
                    0.0D, 1.0D);
            double d2 = MathHelper.clamp(
                    MathHelper.func_233020_c_(entityIn.getPosY() - 1.0D, patternHelper.getFrontTopLeft().getY(),
                            patternHelper.getFrontTopLeft().getY() - patternHelper.getHeight()), 0.0D, 1.0D);
            String worldID =
                    Util.getWorldID(serverWorld.func_234923_W_() != World.field_234918_g_ ? serverWorld : worldIn);
            portalData.setLastPortalVec(worldID, new Vector3d(d1, d2, 0.0D));
            portalData.setTeleportDirection(worldID, patternHelper.getForwards());
            entityIn.changeDimension(serverWorld, new Teleporter(serverWorld));
        }
    }

    public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader worldIn, BlockPos pos)
    {
        return getShape(state, worldIn, pos, ISelectionContext.dummy()).toBoundingBoxList().get(0).offset(pos);
    }

    @SuppressWarnings("deprecation") public static PatternHelper createPatternHelper(IWorld world, BlockPos pos)
    {
        Axis axis = Axis.Z;
        Size size = new Size(world, pos, Axis.X);
        LoadingCache<BlockPos, CachedBlockInfo> loadingCache = BlockPattern.createLoadingCache(world, true);
        if (!size.isValid())
        {
            axis = Direction.Axis.X;
            size = new Size(world, pos, Axis.Z);
        }
        if (!size.isValid())
        {
            return new PatternHelper(pos, Direction.NORTH, Direction.UP, loadingCache, 1, 1, 1);
        }
        int[] intArray = new int[AxisDirection.values().length];
        Direction direction = size.rightDir.rotateYCCW();
        BlockPos blockpos = size.bottomLeft.up(size.getHeight() - 1);
        for (AxisDirection axisDirection1 : AxisDirection.values())
        {
            PatternHelper patternHelper = new PatternHelper(direction.getAxisDirection() == axisDirection1 ?
                    blockpos :
                    blockpos.offset(size.rightDir, size.getWidth() - 1),
                    Direction.getFacingFromAxis(axisDirection1, axis), Direction.UP, loadingCache, size.getWidth(),
                    size.getHeight(), 1);
            for (int i = 0; i < size.getWidth(); ++i)
            {
                for (int j = 0; j < size.getHeight(); ++j)
                {
                    CachedBlockInfo cachedBlockInfo = patternHelper.translateOffset(i, j, 1);
                    if (!cachedBlockInfo.getBlockState().isAir())
                    {
                        ++intArray[axisDirection1.ordinal()];
                    }
                }
            }
        }
        AxisDirection axisDirection2 = AxisDirection.POSITIVE;
        for (AxisDirection axisDirection3 : AxisDirection.values())
        {
            if (intArray[axisDirection3.ordinal()] < intArray[axisDirection2.ordinal()])
            {
                axisDirection2 = axisDirection3;
            }
        }
        return new PatternHelper(direction.getAxisDirection() == axisDirection2 ?
                blockpos :
                blockpos.offset(size.rightDir, size.getWidth() - 1), Direction.getFacingFromAxis(axisDirection2, axis),
                Direction.UP, loadingCache, size.getWidth(), size.getHeight(), 1);
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
        Size size = new Size(world, pos, blockState.get(BlockStateProperties.HORIZONTAL_AXIS));
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

    public static class Size
    {
        public static final int MIN_WIDTH = 1;
        public static final int MIN_HEIGHT = 2;
        public static final int MAX_SIZE = 21;

        private static final Block PORTAL_BLOCK = ObjectHolder.PORTAL_BLOCK;

        private final Direction.Axis axis;
        private final Direction rightDir;
        private final Direction leftDir;
        private int portalBlockCount;
        @Nullable private BlockPos bottomLeft;
        private int height;
        private int width;

        public Size(IWorld iWorld, BlockPos pos, Axis axis)
        {
            this(axis);
            if (!isOrCanPlacePortal(iWorld.getBlockState(pos)))
            {
                this.invalidate();
                return;
            }
            for (BlockPos blockpos = pos;
                 pos.getY() > blockpos.getY() - MAX_SIZE &&
                         pos.getY() > 0 &&
                         isOrCanPlacePortal(iWorld.getBlockState(pos.down()));
                 pos = pos.down())
            {
            }
            int i = getDistanceUntilEdge(pos, this.leftDir, iWorld) - 1;
            if (i >= 0)
            {
                this.bottomLeft = pos.offset(this.leftDir, i);
                this.width = getDistanceUntilEdge(this.bottomLeft, this.rightDir, iWorld);
                if (this.width < MIN_WIDTH || this.width > MAX_SIZE)
                {
                    this.invalidate();
                    return;
                }
            }
            if (this.bottomLeft != null)
            {
                this.height = this.calculatePortalHeight(iWorld);
            }
            if (!this.getPortalController(iWorld).getRight())
            {
                this.invalidate();
            }
        }

        private Size(Axis axis)
        {
            this.axis = axis;
            if (axis == Axis.X)
            {
                this.leftDir = Direction.EAST;
                this.rightDir = Direction.WEST;
            } else
            {
                this.leftDir = Direction.NORTH;
                this.rightDir = Direction.SOUTH;
            }
        }

        protected static int getDistanceUntilEdge(BlockPos pos, Direction direction, IWorld iWorld)
        {
            int i;
            for (i = 0; i < MAX_SIZE; ++i)
            {
                BlockPos blockpos = pos.offset(direction, i);
                if (!isOrCanPlacePortal(iWorld.getBlockState(blockpos)) || !isPortalFrame(iWorld, blockpos.down()))
                {
                    break;
                }
            }

            return isPortalFrame(iWorld, pos.offset(direction, i)) ? i : 0;
        }

        public static boolean isPortalFrame(IWorld iWorld, BlockPos pos)
        {
            return iWorld.getBlockState(pos).getBlock() instanceof PortalFrameBlock;
        }

        @SuppressWarnings("deprecation") protected static boolean isOrCanPlacePortal(BlockState blockState)
        {
            return blockState.isAir() || blockState.isIn(PORTAL_BLOCK);
        }

        @Nullable public Pair<PortalControllerTileEntity, Boolean> getPortalController(IWorld iWorld)
        {
            PortalControllerTileEntity portalControllerTE = null;
            Boolean found = false;
            if (this.isValid())
            {
                for (Pair<BlockPos, Optional<Direction>> edge : this.getFrameBlocks())
                {
                    BlockState state = iWorld.getBlockState(edge.getLeft());
                    if (state.isIn(ObjectHolder.PORTAL_CONTROLLER_BLOCK))
                    {
                        if (!edge.getRight().isPresent() ||
                                state.isSolidSide(iWorld, edge.getLeft(), edge.getRight().get()))
                        {
                            if (found)
                            {
                                return Pair.of(null, false);
                            }
                            TileEntity temp = iWorld.getTileEntity(edge.getLeft());
                            portalControllerTE = temp == null ? null : (PortalControllerTileEntity) temp;
                            found = true;
                        }
                    }
                }
            }
            return Pair.of(portalControllerTE, found);
        }

        public boolean isValid()
        {
            return this.bottomLeft != null &&
                    this.width >= MIN_WIDTH &&
                    this.width <= MAX_SIZE &&
                    this.height >= MIN_HEIGHT &&
                    this.height <= MAX_SIZE;
        }

        public List<Pair<BlockPos, Optional<Direction>>> getFrameBlocks()
        {
            List<Pair<BlockPos, Optional<Direction>>> edges =
                    Lists.newArrayListWithCapacity(this.height * 2 + this.width * 2 + 4);
            //corners
            edges.add(Pair.of(this.bottomLeft.down().offset(this.leftDir), Optional.empty()));
            edges.add(Pair.of(this.bottomLeft.down().offset(this.rightDir, this.width), Optional.empty()));
            edges.add(Pair.of(this.bottomLeft.up(this.height).offset(this.leftDir), Optional.empty()));
            edges.add(Pair.of(this.bottomLeft.up(this.height).offset(this.rightDir, this.width), Optional.empty()));
            // left side
            for (int i = 0; i < this.height; i++)
            {
                edges.add(Pair.of(this.bottomLeft.offset(this.leftDir).up(i), Optional.of(this.rightDir)));
            }
            // right side
            for (int i = 0; i < this.height; i++)
            {
                edges.add(Pair.of(this.bottomLeft.offset(this.rightDir, this.width).up(i), Optional.of(this.leftDir)));
            }
            // top side
            for (int i = 0; i < this.width; i++)
            {
                edges.add(
                        Pair.of(this.bottomLeft.up(this.height).offset(this.rightDir, i), Optional.of(Direction.DOWN)));
            }
            // bottom side
            for (int i = 0; i < this.width; i++)
            {
                edges.add(Pair.of(this.bottomLeft.down().offset(this.rightDir, i), Optional.of(Direction.UP)));
            }
            return edges;
        }

        protected int calculatePortalHeight(IWorld iWorld)
        {
            loop:
            for (this.height = 0; this.height < MAX_SIZE; ++this.height)
            {
                for (int i = 0; i < this.width; ++i)
                {
                    BlockPos testBlockPos = this.bottomLeft.offset(this.rightDir, i).up(this.height);
                    BlockState testBlockState = iWorld.getBlockState(testBlockPos);
                    if (!Size.isOrCanPlacePortal(testBlockState))
                    {
                        break loop;
                    }
                    if (isPortalBlock(testBlockState))
                    {
                        ++this.portalBlockCount;
                    }
                    if (i == 0 && !isPortalFrame(iWorld, testBlockPos.offset(this.leftDir)))
                    {
                        break loop;
                    }
                    if (i == this.width - 1 && !isPortalFrame(iWorld, testBlockPos.offset(this.rightDir)))
                    {
                        break loop;
                    }
                }
            }
            for (int j = 0; j < this.width; ++j)
            {
                if (!isPortalFrame(iWorld, this.bottomLeft.offset(this.rightDir, j).up(this.height)))
                {
                    this.height = 0;
                    break;
                }
            }
            if (this.height <= MAX_SIZE && this.height >= MIN_HEIGHT)
            {
                return this.height;
            }
            this.invalidate();
            return 0;
        }

        public static boolean isPortalBlock(BlockState blockState)
        {
            return blockState.getBlock() instanceof PortalBlock;
        }

        private void invalidate()
        {
            this.bottomLeft = null;
            this.width = 0;
            this.height = 0;
        }

        private Size(Axis axis, BlockPos bottomLeft, int height, int width, int portalBlockCount)
        {
            this(axis);
            this.bottomLeft = bottomLeft;
            this.height = height;
            this.width = width;
            this.portalBlockCount = portalBlockCount;
        }

        public static void writeListToBuffer(List<Size> sizes, PacketBuffer buffer)
        {
            buffer.writeInt(sizes.size());
            for (Size size : sizes)
            {
                size.writeToBuffer(buffer);
            }
        }

        public void writeToBuffer(PacketBuffer buffer)
        {
            buffer.writeBlockPos(this.bottomLeft);
            buffer.writeInt(this.height);
            buffer.writeInt(this.width);
            buffer.writeInt(this.portalBlockCount);
            buffer.writeEnumValue(this.axis);
        }

        public static List<Size> readListFromBuffer(PacketBuffer buffer)
        {
            int length = buffer.readInt();
            List<Size> sizes = Lists.newArrayListWithCapacity(length);
            for (int i = 0; i < length; i++)
            {
                sizes.add(readFromBuffer(buffer));
            }
            return sizes;
        }

        public static Size readFromBuffer(PacketBuffer buffer)
        {
            BlockPos bottomLeft = buffer.readBlockPos();
            int height = buffer.readInt();
            int width = buffer.readInt();
            int portalBlockCount = buffer.readInt();
            Axis axis = buffer.readEnumValue(Axis.class);
            return new Size(axis, bottomLeft, height, width, portalBlockCount);
        }

        public int getHeight()
        {
            return this.height;
        }

        public int getWidth()
        {
            return this.width;
        }

        public void placePortalBlocks(IWorld world)
        {
            this.doOperationOnPortalBlocks((pos) -> world.setBlockState(pos,
                    PORTAL_BLOCK.getDefaultState().with(BlockStateProperties.HORIZONTAL_AXIS, this.axis), 18));
        }

        public void doOperationOnPortalBlocks(Consumer<BlockPos> operation)
        {
            for (int i = 0; i < this.width; ++i)
            {
                BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i);
                for (int j = 0; j < this.height; ++j)
                {
                    operation.accept(blockpos.up(j));
                }
            }
        }

        public Pair<BlockPos, BlockPos> getBlockRange()
        {
            BlockPos a = this.bottomLeft.down().offset(this.leftDir);
            BlockPos b = this.bottomLeft.up(this.height).offset(this.rightDir, this.width);
            return Pair.of(a, b);
        }

        public boolean isValidWithSizeAndCount()
        {
            return this.isValid() && this.doesSizeMatchCount();
        }

        private boolean doesSizeMatchCount()
        {
            return this.portalBlockCount >= this.width * this.height;
        }
    }
}
