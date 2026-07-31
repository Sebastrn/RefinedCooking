package sebastrn.refinedcooking.fabric;

import com.refinedmods.refinedstorage.common.api.support.network.AbstractNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.api.support.network.NetworkNodeContainerProvider;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.blay09.mods.cookingforblockheads.CookingForBlockheads;
import net.blay09.mods.cookingforblockheads.api.KitchenItemProvider;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import sebastrn.refinedcooking.RefinedCooking;
import sebastrn.refinedcooking.RefinedCookingBlockEntities;
import sebastrn.refinedcooking.blockentity.KitchenStationBlockEntity;

/**
 * Fabric entry point. Boots the loader-neutral mod through Balm, then does the Fabric-only provider wiring, the
 * {@code BlockApiLookup} analogs of the NeoForge capabilities registered in {@code NeoForgeRefinedCooking}.
 */
public final class FabricRefinedCooking implements ModInitializer {

    @Override
    public void onInitialize() {
        Balm.initializeMod(RefinedCooking.ID, FabricLoadContext.INSTANCE, RefinedCooking::initialize);

        // RS network-node discovery. Fabric has no NeoForge capabilities, so RS exposes a BlockApiLookup for the same
        // loader-neutral NetworkNodeContainerProvider; RS scans it at each position to build the network graph, the
        // exact analog of NeoForgeRefinedCooking's getNetworkNodeContainerProviderCapability() registration.
        // We fetch the lookup by RS's own id (refinedstorage:network_node_container_provider) rather than via
        // RefinedStorageFabricApi.INSTANCE: the latter's proxy throws "Fabric API not loaded yet" if our initializer
        // runs before RS's (Fabric's `depends` does not order initializers), whereas BlockApiLookup.get returns the
        // same cached instance regardless of order. Context type is a nullable Direction (see RefinedStorageFabricApiImpl).
        BlockApiLookup<NetworkNodeContainerProvider, Direction> nodeLookup = BlockApiLookup.get(
                Identifier.fromNamespaceAndPath("refinedstorage", "network_node_container_provider"),
                NetworkNodeContainerProvider.class, Direction.class);
        nodeLookup.registerForBlockEntities(
                (blockEntity, direction) ->
                        ((AbstractNetworkNodeContainerBlockEntity<?>) blockEntity).getContainerProvider(),
                RefinedCookingBlockEntities.KITCHEN_STATION.value(),
                RefinedCookingBlockEntities.KITCHEN_ACCESS_POINT.value());

        // Expose the Kitchen Station as CFB's KitchenItemProvider on the same BlockApiLookup CFB scans. CFB registers
        // that class under the id cookingforblockheads:kitchen_item_provider (see its FabricCookingForBlockheads), and
        // BlockApiLookup.get returns the shared instance for that id, so registering here is found by CFB's kitchen.
        BlockApiLookup<KitchenItemProvider, Void> kitchenItemProviderLookup = BlockApiLookup.get(
                Identifier.fromNamespaceAndPath(CookingForBlockheads.MOD_ID, "kitchen_item_provider"),
                KitchenItemProvider.class, Void.class);
        kitchenItemProviderLookup.registerForBlockEntities(
                (blockEntity, context) -> ((KitchenStationBlockEntity) blockEntity).getItemProvider(),
                RefinedCookingBlockEntities.KITCHEN_STATION.value());

        registerMilkBucketFluidStorage();
    }

    /**
     * Makes the vanilla milk bucket drainable through Fabric's Transfer API, so milk can be inserted into a Refined
     * Storage grid (and then used by the cooking table, exactly like water). This fills a gap left by the platform +
     * Balm: Balm registers the milk <em>fluid</em> (via CFB's {@code enableMilkFluid}) but no item fluid-storage for the
     * bucket, and vanilla's {@code MilkBucketItem} is not a {@code BucketItem}, so Fabric API's bucket provider skips it
     *, hence RS finds nothing to drain. NeoForge/Forge already make milk buckets drainable at the platform level, so
     * this only brings Fabric to parity. Carried from the 1.21.1 Fabric port (F1); remove if Balm/CFB ship it upstream
     * (a duplicate {@code registerForItems} on the milk bucket would then collide).
     * <p>
     * Drain-only: extracting milk back <em>into</em> an empty bucket isn't cleanly possible on Fabric, because the empty
     * bucket's item storage is owned by Fabric API and only knows {@code BucketItem} fluids, and insertion is all the
     * cooking-from-network use case needs.
     */
    private static void registerMilkBucketFluidStorage() {
        FluidStorage.ITEM.registerForItems((itemStack, context) -> {
            // Read the milk fluid lazily (inside the provider), not at init: CFB's enableMilkFluid() may run after us,
            // and Fabric's `depends` does not order initializers. By drain time it is always set; guard just in case.
            final Fluid milk = Balm.modSupport().milkFluid().get();
            if (milk == null) {
                return null;
            }
            return new FullItemFluidStorage(context, Items.BUCKET, FluidVariant.of(milk), FluidConstants.BUCKET);
        }, Items.MILK_BUCKET);
    }
}
