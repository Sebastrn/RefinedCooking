package sebastrn.refinedcooking;

import net.blay09.mods.balm.core.BalmRegistrars;
import sebastrn.refinedcooking.config.ServerConfig;

/**
 * Loader-agnostic entry point. Each loader's shim ({@code NeoForgeRefinedCooking} / {@code FabricRefinedCooking})
 * calls {@code Balm.initializeMod} with {@link #initialize(BalmRegistrars)}; the loader-specific wiring, 
 * capability/lookup registration for the Refined Storage network node and CFB's {@code KitchenItemProvider}, stays
 * in the shims. (The One Probe integration is disabled on 26.1.2: no 26.1 TOP build.)
 */
public final class RefinedCooking {

    public static final String ID = "refinedcooking";
    public static final ServerConfig SERVER_CONFIG = new ServerConfig();

    private RefinedCooking() {
    }

    public static void initialize(BalmRegistrars registrars) {
        SERVER_CONFIG.initialize();
        registrars.blocks(RefinedCookingBlocks::initialize);
        registrars.blockEntityTypes(RefinedCookingBlockEntities::initialize);
        registrars.items(RefinedCookingItems::initialize);
        registrars.creativeModeTabs(RefinedCookingCreativeTab::initialize);
        registrars.menuTypes(RefinedCookingContainerMenus::initialize);
    }
}
