package sebastrn.refinedcooking;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import sebastrn.refinedcooking.item.KitchenNetworkCardItem;

public final class RefinedCookingItems {

    // 26.1 requires every item to carry its registry id (Item.Properties.setId). The DeferredRegister.Items helper's
    // registerSimpleBlockItem/registerItem set it (registerSimpleBlockItem also applies useBlockDescriptionPrefix, so
    // the block items take the block's translation key); a plain DeferredRegister<Item> leaves the id unset.
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RefinedCooking.ID);

    public static final DeferredItem<BlockItem> KITCHEN_STATION =
            ITEMS.registerSimpleBlockItem(RefinedCookingBlocks.KITCHEN_STATION, () -> new Item.Properties().stacksTo(1));
    /**
     * A plain BlockItem is enough now. On RS1 this had to be RS's own {@code BaseBlockItem}, because that was what
     * turned the rotatable Access Point to face the player on placement; RS2's {@code AbstractDirectionalBlock} does
     * it itself in {@code getStateForPlacement}.
     */
    public static final DeferredItem<BlockItem> KITCHEN_ACCESS_POINT =
            ITEMS.registerSimpleBlockItem(RefinedCookingBlocks.KITCHEN_ACCESS_POINT, () -> new Item.Properties().stacksTo(1));

    public static final DeferredItem<KitchenNetworkCardItem> KITCHEN_NETWORK_CARD =
            ITEMS.registerItem("kitchen_network_card", KitchenNetworkCardItem::new, () -> new Item.Properties().stacksTo(1));

    private RefinedCookingItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
