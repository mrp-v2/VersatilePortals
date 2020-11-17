package mrp_v2.versatileportals.datagen;

import mrp_v2.versatileportals.util.ObjectHolder;

public class LootTables extends mrp_v2.mrp_v2datagenlibrary.datagen.LootTables
{
    public LootTables()
    {
        this.addLootTable(ObjectHolder.PORTAL_FRAME_BLOCK.get(), this::registerDropSelfLootTable);
        this.addLootTable(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get(),
                (block) -> this.registerLootTable(block, LootTables::droppingWithName));
    }
}
