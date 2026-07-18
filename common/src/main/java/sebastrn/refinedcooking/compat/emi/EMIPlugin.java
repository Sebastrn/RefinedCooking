package sebastrn.refinedcooking.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiInfoRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import sebastrn.refinedcooking.RefinedCooking;
import sebastrn.refinedcooking.RefinedCookingItems;

import java.util.ArrayList;
import java.util.List;

/**
 * EMI integration: the same "info" pages the JEI plugin attaches, so EMI users get the how-to on the three Refined
 * Cooking items. Reuses the existing {@code jei.refinedcooking.*} text (the keys are viewer-agnostic). Unlike REI, EMI
 * does not read JEI plugins, so this class is the only way EMI users see these. Discovered by EMI's {@code @EmiEntrypoint}
 * scan, so EMI stays optional.
 */
@EmiEntrypoint
public class EMIPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        info(registry, RefinedCookingItems.KITCHEN_STATION.get(), "kitchen_station",
                "jei.refinedcooking.kitchen_station.1", "jei.refinedcooking.kitchen_station.2");
        info(registry, RefinedCookingItems.KITCHEN_ACCESS_POINT.get(), "kitchen_access_point",
                "jei.refinedcooking.kitchen_access_point.1", "jei.refinedcooking.kitchen_access_point.2");
        info(registry, RefinedCookingItems.KITCHEN_NETWORK_CARD.get(), "kitchen_network_card",
                "jei.refinedcooking.kitchen_network_card.1");
    }

    private static void info(EmiRegistry registry, ItemLike item, String id, String... lineKeys) {
        List<Component> text = new ArrayList<>();
        for (String key : lineKeys) {
            text.add(Component.translatable(key));
        }
        // Synthetic recipe id: these info pages have no data-driven JSON recipe, so EMI wants the path prefixed with
        // '/' (otherwise its dev mode warns that the id isn't in the recipe manager and isn't marked synthetic).
        registry.addRecipe(new EmiInfoRecipe(
                List.<EmiIngredient>of(EmiStack.of(item)),
                text,
                ResourceLocation.fromNamespaceAndPath(RefinedCooking.ID, "/info/" + id)));
    }
}
