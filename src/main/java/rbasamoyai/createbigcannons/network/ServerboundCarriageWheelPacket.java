package rbasamoyai.createbigcannons.network;

import com.mojang.math.Vector4f;
import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import rbasamoyai.createbigcannons.cannon_control.carriage.CannonCarriageEntity;

public class ServerboundCarriageWheelPacket implements C2SPacket {

	private final Vector4f state;
	private final int id;

	public ServerboundCarriageWheelPacket(CannonCarriageEntity entity) {
		this.state = entity.getWheelState();
		this.id = entity.getId();
	}

	public ServerboundCarriageWheelPacket(FriendlyByteBuf buf) {
		this.id = buf.readVarInt();
		this.state = new Vector4f(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeVarInt(this.id).writeFloat(this.state.x()).writeFloat(this.state.y()).writeFloat(this.state.z()).writeFloat(this.state.w());
	}

	@Override
	public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl listener, PacketSender responseSender, SimpleChannel channel) {
		server.execute(() -> {
			if (player.getLevel().getEntity(this.id) instanceof CannonCarriageEntity carriage) {
				carriage.setWheelState(this.state);
			}
		});
	}
}
