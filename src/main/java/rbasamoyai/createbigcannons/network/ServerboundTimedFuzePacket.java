package rbasamoyai.createbigcannons.network;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import rbasamoyai.createbigcannons.munitions.fuzes.TimedFuzeContainer;

public class ServerboundTimedFuzePacket implements C2SPacket {

	private final int time;

	public ServerboundTimedFuzePacket(int time) {
		this.time = time;
	}

	public ServerboundTimedFuzePacket(FriendlyByteBuf buf) {
		this.time = buf.readVarInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeVarInt(this.time);
	}

	@Override
	public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl listener, PacketSender responseSender, SimpleChannel channel) {
		server.execute(() -> {
			if (player.containerMenu instanceof TimedFuzeContainer timedFuzeContainer) {
				timedFuzeContainer.setTime(this.time);
			}
		});
	}

}
