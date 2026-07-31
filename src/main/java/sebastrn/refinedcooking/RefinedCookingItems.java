package sebastrn.refinedcooking;

import com.refinedmods.refinedstorage.item.blockitem.BaseBlockItem;
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
     * The Access Point is rotatable, and RS's {@link BaseBlockItem} is what turns the block to face the player on
     * placement, a plain BlockItem would leave it stuck on the default facing. (The Station doesn't need this: CFB's
     * BaseKitchenBlock sets its own facing on placement.)
     */
    public static final DeferredHolder<Item, BaseBlockItem> KITCHEN_ACCESS_POINT =
            ITEMS.register("kitchen_access_point", () -> new BaseBlockItem(
                    RefinedCookingBlocks.KITCHEN_ACCESS_POINT.get(), new Item.Properties().stacksTo(1)));

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
