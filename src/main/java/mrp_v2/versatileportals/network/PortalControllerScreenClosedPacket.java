package mrp_v2.versatileportals.network;

import mrp_v2.versatileportals.blockentity.PortalControllerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PortalControllerScreenClosedPacket
{
    private final int portalColor;
    private final BlockPos pos;

    public PortalControllerScreenClosedPacket(int portalColor, BlockPos pos)
    {
        this.portalColor = portalColor;
        this.pos = pos;
    }

    public PortalControllerScreenClosedPacket(FriendlyByteBuf buffer)
    {
        this.portalColor = buffer.readInt();
        this.pos = buffer.readBlockPos();
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeInt(this.portalColor);
        buffer.writeBlockPos(this.pos);
    }

    @SuppressWarnings("deprecation") public void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() ->
        {
            ServerPlayer player = context.get().getSender();
            if (player.level.hasChunkAt(this.pos))
            {
                PortalControllerBlockEntity portalController =
                        (PortalControllerBlockEntity) player.level.getBlockEntity(this.pos);
                if (portalController != null)
                {
                    portalController.setPortalColor(this.portalColor);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
