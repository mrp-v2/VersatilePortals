package mrp_v2.randomdimensions.item;

import mrp_v2.randomdimensions.util.Util;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.function.Function;

public abstract class PortalControlItem extends BasicSingleItem
{
    public static final String WORLD_ID_NBT_ID = "WorldID";
    protected static final String ID = "control";

    protected PortalControlItem(String id, Function<Properties, Properties> propertiesModifier)
    {
        super(id, propertiesModifier);
    }

    public static void addTeleportDataToItem(ItemStack stack, ResourceLocation worldID)
    {
        CompoundNBT compound = stack.getOrCreateTag();
        compound.putString(WORLD_ID_NBT_ID, worldID.toString());
        stack.setTag(compound);
        stack.setDisplayName(new StringTextComponent(worldID.getPath()));
    }

    public static RegistryKey<World> getTeleportDestination(ItemStack stack, ServerWorld originWorld)
    {
        String worldID = stack.getOrCreateTag().getString(WORLD_ID_NBT_ID);
        return Util.createWorldKey(worldID);
    }
}
