package rbasamoyai.createbigcannons;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.builders.FluidBuilder;
import com.tterrag.registrate.fabric.SimpleFlowableFluid;
import com.tterrag.registrate.util.entry.FluidEntry;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import io.github.fabricators_of_create.porting_lib.util.FluidAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;

public class CBCFluids {

	public static final FluidEntry<SimpleFlowableFluid.Flowing> MOLTEN_CAST_IRON = standardFluid("molten_cast_iron", NoColorFluidAttributes::new).lang(f -> "fluid.createbigcannons.molten_cast_iron", "Molten Cast Iron").tag(AllTags.forgeFluidTag("molten_cast_iron")).properties(p -> p.levelDecreasePerBlock(2).tickRate(25)).source(SimpleFlowableFluid.Still::new).register();

	public static final FluidEntry<SimpleFlowableFluid.Flowing> MOLTEN_BRONZE = standardFluid("molten_bronze", NoColorFluidAttributes::new).lang(f -> "fluid.createbigcannons.molten_bronze", "Molten Bronze").tag(AllTags.forgeFluidTag("molten_bronze")).properties(p -> p.levelDecreasePerBlock(2).tickRate(25)).source(SimpleFlowableFluid.Still::new).register();

	public static final FluidEntry<SimpleFlowableFluid.Flowing> MOLTEN_STEEL = standardFluid("molten_steel", NoColorFluidAttributes::new).lang(f -> "fluid.createbigcannons.molten_steel", "Molten Steel").tag(AllTags.forgeFluidTag("molten_steel")).properties(p -> p.levelDecreasePerBlock(2).tickRate(25)).source(SimpleFlowableFluid.Still::new).register();

	public static final FluidEntry<SimpleFlowableFluid.Flowing> MOLTEN_NETHERSTEEL = standardFluid("molten_nethersteel", NoColorFluidAttributes::new).lang(f -> "fluid.createbigcannons.molten_nethersteel", "Molten Nethersteel").properties(p -> p.levelDecreasePerBlock(2).tickRate(25)).source(SimpleFlowableFluid.Still::new).register();

	private static FluidBuilder<SimpleFlowableFluid.Flowing, CreateRegistrate> standardFluid(String name, NonNullBiFunction<FluidAttributes.Builder, Fluid, FluidAttributes> factory) {
		return CreateBigCannons.REGISTRATE.fluid(name, CreateBigCannons.resource("fluid/" + name + "_still"), CreateBigCannons.resource("fluid/" + name + "_flow")).removeTag(FluidTags.WATER);
	}

	public static void register() {
	}

	private static class NoColorFluidAttributes extends FluidAttributes {

		protected NoColorFluidAttributes(Builder builder, Fluid fluid) {
			super(builder, fluid);
		}

		@Override
		public int getColor(BlockAndTintGetter level, BlockPos pos) {
			return 0x00ffffff;
		}
	}

}
