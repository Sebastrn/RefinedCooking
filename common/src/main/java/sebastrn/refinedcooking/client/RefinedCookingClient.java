package sebastrn.refinedcooking.client;

import net.blay09.mods.balm.api.client.BalmClient;
import net.minecraft.resources.ResourceLocation;
import sebastrn.refinedcooking.RefinedCooking;
import sebastrn.refinedcooking.RefinedCookingContainerMenus;
import sebastrn.refinedcooking.screen.KitchenAccessPointScreen;

/**
 * Loader-agnostic client init: registers the Access Point screen through Balm. The card's bound/unbound item-model
 * predicate is registered per loader (NeoForge {@code ItemProperties}, Fabric {@code ModelPredicateProviderRegistry}),
 * so it lives in each loader's client shim rather than here.
 */
public final class RefinedCookingClient {

    private RefinedCookingClient() {
    }

    public static void initialize() {
        BalmClient.getScreens().registerScreen(
                ResourceLocation.fromNamespaceAndPath(RefinedCooking.ID, "kitchen_access_point"),
                RefinedCookingContainerMenus.KITCHEN_ACCESS_POINT::get,
                KitchenAccessPointScreen::new);
    }
}
