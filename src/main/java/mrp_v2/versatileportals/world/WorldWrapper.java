package mrp_v2.versatileportals.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.particles.IParticleData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.DimensionType;
import net.minecraft.world.ITickList;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.lighting.WorldLightManager;
import net.minecraft.world.storage.IWorldInfo;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class WorldWrapper implements IWorld
{
    private final World world;
    private final BlockPos overridePos;
    private final BlockState overrideState;

    public WorldWrapper(World world, @Nullable BlockPos overridePos, @Nullable BlockState overrideState)
    {
        this.world = world;
        this.overridePos = overridePos;
        this.overrideState = overrideState;
    }

    @Override public ITickList<Block> getBlockTicks()
    {
        return this.world.getBlockTicks();
    }

    @Override public ITickList<Fluid> getLiquidTicks()
    {
        return this.world.getLiquidTicks();
    }

    @Override public IWorldInfo getLevelData()
    {
        return this.world.getLevelData();
    }

    @Override public DifficultyInstance getCurrentDifficultyAt(BlockPos pos)
    {
        return this.world.getCurrentDifficultyAt(pos);
    }

    @Override public AbstractChunkProvider getChunkSource()
    {
        return this.world.getChunkSource();
    }

    @Override public Random getRandom()
    {
        return this.world.getRandom();
    }

    @Override
    public void playSound(@Nullable PlayerEntity player, BlockPos pos, SoundEvent soundIn, SoundCategory category,
            float volume, float pitch)
    {
        this.world.playSound(player, pos, soundIn, category, volume, pitch);
    }

    @Override
    public void addParticle(IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed,
            double zSpeed)
    {
        this.world.addParticle(particleData, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    @Override public void levelEvent(@Nullable PlayerEntity player, int type, BlockPos pos, int data)
    {
        this.world.levelEvent(player, type, pos, data);
    }

    public World getWorld()
    {
        return this.world;
    }

    @Override public float getShade(Direction direction, boolean b)
    {
        return this.world.getShade(direction, b);
    }

    @Override public WorldLightManager getLightEngine()
    {
        return this.world.getLightEngine();
    }

    @Nullable @Override public IChunk getChunk(int x, int z, ChunkStatus requiredStatus, boolean nonnull)
    {
        return this.world.getChunk(x, z, requiredStatus, nonnull);
    }

    @Override public int getHeight(Heightmap.Type heightmapType, int x, int z)
    {
        return this.world.getHeight(heightmapType, x, z);
    }

    @Override public int getSkyDarken()
    {
        return this.world.getSkyDarken();
    }

    @Override public BiomeManager getBiomeManager()
    {
        return this.world.getBiomeManager();
    }

    @Override public int getBlockTint(BlockPos blockPosIn, ColorResolver colorResolverIn)
    {
        return this.world.getBlockTint(blockPosIn, colorResolverIn);
    }

    @Override public Biome getUncachedNoiseBiome(int x, int y, int z)
    {
        return this.world.getUncachedNoiseBiome(x, y, z);
    }

    @Override public boolean isClientSide()
    {
        return this.world.isClientSide;
    }

    @Override public int getSeaLevel()
    {
        return this.world.getSeaLevel();
    }

    @Override public DimensionType dimensionType()
    {
        return this.world.dimensionType();
    }

    @Nullable @Override public TileEntity getBlockEntity(BlockPos pos)
    {
        return this.world.getBlockEntity(pos);
    }

    @Override public BlockState getBlockState(BlockPos pos)
    {
        if (pos.equals(this.overridePos))
        {
            return this.overrideState;
        }
        return this.world.getBlockState(pos);
    }

    @Override public FluidState getFluidState(BlockPos pos)
    {
        return this.world.getFluidState(pos);
    }

    @Override public WorldBorder getWorldBorder()
    {
        return this.world.getWorldBorder();
    }

    @Override public List<Entity> getEntities(@Nullable Entity entityIn, AxisAlignedBB boundingBox,
            @Nullable Predicate<? super Entity> predicate)
    {
        return this.world.getEntities(entityIn, boundingBox, predicate);
    }

    @Override public <T extends Entity> List<T> getEntitiesOfClass(Class<? extends T> clazz, AxisAlignedBB aabb,
            @Nullable Predicate<? super T> filter)
    {
        return this.world.getEntitiesOfClass(clazz, aabb, filter);
    }

    @Override public List<? extends PlayerEntity> players()
    {
        return this.world.players();
    }

    @Override public boolean setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft)
    {
        return this.world.setBlock(pos, state, flags, recursionLeft);
    }

    @Override public boolean removeBlock(BlockPos pos, boolean isMoving)
    {
        return this.world.removeBlock(pos, isMoving);
    }

    @Override public boolean destroyBlock(BlockPos pos, boolean dropBlock, @Nullable Entity entity, int recursionLeft)
    {
        return this.world.destroyBlock(pos, dropBlock, entity, recursionLeft);
    }

    @Override public boolean isStateAtPosition(BlockPos pos, Predicate<BlockState> state)
    {
        return this.world.isStateAtPosition(pos, state);
    }

    @Override public DynamicRegistries registryAccess()
    {
        return this.world.registryAccess();
    }
}
