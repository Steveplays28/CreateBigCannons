package rbasamoyai.createbigcannons.network;

import me.pepperbell.simplenetworking.C2SPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import rbasamoyai.createbigcannons.cannon_control.carriage.CannonCarriageEntity;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;

public class ServerboundFiringActionPacket implements C2SPacket {

	public ServerboundFiringActionPacket() {
	}

	public ServerboundFiringActionPacket(FriendlyByteBuf buf) {
	}

	public void encode(FriendlyByteBuf buf) {
	}

	@Override
	public void handle(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl listener, PacketSender responseSender, SimpleChannel channel) {
		server.execute(() -> {
			Entity rootVehicle = player.getRootVehicle();

			if (rootVehicle instanceof PitchOrientedContraptionEntity poce) poce.tryFiringShot();
			if (rootVehicle instanceof CannonCarriageEntity carriage) carriage.tryFiringShot();
		});
	}
}
