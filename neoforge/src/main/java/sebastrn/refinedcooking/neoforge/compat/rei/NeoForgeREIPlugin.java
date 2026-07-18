package sebastrn.refinedcooking.neoforge.compat.rei;

import me.shedaniel.rei.forge.REIPluginClient;
import sebastrn.refinedcooking.compat.rei.REIPlugin;

/**
 * NeoForge discovery shim for the loader-neutral {@link REIPlugin}: REI on NeoForge finds client plugins by scanning
 * for {@code @REIPluginClient}, which lives in the NeoForge-only {@code me.shedaniel.rei.forge} package. The shared
 * info-page logic is inherited; on Fabric the same {@link REIPlugin} is registered via the {@code rei_client} entrypoint.
 */
@REIPluginClient
public final class NeoForgeREIPlugin extends REIPlugin {
}
