package mrp_v2.randomdimensions.util;

import com.google.common.collect.Sets;
import mrp_v2.randomdimensions.RandomDimensions;
import mrp_v2.randomdimensions.block.*;
import mrp_v2.randomdimensions.common.capabilities.PlayerPortalDataStorage;
import mrp_v2.randomdimensions.common.capabilities.PortalDataStorage;
import mrp_v2.randomdimensions.inventory.container.PortalControllerContainer;
import mrp_v2.randomdimensions.item.EmptyExistingWorldControlItem;
import mrp_v2.randomdimensions.item.ExistingWorldControlItem;
import mrp_v2.randomdimensions.item.PortalLighter;
import mrp_v2.randomdimensions.particles.PortalControllerParticleData;
import mrp_v2.randomdimensions.particles.PortalParticleData;
import mrp_v2.randomdimensions.tileentity.PortalControllerTileEntity;
import mrp_v2.randomdimensions.village.PortalPointOfInterestType;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.particles.ParticleType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.PointOfInterestType;

import java.util.Set;

public class ObjectHolder
{
    public static final PortalBlock PORTAL_BLOCK;
    public static final IndestructiblePortalBlock INDESTRUCTIBLE_PORTAL_BLOCK;
    public static final PortalControllerBlock PORTAL_CONTROLLER_BLOCK;
    public static final PortalFrameBlock PORTAL_FRAME_BLOCK;
    public static final IndestructiblePortalFrameBlock INDESTRUCTIBLE_PORTAL_FRAME_BLOCK;
    public static final BlockItem PORTAL_CONTROLLER_BLOCK_ITEM;
    public static final BlockItem PORTAL_FRAME_BLOCK_ITEM;
    public static final PortalLighter PORTAL_LIGHTER_ITEM;
    public static final EmptyExistingWorldControlItem EMPTY_EXISTING_WORLD_TELEPORT_ITEM;
    public static final ExistingWorldControlItem EXISTING_WORLD_TELEPORT_ITEM;
    public static final ContainerType<PortalControllerContainer> PORTAL_CONTROLLER_CONTAINER_TYPE;
    public static final ResourceLocation PORTAL_BLOCKS_LOCATION;
    public static final ResourceLocation PORTAL_FRAME_BLOCKS_LOCATION;
    public static final ResourceLocation PORTAL_CONTROLLER_BLOCKS_LOCATION;
    public static final ParticleType<PortalParticleData> PORTAL_PARTICLE_TYPE;
    public static final ParticleType<PortalControllerParticleData> PORTAL_CONTROLLER_PARTICLE_TYPE;
    public static final PlayerPortalDataStorage PLAYER_PORTAL_DATA_STORAGE;
    public static final PortalDataStorage PORTAL_DATA_STORAGE;
    public static final PortalPointOfInterestType PORTAL_POINT_OF_INTEREST_TYPE;
    public static final TileEntityType<PortalControllerTileEntity> PORTAL_CONTROLLER_TILE_ENTITY_TYPE;

    static
    {
        PORTAL_BLOCK = new PortalBlock();
        INDESTRUCTIBLE_PORTAL_BLOCK = new IndestructiblePortalBlock();
        PORTAL_CONTROLLER_BLOCK = new PortalControllerBlock();
        PORTAL_FRAME_BLOCK = new PortalFrameBlock();
        INDESTRUCTIBLE_PORTAL_FRAME_BLOCK = new IndestructiblePortalFrameBlock();
        PORTAL_CONTROLLER_BLOCK_ITEM = PORTAL_CONTROLLER_BLOCK.createBlockItem();
        PORTAL_FRAME_BLOCK_ITEM = PORTAL_FRAME_BLOCK.createBlockItem();
        PORTAL_LIGHTER_ITEM = new PortalLighter();
        EMPTY_EXISTING_WORLD_TELEPORT_ITEM = new EmptyExistingWorldControlItem();
        EXISTING_WORLD_TELEPORT_ITEM = new ExistingWorldControlItem();
        PORTAL_CONTROLLER_CONTAINER_TYPE = new PortalControllerContainer.Type();
        PORTAL_BLOCKS_LOCATION = new ResourceLocation(RandomDimensions.ID, "portals");
        PORTAL_FRAME_BLOCKS_LOCATION = new ResourceLocation(RandomDimensions.ID, "portal_frames");
        PORTAL_CONTROLLER_BLOCKS_LOCATION = new ResourceLocation(RandomDimensions.ID, "portal_controllers");
        PORTAL_PARTICLE_TYPE = PortalParticleData.createParticleType();
        PORTAL_CONTROLLER_PARTICLE_TYPE = PortalControllerParticleData.createParticleType();
        PLAYER_PORTAL_DATA_STORAGE = new PlayerPortalDataStorage();
        PORTAL_DATA_STORAGE = new PortalDataStorage();
        PORTAL_POINT_OF_INTEREST_TYPE = new PortalPointOfInterestType();
        PORTAL_CONTROLLER_TILE_ENTITY_TYPE = PortalControllerTileEntity.createTileEntity();
    }

    public static Set<BlockState> getPortalBlockStates()
    {
        Set<BlockState> states = Sets.newHashSet();
        states.addAll(PointOfInterestType.getAllStates(PORTAL_BLOCK));
        states.addAll(PointOfInterestType.getAllStates(INDESTRUCTIBLE_PORTAL_BLOCK));
        return states;
    }
}
