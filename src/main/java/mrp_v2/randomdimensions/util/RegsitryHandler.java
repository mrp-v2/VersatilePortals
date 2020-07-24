package mrp_v2.randomdimensions.util;

import mrp_v2.randomdimensions.RandomDimensions;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.particles.ParticleType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = RandomDimensions.ID)
public class RegsitryHandler {

	@SubscribeEvent
	public static void registerBlocks(final RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(ObjectHolder.PORTAL_BLOCK, ObjectHolder.INDESTRUCTIBLE_PORTAL_BLOCK,
				ObjectHolder.PORTAL_CONTROLLER_BLOCK, ObjectHolder.PORTAL_FRAME_BLOCK,
				ObjectHolder.INDESTRUCTIBLE_PORTAL_FRAME_BLOCK);
	}

	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(ObjectHolder.PORTAL_CONTROLLER_BLOCK_ITEM, ObjectHolder.PORTAL_FRAME_BLOCK_ITEM,
				ObjectHolder.PORTAL_LIGHTER_ITEM);
	}

	@SubscribeEvent
	public static void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
		event.getRegistry().registerAll(ObjectHolder.PORTAL_CONTROLLER_CONTAINER_TYPE);
	}

	@SubscribeEvent
	public static void registerParicles(final RegistryEvent.Register<ParticleType<?>> event) {
		event.getRegistry().registerAll(ObjectHolder.PORTAL_PARTICLE_TYPE);
	}

	@SubscribeEvent
	public static void registerPointOfInterestTypes(final RegistryEvent.Register<PointOfInterestType> event) {
		event.getRegistry().registerAll(ObjectHolder.PORTAL_POINT_OF_INTEREST_TYPE);
	}

	@SubscribeEvent
	public static void registerTileEntites(final RegistryEvent.Register<TileEntityType<?>> event) {
		event.getRegistry().registerAll(ObjectHolder.PORTAL_CONTROLLER_TILE_ENTITY_TYPE);
	}
}
