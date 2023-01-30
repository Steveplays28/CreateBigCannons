package rbasamoyai.createbigcannons.cannons.autocannon;

import com.jozufozu.flywheel.backend.instancing.InstancedRenderDispatcher;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemHandlerHelper;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rbasamoyai.createbigcannons.cannon_control.contraption.PitchOrientedContraptionEntity;
import rbasamoyai.createbigcannons.munitions.autocannon.AutocannonCartridgeItem;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class AutocannonBreechBlockEntity extends AutocannonBlockEntity implements AnimatedAutocannon {

	protected static final int[] FIRE_RATES = new int[]{120, // 10 rpm
			80, // 15 rpm
			60, // 20 rpm
			48, // 25 rpm
			40, // 30 rpm
			30, // 40 rpm
			24, // 50 rpm
			20, // 60 rpm
			15, // 80 rpm
			12, // 100 rpm
			10, // 120 rpm
			8,  // 150 rpm
			6,  // 200 rpm
			5,  // 240 rpm
			4  // 300 rpm
	};
	private final Deque<ItemStack> inputBuffer = new LinkedList<>();
	private int fireRate = 7;
	private int firingCooldown;
	private int animateTicks = 5;
	private DyeColor seat = null;
	private boolean updateInstance = true;
	private ItemStack outputBuffer = ItemStack.EMPTY;

	public AutocannonBreechBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	public ItemStackHandler createItemHandler() {
		return new BreechItemHandler(this);
	}

	public int getQueueLimit() {
		return 5;
	}

	@Override
	public void tick() {
		super.tick();
		this.allTick(this.level);
	}

	@Override
	public void tickFromContraption(Level level, PitchOrientedContraptionEntity poce, BlockPos localPos) {
		super.tickFromContraption(level, poce, localPos);
		this.allTick(level);
	}

	private void allTick(Level level) {
		if (this.fireRate < 0 || this.fireRate > 15) this.fireRate = 0;
		if (this.firingCooldown < 0) this.firingCooldown = 0;
		if (this.firingCooldown > 0) this.firingCooldown--;

		if (this.animateTicks < 5) ++this.animateTicks;
		if (this.animateTicks < 0) this.animateTicks = 0;
	}

	public int getFireRate() {
		return this.fireRate;
	}

	public void setFireRate(int power) {
		this.fireRate = Mth.clamp(power, 0, 15);
	}

	public int getActualFireRate() {
		if (this.fireRate < 1 || this.fireRate > 15) return 0;
		int cooldown = FIRE_RATES[this.fireRate - 1];
		return 1200 / cooldown;
	}

	public boolean canFire() {
		return this.getFireRate() > 0 && this.firingCooldown <= 0;
	}

	public void handleFiring() {
		if (this.fireRate > 0 && this.fireRate <= FIRE_RATES.length) {
			this.firingCooldown = FIRE_RATES[this.fireRate - 1];
			this.animateTicks = 0;
		}
	}

	public float getAnimateOffset(float partialTicks) {
		float t = ((float) this.animateTicks + partialTicks) * 1.2f;
		if (t <= 0 || t >= 4.8f) return 0;
		float f = t < 1 ? t : (4.8f - t) / 3.8f;
		return Mth.sin(f * Mth.HALF_PI);
	}

	@Override
	public void incrementAnimationTicks() {
		++this.animateTicks;
	}

	@Override
	public int getAnimationTicks() {
		return this.animateTicks;
	}

	public DyeColor getSeatColor() {
		return this.seat;
	}

	public void setSeatColor(DyeColor color) {
		this.seat = color;
		this.updateInstance = true;
		this.notifyUpdate();
	}

	public boolean shouldUpdateInstance() {
		if (this.updateInstance) {
			this.updateInstance = false;
			return true;
		}
		return false;
	}

	public void requestModelDataUpdate() {
		super.setChanged();

		if (!this.remove) {
			InstancedRenderDispatcher.enqueueUpdate(this);
		}
	}

	@Override
	protected void read(CompoundTag tag, boolean clientPacket) {
		super.read(tag, clientPacket);
		this.fireRate = tag.getInt("FiringRate");
		this.firingCooldown = tag.getInt("Cooldown");
		this.animateTicks = tag.getInt("AnimateTicks");
		this.outputBuffer = tag.contains("Output") ? ItemStack.of(tag.getCompound("Output")) : ItemStack.EMPTY;
		this.seat = DyeColor.byName(tag.getString("Seat"), null);

		this.inputBuffer.clear();
		ListTag inputTag = tag.getList("Input", Tag.TAG_COMPOUND);
		for (int i = 0; i < inputTag.size(); ++i) {
			this.inputBuffer.add(ItemStack.of(inputTag.getCompound(i)));
		}

		if (!clientPacket) return;
		this.updateInstance = tag.contains("UpdateInstance");
		if (!this.isVirtual()) this.requestModelDataUpdate();
	}

	@Override
	protected void write(CompoundTag tag, boolean clientPacket) {
		super.write(tag, clientPacket);
		tag.putInt("FiringRate", this.fireRate);
		tag.putInt("Cooldown", this.firingCooldown);
		tag.putInt("AnimateTicks", this.animateTicks);
		if (this.outputBuffer != null && !this.outputBuffer.isEmpty())
			tag.put("Output", this.outputBuffer.getOrCreateTag());
		if (this.seat != null) tag.putString("Seat", this.seat.getSerializedName());

		if (!this.inputBuffer.isEmpty()) {
			tag.put("Input", this.inputBuffer.stream().map(ItemStack::getOrCreateTag).collect(Collectors.toCollection(ListTag::new)));
		}

		if (!clientPacket) return;
		if (this.updateInstance) tag.putBoolean("UpdateInstance", true);
	}

	public boolean isInputFull() {
		return this.inputBuffer.size() >= this.getQueueLimit();
	}

	public boolean isOutputFull() {
		return !this.outputBuffer.isEmpty();
	}

	public ItemStack insertOutput(ItemStack stack) {
		if (stack.isEmpty()) return ItemStack.EMPTY;
		if (this.isOutputFull()) return stack;
		this.outputBuffer = stack;
		return ItemStack.EMPTY;
	}

	public ItemStack extractNextInput() {
		return this.inputBuffer.isEmpty() ? ItemStack.EMPTY : this.inputBuffer.poll();
	}

	@Override
	public List<ItemStack> getDrops() {
		List<ItemStack> list = super.getDrops();
		for (ItemStack s : this.inputBuffer) {
			if (!s.isEmpty()) list.add(s.copy());
		}
		if (!this.outputBuffer.isEmpty()) list.add(this.outputBuffer.copy());
		return list;
	}

	public class BreechItemHandler extends ItemStackHandler {
		public AutocannonBreechBlockEntity breech;

		BreechItemHandler(AutocannonBreechBlockEntity breech) {
			this.breech = breech;
		}

		@Override
		public int getSlots() {
			return 2;
		}

		@NotNull
		@Override
		public ItemStack getStackInSlot(int slot) {
			return switch (slot) {
				case 0 -> this.breech.outputBuffer;
				case 1 -> this.breech.isInputFull() ? this.breech.inputBuffer.peekLast() : ItemStack.EMPTY;
				default -> ItemStack.EMPTY;
			};
		}

		@Override
		public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
			if (maxAmount != 1 || !this.isItemValid((int) maxAmount, resource) || breech.isInputFull()) {
				return maxAmount;
			}

			ItemStack itemStack = resource.toStack();
			this.breech.inputBuffer.add(ItemHandlerHelper.copyStackWithSize(itemStack, 1));

			return itemStack.getCount();
		}

		@Override
		public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
			if (maxAmount <= 0) {
				return 0;
			}

			// FIXME: Bandaid fix
			// TODO: Figure out how this is supposed to work
			return switch (resource.toStack().getCount()) {
				case 0 -> ItemHandlerHelper.copyStackWithSize(this.breech.outputBuffer, 1).getCount();
				default -> ItemStack.EMPTY.getCount();
			};
		}

		@Override
		public long simulateExtract(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
			if (maxAmount <= 0) {
				return 0;
			}

			// FIXME: Bandaid fix
			return switch (resource.toStack().getCount()) {
				case 0 -> this.breech.outputBuffer.split(1).getCount();
				default -> ItemStack.EMPTY.getCount();
			};
		}

		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}

		@Override
		public boolean isItemValid(int slot, ItemVariant resource) {
			return resource.getItem() instanceof AutocannonCartridgeItem;
		}
	}

}
