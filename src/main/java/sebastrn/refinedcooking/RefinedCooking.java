package sebastrn.refinedcooking;

import com.refinedmods.refinedstorage.common.api.support.network.AbstractNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.neoforge.api.RefinedStorageNeoForgeApi;
import net.blay09.mods.cookingforblockheads.api.KitchenItemProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
// The One Probe integration is disabled for 26.1.2 (no 26.1 TOP build). Imports kept, commented, for easy re-enable.
// import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
// import sebastrn.refinedcooking.compat.Compat;
// import sebastrn.refinedcooking.compat.theoneprobe.TheOneProbeAddon;
import sebastrn.refinedcooking.config.ServerConfig;
import sebastrn.refinedcooking.setup.ClientSetup;

@Mod(RefinedCooking.ID)
public final class RefinedCooking {

    public static final String ID = "refinedcooking";
    public static final ServerConfig SERVER_CONFIG = new ServerConfig();

    public RefinedCooking(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG.getSpec());

        RefinedCookingBlocks.register(modEventBus);
        RefinedCookingItems.register(modEventBus);
        RefinedCookingBlockEntities.register(modEventBus);
        RefinedCookingContainerMenus.register(modEventBus);
        RefinedCookingCreativeTab.register(modEventBus);

        modEventBus.addListener(this::registerCapabilities);
        // modEventBus.addListener(this::enqueueIMC);  // TOP integration disabled for 26.1.2

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            modEventBus.addListener(ClientSetup::onRegisterMenuScreens);
        }
    }

    /**
     * Wires the block entities' capabilities on NeoForge (there is no {@code getCapability} override anymore):
     * <ul>
     *     <li>both blocks expose their network-node container to Refined Storage — see
     *     {@link #registerNetworkNodeContainerProvider};</li>
     *     <li>the Kitchen Station exposes Cooking for Blockheads' {@link KitchenItemProvider}. CFB keeps that
     *     block capability private but registers it with Balm, so it is obtained from Balm here (the same path
     *     Applied Cooking uses);</li>
     *     <li>the Kitchen Access Point exposes its network-card slot as a vanilla item handler.</li>
     * </ul>
     */
    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        registerNetworkNodeContainerProvider(event, RefinedCookingBlockEntities.KITCHEN_STATION.get());
        registerNetworkNodeContainerProvider(event, RefinedCookingBlockEntities.KITCHEN_ACCESS_POINT.get());

        // Balm 26.1 dropped the getProviders()/NeoForgeBalmProviders route; CFB now backs its kitchen_item_provider
        // capability with a plain NeoForge BlockCapability. Those are singletons keyed by name+type+context, so
        // re-creating CFB's exact one here returns the very object CFB's scanner looks up, independent of init order.
        BlockCapability<KitchenItemProvider, Void> kitchenItemProvider = BlockCapability.create(
                Identifier.fromNamespaceAndPath("cookingforblockheads", "kitchen_item_provider"),
                KitchenItemProvider.class, Void.class);

        event.registerBlockEntity(kitchenItemProvider, RefinedCookingBlockEntities.KITCHEN_STATION.get(),
                (blockEntity, context) -> blockEntity.getItemProvider());

        // Expose the card slot (a vanilla Container) as the item capability so hoppers and pipes can still insert and
        // pull the card. 26.1's Capabilities.Item.BLOCK is the new ResourceHandler<ItemResource>, not IItemHandler, so
        // wrap via NeoForge's VanillaContainerWrapper (the same adapter RS2 uses for its own disk inventories).
        event.registerBlockEntity(Capabilities.Item.BLOCK, RefinedCookingBlockEntities.KITCHEN_ACCESS_POINT.get(),
                (blockEntity, context) -> VanillaContainerWrapper.of(blockEntity.getNetworkCardInventory()));
    }

    /**
     * Makes our block entities visible to Refined Storage's network graph. <b>Without this nothing connects at all:</b>
     * RS does not walk block entities when it builds a network, it looks up this capability at each position — so an
     * unregistered node simply is not there. Our own nodes still form a network by initialising themselves, which is
     * what made the Access Point look like it was working; but its {@code tryConnect} to the Station resolved to null,
     * so the link never formed and the Station's storage never reached the kitchen.
     * <p>
     * RS registers this for every one of its own block entities in its mod initialiser, which is why extending its
     * base types is not enough on its own — that code does not run for us.
     */
    private void registerNetworkNodeContainerProvider(
            RegisterCapabilitiesEvent event,
            BlockEntityType<? extends AbstractNetworkNodeContainerBlockEntity<?>> type) {
        event.registerBlockEntity(
                RefinedStorageNeoForgeApi.INSTANCE.getNetworkNodeContainerProviderCapability(),
                type,
                (blockEntity, side) -> blockEntity.getContainerProvider()
        );
    }

    // The One Probe integration — disabled for 26.1.2 (no 26.1 TOP build). Re-enable with the imports, the
    // addListener call above, and TheOneProbeAddon when McJty ships a 26.1 TOP.
    // private void enqueueIMC(InterModEnqueueEvent event) {
    //     if (Balm.isModLoaded(Compat.THEONEPROBE)) {
    //         TheOneProbeAddon.register();
    //     }
    // }
}
