package rbasamoyai.createbigcannons.network;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import rbasamoyai.createbigcannons.munitions.fuzes.ProximityFuzeContainer;

public class ServerboundProximityFuzePacket implements C2SPacket {

	private final int distance;

	public ServerboundProximityFuzePacket(int distance) {
		this.distance = distance;
	}

	public ServerboundProximityFuzePacket(FriendlyByteBuf buf) {
		this.distance = buf.readVarInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeVarInt(this.distance);
	}

	@Override
	public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl listener, PacketSender responseSender, SimpleChannel channel) {
		server.execute(() -> {
			if (player.containerMenu instanceof ProximityFuzeContainer ct) {
				ct.setDistance(this.distance);
			}
		});
	}
}
