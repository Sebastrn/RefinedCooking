package sebastrn.refinedcooking.setup;

import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import sebastrn.refinedcooking.RefinedCookingContainerMenus;
import sebastrn.refinedcooking.screen.KitchenAccessPointScreen;

public final class ClientSetup {

    private ClientSetup() {
    }

    /**
     * {@code MenuScreens.register} is no longer callable from mods in 1.21 — NeoForge routes screen registration
     * through this event instead, which also removes the need to enqueue the work ourselves.
     */
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent e) {
        e.register(RefinedCookingContainerMenus.KITCHEN_ACCESS_POINT.get(), KitchenAccessPointScreen::new);
    }
}
