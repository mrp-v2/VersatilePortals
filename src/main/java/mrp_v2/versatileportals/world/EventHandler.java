package mrp_v2.versatileportals.world;

import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.block.PortalBlock;
import mrp_v2.versatileportals.block.PortalControllerBlock;
import mrp_v2.versatileportals.block.util.PortalSize;
import mrp_v2.versatileportals.common.capabilities.IPortalDataCapability;
import mrp_v2.versatileportals.item.IPortalControlItem;
import mrp_v2.versatileportals.tileentity.PortalControllerTileEntity;
import mrp_v2.versatileportals.util.Util;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

@EventBusSubscriber
public class EventHandler {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final TranslatableComponent noPortalController = new TranslatableComponent(
            "block." + VersatilePortals.ID + "." + PortalBlock.ID + ".message.noPortalController"), noControlItem =
            new TranslatableComponent(
                    "block." + VersatilePortals.ID + "." + PortalControllerBlock.ID + ".message.noControlItem"),
            teleported = new TranslatableComponent(
                    "block." + VersatilePortals.ID + "." + PortalControllerBlock.ID + ".message.teleported");
    public static final Function<Object[], TranslatableComponent> teleportingInFunction =
            (args) -> new TranslatableComponent(
                    "block." + VersatilePortals.ID + "." + PortalControllerBlock.ID + ".message.teleportingIn", args);

    @SubscribeEvent
    public static void worldTick(final WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        if (event.world instanceof ServerLevel world) {
            Iterable<Entity> relevantEntities = world.getEntities().getAll();
            for (Entity entity : relevantEntities) {
                if (entity == null) {
                    continue;
                }
                if (!entity.isAlive()) {
                    continue;
                }
                IPortalDataCapability portalData = Util.getPortalData(entity);
                if (portalData == null) {
                    LOGGER.debug("Could not get IPortalDataCapability for entity: " + entity);
                    continue;
                }
                if (portalData.getInPortal()) {
                    portalData.setInPortal(false);
                    if (portalData.getRemainingPortalCooldown() > 0) {
                        portalData.setRemainingPortalCooldown(entity.getDimensionChangingDelay());
                        return;
                    }
                    if (portalData.incrementInPortalTime() < entity.getPortalWaitTime()) {
                        if (entity.getPortalWaitTime() > 1) {
                            if (entity instanceof ServerPlayer) {
                                int remainingInPortalTime = entity.getPortalWaitTime() - portalData.getInPortalTime();
                                Util.sendMessage((ServerPlayer) entity, teleportingInFunction
                                        .apply(new Object[]{Math.ceil(remainingInPortalTime / 2.0F) / 10.0F}));
                            }
                        }
                    } else {
                        PortalSize portalSize = new PortalSize(world, portalData.getPortalPos(),
                                world.getBlockState(portalData.getPortalPos()).getValue(BlockStateProperties.AXIS));
                        PortalControllerTileEntity controller = portalSize.getPortalController(world).getLeft();
                        if (controller == null) {
                            if (entity instanceof ServerPlayer) {
                                Util.sendMessage((ServerPlayer) entity, noPortalController);
                            }
                            return;
                        }
                        ItemStack portalControlItemStack = controller.getControlItemStack();
                        IPortalControlItem portalControlItem = null;
                        if (!portalControlItemStack.isEmpty()) {
                            if (portalControlItemStack.getItem() instanceof IPortalControlItem) {
                                portalControlItem = (IPortalControlItem) portalControlItemStack.getItem();
                            }
                        }
                        if (portalControlItem == null) {
                            if (entity instanceof ServerPlayer) {
                                Util.sendMessage((ServerPlayer) entity, noControlItem);
                            }
                            return;
                        }
                        Entity teleportedEntity =
                                portalControlItem.teleportEntity(entity, world, portalSize, portalControlItemStack);
                        if (teleportedEntity instanceof ServerPlayer) {
                            Util.sendMessage((ServerPlayer) teleportedEntity, teleported);
                        }
                    }
                } else {
                    portalData.decrementRemainingPortalCooldown();
                    portalData.setInPortalTime(0);
                }
            }
        }
    }
}
