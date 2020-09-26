package mrp_v2.randomdimensions.util;

import mrp_v2.randomdimensions.RandomDimensions;
import mrp_v2.randomdimensions.block.PortalBlock;
import mrp_v2.randomdimensions.block.PortalControllerBlock;
import mrp_v2.randomdimensions.block.PortalFrameBlock;
import mrp_v2.randomdimensions.common.capabilities.PortalDataStorage;
import mrp_v2.randomdimensions.inventory.container.PortalControllerContainer;
import mrp_v2.randomdimensions.item.EmptyExistingWorldControlItem;
import mrp_v2.randomdimensions.item.ExistingWorldControlItem;
import mrp_v2.randomdimensions.item.PortalLighter;
import mrp_v2.randomdimensions.item.RandomWorldControlItem;
import mrp_v2.randomdimensions.particles.PortalControllerParticleData;
import mrp_v2.randomdimensions.particles.PortalParticleData;
import mrp_v2.randomdimensions.tileentity.PortalControllerTileEntity;
import mrp_v2.randomdimensions.village.PortalPointOfInterestType;
import mrp_v2.randomdimensions.world.util.WorldUtil;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = RandomDimensions.ID) public class ObjectHolder
{
    public static final PortalBlock PORTAL_BLOCK;
    public static final PortalControllerBlock PORTAL_CONTROLLER_BLOCK;
    public static final PortalFrameBlock PORTAL_FRAME_BLOCK;
    public static final BlockItem PORTAL_CONTROLLER_BLOCK_ITEM;
    public static final BlockItem PORTAL_FRAME_BLOCK_ITEM;
    public static final PortalLighter PORTAL_LIGHTER_ITEM;
    public static final EmptyExistingWorldControlItem EMPTY_EXISTING_WORLD_TELEPORT_ITEM;
    public static final ExistingWorldControlItem EXISTING_WORLD_TELEPORT_ITEM;
    public static final RandomWorldControlItem RANDOM_WORLD_CONTROL_ITEM;
    public static final ContainerType<PortalControllerContainer> PORTAL_CONTROLLER_CONTAINER_TYPE;
    public static final ParticleType<PortalParticleData> PORTAL_PARTICLE_TYPE;
    public static final ParticleType<PortalControllerParticleData> PORTAL_CONTROLLER_PARTICLE_TYPE;
    public static final PortalDataStorage PORTAL_DATA_STORAGE;
    public static final PortalPointOfInterestType PORTAL_POINT_OF_INTEREST_TYPE;
    public static final TileEntityType<PortalControllerTileEntity> PORTAL_CONTROLLER_TILE_ENTITY_TYPE;

    static
    {
        WorldUtil.addInvalidBlockSupertypes(PortalBlock.class);
        PORTAL_BLOCK = new PortalBlock();
        PORTAL_CONTROLLER_BLOCK = new PortalControllerBlock();
        PORTAL_FRAME_BLOCK = new PortalFrameBlock();
        PORTAL_CONTROLLER_BLOCK_ITEM = PORTAL_CONTROLLER_BLOCK.createBlockItem();
        PORTAL_FRAME_BLOCK_ITEM = PORTAL_FRAME_BLOCK.createBlockItem();
        PORTAL_LIGHTER_ITEM = new PortalLighter();
        EMPTY_EXISTING_WORLD_TELEPORT_ITEM = new EmptyExistingWorldControlItem();
        EXISTING_WORLD_TELEPORT_ITEM = new ExistingWorldControlItem();
        RANDOM_WORLD_CONTROL_ITEM = new RandomWorldControlItem();
        PORTAL_CONTROLLER_CONTAINER_TYPE = new PortalControllerContainer.Type();
        PORTAL_PARTICLE_TYPE = PortalParticleData.createParticleType();
        PORTAL_CONTROLLER_PARTICLE_TYPE = PortalControllerParticleData.createParticleType();
        PORTAL_DATA_STORAGE = new PortalDataStorage();
        PORTAL_POINT_OF_INTEREST_TYPE = new PortalPointOfInterestType();
        PORTAL_CONTROLLER_TILE_ENTITY_TYPE = PortalControllerTileEntity.createTileEntity();
    }

    @SubscribeEvent public static void registerBlocks(final RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(PORTAL_BLOCK, PORTAL_CONTROLLER_BLOCK, PORTAL_FRAME_BLOCK);
    }

    @SubscribeEvent public static void registerItems(final RegistryEvent.Register<Item> event)
    {
        event.getRegistry()
                .registerAll(PORTAL_CONTROLLER_BLOCK_ITEM, PORTAL_FRAME_BLOCK_ITEM, PORTAL_LIGHTER_ITEM,
                        EMPTY_EXISTING_WORLD_TELEPORT_ITEM, EXISTING_WORLD_TELEPORT_ITEM, RANDOM_WORLD_CONTROL_ITEM);
    }

    @SubscribeEvent public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event)
    {
        event.getRegistry().registerAll(PORTAL_CONTROLLER_CONTAINER_TYPE);
    }

    @SubscribeEvent public static void registerParticles(final RegistryEvent.Register<ParticleType<?>> event)
    {
        event.getRegistry().registerAll(PORTAL_PARTICLE_TYPE, PORTAL_CONTROLLER_PARTICLE_TYPE);
    }

    @SubscribeEvent
    public static void registerPointOfInterestTypes(final RegistryEvent.Register<PointOfInterestType> event)
    {
        event.getRegistry().registerAll(PORTAL_POINT_OF_INTEREST_TYPE.register());
    }

    @SubscribeEvent public static void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event)
    {
        event.getRegistry().registerAll(PORTAL_CONTROLLER_TILE_ENTITY_TYPE);
    }
}
