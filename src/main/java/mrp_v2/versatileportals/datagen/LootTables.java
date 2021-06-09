package mrp_v2.versatileportals.datagen;

import mrp_v2.mrplibrary.datagen.BlockLootTables;
import mrp_v2.versatileportals.util.ObjectHolder;

public class LootTables extends BlockLootTables
{
    public LootTables()
    {
        this.addLootTable(ObjectHolder.PORTAL_FRAME_BLOCK.get(), this::dropSelf);
        this.addLootTable(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get(),
                (block) -> this.add(block, LootTables::createNameableBlockEntityTable));
    }
}
