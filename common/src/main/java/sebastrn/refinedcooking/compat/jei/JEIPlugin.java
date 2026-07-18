package sebastrn.refinedcooking.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import sebastrn.refinedcooking.RefinedCooking;
import sebastrn.refinedcooking.RefinedCookingItems;

/**
 * JEI integration: attaches "info" pages to the three Refined Cooking items explaining how they link and work, so the
 * how-to is discoverable right where players look things up. Loaded only when JEI is present (JEI's {@code @JeiPlugin}
 * scan never touches this class otherwise), so JEI stays an optional dependency.
 */
@JeiPlugin
public class JEIPlugin implements IModPlugin {

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(RefinedCooking.ID, "jei");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addIngredientInfo(
                new ItemStack(RefinedCookingItems.KITCHEN_STATION.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.refinedcooking.kitchen_station.1"),
                Component.translatable("jei.refinedcooking.kitchen_station.2"));

        registration.addIngredientInfo(
                new ItemStack(RefinedCookingItems.KITCHEN_ACCESS_POINT.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.refinedcooking.kitchen_access_point.1"),
                Component.translatable("jei.refinedcooking.kitchen_access_point.2"));

        registration.addIngredientInfo(
                new ItemStack(RefinedCookingItems.KITCHEN_NETWORK_CARD.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.refinedcooking.kitchen_network_card.1"));
    }
}
