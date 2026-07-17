package sebastrn.refinedcooking;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import sebastrn.refinedcooking.item.KitchenNetworkCardItem;

public final class RefinedCookingItems {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, RefinedCooking.ID);

    public static final DeferredHolder<Item, BlockItem> KITCHEN_STATION =
            registerBlockItemFor(RefinedCookingBlocks.KITCHEN_STATION);
    /**
     * A plain BlockItem is enough now. On RS1 this had to be RS's own {@code BaseBlockItem}, because that was what
     * turned the rotatable Access Point to face the player on placement; RS2's {@code AbstractDirectionalBlock} does
     * it itself in {@code getStateForPlacement}.
     */
    public static final DeferredHolder<Item, BlockItem> KITCHEN_ACCESS_POINT =
            registerBlockItemFor(RefinedCookingBlocks.KITCHEN_ACCESS_POINT);

    public static final DeferredHolder<Item, KitchenNetworkCardItem> KITCHEN_NETWORK_CARD =
            ITEMS.register("kitchen_network_card", KitchenNetworkCardItem::new);

    private RefinedCookingItems() {
    }

    private static <T extends Block> DeferredHolder<Item, BlockItem> registerBlockItemFor(DeferredHolder<Block, T> block) {
        return ITEMS.register(
                block.getId().getPath(),
                () -> new BlockItem(block.get(), new Item.Properties().stacksTo(1)));
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
