package rbasamoyai.createbigcannons.network;

import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

public class ClientboundAnimateCannonContraptionPacket implements S2CPacket {

	private final int id;

	public ClientboundAnimateCannonContraptionPacket(AbstractContraptionEntity entity) {
		this.id = entity.getId();
	}

	public ClientboundAnimateCannonContraptionPacket(FriendlyByteBuf buf) {
		this.id = buf.readVarInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeVarInt(this.id);
	}

	public int id() {
		return this.id;
	}

	@Override
	public void handle(Minecraft client, ClientPacketListener listener, PacketSender responseSender, SimpleChannel channel) {
		client.execute(() -> {
			CBCClientHandlers.animateCannon(this);
		});
	}

}
