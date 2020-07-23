package mrp_v2.randomdimensions.network;

import java.util.function.Supplier;

import mrp_v2.randomdimensions.RandomDimensions;
import mrp_v2.randomdimensions.tileentity.PortalControllerTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Packet {

	public static class Handler {

		private static final String PROTOCOL_VERSION = "1";

		public static final String ID = "portal_color";

		public static SimpleChannel INSTANCE;

		private static int id = 0;

		public static void createChannel() {
			INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(RandomDimensions.ID, ID),
					() -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
			INSTANCE.registerMessage(id++, PortalColor.class, PortalColor::encode, PortalColor::decode,
					PortalColor::handle);
		}
	}

	public static class PortalColor {

		private int portalColor;
		private BlockPos pos;

		public PortalColor(int portalColor, BlockPos pos) {
			this.portalColor = portalColor;
			this.pos = pos;
		}

		public static void encode(PortalColor message, PacketBuffer buffer) {
			buffer.writeInt(message.portalColor);
			buffer.writeBlockPos(message.pos);
		}

		public static PortalColor decode(PacketBuffer buffer) {
			int portalColor = buffer.readInt();
			BlockPos pos = buffer.readBlockPos();
			return new PortalColor(portalColor, pos);
		}

		@SuppressWarnings("deprecation")
		public static void handle(PortalColor message, Supplier<NetworkEvent.Context> context) {
			context.get().enqueueWork(() -> {
				ServerPlayerEntity player = context.get().getSender();
				if (player.world.isBlockLoaded(message.pos)) {
					PortalControllerTileEntity portalController = (PortalControllerTileEntity) player.world
							.getTileEntity(message.pos);
					if (portalController != null) {
						portalController.setPortalColor(message.portalColor);
					}
				}
			});
			context.get().setPacketHandled(true);
		}
	}
}
