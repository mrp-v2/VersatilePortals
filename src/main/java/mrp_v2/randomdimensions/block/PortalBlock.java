package mrp_v2.randomdimensions.block;

import java.util.Random;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import mrp_v2.randomdimensions.particles.PortalParticleData;
import mrp_v2.randomdimensions.tileentity.PortalControllerTileEntity;
import mrp_v2.randomdimensions.util.ObjectHolder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PortalBlock extends BasicBlock {

	public static final String ID = "portal";
	public static final VoxelShape X_AABB = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
	public static final VoxelShape Z_AABB = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

	public PortalBlock() {
		super(ID, Properties.create(Material.PORTAL).doesNotBlockMovement().hardnessAndResistance(-1.0F)
				.sound(SoundType.GLASS).func_235838_a_((i) -> {
					return 11;
				}));
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

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch (state.get(BlockStateProperties.HORIZONTAL_AXIS)) {
		case Z:
			return Z_AABB;
		case X:
		default:
			return X_AABB;
		}
	}

	public static boolean trySpawnPortal(IWorld world, BlockPos pos) {
		PortalBlock.Size size = isPortal(world, pos);
		if (size != null) {
			size.placePortalBlocks(world);
			return true;
		}
		return false;
	}

	@Nullable
	public static PortalBlock.Size isPortal(IWorld world, BlockPos pos) {
		PortalBlock.Size size = new PortalBlock.Size(world, pos, Direction.Axis.X);
		if (size.isValid() && size.portalBlockCount == 0) {
			return size;
		}
		size = new PortalBlock.Size(world, pos, Direction.Axis.Z);
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
				&& !(new PortalBlock.Size(worldIn, currentPos, thisAxis)).isValidWithSizeAndCount()
						? Blocks.AIR.getDefaultState()
						: super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) { // TODO correct
																									// teleportation
		if (!entityIn.isPassenger() && !entityIn.isBeingRidden() && entityIn.isNonBoss()) {
			// entityIn.setPortal(pos);
		}
	}

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

		public Size(IBlockDisplayReader reader, BlockPos pos, Direction.Axis axisIn) {
			this.axis = axisIn;
			if (axisIn == Direction.Axis.X) {
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
			return state.isAir() || state.func_235714_a_(BlockTags.field_232872_am_) || state.isIn(getPortalBlock());
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
