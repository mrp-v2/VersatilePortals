package mrp_v2.versatileportals.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.ticks.LevelTickAccess;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class WorldWrapper implements LevelAccessor {
    private final Level world;
    private final BlockPos overridePos;
    private final BlockState overrideState;

    public WorldWrapper(Level world, @Nullable BlockPos overridePos, @Nullable BlockState overrideState) {
        this.world = world;
        this.overridePos = overridePos;
        this.overrideState = overrideState;
    }

    @Override
    public long nextSubTickCount() {
        return this.world.nextSubTickCount();
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return this.world.getBlockTicks();
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return this.world.getFluidTicks();
    }

    @Override
    public LevelData getLevelData() {
        return this.world.getLevelData();
    }

    @Override
    public DifficultyInstance getCurrentDifficultyAt(BlockPos pos) {
        return this.world.getCurrentDifficultyAt(pos);
    }

    @Nullable
    @Override
    public MinecraftServer getServer() {
        return this.world.getServer();
    }

    @Override
    public ChunkSource getChunkSource() {
        return this.world.getChunkSource();
    }

    @Override
    public Random getRandom() {
        return this.world.getRandom();
    }

    @Override
    public void playSound(@Nullable Player player, BlockPos pos, SoundEvent soundIn, SoundSource category,
                          float volume, float pitch) {
        this.world.playSound(player, pos, soundIn, category, volume, pitch);
    }

    @Override
    public void addParticle(ParticleOptions particleData, double x, double y, double z, double xSpeed, double ySpeed,
                            double zSpeed) {
        this.world.addParticle(particleData, x, y, z, xSpeed, ySpeed, zSpeed);
    }

    @Override
    public void levelEvent(@Nullable Player player, int type, BlockPos pos, int data) {
        this.world.levelEvent(player, type, pos, data);
    }

    @Override
    public void gameEvent(@Nullable Entity p_151549_, GameEvent p_151550_, BlockPos p_151551_) {
        this.world.gameEvent(p_151549_, p_151550_, p_151551_);
    }

    public Level getWorld() {
        return this.world;
    }

    @Override
    public float getShade(Direction direction, boolean b) {
        return this.world.getShade(direction, b);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return this.world.getLightEngine();
    }

    @Nullable
    @Override
    public ChunkAccess getChunk(int x, int z, ChunkStatus requiredStatus, boolean nonnull) {
        return this.world.getChunk(x, z, requiredStatus, nonnull);
    }

    @Override
    public int getHeight(Heightmap.Types heightmapType, int x, int z) {
        return this.world.getHeight(heightmapType, x, z);
    }

    @Override
    public int getSkyDarken() {
        return this.world.getSkyDarken();
    }

    @Override
    public BiomeManager getBiomeManager() {
        return this.world.getBiomeManager();
    }

    @Override
    public int getBlockTint(BlockPos blockPosIn, ColorResolver colorResolverIn) {
        return this.world.getBlockTint(blockPosIn, colorResolverIn);
    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int x, int y, int z) {
        return this.world.getUncachedNoiseBiome(x, y, z);
    }

    @Override
    public boolean isClientSide() {
        return this.world.isClientSide;
    }

    @Override
    public int getSeaLevel() {
        return this.world.getSeaLevel();
    }

    @Override
    public DimensionType dimensionType() {
        return this.world.dimensionType();
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return this.world.getBlockEntity(pos);
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        if (pos.equals(this.overridePos)) {
            return this.overrideState;
        }
        return this.world.getBlockState(pos);
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return this.world.getFluidState(pos);
    }

    @Override
    public WorldBorder getWorldBorder() {
        return this.world.getWorldBorder();
    }

    @Override
    public List<Entity> getEntities(@Nullable Entity entityIn, AABB boundingBox,
                                    @Nullable Predicate<? super Entity> predicate) {
        return this.world.getEntities(entityIn, boundingBox, predicate);
    }

    @Override
    public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> p_151464_, AABB p_151465_, Predicate<? super T> p_151466_) {
        return this.world.getEntities(p_151464_, p_151465_, p_151466_);
    }

    @Override
    public <T extends Entity> List<T> getEntitiesOfClass(Class<T> clazz, AABB aabb,
                                                         @Nullable Predicate<? super T> filter) {
        return this.world.getEntitiesOfClass(clazz, aabb, filter);
    }

    @Override
    public List<? extends Player> players() {
        return this.world.players();
    }

    @Override
    public boolean setBlock(BlockPos pos, BlockState state, int flags, int recursionLeft) {
        return this.world.setBlock(pos, state, flags, recursionLeft);
    }

    @Override
    public boolean removeBlock(BlockPos pos, boolean isMoving) {
        return this.world.removeBlock(pos, isMoving);
    }

    @Override
    public boolean destroyBlock(BlockPos pos, boolean dropBlock, @Nullable Entity entity, int recursionLeft) {
        return this.world.destroyBlock(pos, dropBlock, entity, recursionLeft);
    }

    @Override
    public boolean isStateAtPosition(BlockPos pos, Predicate<BlockState> state) {
        return this.world.isStateAtPosition(pos, state);
    }

    @Override
    public boolean isFluidAtPosition(BlockPos p_151584_, Predicate<FluidState> p_151585_) {
        return this.world.isFluidAtPosition(p_151584_, p_151585_);
    }

    @Override
    public RegistryAccess registryAccess() {
        return this.world.registryAccess();
    }
}
