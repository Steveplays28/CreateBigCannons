package rbasamoyai.createbigcannons.munitions.big_cannon.grapeshot;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.createbigcannons.CBCTags;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.munitions.big_cannon.shrapnel.Shrapnel;

public class Grapeshot extends Shrapnel {

	private static final DamageSource GRAPESHOT = DamageSource.MAGIC;

	public Grapeshot(EntityType<? extends Grapeshot> type, Level level) {
		super(type, level);
	}

	@Override
	protected boolean canDestroyBlock(BlockState state) {
		return state.is(CBCTags.BlockCBC.GRAPESHOT_SHATTERABLE)
				|| state.is(CBCTags.BlockCBC.GRAPESHOT_VULNERABLE) && this.random.nextFloat() < CBCConfigs.SERVER.munitions.grapeshotVulnerableBreakChance.getF();
	}

	@Override
	protected DamageSource getDamageSource() {
		return GRAPESHOT;
	}

}
