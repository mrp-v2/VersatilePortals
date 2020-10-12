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
import net.minecraft.world.*;
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

    @Override public ITickList<Block> getPendingBlockTicks()
    {
        return this.world.getPendingBlockTicks();
    }

    @Override public ITickList<Fluid> getPendingFluidTicks()
    {
        return this.world.getPendingFluidTicks();
    }

    @Override public IWorldInfo getWorldInfo()
    {
        return this.world.getWorldInfo();
    }

    @Override public DifficultyInstance getDifficultyForLocation(BlockPos pos)
    {
        return this.world.getDifficultyForLocation(pos);
    }

    @Override public AbstractChunkProvider getChunkProvider()
    {
        return this.world.getChunkProvider();
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

    @Override public void playEvent(@Nullable PlayerEntity player, int type, BlockPos pos, int data)
    {
        this.world.playEvent(player, type, pos, data);
    }

    public World getWorld()
    {
        return this.world;
    }

    @Override public float func_230487_a_(Direction direction, boolean b)
    {
        return this.world.func_230487_a_(direction, b);
    }

    @Override public WorldLightManager getLightManager()
    {
        return this.world.getLightManager();
    }

    @Nullable @Override public IChunk getChunk(int x, int z, ChunkStatus requiredStatus, boolean nonnull)
    {
        return this.world.getChunk(x, z, requiredStatus, nonnull);
    }

    @Override public int getHeight(Heightmap.Type heightmapType, int x, int z)
    {
        return this.world.getHeight(heightmapType, x, z);
    }

    @Override public int getSkylightSubtracted()
    {
        return this.world.getSkylightSubtracted();
    }

    @Override public BiomeManager getBiomeManager()
    {
        return this.world.getBiomeManager();
    }

    @Override public int getBlockColor(BlockPos blockPosIn, ColorResolver colorResolverIn)
    {
        return this.world.getBlockColor(blockPosIn, colorResolverIn);
    }

    @Override public Biome getNoiseBiomeRaw(int x, int y, int z)
    {
        return this.world.getNoiseBiomeRaw(x, y, z);
    }

    @Override public boolean isRemote()
    {
        return this.world.isRemote;
    }

    @Override public int getSeaLevel()
    {
        return this.world.getSeaLevel();
    }

    @Override public DimensionType getDimensionType()
    {
        return this.world.getDimensionType();
    }

    @Nullable @Override public TileEntity getTileEntity(BlockPos pos)
    {
        return this.world.getTileEntity(pos);
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

    @Override
    public List<Entity> getEntitiesInAABBexcluding(@Nullable Entity entityIn, AxisAlignedBB boundingBox,
            @Nullable Predicate<? super Entity> predicate)
    {
        return this.world.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);
    }

    @Override
    public <T extends Entity> List<T> getEntitiesWithinAABB(Class<? extends T> clazz, AxisAlignedBB aabb,
            @Nullable Predicate<? super T> filter)
    {
        return this.world.getEntitiesWithinAABB(clazz, aabb, filter);
    }

    @Override public List<? extends PlayerEntity> getPlayers()
    {
        return this.world.getPlayers();
    }

    @Override public boolean setBlockState(BlockPos pos, BlockState state, int flags, int recursionLeft)
    {
        return this.world.setBlockState(pos, state, flags, recursionLeft);
    }

    @Override public boolean removeBlock(BlockPos pos, boolean isMoving)
    {
        return this.world.removeBlock(pos, isMoving);
    }

    @Override public boolean destroyBlock(BlockPos pos, boolean dropBlock, @Nullable Entity entity, int recursionLeft)
    {
        return this.world.destroyBlock(pos, dropBlock, entity, recursionLeft);
    }

    @Override public boolean hasBlockState(BlockPos pos, Predicate<BlockState> state)
    {
        return this.world.hasBlockState(pos, state);
    }

    @Override public DynamicRegistries func_241828_r()
    {
        return this.world.func_241828_r();
    }
}
