package sebastrn.refinedcooking.neoforge;

import com.refinedmods.refinedstorage.common.api.support.network.AbstractNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.neoforge.api.RefinedStorageNeoForgeApi;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.neoforge.NeoForgeLoadContext;
import net.blay09.mods.balm.neoforge.provider.NeoForgeBalmProviders;
import net.blay09.mods.cookingforblockheads.api.KitchenItemProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import sebastrn.refinedcooking.RefinedCooking;
import sebastrn.refinedcooking.RefinedCookingBlockEntities;
import sebastrn.refinedcooking.compat.Compat;
import sebastrn.refinedcooking.compat.theoneprobe.TheOneProbeAddon;

/**
 * NeoForge entry point. Boots the loader-neutral mod through Balm, then does the NeoForge-only capability wiring
 * (moved here from the old single-module {@code RefinedCooking}). See {@link #registerCapabilities}.
 */
@Mod(RefinedCooking.ID)
public final class NeoForgeRefinedCooking {

    public NeoForgeRefinedCooking(IEventBus modEventBus) {
        Balm.initialize(RefinedCooking.ID, new NeoForgeLoadContext(modEventBus), RefinedCooking::initialize);

        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::enqueueIMC);
    }

    /**
     * Wires the block entities' capabilities on NeoForge:
     * <ul>
     *     <li>both blocks expose their network-node container to Refined Storage (see
     *     {@link #registerNetworkNodeContainerProvider}), without it nothing connects, because RS looks the capability
     *     up at each position rather than walking block entities;</li>
     *     <li>the Kitchen Station exposes CFB's {@link KitchenItemProvider}; CFB keeps that block capability private but
     *     registers it with Balm, so it is obtained from Balm here;</li>
     *     <li>the Kitchen Access Point exposes its network-card slot as a vanilla item handler.</li>
     * </ul>
     */
    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        registerNetworkNodeContainerProvider(event, RefinedCookingBlockEntities.KITCHEN_STATION.get());
        registerNetworkNodeContainerProvider(event, RefinedCookingBlockEntities.KITCHEN_ACCESS_POINT.get());

        @SuppressWarnings("unchecked")
        BlockCapability<KitchenItemProvider, Void> kitchenItemProvider =
                (BlockCapability<KitchenItemProvider, Void>) ((NeoForgeBalmProviders) Balm.getProviders())
                        .getBlockCapability(KitchenItemProvider.class);
        event.registerBlockEntity(kitchenItemProvider, RefinedCookingBlockEntities.KITCHEN_STATION.get(),
                (blockEntity, context) -> blockEntity.getItemProvider());

        // RS2's card slot is a vanilla Container (RS1's was an IItemHandler), wrap it so hoppers/pipes can still
        // insert and pull the card.
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, RefinedCookingBlockEntities.KITCHEN_ACCESS_POINT.get(),
                (blockEntity, context) -> new InvWrapper(blockEntity.getNetworkCardInventory()));
    }

    private void registerNetworkNodeContainerProvider(RegisterCapabilitiesEvent event,
            BlockEntityType<? extends AbstractNetworkNodeContainerBlockEntity<?>> type) {
        event.registerBlockEntity(
                RefinedStorageNeoForgeApi.INSTANCE.getNetworkNodeContainerProviderCapability(),
                type,
                (blockEntity, side) -> blockEntity.getContainerProvider());
    }

    private void enqueueIMC(InterModEnqueueEvent event) {
        if (Balm.isModLoaded(Compat.THEONEPROBE)) {
            TheOneProbeAddon.register();
        }
    }
}
