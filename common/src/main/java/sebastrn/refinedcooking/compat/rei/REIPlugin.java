package sebastrn.refinedcooking.compat.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.client.BuiltinClientPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import sebastrn.refinedcooking.RefinedCookingItems;

/**
 * REI integration: the same "info" pages the JEI plugin attaches, so REI users get the how-to on the three Refined
 * Cooking items. REI reuses the existing {@code jei.refinedcooking.*} text — the keys are viewer-agnostic. Loader-neutral
 * (no discovery annotation) so it compiles on both loaders: NeoForge discovers it via the {@code @REIPluginClient}
 * subclass {@code NeoForgeREIPlugin}, Fabric via the {@code rei_client} entrypoint. Only touched when REI is present.
 */
public class REIPlugin implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        info(RefinedCookingItems.KITCHEN_STATION.get(),
                "jei.refinedcooking.kitchen_station.1", "jei.refinedcooking.kitchen_station.2");
        info(RefinedCookingItems.KITCHEN_ACCESS_POINT.get(),
                "jei.refinedcooking.kitchen_access_point.1", "jei.refinedcooking.kitchen_access_point.2");
        info(RefinedCookingItems.KITCHEN_NETWORK_CARD.get(),
                "jei.refinedcooking.kitchen_network_card.1");
    }

    private static void info(ItemLike item, String... lineKeys) {
        BuiltinClientPlugin.getInstance().registerInformation(
                EntryIngredients.of(item),
                new ItemStack(item).getHoverName(),
                lines -> {
                    for (String key : lineKeys) {
                        lines.add(Component.translatable(key));
                    }
                    return lines;
                });
    }
}
