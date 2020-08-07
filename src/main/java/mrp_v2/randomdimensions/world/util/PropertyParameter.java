package mrp_v2.randomdimensions.world.util;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.Property;

import java.util.Set;

public abstract class PropertyParameter<T extends Comparable<T>>
{
    protected final Property<T> property;
    private final Set<Block> blocks;

    protected PropertyParameter(Property<T> property)
    {
        this.property = property;
        this.blocks = null;
    }

    protected PropertyParameter(Property<T> property, Block... blocks)
    {
        this.property = property;
        this.blocks = Sets.newHashSet(blocks);
    }

    public boolean matchesBlock(BlockState blockState)
    {
        if (blocks == null)
        {
            return true;
        }
        return blocks.contains(blockState.getBlock());
    }

    public boolean matchesProperty(BlockState blockState)
    {
        return blockState.hasProperty(this.property);
    }

    public abstract boolean isInvalid(BlockState blockState);

    /*public static class DoesNotHave<T extends Comparable<T>> extends PropertyParameter<T>
    {
        public DoesNotHave(Property<T> property)
        {
            super(property);
        }

        @Override
        public boolean isValid(BlockState blockState)
        {
            return !this.matchesProperty(blockState);
        }
    }*/

    public static class ValuesOrMissing<T extends Comparable<T>> extends PropertyParameter<T>
    {
        private final Set<T> values;
        private final boolean areValuesValid;

        public ValuesOrMissing(Property<T> property, boolean areValuesValid, T... values)
        {
            super(property);
            this.areValuesValid = areValuesValid;
            this.values = Sets.newHashSet(values);
        }

        public ValuesOrMissing(Property<T> property, Block[] blocks, boolean areValuesValid, T... values)
        {
            super(property, blocks);
            this.areValuesValid = areValuesValid;
            this.values = Sets.newHashSet(values);
        }

        @Override public boolean isInvalid(BlockState blockState)
        {
            if (!this.matchesProperty(blockState))
            {
                return false;
            }
            return matchesValues(blockState) != this.areValuesValid;
        }

        protected boolean matchesValues(BlockState blockState)
        {
            return this.values.contains(blockState.get(this.property));
        }
    }
}
