package mrp_v2.randomdimensions.world;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.chunk.listener.ChainedChunkStatusListener;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.chunk.listener.LoggingChunkStatusListener;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DerivedWorldInfo;

public class Util
{
    public static void makeWorld(MinecraftServer server, RegistryKey<Dimension> dimensionKey, Dimension dimension)
    {
        IChunkStatusListener chunkStatusListener;
        if (server.isDedicatedServer())
        {
            chunkStatusListener = new LoggingChunkStatusListener(11);
        } else
        {
            chunkStatusListener = new ChainedChunkStatusListener(Minecraft.getInstance().refChunkStatusListener.get(),
                    Minecraft.getInstance().queueChunkTracking::add);
        }
        boolean isDebugChunkGenerator = server.field_240768_i_.getDimensionGeneratorSettings().func_236227_h_();
        long seed = BiomeManager.func_235200_a_(server.field_240768_i_.getDimensionGeneratorSettings().getSeed());
        RegistryKey<World> worldKey = RegistryKey.func_240903_a_(Registry.WORLD_KEY, dimensionKey.func_240901_a_());
        DimensionType dimensionType = dimension.getDimensionType();
        ChunkGenerator chunkGenerator = dimension.getChunkGenerator();
        DerivedWorldInfo derivedWorldInfo =
                new DerivedWorldInfo(server.field_240768_i_, server.field_240768_i_.func_230407_G_());
        ServerWorld serverWorld =
                new ServerWorld(server, server.backgroundExecutor, server.anvilConverterForAnvilFile, derivedWorldInfo,
                        worldKey, dimensionType, chunkStatusListener, chunkGenerator, isDebugChunkGenerator, seed,
                        ImmutableList.of(), false);
        server.forgeGetWorldMap()
                .get(World.field_234918_g_)
                .getWorldBorder()
                .addListener(new IBorderListener.Impl(serverWorld.getWorldBorder()));
        server.forgeGetWorldMap().put(worldKey, serverWorld);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(
                new net.minecraftforge.event.world.WorldEvent.Load(server.forgeGetWorldMap().get(dimensionKey)));
        server.markWorldsDirty();
    }
}
