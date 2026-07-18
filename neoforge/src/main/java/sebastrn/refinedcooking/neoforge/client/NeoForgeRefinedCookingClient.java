package sebastrn.refinedcooking.neoforge.client;

import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.neoforge.NeoForgeLoadContext;
import net.minecraft.client.renderer.item.ItemProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import sebastrn.refinedcooking.RefinedCooking;
import sebastrn.refinedcooking.RefinedCookingItems;
import sebastrn.refinedcooking.client.RefinedCookingClient;
import sebastrn.refinedcooking.item.KitchenNetworkCardBoundProperty;

/**
 * NeoForge client entry: boots the loader-neutral client init through Balm, then registers the network-card
 * bound/unbound item-model predicate the NeoForge way ({@code ItemProperties.register} on the main thread).
 */
@Mod(value = RefinedCooking.ID, dist = Dist.CLIENT)
public final class NeoForgeRefinedCookingClient {

    public NeoForgeRefinedCookingClient(IEventBus modEventBus) {
        BalmClient.initialize(RefinedCooking.ID, new NeoForgeLoadContext(modEventBus), RefinedCookingClient::initialize);
        modEventBus.addListener(this::onClientSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(
                RefinedCookingItems.KITCHEN_NETWORK_CARD.get(),
                KitchenNetworkCardBoundProperty.NAME,
                new KitchenNetworkCardBoundProperty()));
    }
}
