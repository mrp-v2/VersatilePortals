package mrp_v2.versatileportals.util;

import com.google.common.collect.ImmutableSet;
import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.block.PortalBlock;
import mrp_v2.versatileportals.block.PortalControllerBlock;
import mrp_v2.versatileportals.block.PortalFrameBlock;
import mrp_v2.versatileportals.blockentity.PortalControllerBlockEntity;
import mrp_v2.versatileportals.common.capabilities.PortalDataStorage;
import mrp_v2.versatileportals.inventory.container.PortalControllerMenu;
import mrp_v2.versatileportals.item.EmptyExistingWorldControlItem;
import mrp_v2.versatileportals.item.ExistingWorldControlItem;
import mrp_v2.versatileportals.item.PortalLighter;
import mrp_v2.versatileportals.particles.PortalControllerParticleData;
import mrp_v2.versatileportals.particles.PortalParticleData;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = VersatilePortals.ID)
public class ObjectHolder {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, VersatilePortals.ID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, VersatilePortals.ID);
    public static final DeferredRegister<MenuType<?>> CONTAINERS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, VersatilePortals.ID);
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, VersatilePortals.ID);
    public static final DeferredRegister<PoiType> POIS =
            DeferredRegister.create(ForgeRegistries.POI_TYPES, VersatilePortals.ID);
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, VersatilePortals.ID);
    public static final RegistryObject<PortalBlock> PORTAL_BLOCK;
    public static final RegistryObject<PortalControllerBlock> PORTAL_CONTROLLER_BLOCK;
    public static final RegistryObject<PortalFrameBlock> PORTAL_FRAME_BLOCK;
    public static final RegistryObject<BlockItem> PORTAL_CONTROLLER_BLOCK_ITEM;
    public static final RegistryObject<BlockItem> PORTAL_FRAME_BLOCK_ITEM;
    public static final RegistryObject<PortalLighter> PORTAL_LIGHTER_ITEM;
    public static final RegistryObject<EmptyExistingWorldControlItem> EMPTY_EXISTING_WORLD_TELEPORT_ITEM;
    public static final RegistryObject<ExistingWorldControlItem> EXISTING_WORLD_TELEPORT_ITEM;
    public static final RegistryObject<MenuType<PortalControllerMenu>> PORTAL_CONTROLLER_CONTAINER_TYPE;
    public static final RegistryObject<ParticleType<PortalParticleData>> PORTAL_PARTICLE_TYPE;
    public static final RegistryObject<ParticleType<PortalControllerParticleData>> PORTAL_CONTROLLER_PARTICLE_TYPE;
    public static final PortalDataStorage PORTAL_DATA_STORAGE;
    public static final ResourceKey<PoiType> VERSATILE_PORTAL_POI_KEY;
    public static final RegistryObject<PoiType> VERSATILE_PORTAL_POI_TYPE;
    public static final RegistryObject<BlockEntityType<PortalControllerBlockEntity>> PORTAL_CONTROLLER_TILE_ENTITY_TYPE;

    static {
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
                CONTAINERS.register(PortalControllerBlock.ID, PortalControllerMenu.Type::new);
        PORTAL_PARTICLE_TYPE = PARTICLES.register(PortalParticleData.ID, PortalParticleData::createParticleType);
        PORTAL_CONTROLLER_PARTICLE_TYPE =
                PARTICLES.register(PortalControllerParticleData.ID, PortalControllerParticleData::createParticleType);
        PORTAL_DATA_STORAGE = new PortalDataStorage();
        VERSATILE_PORTAL_POI_KEY = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, new ResourceLocation(VersatilePortals
                .ID, "versatile_portal"));
        VERSATILE_PORTAL_POI_TYPE = POIS.register(VERSATILE_PORTAL_POI_KEY.location().getPath(), () -> new PoiType(ImmutableSet.copyOf(ObjectHolder.PORTAL_BLOCK.get().getStateDefinition().getPossibleStates()), 0, 1));
        PORTAL_CONTROLLER_TILE_ENTITY_TYPE =
                TILE_ENTITIES.register(PortalControllerBlockEntity.ID, PortalControllerBlockEntity::createTileEntity);
    }

    public static void registerListeners(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        CONTAINERS.register(bus);
        PARTICLES.register(bus);
        POIS.register(bus);
        TILE_ENTITIES.register(bus);
    }

    public static BlockItem createBlockItem(Block block) {
        return new BlockItem(block, new Item.Properties());
    }
}
