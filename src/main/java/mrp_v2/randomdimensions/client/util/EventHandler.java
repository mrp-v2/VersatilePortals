package mrp_v2.randomdimensions.client.util;

import mrp_v2.randomdimensions.RandomDimensions;
import mrp_v2.randomdimensions.util.ObjectHolder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD, modid = RandomDimensions.ID)
public class EventHandler {

	/**
	 * @param event
	 */
	@SubscribeEvent
	public static void clientSetup(final FMLClientSetupEvent event) {
		RenderTypeLookup.setRenderLayer(ObjectHolder.PORTAL_BLOCK, RenderType.getTranslucent());
		RenderTypeLookup.setRenderLayer(ObjectHolder.INDESTRUCTIBLE_PORTAL_BLOCK, RenderType.getTranslucent());
	}
}
