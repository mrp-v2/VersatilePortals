package mrp_v2.versatileportals.network;

import mrp_v2.versatileportals.item.ExistingWorldControlItem;
import net.minecraft.nbt.IntTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ControlItemEditedPacket {

    private final int slot;
    private final int color;

    public ControlItemEditedPacket(int slot, int color) {
        this.slot = slot;
        this.color = color;
    }

    public ControlItemEditedPacket(FriendlyByteBuf buffer) {
        this.slot = buffer.readInt();
        this.color = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(slot);
        buffer.writeInt(color);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
        {
            ServerPlayer player = context.get().getSender();
            player.getInventory().getItem(slot).addTagElement(ExistingWorldControlItem.COLOR_NBT_ID, IntTag.valueOf(color));
        });
        context.get().setPacketHandled(true);
    }
}
