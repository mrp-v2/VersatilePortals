package mrp_v2.randomdimensions.client.renderer.color;

import mrp_v2.randomdimensions.block.PortalBlock;
import mrp_v2.randomdimensions.tileentity.PortalControllerTileEntity;
import mrp_v2.randomdimensions.util.ObjectHolder;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PortalColorer implements IBlockColor {

	public static final PortalColorer INSTANCE = new PortalColorer();

	@Override
	public int getColor(BlockState state, IBlockDisplayReader blockReader, BlockPos pos, int tint) {
		if (state.isIn(ObjectHolder.PORTAL_BLOCK)) {
			return PortalBlock.getColor(state, blockReader, pos);
		}
		return PortalControllerTileEntity.DEFAULT_PORTAL_COLOR;
	}
}
