package mrp_v2.versatileportals.world;

import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.block.PortalBlock;
import mrp_v2.versatileportals.block.PortalControllerBlock;
import mrp_v2.versatileportals.block.PortalSize;
import mrp_v2.versatileportals.common.capabilities.IPortalDataCapability;
import mrp_v2.versatileportals.datagen.EN_USTranslationGenerator;
import mrp_v2.versatileportals.tileentity.PortalControllerTileEntity;
import mrp_v2.versatileportals.util.ObjectHolder;
import mrp_v2.versatileportals.util.Util;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.WorldTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber public class EventHandler
{
    public static final TranslationTextComponent noPortalController, noKey, worldDoesNotExist;

    static
    {
        noPortalController = EN_USTranslationGenerator.makeTextTranslation(
                String.join(".", "block", VersatilePortals.ID, PortalBlock.ID, "message", "noPortalController"),
                "A Portal Controller could not be found for this portal");
        String stem = String.join(".", "block", VersatilePortals.ID, PortalControllerBlock.ID, "message",
                "invalidControlItem");
        noKey = EN_USTranslationGenerator.makeTextTranslation(stem + ".hasNoKey",
                "There is no control item or it is invalid");
        worldDoesNotExist = EN_USTranslationGenerator.makeTextTranslation(stem + ".worldDoesNotExist",
                "There is no world matching the control item");
    }

    @SubscribeEvent public static void worldTick(final WorldTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START)
        {
            return;
        }
        if (event.world instanceof ServerWorld)
        {
            ServerWorld world = (ServerWorld) event.world;
            world.getEntities().forEach((entity) ->
            {
                if (entity == null)
                {
                    return;
                }
                if (!entity.isAlive())
                {
                    return;
                }
                IPortalDataCapability portalData = Util.getPortalData(entity);
                boolean collidingWithPortal = false;
                BlockPos portalBlockPos = null;
                if (!entity.isPassenger() && !entity.isBeingRidden() && entity.isNonBoss())
                {
                    for (BlockPos pos : Util.getCollidingBlocks(entity.getBoundingBox()))
                    {
                        if (world.getBlockState(pos).getBlock() instanceof PortalBlock)
                        {
                            if (entity.getBoundingBox()
                                    .intersects(
                                            ObjectHolder.PORTAL_BLOCK.getBoundingBox(world.getBlockState(pos), world,
                                                    pos)))
                            {
                                collidingWithPortal = true;
                                portalBlockPos = pos;
                                break;
                            }
                        }
                    }
                }
                if (collidingWithPortal)
                {
                    if (portalData.getRemainingPortalCooldown() > 0)
                    {
                        portalData.setRemainingPortalCooldown(entity.getPortalCooldown());
                        return;
                    }
                    if (portalData.incrementInPortalTime() >= entity.getMaxInPortalTime())
                    {
                        PortalSize portalSize = new PortalSize(world, portalBlockPos,
                                world.getBlockState(portalBlockPos).get(BlockStateProperties.HORIZONTAL_AXIS));
                        PortalControllerTileEntity controller = portalSize.getPortalController(world).getLeft();
                        if (controller == null)
                        {
                            if (entity instanceof ServerPlayerEntity)
                            {
                                Util.sendMessage((ServerPlayerEntity) entity, noPortalController);
                            }
                            return;
                        }
                        RegistryKey<World> destinationKey = controller.getTeleportDestination(world);
                        if (destinationKey == null)
                        {
                            if (entity instanceof ServerPlayerEntity)
                            {
                                Util.sendMessage((ServerPlayerEntity) entity, noKey);
                            }
                            return;
                        }
                        ServerWorld destinationWorld = world.getServer().getWorld(destinationKey);
                        if (destinationWorld == null)
                        {
                            if (entity instanceof ServerPlayerEntity)
                            {
                                Util.sendMessage((ServerPlayerEntity) entity, worldDoesNotExist);
                            }
                            return;
                        }
                        entity.changeDimension(destinationWorld, new Teleporter(destinationWorld, world, portalSize));
                    }
                } else
                {
                    portalData.decrementRemainingPortalCooldown();
                    portalData.setInPortalTime(0);
                }
            });
        }
    }
}
