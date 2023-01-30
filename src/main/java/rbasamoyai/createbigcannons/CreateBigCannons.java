package rbasamoyai.createbigcannons;

import com.simibubi.create.foundation.data.CreateRegistrate;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rbasamoyai.createbigcannons.base.CBCRegistries;
import rbasamoyai.createbigcannons.config.CBCConfigs;
import rbasamoyai.createbigcannons.crafting.BlockRecipeFinder;
import rbasamoyai.createbigcannons.crafting.BlockRecipeSerializer;
import rbasamoyai.createbigcannons.crafting.BlockRecipeType;
import rbasamoyai.createbigcannons.crafting.BlockRecipesManager;
import rbasamoyai.createbigcannons.crafting.casting.CannonCastShape;
import rbasamoyai.createbigcannons.munitions.big_cannon.fluid_shell.FluidBlob;
import rbasamoyai.createbigcannons.munitions.config.BlockHardnessHandler;
import rbasamoyai.createbigcannons.network.CBCNetwork;

public class CreateBigCannons implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "createbigcannons";

	public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MOD_ID);

	public static ResourceLocation resource(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		// Registry
		CBCRegistries.init();

		ModGroup.register();
		CBCBlocks.register();
		CBCItems.register();
		CBCBlockEntities.register();
		CBCEntityTypes.register();
		CBCMenuTypes.register();
		CBCFluids.register();
		CBCRecipeTypes.register();
		CannonCastShape.CANNON_CAST_SHAPES.register();
		CBCContraptionTypes.prepare();
		CBCChecks.register();
		BlockRecipeSerializer.register();
		BlockRecipeType.register();
		CBCParticleTypes.PARTICLE_TYPES.register();
		CBCTags.register();

		// Events
		ServerLifecycleEvents.SERVER_STARTING.register(this::onCommonSetup);
		addDatapackReloadListeners();

		// Config
		CBCConfigs.registerConfigs();

		// Serializers
		this.registerSerializers();

		// Registry finalizer, should be called after everything has been registered
		REGISTRATE.register();
	}

	private void onCommonSetup(MinecraftServer minecraftServer) {
		CBCNetwork.init();
		FluidBlob.registerDefaultBlobEffects();
	}

	private void addDatapackReloadListeners() {
		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(BlockRecipeFinder::onDatapackReload);
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(BlockRecipesManager.ReloadListener.INSTANCE);
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(BlockHardnessHandler.ReloadListener.INSTANCE);
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(this::onDatapackSync);
	}

	private void onDatapackSync(ServerPlayer serverPlayer, boolean b) {
		if (serverPlayer == null) {
			BlockRecipesManager.syncToAll();
		} else {
			BlockRecipesManager.syncTo(serverPlayer);
		}
	}

	private void registerSerializers() {
		EntityDataSerializers.registerSerializer(FluidBlob.FLUID_STACK_SERIALIZER);
	}

}
