package mrp_v2.versatileportals.tileentity;

import com.mojang.serialization.DataResult;
import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.block.PortalControllerBlock;
import mrp_v2.versatileportals.block.util.PortalFrameUtil;
import mrp_v2.versatileportals.inventory.PortalControllerItemStackHandler;
import mrp_v2.versatileportals.inventory.container.PortalControllerMenu;
import mrp_v2.versatileportals.item.ExistingWorldControlItem;
import mrp_v2.versatileportals.tileentity.util.PortalControllerData;
import mrp_v2.versatileportals.util.ObjectHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class PortalControllerBlockEntity extends BlockEntity
        implements ICapabilityProvider, MenuProvider, Nameable {
    public static final String ID = PortalControllerBlock.ID;
    public static final int DEFAULT_PORTAL_COLOR = 0x00FF00;
    public static final int ERROR_PORTAL_COLOR = 0xFFFFFF;
    public static final int TICKS_PER_RENDER_REVOLUTION = 120;
    private static final String DATA_NBT_ID = "PortalControllerData";
    private final PortalControllerItemStackHandler inventory;
    private final LazyOptional<PortalControllerItemStackHandler> inventoryLazyOptional;
    public int ticks;
    private Component customName;
    private int portalColor;

    public PortalControllerBlockEntity(BlockPos pos, BlockState state) {
        super(ObjectHolder.PORTAL_CONTROLLER_TILE_ENTITY_TYPE.get(), pos, state);
        this.inventory = new PortalControllerItemStackHandler(this);
        this.inventoryLazyOptional = LazyOptional.of(() -> this.inventory);
        this.portalColor = DEFAULT_PORTAL_COLOR;
    }

    public static BlockEntityType<PortalControllerBlockEntity> createTileEntity() {
        return BlockEntityType.Builder.of(PortalControllerBlockEntity::new, ObjectHolder.PORTAL_CONTROLLER_BLOCK.get())
                .build(null);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, PortalControllerBlockEntity controller) {
        if (!controller.level.isClientSide) {
            return;
        }
        if (++controller.ticks >= TICKS_PER_RENDER_REVOLUTION) {
            controller.ticks = 0;
        }
        if (controller.ticks % 4 == 0) {
            if (controller.inventory.getStackInSlot(0).isEmpty()) {
                return;
            }
            PortalControllerBlock.animateTick(controller.getBlockState(), controller.level, controller.worldPosition);
        }
    }

    public PortalControllerItemStackHandler getInventory() {
        return inventory;
    }

    public ItemStack getControlItemStack() {
        return this.inventory.getStackInSlot(0);
    }

    @Nullable
    public ResourceKey<Level> getTeleportDestination() {
        if (this.inventory.getStackInSlot(0).isEmpty()) {
            return null;
        }
        return ExistingWorldControlItem.getTeleportDestination(this.inventory.getStackInSlot(0));
    }

    @Override
    public Component getName() {
        return this.customName != null ? this.customName : this.getDefaultName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.customName;
    }

    public void setCustomName(@Nullable Component name) {
        this.customName = name;
    }

    public Component getDefaultName() {
        return new TranslatableComponent(ObjectHolder.PORTAL_CONTROLLER_BLOCK.get().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventoryIn, Player playerIn) {
        return new PortalControllerMenu(id, playerInventoryIn, this.inventory, this.portalColor,
                this.getBlockPos());
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.inventoryLazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
        if (this.level.hasChunkAt(this.worldPosition)) {
            PortalFrameUtil.updatePortals(PortalFrameUtil.getPortalSizes(this.worldPosition, this.level, false));
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(DATA_NBT_ID, 10)) {
            CompoundTag dataNBT = tag.getCompound(DATA_NBT_ID);
            DataResult<PortalControllerData> dataResult =
                    PortalControllerData.CODEC.parse(NbtOps.INSTANCE, dataNBT);
            dataResult.resultOrPartial(VersatilePortals.LOGGER::error).ifPresent((data) ->
            {
                this.portalColor = data.getPortalColor();
                this.inventory.deserializeNBT(data.getInventoryData());
                this.customName = data.getCustomName();
            });
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        PortalControllerData.CODEC.encodeStart(NbtOps.INSTANCE, new PortalControllerData(this))
                .resultOrPartial(VersatilePortals.LOGGER::error).ifPresent((data) -> tag.put(DATA_NBT_ID, data));
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void setRemoved() {
        this.inventoryLazyOptional.invalidate();
        super.setRemoved();
    }

    public int getPortalColor() {
        return this.portalColor;
    }

    public void setPortalColor(int newPortalColor) {
        if (this.portalColor != newPortalColor) {
            this.portalColor = newPortalColor;
            this.setChanged();
            this.sendUpdateToClient();
        }
    }

    private void sendUpdateToClient() {
        BlockState state = this.getBlockState();
        this.level.sendBlockUpdated(this.getBlockPos(), state, state, 2);
    }

    public void onInventorySlotChanged() {
        this.setChanged();
        this.sendUpdateToClient();
    }

    @Override
    public Component getDisplayName() {
        return Nameable.super.getDisplayName();
    }
}
