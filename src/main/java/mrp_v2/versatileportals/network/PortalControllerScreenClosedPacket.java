package mrp_v2.versatileportals.network;

import mrp_v2.versatileportals.tileentity.PortalControllerTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

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

    public PortalControllerScreenClosedPacket(PacketBuffer buffer)
    {
        this.portalColor = buffer.readInt();
        this.pos = buffer.readBlockPos();
    }

    public void encode(PacketBuffer buffer)
    {
        buffer.writeInt(this.portalColor);
        buffer.writeBlockPos(this.pos);
    }

    @SuppressWarnings("deprecation") public void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() ->
        {
            ServerPlayerEntity player = context.get().getSender();
            if (player.world.isBlockLoaded(this.pos))
            {
                PortalControllerTileEntity portalController =
                        (PortalControllerTileEntity) player.world.getTileEntity(this.pos);
                if (portalController != null)
                {
                    portalController.setPortalColor(this.portalColor);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
