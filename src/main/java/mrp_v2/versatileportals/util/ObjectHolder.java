package mrp_v2.versatileportals.util;

import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.block.PortalBlock;
import mrp_v2.versatileportals.block.PortalControllerBlock;
import mrp_v2.versatileportals.block.PortalFrameBlock;
import mrp_v2.versatileportals.common.capabilities.PortalDataStorage;
import mrp_v2.versatileportals.inventory.container.PortalControllerContainer;
import mrp_v2.versatileportals.item.EmptyExistingWorldControlItem;
import mrp_v2.versatileportals.item.ExistingWorldControlItem;
import mrp_v2.versatileportals.item.PortalLighter;
import mrp_v2.versatileportals.particles.PortalControllerParticleData;
import mrp_v2.versatileportals.particles.PortalParticleData;
import mrp_v2.versatileportals.tileentity.PortalControllerTileEntity;
import mrp_v2.versatileportals.village.PortalPointOfInterestType;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = VersatilePortals.ID) public class ObjectHolder
{
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, VersatilePortals.ID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, VersatilePortals.ID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, VersatilePortals.ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, VersatilePortals.ID);
    public static final DeferredRegister<PointOfInterestType> POIS =
            DeferredRegister.create(ForgeRegistries.POI_TYPES, VersatilePortals.ID);
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, VersatilePortals.ID);
    public static final RegistryObject<PortalBlock> PORTAL_BLOCK;
    public static final RegistryObject<PortalControllerBlock> PORTAL_CONTROLLER_BLOCK;
    public static final RegistryObject<PortalFrameBlock> PORTAL_FRAME_BLOCK;
    public static final RegistryObject<BlockItem> PORTAL_CONTROLLER_BLOCK_ITEM;
    public static final ItemGroup MAIN_ITEM_GROUP = new ItemGroup(VersatilePortals.ID)
    {
        @Override @OnlyIn(Dist.CLIENT) public ItemStack makeIcon()
        {
            return new ItemStack(PORTAL_CONTROLLER_BLOCK_ITEM.get());
        }
    };
    public static final RegistryObject<BlockItem> PORTAL_FRAME_BLOCK_ITEM;
    public static final RegistryObject<PortalLighter> PORTAL_LIGHTER_ITEM;
    public static final RegistryObject<EmptyExistingWorldControlItem> EMPTY_EXISTING_WORLD_TELEPORT_ITEM;
    public static final RegistryObject<ExistingWorldControlItem> EXISTING_WORLD_TELEPORT_ITEM;
    public static final RegistryObject<ContainerType<PortalControllerContainer>> PORTAL_CONTROLLER_CONTAINER_TYPE;
    public static final RegistryObject<ParticleType<PortalParticleData>> PORTAL_PARTICLE_TYPE;
    public static final RegistryObject<ParticleType<PortalControllerParticleData>> PORTAL_CONTROLLER_PARTICLE_TYPE;
    public static final PortalDataStorage PORTAL_DATA_STORAGE;
    public static final RegistryObject<PortalPointOfInterestType> PORTAL_POINT_OF_INTEREST_TYPE;
    public static final RegistryObject<TileEntityType<PortalControllerTileEntity>> PORTAL_CONTROLLER_TILE_ENTITY_TYPE;

    static
    {
        PORTAL_BLOCK = BLOCKS.register(PortalBlock.ID, PortalBlock::new);
        PORTAL_CONTROLLER_BLOCK = BLOCKS.register(PortalControllerBlock.ID, PortalControllerBlock::new);
        PORTAL_FRAME_BLOCK = BLOCKS.register(PortalFrameBlock.ID, PortalFrameBlock::new);
        PORTAL_CONTROLLER_BLOCK_ITEM =
                ITEMS.register(PortalControllerBlock.ID, () -> createBlockItem(PORTAL_CONTROLLER_BLOCK.get()));
        PORTAL_FRAME_BLOCK_ITEM = ITEMS.register(PortalFrameBlock.ID, () -> createBlockItem(PORTAL_FRAME_BLOCK.get()));
        PORTAL_LIGHTER_ITEM = ITEMS.register(PortalLighter.ID, PortalLighter::new);
        EMPTY_EXISTING_WORLD_TELEPORT_ITEM =
                ITEMS.register(EmptyExistingWorldControlItem.ID, EmptyExistingWorldControlItem::new);
        EXISTING_WORLD_TELEPORT_ITEM = ITEMS.register(ExistingWorldControlItem.ID, ExistingWorldControlItem::new);
        PORTAL_CONTROLLER_CONTAINER_TYPE =
                CONTAINERS.register(PortalControllerBlock.ID, PortalControllerContainer.Type::new);
        PORTAL_PARTICLE_TYPE = PARTICLES.register(PortalParticleData.ID, PortalParticleData::createParticleType);
        PORTAL_CONTROLLER_PARTICLE_TYPE =
                PARTICLES.register(PortalControllerParticleData.ID, PortalControllerParticleData::createParticleType);
        PORTAL_DATA_STORAGE = new PortalDataStorage();
        PORTAL_POINT_OF_INTEREST_TYPE = POIS.register(PortalPointOfInterestType.ID, PortalPointOfInterestType::new);
        PORTAL_CONTROLLER_TILE_ENTITY_TYPE =
                TILE_ENTITIES.register(PortalControllerTileEntity.ID, PortalControllerTileEntity::createTileEntity);
    }

    public static void registerListeners(IEventBus bus)
    {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        CONTAINERS.register(bus);
        PARTICLES.register(bus);
        POIS.register(bus);
        TILE_ENTITIES.register(bus);
    }

    public static BlockItem createBlockItem(Block block)
    {
        return new BlockItem(block, new Item.Properties().tab(MAIN_ITEM_GROUP));
    }
}
