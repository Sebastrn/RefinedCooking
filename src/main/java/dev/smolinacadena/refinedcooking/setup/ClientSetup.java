package dev.smolinacadena.refinedcooking.setup;

import dev.smolinacadena.refinedcooking.RefinedCookingContainerMenus;
import dev.smolinacadena.refinedcooking.screen.KitchenAccessPointScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


public final class ClientSetup {

    private ClientSetup() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent e) {
        e.enqueueWork(() -> {
            MenuScreens.register(RefinedCookingContainerMenus.KITCHEN_ACCESS_POINT, KitchenAccessPointScreen::new);
        });
    }
}
