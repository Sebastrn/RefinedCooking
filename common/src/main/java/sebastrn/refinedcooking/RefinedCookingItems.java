package sebastrn.refinedcooking;

import net.blay09.mods.balm.world.item.BalmItemRegistrar;
import net.blay09.mods.balm.world.item.DeferredItem;
import net.minecraft.world.item.Item;
import sebastrn.refinedcooking.item.KitchenNetworkCardItem;

import java.util.function.Supplier;

public final class RefinedCookingItems {

    // The Kitchen Station / Access Point items are the block items registered by RefinedCookingBlocks; the network card
    // is a real item. All three are exposed as Supplier<Item> so existing call sites (compat plugins, creative tab)
    // keep working through the same `.get()` shape — Balm's DeferredBlock/DeferredItem are Holders (`.value()`), not
    // the old `.get()` DeferredObjects.
    public static final Supplier<Item> KITCHEN_STATION = () -> RefinedCookingBlocks.KITCHEN_STATION.value().asItem();
    public static final Supplier<Item> KITCHEN_ACCESS_POINT = () -> RefinedCookingBlocks.KITCHEN_ACCESS_POINT.value().asItem();
    public static Supplier<Item> KITCHEN_NETWORK_CARD;

    private RefinedCookingItems() {
    }

    public static void initialize(BalmItemRegistrar items) {
        DeferredItem card = items.register("kitchen_network_card", KitchenNetworkCardItem::new, it -> it.stacksTo(1))
                .asDeferredItem();
        KITCHEN_NETWORK_CARD = card::value;
    }
}
