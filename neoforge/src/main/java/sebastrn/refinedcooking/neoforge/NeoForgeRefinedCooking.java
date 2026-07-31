package sebastrn.refinedcooking.neoforge;

import com.refinedmods.refinedstorage.common.api.support.network.AbstractNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.neoforge.api.RefinedStorageNeoForgeApi;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.blay09.mods.cookingforblockheads.api.KitchenItemProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import sebastrn.refinedcooking.RefinedCooking;
import sebastrn.refinedcooking.RefinedCookingBlockEntities;

/**
 * NeoForge entry point. Boots the loader-neutral mod through Balm, then does the NeoForge-only capability wiring
 * (moved here from the old single-module {@code RefinedCooking}). See {@link #registerCapabilities}. The One Probe
 * integration is disabled for 26.1.2 (no 26.1 build), see {@code compat.theoneprobe.TheOneProbeAddon}.
 */
@Mod(RefinedCooking.ID)
public final class NeoForgeRefinedCooking {

    public NeoForgeRefinedCooking(IEventBus modEventBus, ModContainer modContainer) {
        Balm.initializeMod(RefinedCooking.ID, new NeoForgeLoadContext(modContainer, modEventBus), RefinedCooking::initialize);

        modEventBus.addListener(this::registerCapabilities);
    }

    /**
     * Wires the block entities' capabilities on NeoForge (there is no {@code getCapability} override anymore):
     * <ul>
     *     <li>both blocks expose their network-node container to Refined Storage, without it nothing connects, because
     *     RS looks the capability up at each position rather than walking block entities;</li>
     *     <li>the Kitchen Station exposes CFB's {@link KitchenItemProvider}. Balm 26.1 dropped the
     *     getProviders()/NeoForgeBalmProviders route; CFB now backs its {@code kitchen_item_provider} capability with a
     *     plain NeoForge {@link BlockCapability}. Those are singletons keyed by name+type+context, so re-creating CFB's
     *     exact one here returns the very object CFB's scanner looks up, independent of init order;</li>
     *     <li>the Kitchen Access Point exposes its network-card slot as the item capability.</li>
     * </ul>
     */
    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        registerNetworkNodeContainerProvider(event, RefinedCookingBlockEntities.KITCHEN_STATION.value());
        registerNetworkNodeContainerProvider(event, RefinedCookingBlockEntities.KITCHEN_ACCESS_POINT.value());

        BlockCapability<KitchenItemProvider, Void> kitchenItemProvider = BlockCapability.create(
                Identifier.fromNamespaceAndPath("cookingforblockheads", "kitchen_item_provider"),
                KitchenItemProvider.class, Void.class);
        event.registerBlockEntity(kitchenItemProvider, RefinedCookingBlockEntities.KITCHEN_STATION.value(),
                (blockEntity, context) -> blockEntity.getItemProvider());

        // 26.1's Capabilities.Item.BLOCK is the new ResourceHandler<ItemResource>, not IItemHandler, so wrap the card
        // slot (a vanilla Container) via NeoForge's VanillaContainerWrapper, the same adapter RS2 uses for its disks.
        event.registerBlockEntity(Capabilities.Item.BLOCK, RefinedCookingBlockEntities.KITCHEN_ACCESS_POINT.value(),
                (blockEntity, context) -> VanillaContainerWrapper.of(blockEntity.getNetworkCardInventory()));
    }

    private void registerNetworkNodeContainerProvider(RegisterCapabilitiesEvent event,
            BlockEntityType<? extends AbstractNetworkNodeContainerBlockEntity<?>> type) {
        event.registerBlockEntity(
                RefinedStorageNeoForgeApi.INSTANCE.getNetworkNodeContainerProviderCapability(),
                type,
                (blockEntity, side) -> blockEntity.getContainerProvider());
    }
}
