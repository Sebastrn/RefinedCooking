package sebastrn.refinedcooking.fabric.client;

import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperties;
import sebastrn.refinedcooking.RefinedCooking;
import sebastrn.refinedcooking.client.RefinedCookingClient;
import sebastrn.refinedcooking.item.KitchenNetworkCardBoundProperty;

/**
 * Fabric client entry: boots the loader-neutral client init through Balm (the Access Point screen), then registers the
 * network-card bound/unbound item-model condition by adding it to the vanilla {@code ConditionalItemModelProperties}
 * id-mapper, the Fabric analog of NeoForge's {@code RegisterConditionalItemModelPropertyEvent} (see RS2's own
 * ClientModInitializerImpl for the same pattern).
 */
public final class FabricRefinedCookingClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BalmClient.initializeMod(RefinedCooking.ID, FabricLoadContext.INSTANCE, RefinedCookingClient::initialize);
        ConditionalItemModelProperties.ID_MAPPER.put(
                KitchenNetworkCardBoundProperty.NAME, KitchenNetworkCardBoundProperty.MAP_CODEC);
    }
}
