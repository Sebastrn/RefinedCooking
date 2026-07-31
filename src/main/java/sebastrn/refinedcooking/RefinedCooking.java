package sebastrn.refinedcooking;

import com.refinedmods.refinedstorage.api.IRSAPI;
import com.refinedmods.refinedstorage.api.RSAPIInject;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.neoforge.provider.NeoForgeBalmProviders;
import net.blay09.mods.cookingforblockheads.api.KitchenItemProvider;
import net.blay09.mods.cookingforblockheads.block.entity.ModBlockEntities;
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

        registerOvenItemProvider(event, kitchenItemProvider);
    }

    /**
     * Registers Cooking for Blockheads' <em>own</em> Oven against its <em>own</em> item provider capability, which
     * CFB forgot to do here, every other complex block entity of theirs (sink, milk jar, fridge, cutting board) is
     * registered, and the Oven is registered for its energy capability two lines away, but never for this one. The
     * result is that the Oven's tool and output slots are invisible to the Cooking Table: a pot sitting in the Oven
     * does not count towards a recipe, though the same pot in a Refined Storage network does.
     * <p>
     * This is not our bug and not our block, so it is worth being explicit about why we fix it anyway: Cooking for
     * Blockheads 18.0.9 is the <em>final</em> 1.20.4 build, so upstream will never correct it. CFB fixed it on their
     * 1.21.1 line (21.1.24 registers the Oven), so this will not be needed when this mod moves there.
     * <p>
     * We deliberately hand back CFB's own provider via {@code getProvider(...)} rather than wrapping the Oven's
     * container ourselves. Their provider is scoped to the tools and output slots only; the Oven's full container is
     * 20 slots including the inputs, so exposing that would let the Cooking Table eat raw food queued for cooking.
     * Tagging the Oven into {@code kitchen_item_providers} does exactly that, and would additionally break when
     * {@code disallowOvenAutomation} is enabled, since that makes {@code getContainer()} return null.
     * <p>
     * Applied Cooking ships the identical fix. If both mods are present each registers the Oven, which is harmless, 
     * NeoForge tries registrations in turn until one returns non-null, but a player running only one of them still
     * gets a working Oven, which is the point.
     */
    private void registerOvenItemProvider(RegisterCapabilitiesEvent event,
                                          BlockCapability<KitchenItemProvider, Void> kitchenItemProvider) {
        event.registerBlockEntity(kitchenItemProvider, ModBlockEntities.oven.get(),
                (blockEntity, context) -> blockEntity.getProvider(KitchenItemProvider.class));
    }

    private void enqueueIMC(InterModEnqueueEvent event) {
        if (Balm.isModLoaded(Compat.THEONEPROBE)) {
            TheOneProbeAddon.register();
        }
    }
}
