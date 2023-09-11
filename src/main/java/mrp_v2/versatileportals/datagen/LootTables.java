package mrp_v2.versatileportals.datagen;

import mrp_v2.mrplibrary.datagen.BlockLootTables;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Set;

public class LootTables extends BlockLootTables
{
    public LootTables()
    {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        dropSelf(ObjectHolder.PORTAL_FRAME_BLOCK.get());
        this.add(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get(), this::createNameableBlockEntityTable);
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return List.of(ObjectHolder.PORTAL_FRAME_BLOCK.get(), ObjectHolder.PORTAL_CONTROLLER_BLOCK.get());
    }
}
