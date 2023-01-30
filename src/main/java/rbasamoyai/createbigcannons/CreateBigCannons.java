package rbasamoyai.createbigcannons;

import com.simibubi.create.foundation.data.CreateRegistrate;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rbasamoyai.createbigcannons.base.CBCCommonEvents;
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
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
		ModLoadingContext mlContext = ModLoadingContext.get();

		REGISTRATE.registerEventListeners(modEventBus);

		CBCRegistries.init();

		ModGroup.register();
		CBCBlocks.register();
		CBCItems.register();
		CBCBlockEntities.register();
		CBCEntityTypes.register();
		CBCMenuTypes.register();
		CBCFluids.register();
		CBCRecipeTypes.register(modEventBus);

		CannonCastShape.CANNON_CAST_SHAPES.register(modEventBus);
		CBCContraptionTypes.prepare();
		CBCChecks.register();
		BlockRecipeSerializer.register();
		BlockRecipeType.register();

		CBCParticleTypes.PARTICLE_TYPES.register(modEventBus);

		CBCTags.register();

		modEventBus.addListener(this::onCommonSetup);

		addDatapackReloadListeners();
		forgeEventBus.addListener(this::onDatapackSync);
		CBCCommonEvents.register(forgeEventBus);

		CBCConfigs.registerConfigs(mlContext);

		this.registerSerializers();

		// Should be called after everything has been registered
		REGISTRATE.register();

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CreateBigCannonsClient.prepareClient(modEventBus, forgeEventBus));
	}

	private void onCommonSetup(FMLCommonSetupEvent event) {
		CBCNetwork.init();
		FluidBlob.registerDefaultBlobEffects();
	}

	private void addDatapackReloadListeners() {
		ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(BlockRecipeFinder::onDatapackReload);
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(BlockRecipesManager.ReloadListener.INSTANCE);
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(BlockHardnessHandler.ReloadListener.INSTANCE);
	}

	private void onDatapackSync(OnDatapackSyncEvent event) {
		ServerPlayer player = event.getPlayer();
		if (player == null) {
			BlockRecipesManager.syncToAll();
		} else {
			BlockRecipesManager.syncTo(player);
		}
	}

	private void registerSerializers() {
		EntityDataSerializers.registerSerializer(FluidBlob.FLUID_STACK_SERIALIZER);
	}

}
