package sebastrn.refinedcooking.fabric;

import com.refinedmods.refinedstorage.common.api.support.network.AbstractNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.api.support.network.NetworkNodeContainerProvider;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.blay09.mods.cookingforblockheads.CookingForBlockheads;
import net.blay09.mods.cookingforblockheads.api.KitchenItemProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import sebastrn.refinedcooking.RefinedCooking;
import sebastrn.refinedcooking.RefinedCookingBlockEntities;
import sebastrn.refinedcooking.blockentity.KitchenStationBlockEntity;

/**
 * Fabric entry point. Boots the loader-neutral mod through Balm, then does the Fabric-only provider wiring — the
 * BlockApiLookup analogs of the NeoForge capabilities registered in {@code NeoForgeRefinedCooking}.
 */
public final class FabricRefinedCooking implements ModInitializer {

    @Override
    public void onInitialize() {
        Balm.initialize(RefinedCooking.ID, EmptyLoadContext.INSTANCE, RefinedCooking::initialize);

        // RS network-node discovery. Fabric has no NeoForge capabilities, so RS exposes a BlockApiLookup for the same
        // loader-neutral NetworkNodeContainerProvider; RS scans it at each position to build the network graph — the
        // exact analog of NeoForgeRefinedCooking's getNetworkNodeContainerProviderCapability() registration.
        // We fetch the lookup by RS's own id (refinedstorage:network_node_container_provider) rather than via
        // RefinedStorageFabricApi.INSTANCE: the latter's proxy throws "Fabric API not loaded yet" if our initializer
        // runs before RS's (Fabric's `depends` does not guarantee initializer order), whereas BlockApiLookup.get
        // returns the same cached instance regardless of order.
        BlockApiLookup<NetworkNodeContainerProvider, Direction> nodeLookup = BlockApiLookup.get(
                ResourceLocation.fromNamespaceAndPath("refinedstorage", "network_node_container_provider"),
                NetworkNodeContainerProvider.class, Direction.class);
        nodeLookup.registerForBlockEntities(
                (blockEntity, direction) ->
                        ((AbstractNetworkNodeContainerBlockEntity<?>) blockEntity).getContainerProvider(),
                RefinedCookingBlockEntities.KITCHEN_STATION.get(),
                RefinedCookingBlockEntities.KITCHEN_ACCESS_POINT.get());

        // Expose the Kitchen Station as CFB's KitchenItemProvider on the same BlockApiLookup CFB scans. CFB registers
        // that class under the id cookingforblockheads:kitchen_item_provider (see its FabricCookingForBlockheads), and
        // BlockApiLookup.get returns the shared instance for that id — so registering here is found by CFB's kitchen.
        BlockApiLookup<KitchenItemProvider, Void> kitchenItemProviderLookup = BlockApiLookup.get(
                ResourceLocation.fromNamespaceAndPath(CookingForBlockheads.MOD_ID, "kitchen_item_provider"),
                KitchenItemProvider.class, Void.class);
        kitchenItemProviderLookup.registerForBlockEntities(
                (blockEntity, context) -> ((KitchenStationBlockEntity) blockEntity).getItemProvider(),
                RefinedCookingBlockEntities.KITCHEN_STATION.get());
    }
}
