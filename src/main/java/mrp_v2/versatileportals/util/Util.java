package mrp_v2.versatileportals.util;

import com.google.common.collect.ImmutableMap;
import mrp_v2.versatileportals.common.capabilities.CapabilityHandler;
import mrp_v2.versatileportals.common.capabilities.IPortalDataCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

public class Util {
    public static final ImmutableMap<Direction.Axis, Pair<Direction.Axis, Direction.Axis>> OTHER_AXES_MAP = ImmutableMap
            .of(Direction.Axis.X, getOtherOxes(Direction.Axis.X), Direction.Axis.Y, getOtherOxes(Direction.Axis.Y),
                    Direction.Axis.Z, getOtherOxes(Direction.Axis.Z));

    private static Pair<Direction.Axis, Direction.Axis> getOtherOxes(Direction.Axis axis) {
        return switch (axis) {
            case X -> Pair.of(Direction.Axis.Y, Direction.Axis.Z);
            case Y -> Pair.of(Direction.Axis.X, Direction.Axis.Z);
            case Z -> Pair.of(Direction.Axis.X, Direction.Axis.Y);
        };
    }

    public static int iGetColorR(int color) {
        return (color & 0xFF0000) >> 16;
    }

    public static int iGetColorG(int color) {
        return (color & 0x00FF00) >> 8;
    }

    public static int iGetColorB(int color) {
        return color & 0x0000FF;
    }

    public static float fGetColorR(int color) {
        return ((color & 0xFF0000) >> 16) / 255.0F;
    }

    public static float fGetColorG(int color) {
        return ((color & 0x00FF00) >> 8) / 255.0F;
    }

    public static float fGetColorB(int color) {
        return (color & 0x0000FF) / 255.0F;
    }

    public static int createColor(int r, int g, int b) {
        return (r << 16) | (g << 8) | b;
    }

    public static IPortalDataCapability getPortalData(Entity entity) {
        return entity.getCapability(CapabilityHandler.GetPortalDataCapability()).orElse(null);
    }

    @Nullable
    public static ResourceKey<Level> createWorldKey(String worldID) {
        if (worldID.isEmpty()) {
            return null;
        }
        return ResourceKey.create(Registries.DIMENSION, new ResourceLocation(worldID));
    }

    public static String getWorldID(Level world) {
        return getWorldID(world.dimension());
    }

    public static String getWorldID(ResourceKey<Level> world) {
        return world.location().toString();
    }

    public static BlockPos[] getCollidingBlocks(AABB box) {
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
        for (int i = 0; i < xSize; i++) {
            int x = i + minX;
            for (int j = 0; j < ySize; j++) {
                int y = j + minY;
                for (int k = 0; k < zSize; k++) {
                    int z = k + minZ;
                    blocks[(i * ySize + j) * zSize + k] = new BlockPos(x, y, z);
                }
            }
        }
        return blocks;
    }

    @SafeVarargs
    public static <T> T[] makeArray(T... objects) {
        return objects;
    }

    @SuppressWarnings("unchecked")
    @SafeVarargs
    public static <L, R> Pair<L, R>[] mergePairArrays(Pair<L, R>[]... arrays) {
        int totalLength = 0;
        for (Pair<L, R>[] array : arrays) {
            totalLength += array.length;
        }
        Pair<L, R>[] mergedArray = new Pair[totalLength];
        int i = 0;
        for (Pair<L, R>[] array : arrays) {
            for (Pair<L, R> obj : array) {
                mergedArray[i++] = obj;
            }
        }
        return mergedArray;
    }

    public static void sendMessage(Player player, Component message) {
        player.displayClientMessage(message, true);
    }
}
