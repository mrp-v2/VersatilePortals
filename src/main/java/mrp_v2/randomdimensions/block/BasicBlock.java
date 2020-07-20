package mrp_v2.randomdimensions.block;

import mrp_v2.randomdimensions.RandomDimensions;
import mrp_v2.randomdimensions.item.BasicItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class BasicBlock extends Block {

	public BasicBlock(String id, Block baseBlock) {
		this(id, Properties.from(baseBlock));
	}
	
	public BasicBlock(String id, Properties properties) {
		super(properties);
		this.setRegistryName(RandomDimensions.ID, id);
	}

	public BlockItem createBlockItem() {
		BlockState defaultState = this.getDefaultState();
		BlockItem blockItem = new BlockItem(this,
				new Item.Properties().addToolType(this.getHarvestTool(defaultState), this.getHarvestLevel(defaultState))
						.group(BasicItem.MAIN_ITEM_GROUP));
		blockItem.setRegistryName(this.getRegistryName());
		return blockItem;
	}
}
