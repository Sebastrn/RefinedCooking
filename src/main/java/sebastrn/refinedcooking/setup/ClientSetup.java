package sebastrn.refinedcooking.setup;

import net.minecraft.client.gui.screens.MenuScreens;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import sebastrn.refinedcooking.RefinedCookingContainerMenus;
import sebastrn.refinedcooking.screen.KitchenAccessPointScreen;

public final class ClientSetup {

    private ClientSetup() {
    }

    public static void onClientSetup(FMLClientSetupEvent e) {
        e.enqueueWork(() -> MenuScreens.register(RefinedCookingContainerMenus.KITCHEN_ACCESS_POINT.get(), KitchenAccessPointScreen::new));
    }
}
