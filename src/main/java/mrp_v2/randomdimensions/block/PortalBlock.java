package mrp_v2.randomdimensions.block;

import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.cache.LoadingCache;

import mrp_v2.randomdimensions.common.capabilities.IPortalDataCapability;
import mrp_v2.randomdimensions.particles.PortalParticleData;
import mrp_v2.randomdimensions.tileentity.PortalControllerTileEntity;
import mrp_v2.randomdimensions.util.ObjectHolder;
import mrp_v2.randomdimensions.world.Teleporter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.BlockPattern.PatternHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Direction.AxisDirection;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PortalBlock extends BasicBlock {

	public static final String ID = "portal";

	public PortalBlock() {
		this(ID, (properties) -> properties);
	}

	protected PortalBlock(String id, Function<Properties, Properties> propertiesModifier) {
		super(id,
				propertiesModifier
						.apply(Properties.create(Material.PORTAL).doesNotBlockMovement().hardnessAndResistance(-1.0F)
								.sound(SoundType.GLASS).setLightLevel((i) -> {
									return 11;
								})));
		this.setDefaultState(
				this.stateContainer.getBaseState().with(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.X));
	}

	public static int getColor(BlockState state, IBlockDisplayReader reader, BlockPos pos) {
		Size size = new Size(reader, pos, state.get(BlockStateProperties.HORIZONTAL_AXIS));
		if (size.isValid()) {
			PortalControllerTileEntity portalControllerTE = size.getPortalController(reader);
			if (portalControllerTE != null) {
				return portalControllerTE.getPortalColor();
			}
		}
		return PortalControllerTileEntity.DEFAULT_PORTAL_COLOR;
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return Blocks.NETHER_PORTAL.getShape(state, worldIn, pos, context);
	}

	public static boolean trySpawnPortal(World world, BlockPos pos) {
		if (world.func_234923_W_() == World.field_234919_h_ || world.func_234923_W_() == World.field_234920_i_) {
			return false;
		}
		Size size = isPortal(world, pos);
		if (size != null) {
			size.placePortalBlocks(world);
			return true;
		}
		return false;
	}

	@Nullable
	public static Size isPortal(IWorld world, BlockPos pos) {
		Size size = new Size(world, pos, Direction.Axis.X);
		if (size.isValid() && size.portalBlockCount == 0) {
			return size;
		}
		size = new Size(world, pos, Direction.Axis.Z);
		return size.isValid() && size.portalBlockCount == 0 ? size : null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
			BlockPos currentPos, BlockPos facingPos) {
		Direction.Axis updateAxis = facing.getAxis();
		Direction.Axis thisAxis = stateIn.get(BlockStateProperties.HORIZONTAL_AXIS);
		boolean flag = thisAxis != updateAxis && updateAxis.isHorizontal();
		return !flag && !facingState.isIn(this)
				&& !(new Size(worldIn, currentPos, thisAxis)).isValidWithSizeAndCount()
						? Blocks.AIR.getDefaultState()
						: super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
		if (worldIn instanceof ServerWorld && !entityIn.isPassenger() && !entityIn.isBeingRidden()
				&& entityIn.isNonBoss()) {
			RegistryKey<World> registryKey = worldIn.func_234923_W_() == World.field_234918_g_
					? new Size(worldIn, pos, state.get(BlockStateProperties.HORIZONTAL_AXIS))
							.getPortalController(worldIn).getTeleportDestination()
					: World.field_234918_g_;
			ServerWorld serverWorld = ((ServerWorld) worldIn).getServer().getWorld(registryKey);
			if (serverWorld == null) {
				return;
			}
			PatternHelper patternHelper = createPatternHelper(worldIn, pos);
			double d0 = patternHelper.getForwards().getAxis() == Direction.Axis.X
					? (double) patternHelper.getFrontTopLeft().getZ()
					: (double) patternHelper.getFrontTopLeft().getX();
			double d1 = MathHelper.clamp(Math.abs(MathHelper.func_233020_c_(
					(patternHelper.getForwards().getAxis() == Direction.Axis.X
							? entityIn.getPosZ()
							: entityIn.getPosX())
							- (patternHelper.getForwards().rotateY()
									.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? 1 : 0),
					d0, d0 - patternHelper.getWidth())), 0.0D, 1.0D);
			double d2 = MathHelper
					.clamp(MathHelper.func_233020_c_(entityIn.getPosY() - 1.0D, patternHelper.getFrontTopLeft().getY(),
							patternHelper.getFrontTopLeft().getY() - patternHelper.getHeight()), 0.0D, 1.0D);
			IPortalDataCapability portalData = Teleporter.getPortalData(entityIn);
			String worldID = Teleporter
					.getWorldID(serverWorld.func_234923_W_() != World.field_234918_g_ ? serverWorld : worldIn);
			portalData.setLastPortalVec(worldID, new Vector3d(d1, d2, 0.0D));
			portalData.setTeleportDirection(worldID, patternHelper.getForwards());
			entityIn.changeDimension(serverWorld, new Teleporter(serverWorld));
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		for (int i = 0; i < 4; ++i) {
			double d0 = pos.getX() + rand.nextDouble();
			double d1 = pos.getY() + rand.nextDouble();
			double d2 = pos.getZ() + rand.nextDouble();
			double d3 = (rand.nextFloat() - 0.5D) * 0.5D;
			double d4 = (rand.nextFloat() - 0.5D) * 0.5D;
			double d5 = (rand.nextFloat() - 0.5D) * 0.5D;
			int j = rand.nextInt(2) * 2 - 1;
			if (stateIn.get(BlockStateProperties.HORIZONTAL_AXIS) == Axis.Z) {
				d0 = pos.getX() + 0.5D + 0.25D * j;
				d3 = rand.nextFloat() * 2.0F * j;
			} else {
				d2 = pos.getZ() + 0.5D + 0.25D * j;
				d5 = rand.nextFloat() * 2.0F * j;
			}
			worldIn.addParticle(new PortalParticleData(PortalBlock.getColor(stateIn, worldIn, pos)), d0, d1, d2, d3, d4,
					d5);
		}
	}

	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
		return ItemStack.EMPTY;
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.HORIZONTAL_AXIS);
	}

	@SuppressWarnings("deprecation")
	public static void rerenderPortal(World world, BlockPos pos) {
		if (world.isBlockLoaded(pos) && world.getBlockState(pos).isIn(ObjectHolder.PORTAL_BLOCK)) {
			new Size(world, pos, world.getBlockState(pos).get(BlockStateProperties.HORIZONTAL_AXIS))
					.doOperationOnBlocks((blockPos) -> {
						BlockState state = world.getBlockState(pos);
						world.notifyBlockUpdate(pos, state, state, 16 | 32);
					});
		}
	}

	@SuppressWarnings("deprecation")
	public static PatternHelper createPatternHelper(IWorld world, BlockPos pos) {
		Axis axis = Axis.Z;
		Size size = new Size(world, pos, Axis.X);
		LoadingCache<BlockPos, CachedBlockInfo> loadingCache = BlockPattern.createLoadingCache(world, true);
		if (!size.isValid()) {
			axis = Direction.Axis.X;
			size = new Size(world, pos, Axis.Z);
		}
		if (!size.isValid()) {
			return new PatternHelper(pos, Direction.NORTH, Direction.UP, loadingCache, 1, 1, 1);
		}
		int[] intArray = new int[AxisDirection.values().length];
		Direction direction = size.rightDir.rotateYCCW();
		BlockPos blockpos = size.bottomLeft.up(size.getHeight() - 1);
		for (AxisDirection axisDirection1 : AxisDirection.values()) {
			PatternHelper patternHelper = new PatternHelper(
					direction.getAxisDirection() == axisDirection1 ? blockpos
							: blockpos.offset(size.rightDir, size.getWidth() - 1),
					Direction.getFacingFromAxis(axisDirection1, axis), Direction.UP, loadingCache, size.getWidth(),
					size.getHeight(), 1);
			for (int i = 0; i < size.getWidth(); ++i) {
				for (int j = 0; j < size.getHeight(); ++j) {
					CachedBlockInfo cachedBlockInfo = patternHelper.translateOffset(i, j, 1);
					if (!cachedBlockInfo.getBlockState().isAir()) {
						++intArray[axisDirection1.ordinal()];
					}
				}
			}
		}
		AxisDirection axisDirection2 = AxisDirection.POSITIVE;
		for (AxisDirection axisDirection3 : AxisDirection.values()) {
			if (intArray[axisDirection3.ordinal()] < intArray[axisDirection2.ordinal()]) {
				axisDirection2 = axisDirection3;
			}
		}
		return new PatternHelper(
				direction.getAxisDirection() == axisDirection2 ? blockpos
						: blockpos.offset(size.rightDir, size.getWidth() - 1),
				Direction.getFacingFromAxis(axisDirection2, axis), Direction.UP, loadingCache, size.getWidth(),
				size.getHeight(), 1);
	}

	public static class Size {

		private static final int MIN_WIDTH = 1;
		private static final int MIN_HEIGHT = 2;
		private static final int MAX_SIZE = 21;

		private final Direction.Axis axis;
		private final Direction rightDir;
		private final Direction leftDir;
		private int portalBlockCount;
		@Nullable
		private BlockPos bottomLeft;
		private int height;
		private int width;

		public Size(IBlockDisplayReader reader, BlockPos pos, Axis axisIn) {
			this.axis = axisIn;
			if (axisIn == Axis.X) {
				this.leftDir = Direction.EAST;
				this.rightDir = Direction.WEST;
			} else {
				this.leftDir = Direction.NORTH;
				this.rightDir = Direction.SOUTH;
			}
			for (BlockPos blockpos = pos; pos.getY() > blockpos.getY() - MAX_SIZE && pos.getY() > 0
					&& Size.canPlacePortal(reader.getBlockState(pos.down())); pos = pos.down()) {
			}
			int i = Size.getDistanceUntilEdge(pos, this.leftDir, reader) - 1;
			if (i >= 0) {
				this.bottomLeft = pos.offset(this.leftDir, i);
				this.width = Size.getDistanceUntilEdge(this.bottomLeft, this.rightDir, reader);
				if (this.width < MIN_WIDTH || this.width > MAX_SIZE) {
					this.invalidate();
				}
			}
			if (this.bottomLeft != null) {
				this.height = this.calculatePortalHeight(reader);
			}
			if (this.getPortalController(reader) == null) {
				this.invalidate();
			}
		}

		protected static int getDistanceUntilEdge(BlockPos pos, Direction directionIn, IBlockDisplayReader reader) {
			int i;
			for (i = 0; i < MAX_SIZE; ++i) {
				BlockPos blockpos = pos.offset(directionIn, i);
				if (!Size.canPlacePortal(reader.getBlockState(blockpos)) || !isPortalFrame(reader, blockpos.down())) {
					break;
				}
			}

			return isPortalFrame(reader, pos.offset(directionIn, i)) ? i : 0;
		}

		public int getHeight() {
			return this.height;
		}

		public int getWidth() {
			return this.width;
		}

		protected int calculatePortalHeight(IBlockDisplayReader reader) {
			loop:
			for (this.height = 0; this.height < MAX_SIZE; ++this.height) {
				for (int i = 0; i < this.width; ++i) {
					BlockPos testBlockPos = this.bottomLeft.offset(this.rightDir, i).up(this.height);
					BlockState testBlockState = reader.getBlockState(testBlockPos);
					if (!Size.canPlacePortal(testBlockState)) {
						break loop;
					}
					if (testBlockState.isIn(getPortalBlock())) {
						++this.portalBlockCount;
					}
					if (i == 0 && !isPortalFrame(reader, testBlockPos.offset(this.leftDir))) {
						break loop;
					}
					if (i == this.width - 1 && !isPortalFrame(reader, testBlockPos.offset(this.rightDir))) {
						break loop;
					}
				}
			}
			for (int j = 0; j < this.width; ++j) {
				if (!isPortalFrame(reader, this.bottomLeft.offset(this.rightDir, j).up(this.height))) {
					this.height = 0;
					break;
				}
			}
			if (this.height <= MAX_SIZE && this.height >= MIN_HEIGHT) {
				return this.height;
			}
			this.invalidate();
			return 0;
		}

		@SuppressWarnings("deprecation")
		protected static boolean canPlacePortal(BlockState state) {
			return state.isAir() || state.isIn(getPortalBlock());
		}

		public boolean isValid() {
			return this.bottomLeft != null && this.width >= MIN_WIDTH && this.width <= MAX_SIZE
					&& this.height >= MIN_HEIGHT && this.height <= MAX_SIZE;
		}

		public void placePortalBlocks(IWorld world) {
			this.doOperationOnBlocks((pos) -> {
				world.setBlockState(pos,
						getPortalBlock().getDefaultState().with(BlockStateProperties.HORIZONTAL_AXIS, this.axis), 18);
			});
		}

		public void doOperationOnBlocks(Consumer<BlockPos> operation) {
			for (int i = 0; i < this.width; ++i) {
				BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i);
				for (int j = 0; j < this.height; ++j) {
					operation.accept(blockpos.up(j));
				}
			}
		}

		private boolean doesSizeMatchCount() {
			return this.portalBlockCount >= this.width * this.height;
		}

		public boolean isValidWithSizeAndCount() {
			return this.isValid() && this.doesSizeMatchCount();
		}

		public static boolean isPortalFrame(IBlockDisplayReader world, BlockPos pos) {
			return world.getBlockState(pos).getBlock() instanceof PortalFrameBlock;
		}

		private void invalidate() {
			this.bottomLeft = null;
			this.width = 0;
			this.height = 0;
		}

		public static Block getPortalBlock() {
			return ObjectHolder.PORTAL_BLOCK;
		}

		public BlockPos[] getEdges() {
			BlockPos[] edges = new BlockPos[this.height * 2 + this.width * 2];
			// left side
			for (int i = 0; i < this.height; i++) {
				edges[i] = this.bottomLeft.offset(this.leftDir, 1).up(i);
			}
			// right side
			for (int i = 0; i < this.height; i++) {
				edges[i + this.height] = this.bottomLeft.offset(this.rightDir, this.width).up(i);
			}
			// top side
			for (int i = 0; i < this.width; i++) {
				edges[i + this.height * 2] = this.bottomLeft.up(this.height).offset(this.rightDir, i);
			}
			// bottom side
			for (int i = 0; i < this.width; i++) {
				edges[i + this.height * 2 + this.width] = this.bottomLeft.down().offset(this.rightDir, i);
			}
			return edges;
		}

		public PortalControllerTileEntity getPortalController(IBlockDisplayReader world) {
			PortalControllerTileEntity portalControllerTE = null;
			if (this.isValid()) {
				for (BlockPos edge : this.getEdges()) {
					if (world.getBlockState(edge).isIn(ObjectHolder.PORTAL_CONTROLLER_BLOCK)) {
						TileEntity tileEntity = world.getTileEntity(edge);
						if (tileEntity != null && tileEntity instanceof PortalControllerTileEntity) {
							if (portalControllerTE != null) {
								return null;
							}
							portalControllerTE = (PortalControllerTileEntity) tileEntity;
						}
					}
				}
			}
			return portalControllerTE;
		}
	}
}
