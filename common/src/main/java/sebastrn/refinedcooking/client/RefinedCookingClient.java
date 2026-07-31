package sebastrn.refinedcooking.client;

import net.blay09.mods.balm.client.BalmClientRegistrars;
import sebastrn.refinedcooking.RefinedCookingContainerMenus;
import sebastrn.refinedcooking.screen.KitchenAccessPointScreen;

/**
 * Loader-agnostic client init: registers the Access Point screen through Balm. The card's bound/unbound item-model
 * condition ({@code KitchenNetworkCardBoundProperty}) is registered per loader, NeoForge via
 * {@code RegisterConditionalItemModelPropertyEvent}, Fabric via {@code ConditionalItemModelProperties.ID_MAPPER}, 
 * so it lives in each loader's client shim rather than here.
 */
public final class RefinedCookingClient {

    private RefinedCookingClient() {
    }

    public static void initialize(BalmClientRegistrars registrars) {
        registrars.menuScreens(screens ->
                screens.register(RefinedCookingContainerMenus.KITCHEN_ACCESS_POINT, KitchenAccessPointScreen::new));
    }
}
