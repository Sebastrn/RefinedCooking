package sebastrn.refinedcooking.fabric.client;

import net.blay09.mods.balm.api.EmptyLoadContext;
import net.blay09.mods.balm.api.client.BalmClient;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.item.ItemProperties;
import sebastrn.refinedcooking.RefinedCooking;
import sebastrn.refinedcooking.RefinedCookingItems;
import sebastrn.refinedcooking.client.RefinedCookingClient;
import sebastrn.refinedcooking.item.KitchenNetworkCardBoundProperty;

/**
 * Fabric client entry: boots the loader-neutral client init through Balm, then registers the network-card
 * bound/unbound item-model predicate via the vanilla {@link ItemProperties} (the same call NeoForge makes, done here
 * in the client initializer instead of FMLClientSetupEvent).
 */
public final class FabricRefinedCookingClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BalmClient.initialize(RefinedCooking.ID, EmptyLoadContext.INSTANCE, RefinedCookingClient::initialize);
        ItemProperties.register(
                RefinedCookingItems.KITCHEN_NETWORK_CARD.get(),
                KitchenNetworkCardBoundProperty.NAME,
                new KitchenNetworkCardBoundProperty());
    }
}
