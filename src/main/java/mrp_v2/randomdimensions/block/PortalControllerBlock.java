package mrp_v2.randomdimensions.block;

import mrp_v2.randomdimensions.tileentity.PortalControllerTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class PortalControllerBlock extends PortalFrameBlock {

	public static final String ID = "portal_controller";
	public static final TranslationTextComponent CONTAINER_TRANSLATION = new TranslationTextComponent(
			"container." + ID);

	public PortalControllerBlock() {
		super(ID);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new PortalControllerTileEntity();
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof PortalControllerTileEntity) {
			((PortalControllerTileEntity) tileEntity).openMenu(player);
			return ActionResultType.func_233537_a_(worldIn.isRemote);
		} else {
			return ActionResultType.PASS;
		}
	}
}
