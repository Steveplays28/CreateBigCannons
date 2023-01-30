package rbasamoyai.createbigcannons.network;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import rbasamoyai.createbigcannons.cannon_control.carriage.CannonCarriageEntity;

public class ServerboundSetFireRatePacket implements C2SPacket {

	private final int fireRateAdjustment;

	public ServerboundSetFireRatePacket(int fireRateAdjustment) {
		this.fireRateAdjustment = fireRateAdjustment;
	}

	public ServerboundSetFireRatePacket(FriendlyByteBuf buf) {
		this.fireRateAdjustment = buf.readVarInt();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeVarInt(this.fireRateAdjustment);
	}

	@Override
	public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl listener, PacketSender responseSender, SimpleChannel channel) {
		server.execute(() -> {
			if (this.fireRateAdjustment != 0 && player.getRootVehicle() instanceof CannonCarriageEntity carriage) {
				carriage.trySettingFireRateCarriage(this.fireRateAdjustment);
			}
		});
	}
}
