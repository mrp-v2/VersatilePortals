package mrp_v2.versatileportals.util;

import com.google.common.collect.ImmutableMap;
import mrp_v2.versatileportals.common.capabilities.CapabilityHandler;
import mrp_v2.versatileportals.common.capabilities.IPortalDataCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

public class Util
{
    public static final ImmutableMap<Direction.Axis, Pair<Direction.Axis, Direction.Axis>> OTHER_AXES_MAP = ImmutableMap
            .of(Direction.Axis.X, getOtherOxes(Direction.Axis.X), Direction.Axis.Y, getOtherOxes(Direction.Axis.Y),
                    Direction.Axis.Z, getOtherOxes(Direction.Axis.Z));

    private static Pair<Direction.Axis, Direction.Axis> getOtherOxes(Direction.Axis axis)
    {
        switch (axis)
        {
            case X:
                return Pair.of(Direction.Axis.Y, Direction.Axis.Z);
            case Y:
                return Pair.of(Direction.Axis.X, Direction.Axis.Z);
            case Z:
                return Pair.of(Direction.Axis.X, Direction.Axis.Y);
            default:
                throw new IllegalArgumentException();
        }
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

    @Nullable public static IPortalDataCapability getPortalData(Entity entity)
    {
        //noinspection ConstantConditions
        return entity.getCapability(CapabilityHandler.PORTAL_DATA_CAPABILITY).orElse(null);
    }

    @Nullable public static RegistryKey<World> createWorldKey(String worldID)
    {
        if (worldID.isEmpty())
        {
            return null;
        }
        return RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(worldID));
    }

    public static String getWorldID(World world)
    {
        return getWorldID(world.dimension());
    }

    public static String getWorldID(RegistryKey<World> world)
    {
        return world.location().toString();
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

    @SuppressWarnings("unchecked") @SafeVarargs
    public static <L, R> Pair<L, R>[] mergePairArrays(Pair<L, R>[]... arrays)
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
        player.sendMessage(message, ChatType.GAME_INFO, net.minecraft.util.Util.NIL_UUID);
    }
}
