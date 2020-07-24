package mrp_v2.randomdimensions.world;

import mrp_v2.randomdimensions.block.PortalBlock;
import mrp_v2.randomdimensions.common.capabilities.IPortalDataCapability;
import mrp_v2.randomdimensions.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class EventHandler {

	@SuppressWarnings("deprecation")
	@SubscribeEvent
	public static void worldTick(final WorldTickEvent event) {
		if (event.world instanceof ServerWorld) {
			ServerWorld serverWorld = (ServerWorld) event.world;
			serverWorld.getEntities().forEach((entity) -> {
				if (!entity.isAlive() || !serverWorld.isBlockLoaded(entity.getPosition())) {
					return;
				}
				IPortalDataCapability portalData = Util.getPortalData(entity);
				portalData.decrementRemainingPortalCooldown();
				boolean collidingWithPortal = false;
				for (BlockPos pos : Util.getCollidingBlocks(entity.getBoundingBox())) {
					if (serverWorld.getBlockState(pos).getBlock() instanceof PortalBlock) {
						collidingWithPortal = true;
						break;
					}
				}
				if (!collidingWithPortal) {
					portalData.setInPortal(false);
				}
			});
		}
	}
}
