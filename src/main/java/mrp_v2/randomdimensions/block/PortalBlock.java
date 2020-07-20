package mrp_v2.randomdimensions.block;

import java.util.Random;

import javax.annotation.Nullable;

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

	@OnlyIn(Dist.CLIENT)
	public static int getColor(BlockState state, IBlockDisplayReader reader, BlockPos pos) {
		Size size = new Size(reader, pos, reader.getBlockState(pos).get(BlockStateProperties.HORIZONTAL_AXIS));
		for (BlockPos edge : size.getEdges()) {
			if (reader.getBlockState(edge).getBlock() == ObjectHolder.PORTAL_CONTROLLER_BLOCK) {
				TileEntity test = reader.getTileEntity(edge);
				if (test instanceof PortalControllerTileEntity) {
					return ((PortalControllerTileEntity) reader.getTileEntity(edge)).getPortalColor();
				}
			}
		}
		return PortalControllerTileEntity.DEFAULT_PORTAL_COLOR;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch ((Direction.Axis) state.get(BlockStateProperties.HORIZONTAL_AXIS)) {
		case Z:
			return Z_AABB;
		case X:
		default:
			return X_AABB;
		}
	}

	public static boolean trySpawnPortal(IWorld world, BlockPos worldIn) {
		PortalBlock.Size portalblock$size = isPortal(world, worldIn);
		if (portalblock$size != null) {
			portalblock$size.placePortalBlocks(world);
			return true;
		} else {
			return false;
		}
	}

	@Nullable
	public static PortalBlock.Size isPortal(IWorld p_201816_0_, BlockPos worldIn) {
		PortalBlock.Size portalblock$size = new PortalBlock.Size(p_201816_0_, worldIn, Direction.Axis.X);
		if (portalblock$size.isValid() && portalblock$size.portalBlockCount == 0) {
			return portalblock$size;
		} else {
			PortalBlock.Size portalblock$size1 = new PortalBlock.Size(p_201816_0_, worldIn, Direction.Axis.Z);
			return portalblock$size1.isValid() && portalblock$size1.portalBlockCount == 0 ? portalblock$size1 : null;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
			BlockPos currentPos, BlockPos facingPos) {
		Direction.Axis direction$axis = facing.getAxis();
		Direction.Axis direction$axis1 = stateIn.get(BlockStateProperties.HORIZONTAL_AXIS);
		boolean flag = direction$axis1 != direction$axis && direction$axis.isHorizontal();
		return !flag && !facingState.isIn(this)
				&& !(new PortalBlock.Size(worldIn, currentPos, direction$axis1)).func_208508_f()
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
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) { // TODO change particle
																							// colors
		for (int i = 0; i < 4; ++i) {
			double d0 = (double) pos.getX() + rand.nextDouble();
			double d1 = (double) pos.getY() + rand.nextDouble();
			double d2 = (double) pos.getZ() + rand.nextDouble();
			double d3 = ((double) rand.nextFloat() - 0.5D) * 0.5D;
			double d4 = ((double) rand.nextFloat() - 0.5D) * 0.5D;
			double d5 = ((double) rand.nextFloat() - 0.5D) * 0.5D;
			int j = rand.nextInt(2) * 2 - 1;
			if (!worldIn.getBlockState(pos.west()).isIn(this) && !worldIn.getBlockState(pos.east()).isIn(this)) {
				d0 = (double) pos.getX() + 0.5D + 0.25D * (double) j;
				d3 = (double) (rand.nextFloat() * 2.0F * (float) j);
			} else {
				d2 = (double) pos.getZ() + 0.5D + 0.25D * (double) j;
				d5 = (double) (rand.nextFloat() * 2.0F * (float) j);
			}
			worldIn.addParticle(ObjectHolder.PORTAL_PARTICLE, d0, d1, d2, d3, d4, d5);
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

	public static class Size {
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

			for (BlockPos blockpos = pos; pos.getY() > blockpos.getY() - 21 && pos.getY() > 0
					&& this.func_196900_a(reader.getBlockState(pos.down())); pos = pos.down()) {
			}

			int i = this.getDistanceUntilEdge(pos, this.leftDir, reader) - 1;
			if (i >= 0) {
				this.bottomLeft = pos.offset(this.leftDir, i);
				this.width = this.getDistanceUntilEdge(this.bottomLeft, this.rightDir, reader);
				if (this.width < 2 || this.width > 21) {
					this.bottomLeft = null;
					this.width = 0;
				}
			}

			if (this.bottomLeft != null) {
				this.height = this.calculatePortalHeight(reader);
			}

		}

		protected int getDistanceUntilEdge(BlockPos pos, Direction directionIn, IBlockDisplayReader reader) {
			int i;
			for (i = 0; i < 22; ++i) {
				BlockPos blockpos = pos.offset(directionIn, i);
				if (!this.func_196900_a(reader.getBlockState(blockpos)) || !isPortalFrame(reader, blockpos.down())) {
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
			label56: for (this.height = 0; this.height < 21; ++this.height) {
				for (int i = 0; i < this.width; ++i) {
					BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i).up(this.height);
					BlockState blockstate = reader.getBlockState(blockpos);
					if (!this.func_196900_a(blockstate)) {
						break label56;
					}

					if (blockstate.isIn(getPortalBlock())) {
						++this.portalBlockCount;
					}

					if (i == 0) {
						if (!isPortalFrame(reader, blockpos.offset(this.leftDir))) {
							break label56;
						}
					} else if (i == this.width - 1 && !isPortalFrame(reader, blockpos.offset(this.rightDir))) {
						break label56;
					}
				}
			}

			for (int j = 0; j < this.width; ++j) {
				if (!isPortalFrame(reader, this.bottomLeft.offset(this.rightDir, j).up(this.height))) {
					this.height = 0;
					break;
				}
			}

			if (this.height <= 21 && this.height >= 3) {
				return this.height;
			} else {
				this.bottomLeft = null;
				this.width = 0;
				this.height = 0;
				return 0;
			}
		}

		@SuppressWarnings("deprecation")
		protected boolean func_196900_a(BlockState state) {
			return state.isAir() || state.func_235714_a_(BlockTags.field_232872_am_) || state.isIn(getPortalBlock());
		}

		public boolean isValid() {
			return this.bottomLeft != null && this.width >= 2 && this.width <= 21 && this.height >= 3
					&& this.height <= 21;
		}

		public void placePortalBlocks(IWorld world) {
			for (int i = 0; i < this.width; ++i) {
				BlockPos blockpos = this.bottomLeft.offset(this.rightDir, i);

				for (int j = 0; j < this.height; ++j) {
					world.setBlockState(blockpos.up(j),
							getPortalBlock().getDefaultState().with(BlockStateProperties.HORIZONTAL_AXIS, this.axis),
							18);
				}
			}

		}

		private boolean func_196899_f() {
			return this.portalBlockCount >= this.width * this.height;
		}

		public boolean func_208508_f() {
			return this.isValid() && this.func_196899_f();
		}

		public static boolean isPortalFrame(IBlockDisplayReader world, BlockPos pos) {
			return world.getBlockState(pos).getBlock() instanceof PortalFrameBlock;
		}

		public static Block getPortalBlock() {
			return ObjectHolder.PORTAL_BLOCK;
		}

		public BlockPos[] getEdges() {
			BlockPos[] edges = new BlockPos[this.height * 2 + this.width * 2];
			for (int i = 0; i < this.height; i++) {
				edges[i] = this.bottomLeft.offset(this.leftDir, 1).up(i);
			}
			for (int i = 0; i < this.height; i++) {
				edges[i + this.height] = this.bottomLeft.offset(this.rightDir, this.width).up(i);
			}
			for (int i = 0; i < this.width; i++) {
				edges[i + this.height * 2] = this.bottomLeft.up(this.height).offset(this.rightDir, i);
			}
			for (int i = 0; i < this.width; i++) {
				edges[i + this.height * 2 + this.width] = this.bottomLeft.down().offset(this.rightDir, i);
			}
			return edges;
		}
	}
}
