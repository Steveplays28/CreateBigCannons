package rbasamoyai.createbigcannons.network;

import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import me.pepperbell.simplenetworking.S2CPacket;
import me.pepperbell.simplenetworking.SimpleChannel;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.StructureBlockInfo;

public class ClientboundUpdateContraptionPacket implements S2CPacket {

	private final int id;
	private final BlockPos pos;
	private final StructureBlockInfo info;

	public ClientboundUpdateContraptionPacket(AbstractContraptionEntity entity, BlockPos pos, StructureBlockInfo info) {
		this.id = entity.getId();
		this.pos = pos;
		this.info = info;
	}

	public ClientboundUpdateContraptionPacket(FriendlyByteBuf buf) {
		this.id = buf.readVarInt();
		this.pos = buf.readBlockPos();
		BlockPos infoPos = buf.readBlockPos();
		@SuppressWarnings("deprecation") BlockState state = Block.BLOCK_STATE_REGISTRY.byId(buf.readVarInt());
		CompoundTag tag = buf.readBoolean() ? buf.readNbt() : null;
		this.info = new StructureBlockInfo(infoPos, state, tag);
	}

	public int id() {
		return this.id;
	}

	public BlockPos pos() {
		return this.pos;
	}

	public StructureBlockInfo info() {
		return this.info;
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeVarInt(this.id).writeBlockPos(this.pos).writeBlockPos(this.info.pos).writeVarInt(Block.getId(this.info.state));
		buf.writeBoolean(this.info.nbt != null);
		if (this.info.nbt != null) {
			buf.writeNbt(this.info.nbt);
		}
	}

	@Override
	public void handle(Minecraft client, ClientPacketListener listener, PacketSender responseSender, SimpleChannel channel) {
		client.execute(() -> CBCClientHandlers.updateContraption(this));
	}
}
