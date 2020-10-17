package mrp_v2.versatileportals.util;

import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.common.capabilities.CapabilityHandler;
import mrp_v2.versatileportals.common.capabilities.IPortalDataCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

public class Util
{
    public static TranslationTextComponent makeTranslation(String id)
    {
        return new TranslationTextComponent(VersatilePortals.ID + "." + id);
    }

    public static int iGetColorR(int color)
    {
        return (color & 0xFF0000) >> 16;
    }

    public static int iGetColorG(int color)
    {
        return (color & 0x00FF00) >> 8;
    }

    public static int iGetColorB(int color)
    {
        return color & 0x0000FF;
    }

    public static float fGetColorR(int color)
    {
        return ((color & 0xFF0000) >> 16) / 255.0F;
    }

    public static float fGetColorG(int color)
    {
        return ((color & 0x00FF00) >> 8) / 255.0F;
    }

    public static float fGetColorB(int color)
    {
        return (color & 0x0000FF) / 255.0F;
    }

    public static int createColor(int r, int g, int b)
    {
        return (r << 16) | (g << 8) | b;
    }

    public static IPortalDataCapability getPortalData(Entity entity)
    {
        return entity.getCapability(CapabilityHandler.PORTAL_DATA_CAPABILITY)
                .orElseThrow(() -> new RuntimeException("Could not get an IPortalDataCapability!"));
    }

    @Nullable public static RegistryKey<World> createWorldKey(String worldID)
    {
        if (worldID.isEmpty())
        {
            return null;
        }
        return RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(worldID));
    }

    public static String getWorldID(World world)
    {
        return getWorldID(world.getDimensionKey());
    }

    public static String getWorldID(RegistryKey<World> world)
    {
        return world.getLocation().toString();
    }

    public static BlockPos[] getCollidingBlocks(AxisAlignedBB box)
    {
        int minX = (int) Math.floor(box.minX);
        int minY = (int) Math.floor(box.minY);
        int minZ = (int) Math.floor(box.minZ);
        int maxX = (int) Math.ceil(box.maxX);
        int maxY = (int) Math.ceil(box.maxY);
        int maxZ = (int) Math.ceil(box.maxZ);
        int xSize = maxX - minX;
        int ySize = maxY - minY;
        int zSize = maxZ - minZ;
        BlockPos[] blocks = new BlockPos[xSize * ySize * zSize];
        for (int i = 0; i < xSize; i++)
        {
            int x = i + minX;
            for (int j = 0; j < ySize; j++)
            {
                int y = j + minY;
                for (int k = 0; k < zSize; k++)
                {
                    int z = k + minZ;
                    blocks[(i * ySize + j) * zSize + k] = new BlockPos(x, y, z);
                }
            }
        }
        return blocks;
    }

    @SafeVarargs public static <T> T[] makeArray(T... objects)
    {
        return objects;
    }

    @SafeVarargs public static <L, R> Pair<L, R>[] mergePairArrays(Pair<L, R>[]... arrays)
    {
        int totalLength = 0;
        for (Pair<L, R>[] array : arrays)
        {
            totalLength += array.length;
        }
        Pair<L, R>[] mergedArray = new Pair[totalLength];
        int i = 0;
        for (Pair<L, R>[] array : arrays)
        {
            for (Pair<L, R> obj : array)
            {
                mergedArray[i++] = obj;
            }
        }
        return mergedArray;
    }

    public static void sendMessage(ServerPlayerEntity player, ITextComponent message)
    {
        player.func_241151_a_(message, ChatType.GAME_INFO, net.minecraft.util.Util.DUMMY_UUID);
    }
}
