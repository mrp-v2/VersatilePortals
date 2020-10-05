package mrp_v2.customteleporters.block;

import mrp_v2.customteleporters.CustomTeleporters;
import mrp_v2.customteleporters.item.BasicSingleItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class BasicBlock extends Block {

	protected BasicBlock(String id, Properties properties) {
		super(properties);
		this.setRegistryName(CustomTeleporters.ID, id);
	}

	public BlockItem createBlockItem() {
        BlockState defaultState = this.getDefaultState();
        BlockItem blockItem = new BlockItem(this,
                new Item.Properties().addToolType(this.getHarvestTool(defaultState), this.getHarvestLevel(defaultState))
                                     .group(BasicSingleItem.MAIN_ITEM_GROUP));
		blockItem.setRegistryName(this.getRegistryName());
		return blockItem;
	}
}
