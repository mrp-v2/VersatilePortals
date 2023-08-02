package mrp_v2.versatileportals.item;

import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.block.util.PortalSize;
import mrp_v2.versatileportals.util.ObjectHolder;
import mrp_v2.versatileportals.util.Util;
import mrp_v2.versatileportals.world.BasicWorldTeleporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;

public class ExistingWorldControlItem extends BasicSingleItem implements IPortalControlItem
{
    public static final String ID = "existing_world_control";
    public static final String COLOR_NBT_ID = "Color";
    public static final String WORLD_ID_NBT_ID = "WorldID";
    public static final TranslatableComponent worldDoesNotExist =
            new TranslatableComponent("item." + VersatilePortals.ID + "." + ID + ".message.worldDoesNotExist"),
            noTeleportSelf =
                    new TranslatableComponent("item." + VersatilePortals.ID + "." + ID + ".message.noTeleportSelf");

    public ExistingWorldControlItem()
    {
        //noinspection ConstantConditions
        super(properties -> properties.tab(null));
    }

    public static int getColorDataFromItem(ItemStack stack)
    {
        CompoundTag compound = stack.getOrCreateTag();
        return compound.contains(COLOR_NBT_ID) ? compound.getInt(COLOR_NBT_ID) : 0x808080;
    }

    public static ItemStack getItemForWorld(Level world)
    {
        ItemStack itemStack = new ItemStack(ObjectHolder.EXISTING_WORLD_TELEPORT_ITEM.get());
        addTeleportDataToItem(itemStack, new ResourceLocation(Util.getWorldID(world)));
        ExistingWorldControlItem.addColorDataToItem(itemStack, getColorFromWorld(world));
        return itemStack;
    }

    public static void addColorDataToItem(ItemStack stack, int color)
    {
        CompoundTag compound = stack.getOrCreateTag();
        compound.putInt(COLOR_NBT_ID, color);
        stack.setTag(compound);
    }

    private static int getColorFromWorld(Level world)
    {
        return world.dimension().toString().hashCode() & 0xFFFFFF;
    }

    public static void addTeleportDataToItem(ItemStack stack, ResourceLocation worldID)
    {
        CompoundTag compound = stack.getOrCreateTag();
        compound.putString(WORLD_ID_NBT_ID, worldID.toString());
        stack.setTag(compound);
        stack.setHoverName(new TextComponent(worldID.getPath()));
    }

    public static ResourceKey<Level> getTeleportDestination(ItemStack stack)
    {
        String worldID = stack.getOrCreateTag().getString(WORLD_ID_NBT_ID);
        return Util.createWorldKey(worldID);
    }

    @Nullable @Override
    public Entity teleportEntity(Entity entity, ServerLevel currentWorld, PortalSize portalSize, ItemStack itemStack)
    {
        ServerLevel destinationWorld = currentWorld.getServer().getLevel(getTeleportDestination(itemStack));
        if (destinationWorld == null)
        {
            if (entity instanceof ServerPlayer)
            {
                Util.sendMessage((ServerPlayer) entity, worldDoesNotExist);
            }
            return null;
        }
        if (destinationWorld == currentWorld)
        {
            if (entity instanceof ServerPlayer)
            {
                Util.sendMessage((ServerPlayer) entity, noTeleportSelf);
            }
            return null;
        }
        return entity.changeDimension(destinationWorld,
                new BasicWorldTeleporter(destinationWorld, currentWorld, portalSize));
    }
}
