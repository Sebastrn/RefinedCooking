package dev.smolinacadena.refinedcooking;

import dev.smolinacadena.refinedcooking.item.KitchenNetworkCardItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class RefinedCookingItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RefinedCooking.ID);

    public static final RegistryObject<KitchenNetworkCardItem> KITCHEN_NETWORK_CARD;

    static {
        registerBlockItemFor(RefinedCookingBlocks.KITCHEN_STATION);
        registerBlockItemFor(RefinedCookingBlocks.KITCHEN_ACCESS_POINT);

        KITCHEN_NETWORK_CARD = ITEMS.register("kitchen_network_card", KitchenNetworkCardItem::new);
    }

    private RefinedCookingItems() {
    }

    private static <T extends Block> RegistryObject<BlockItem> registerBlockItemFor(RegistryObject<T> block) {
        return ITEMS.register(
                block.getId().getPath(),
                () -> new BlockItem(block.get(), new Item.Properties().tab(RefinedCooking.MAIN_GROUP).stacksTo(1)));
    }

    public static void register() {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
