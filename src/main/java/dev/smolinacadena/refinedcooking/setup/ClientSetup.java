package dev.smolinacadena.refinedcooking.setup;

import dev.smolinacadena.refinedcooking.RefinedCooking;
import dev.smolinacadena.refinedcooking.RefinedCookingContainerMenus;
import dev.smolinacadena.refinedcooking.screen.KitchenAccessPointScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


public final class ClientSetup {

    private ClientSetup() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent e){
        e.enqueueWork(() -> {
            MenuScreens.register(RefinedCookingContainerMenus.KITCHEN_ACCESS_POINT, KitchenAccessPointScreen::new);
        });
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent e) {
        ResourceLocation kitchenStation = new ResourceLocation(RefinedCooking.ID, "block/kitchen_station");
        ResourceLocation kitchenStationConnected = new ResourceLocation(RefinedCooking.ID, "block/kitchen_station_connected");
        //overrideWithDynamicModel(e, RefinedCookingBlocks.KITCHEN_STATION.get(), "block/kitchen_station", it -> it.getValue(KitchenStationBlock.CONNECTED) ? kitchenStationConnected : kitchenStation, null);

        ResourceLocation kitchenAccessPoint = new ResourceLocation(RefinedCooking.ID, "block/kitchen_access_point");
        ResourceLocation kitchenAccessPointConnected = new ResourceLocation(RefinedCooking.ID, "block/kitchen_access_point_connected");
        //overrideWithDynamicModel(e, RefinedCookingBlocks.KITCHEN_ACCESS_POINT.get(), "block/kitchen_access_point", it -> it.getValue(KitchenAccessPointBlock.CONNECTED_TO_STATION) ? kitchenAccessPointConnected : kitchenAccessPoint, null);
    }

//    private static void overrideWithDynamicModel(ModelBakeEvent event, Block block, String modelPath, @Nullable Function<BlockState, ResourceLocation> modelFunction, @Nullable Function<BlockState, Map<String, String>> textureMapFunction) {
//        ResourceLocation modelLocation = new ResourceLocation(RefinedCooking.ID, modelPath);
//        if (modelFunction == null) {
//            modelFunction = it -> modelLocation;
//        }
//
//        CachedDynamicModel dynamicModel = new CachedDynamicModel(event.getModelLoader(), modelFunction, null, textureMapFunction, modelLocation);
//        overrideModelIgnoreState(block, dynamicModel, event);
//    }
//
//    private static void overrideModelIgnoreState(Block block, IBakedModel model, ModelBakeEvent event) {
//        block.getStateDefinition().getPossibleStates().forEach((state) -> {
//            ModelResourceLocation modelLocation = BlockModelShapes.stateToModelLocation(state);
//            event.getModelRegistry().put(modelLocation, model);
//        });
//    }
}
