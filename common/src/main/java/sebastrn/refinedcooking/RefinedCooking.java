package sebastrn.refinedcooking;

import net.blay09.mods.balm.api.Balm;
import sebastrn.refinedcooking.config.ServerConfig;

/**
 * Loader-agnostic entry point. Each loader's shim ({@code NeoForgeRefinedCooking} / {@code FabricRefinedCooking})
 * calls {@link Balm#initialize} with {@link #initialize()}; the loader-specific wiring — capability/lookup
 * registration for the Refined Storage network node and CFB's {@code KitchenItemProvider}, plus the IMC to The One
 * Probe — stays in the shims.
 */
public final class RefinedCooking {

    public static final String ID = "refinedcooking";
    public static final ServerConfig SERVER_CONFIG = new ServerConfig();

    private RefinedCooking() {
    }

    public static void initialize() {
        SERVER_CONFIG.initialize();
        RefinedCookingCreativeTab.initialize(Balm.getItems());
        RefinedCookingBlocks.initialize(Balm.getBlocks());
        RefinedCookingItems.initialize(Balm.getItems());
        RefinedCookingBlockEntities.initialize(Balm.getBlockEntities());
        RefinedCookingContainerMenus.initialize(Balm.getMenus());
    }
}
