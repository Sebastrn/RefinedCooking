package dev.smolinacadena.refinedcooking.setup;

import dev.smolinacadena.refinedcooking.RefinedCooking;
import dev.smolinacadena.refinedcooking.RefinedCookingContainerMenus;
import dev.smolinacadena.refinedcooking.screen.KitchenAccessPointScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent.BakingCompleted;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


public final class ClientSetup {

    private ClientSetup() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent e){
        e.enqueueWork(() -> {
            MenuScreens.register(RefinedCookingContainerMenus.KITCHEN_ACCESS_POINT.get(), KitchenAccessPointScreen::new);
        });
    }

    @SubscribeEvent
    public static void onModelBake(BakingCompleted e) {
        ResourceLocation kitchenStation = new ResourceLocation(RefinedCooking.ID, "block/kitchen_station");
        ResourceLocation kitchenStationConnected = new ResourceLocation(RefinedCooking.ID, "block/kitchen_station_connected");

        ResourceLocation kitchenAccessPoint = new ResourceLocation(RefinedCooking.ID, "block/kitchen_access_point");
        ResourceLocation kitchenAccessPointConnected = new ResourceLocation(RefinedCooking.ID, "block/kitchen_access_point_connected");
    }
}
