package rbasamoyai.createbigcannons.network;

import me.pepperbell.simplenetworking.SimpleChannel;
import rbasamoyai.createbigcannons.CreateBigCannons;
import rbasamoyai.createbigcannons.crafting.BlockRecipesManager.ClientboundRecipesPacket;

public class CBCNetwork {

	public static final SimpleChannel INSTANCE = construct();

	protected static SimpleChannel construct() {
		SimpleChannel channel = new SimpleChannel(CreateBigCannons.resource("network"));
		int id = 0;

		// Register C2S packets
		channel.registerC2SPacket(ServerboundTimedFuzePacket.class, id++, ServerboundTimedFuzePacket::new);
		channel.registerC2SPacket(ServerboundProximityFuzePacket.class, id++, ServerboundProximityFuzePacket::new);
		channel.registerC2SPacket(ServerboundFiringActionPacket.class, id++, ServerboundFiringActionPacket::new);
		channel.registerC2SPacket(ServerboundCarriageWheelPacket.class, id++, ServerboundCarriageWheelPacket::new);
		channel.registerC2SPacket(ServerboundSetFireRatePacket.class, id++, ServerboundSetFireRatePacket::new);

		// Register S2C packets
		channel.registerS2CPacket(ClientboundRecipesPacket.class, id++, ClientboundRecipesPacket::new);
		channel.registerS2CPacket(ClientboundUpdateContraptionPacket.class, id++, ClientboundUpdateContraptionPacket::new);
		channel.registerS2CPacket(ClientboundAnimateCannonContraptionPacket.class, id++, ClientboundAnimateCannonContraptionPacket::new);

		return channel;
	}

}
