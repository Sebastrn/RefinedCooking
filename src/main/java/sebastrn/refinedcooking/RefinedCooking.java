package sebastrn.refinedcooking;

import com.refinedmods.refinedstorage.api.IRSAPI;
import com.refinedmods.refinedstorage.api.RSAPIInject;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.neoforge.provider.NeoForgeBalmProviders;
import net.blay09.mods.cookingforblockheads.api.KitchenItemProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import sebastrn.refinedcooking.compat.Compat;
import sebastrn.refinedcooking.compat.theoneprobe.TheOneProbeAddon;
import sebastrn.refinedcooking.config.ServerConfig;
import sebastrn.refinedcooking.setup.ClientSetup;
import sebastrn.refinedcooking.setup.CommonSetup;

@Mod(RefinedCooking.ID)
public final class RefinedCooking {
    @RSAPIInject
    public static IRSAPI RSAPI;

    public static final String ID = "refinedcooking";
    public static final ServerConfig SERVER_CONFIG = new ServerConfig();

    public RefinedCooking(IEventBus modEventBus) {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG.getSpec());

        RefinedCookingBlocks.register(modEventBus);
        RefinedCookingItems.register(modEventBus);
        RefinedCookingBlockEntities.register(modEventBus);
        RefinedCookingContainerMenus.register(modEventBus);
        RefinedCookingCreativeTab.register(modEventBus);

        modEventBus.addListener(CommonSetup::onCommonSetup);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::enqueueIMC);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(ClientSetup::onClientSetup);
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

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, RefinedCookingBlockEntities.KITCHEN_ACCESS_POINT.get(),
                (blockEntity, context) -> blockEntity.getNode().getNetworkCard());
    }

    private void enqueueIMC(InterModEnqueueEvent event) {
        if (Balm.isModLoaded(Compat.THEONEPROBE)) {
            TheOneProbeAddon.register();
        }
    }
}
