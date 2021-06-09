package mrp_v2.versatileportals.network;

import mrp_v2.versatileportals.VersatilePortals;
import mrp_v2.versatileportals.block.util.PortalFrameUtil;
import mrp_v2.versatileportals.block.util.PortalSize;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class PortalFrameUpdatePacket
{
    private final BlockPos pos;
    private final List<PortalSize> sizes;

    public PortalFrameUpdatePacket(BlockPos pos, List<PortalSize> sizes)
    {
        this.pos = pos;
        this.sizes = sizes;
    }

    public PortalFrameUpdatePacket(PacketBuffer buffer)
    {
        this.pos = buffer.readBlockPos();
        this.sizes = PortalSize.readListFromBuffer(buffer);
    }

    public void encode(PacketBuffer buffer)
    {
        buffer.writeBlockPos(this.pos);
        PortalSize.writeListToBuffer(this.sizes, buffer);
    }

    public void handle(Supplier<NetworkEvent.Context> context)
    {
        context.get().enqueueWork(() ->
        {
            World world = VersatilePortals.WORLD_SUPPLIER.get();
            if (world != null)
            {
                if (world.hasChunkAt(this.pos))
                {
                    PortalFrameUtil.updatePortals(this.sizes);
                }
            }
        });
        context.get().setPacketHandled(true);
    }
}
