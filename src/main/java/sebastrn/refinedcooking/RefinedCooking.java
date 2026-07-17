package sebastrn.refinedcooking;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.neoforge.provider.NeoForgeBalmProviders;
import net.blay09.mods.cookingforblockheads.api.KitchenItemProvider;
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
     *     <li>the Kitchen Station exposes Cooking for Blockheads' {@link KitchenItemProvider}. CFB keeps that
     *     block capability private but registers it with Balm, so it is obtained from Balm here (the same path
     *     Applied Cooking uses);</li>
     *     <li>the Kitchen Access Point exposes its network-card slot as a vanilla item handler.</li>
     * </ul>
     */
    private void registerCapabilities(RegisterCapabilitiesEvent event) {
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

    private void enqueueIMC(InterModEnqueueEvent event) {
        if (Balm.isModLoaded(Compat.THEONEPROBE)) {
            TheOneProbeAddon.register();
        }
    }
}
