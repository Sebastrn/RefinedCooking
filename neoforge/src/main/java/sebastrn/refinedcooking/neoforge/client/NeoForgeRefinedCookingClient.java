package sebastrn.refinedcooking.neoforge.client;

import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
import sebastrn.refinedcooking.RefinedCooking;
import sebastrn.refinedcooking.client.RefinedCookingClient;
import sebastrn.refinedcooking.item.KitchenNetworkCardBoundProperty;

/**
 * NeoForge client entry: boots the loader-neutral client init through Balm (the Access Point screen), then registers
 * the network-card bound/unbound item-model condition the NeoForge way, through
 * {@link RegisterConditionalItemModelPropertyEvent}. On Fabric the same condition is registered by adding it to
 * {@code ConditionalItemModelProperties.ID_MAPPER}.
 */
@Mod(value = RefinedCooking.ID, dist = Dist.CLIENT)
public final class NeoForgeRefinedCookingClient {

    public NeoForgeRefinedCookingClient(IEventBus modEventBus, ModContainer modContainer) {
        BalmClient.initializeMod(RefinedCooking.ID, new NeoForgeLoadContext(modContainer, modEventBus),
                RefinedCookingClient::initialize);
        modEventBus.addListener(this::onRegisterItemModelProperties);
    }

    private void onRegisterItemModelProperties(RegisterConditionalItemModelPropertyEvent e) {
        e.register(KitchenNetworkCardBoundProperty.NAME, KitchenNetworkCardBoundProperty.MAP_CODEC);
    }
}
