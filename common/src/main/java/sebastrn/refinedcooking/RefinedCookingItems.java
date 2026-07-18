package sebastrn.refinedcooking;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.item.BalmItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import sebastrn.refinedcooking.item.KitchenNetworkCardItem;

import java.util.function.Supplier;

public final class RefinedCookingItems {

    // The Kitchen Station / Access Point items are the block items registered by RefinedCookingBlocks; exposed here as
    // Item suppliers so existing call sites (compat plugins, TOP addon) keep working through the same `.get()` shape.
    public static final Supplier<Item> KITCHEN_STATION = () -> RefinedCookingBlocks.KITCHEN_STATION.get().asItem();
    public static final Supplier<Item> KITCHEN_ACCESS_POINT = () -> RefinedCookingBlocks.KITCHEN_ACCESS_POINT.get().asItem();

    public static DeferredObject<Item> KITCHEN_NETWORK_CARD;

    private RefinedCookingItems() {
    }

    public static void initialize(BalmItems items) {
        KITCHEN_NETWORK_CARD = items.registerItem(id -> new KitchenNetworkCardItem(),
                id("kitchen_network_card"), RefinedCookingCreativeTab.TAB_ID);
    }

    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(RefinedCooking.ID, name);
    }
}
