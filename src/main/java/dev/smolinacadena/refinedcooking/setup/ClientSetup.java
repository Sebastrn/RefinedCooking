package dev.smolinacadena.refinedcooking.setup;

import dev.smolinacadena.refinedcooking.RefinedCooking;
import dev.smolinacadena.refinedcooking.RefinedCookingBlocks;
import dev.smolinacadena.refinedcooking.RefinedCookingContainers;
import dev.smolinacadena.refinedcooking.block.KitchenAccessPointBlock;
import dev.smolinacadena.refinedcooking.block.KitchenStationBlock;
import dev.smolinacadena.refinedcooking.screen.KitchenAccessPointScreen;
import net.blay09.mods.cookingforblockheads.client.CachedDynamicModel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;

public class ClientSetup {

    public ClientSetup() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelBake);
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent e){
        ScreenManager.register(RefinedCookingContainers.KITCHEN_ACCESS_POINT, KitchenAccessPointScreen::new);
    }

    @SubscribeEvent
    public void onModelBake(ModelBakeEvent e) {
        ResourceLocation kitchenStation = new ResourceLocation(RefinedCooking.ID, "block/kitchen_station");
        ResourceLocation kitchenStationConnected = new ResourceLocation(RefinedCooking.ID, "block/kitchen_station_connected");
        overrideWithDynamicModel(e, RefinedCookingBlocks.KITCHEN_STATION.get(), "block/kitchen_station", it -> it.getValue(KitchenStationBlock.CONNECTED) ? kitchenStationConnected : kitchenStation, null);

        ResourceLocation kitchenAccessPoint = new ResourceLocation(RefinedCooking.ID, "block/kitchen_access_point");
        ResourceLocation kitchenAccessPointConnected = new ResourceLocation(RefinedCooking.ID, "block/kitchen_access_point_connected");
        overrideWithDynamicModel(e, RefinedCookingBlocks.KITCHEN_ACCESS_POINT.get(), "block/kitchen_access_point", it -> it.getValue(KitchenAccessPointBlock.CONNECTED_TO_STATION) ? kitchenAccessPointConnected : kitchenAccessPoint, null);
    }

    private static void overrideWithDynamicModel(ModelBakeEvent event, Block block, String modelPath, @Nullable Function<BlockState, ResourceLocation> modelFunction, @Nullable Function<BlockState, Map<String, String>> textureMapFunction) {
        ResourceLocation modelLocation = new ResourceLocation(RefinedCooking.ID, modelPath);
        if (modelFunction == null) {
            modelFunction = it -> modelLocation;
        }

        CachedDynamicModel dynamicModel = new CachedDynamicModel(event.getModelLoader(), modelFunction, null, textureMapFunction, modelLocation);
        overrideModelIgnoreState(block, dynamicModel, event);
    }

    private static void overrideModelIgnoreState(Block block, IBakedModel model, ModelBakeEvent event) {
        block.getStateDefinition().getPossibleStates().forEach((state) -> {
            ModelResourceLocation modelLocation = BlockModelShapes.stateToModelLocation(state);
            event.getModelRegistry().put(modelLocation, model);
        });
    }
}
