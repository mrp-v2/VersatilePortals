package mrp_v2.versatileportals.item;

import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.block.util.PortalSize;
import mrp_v2.versatileportals.util.ObjectHolder;
import mrp_v2.versatileportals.util.Util;
import mrp_v2.versatileportals.world.BasicWorldTeleporter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;

public class ExistingWorldControlItem extends BasicSingleItem implements IPortalControlItem
{
    public static final String ID = "existing_world_control";
    public static final String COLOR_NBT_ID = "Color";
    public static final String WORLD_ID_NBT_ID = "WorldID";
    public static final TranslationTextComponent worldDoesNotExist =
            new TranslationTextComponent("item." + VersatilePortals.ID + "." + ID + ".message.worldDoesNotExist"),
            noTeleportSelf =
                    new TranslationTextComponent("item." + VersatilePortals.ID + "." + ID + ".message.noTeleportSelf");

    public ExistingWorldControlItem()
    {
        //noinspection ConstantConditions
        super(properties -> properties.group(null));
    }

    public static int getColorDataFromItem(ItemStack stack)
    {
        CompoundNBT compound = stack.getOrCreateTag();
        return compound.contains(COLOR_NBT_ID) ? compound.getInt(COLOR_NBT_ID) : 0x808080;
    }

    public static ItemStack getItemForWorld(World world)
    {
        ItemStack itemStack = new ItemStack(ObjectHolder.EXISTING_WORLD_TELEPORT_ITEM.get());
        addTeleportDataToItem(itemStack, new ResourceLocation(Util.getWorldID(world)));
        ExistingWorldControlItem.addColorDataToItem(itemStack, getColorFromWorld(world));
        return itemStack;
    }

    public static void addColorDataToItem(ItemStack stack, int color)
    {
        CompoundNBT compound = stack.getOrCreateTag();
        compound.putInt(COLOR_NBT_ID, color);
        stack.setTag(compound);
    }

    private static int getColorFromWorld(World world)
    {
        return world.getDimensionKey().toString().hashCode() & 0xFFFFFF;
    }

    public static void addTeleportDataToItem(ItemStack stack, ResourceLocation worldID)
    {
        CompoundNBT compound = stack.getOrCreateTag();
        compound.putString(WORLD_ID_NBT_ID, worldID.toString());
        stack.setTag(compound);
        stack.setDisplayName(new StringTextComponent(worldID.getPath()));
    }

    @Nullable @Override
    public Entity teleportEntity(Entity entity, ServerWorld currentWorld, PortalSize portalSize, ItemStack itemStack)
    {
        ServerWorld destinationWorld = currentWorld.getServer().getWorld(getTeleportDestination(itemStack));
        if (destinationWorld == null)
        {
            if (entity instanceof ServerPlayerEntity)
            {
                Util.sendMessage((ServerPlayerEntity) entity, worldDoesNotExist);
            }
            return null;
        }
        if (destinationWorld == currentWorld)
        {
            if (entity instanceof ServerPlayerEntity)
            {
                Util.sendMessage((ServerPlayerEntity) entity, noTeleportSelf);
            }
            return null;
        }
        return entity.changeDimension(destinationWorld,
                new BasicWorldTeleporter(destinationWorld, currentWorld, portalSize));
    }

    public static RegistryKey<World> getTeleportDestination(ItemStack stack)
    {
        String worldID = stack.getOrCreateTag().getString(WORLD_ID_NBT_ID);
        return Util.createWorldKey(worldID);
    }
}
