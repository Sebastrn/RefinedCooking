package sebastrn.refinedcooking.setup;

import net.minecraft.client.renderer.item.ItemProperties;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import sebastrn.refinedcooking.RefinedCookingContainerMenus;
import sebastrn.refinedcooking.RefinedCookingItems;
import sebastrn.refinedcooking.item.KitchenNetworkCardBoundProperty;
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

    /**
     * Binds the card's bound/unbound item-model predicate so {@code models/item/kitchen_network_card.json} can pick a
     * texture from it. {@code ItemProperties.register} touches shared render state, so it runs on the main thread via
     * {@code enqueueWork}, exactly as RS registers its own Network Card predicate.
     */
    public static void onClientSetup(FMLClientSetupEvent e) {
        e.enqueueWork(() -> ItemProperties.register(
                RefinedCookingItems.KITCHEN_NETWORK_CARD.get(),
                KitchenNetworkCardBoundProperty.NAME,
                new KitchenNetworkCardBoundProperty()));
    }
}
