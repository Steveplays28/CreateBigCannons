package rbasamoyai.createbigcannons.munitions;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import rbasamoyai.createbigcannons.munitions.big_cannon.FuzedBlockEntity;
import rbasamoyai.createbigcannons.munitions.fuzes.FuzeItem;

public class FuzeItemHandler extends ItemStackHandler {

	private final FuzedBlockEntity be;

	public FuzeItemHandler(FuzedBlockEntity be) {
		this.be = be;
	}

	@Override
	public int getSlots() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return slot == 0 ? this.be.getFuze() : ItemStack.EMPTY;
	}

	@Override
	public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (!this.be.getFuze().isEmpty()) {
			return resource.toStack().getCount();
		}

		ItemStack result = resource.toStack().copy();
		this.be.setFuze(result.split(1));

		return Math.abs(result.getCount() - resource.toStack().getCount());
	}

	@Override
	public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
		if (this.be.getFuze().isEmpty()) {
			return 0;
		}

		this.be.getFuze().setCount(this.be.getFuze().split(resource.toStack().getCount()).getCount());
		return Math.abs(this.be.getFuze().getCount() - resource.toStack().getCount());
	}

	@Override
	public long simulateExtract(ItemVariant resource, long maxAmount, @Nullable TransactionContext transaction) {
		if (this.be.getFuze().isEmpty()) {
			return 0;
		}

		ItemStack copy = this.be.getFuze().copy();
		copy.setCount(Math.min((int) maxAmount, copy.getCount()));

		return Math.abs(copy.getCount() - resource.toStack().getCount());
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public boolean isItemValid(int slot, ItemVariant resource) {
		return slot == 0 && !resource.toStack().isEmpty() && resource.toStack().getItem() instanceof FuzeItem && this.be.getFuze().isEmpty();
	}

	@Override
	public void setStackInSlot(int slot, ItemStack stack) {
		if (this.isItemValid(slot, ItemVariant.of(stack))) this.be.setFuze(stack);
	}

}
