package sebastrn.refinedcooking;

import com.refinedmods.refinedstorage.common.api.support.network.AbstractNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.neoforge.api.RefinedStorageNeoForgeApi;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.neoforge.provider.NeoForgeBalmProviders;
import net.blay09.mods.cookingforblockheads.api.KitchenItemProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import sebastrn.refinedcooking.compat.Compat;
import sebastrn.refinedcooking.compat.theoneprobe.TheOneProbeAddon;
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
        modEventBus.addListener(this::enqueueIMC);

        if (FMLEnvironment.dist == Dist.CLIENT) {
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

        @SuppressWarnings("unchecked")
        BlockCapability<KitchenItemProvider, Void> kitchenItemProvider =
                (BlockCapability<KitchenItemProvider, Void>) ((NeoForgeBalmProviders) Balm.getProviders())
                        .getBlockCapability(KitchenItemProvider.class);

        event.registerBlockEntity(kitchenItemProvider, RefinedCookingBlockEntities.KITCHEN_STATION.get(),
                (blockEntity, context) -> blockEntity.getItemProvider());

        // RS2's card slot is a vanilla Container, where RS1's was an IItemHandler — wrap it so hoppers and pipes can
        // still insert and pull the card, as they could before.
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, RefinedCookingBlockEntities.KITCHEN_ACCESS_POINT.get(),
                (blockEntity, context) -> new InvWrapper(blockEntity.getNetworkCardInventory()));
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

    private void enqueueIMC(InterModEnqueueEvent event) {
        if (Balm.isModLoaded(Compat.THEONEPROBE)) {
            TheOneProbeAddon.register();
        }
    }
}
