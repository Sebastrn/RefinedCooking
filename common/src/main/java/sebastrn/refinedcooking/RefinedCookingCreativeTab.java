package sebastrn.refinedcooking;

import net.blay09.mods.balm.world.item.BalmCreativeModeTabRegistrar;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class RefinedCookingCreativeTab {

    private RefinedCookingCreativeTab() {
    }

    public static void initialize(BalmCreativeModeTabRegistrar creativeModeTabs) {
        // Balm names creative tabs by the explicit title set here. Uses the "itemGroup.<namespace>.<path>" key the lang
        // files carry (itemGroup.refinedcooking.refinedcooking), so the tab shows "Refined Cooking".
        creativeModeTabs.register(RefinedCooking.ID, builder ->
                builder.title(Component.translatable("itemGroup." + RefinedCooking.ID + "." + RefinedCooking.ID))
                        .icon(() -> new ItemStack(RefinedCookingItems.KITCHEN_NETWORK_CARD.get()))
                        .displayItems((parameters, output) -> {
                            output.accept(RefinedCookingItems.KITCHEN_STATION.get());
                            output.accept(RefinedCookingItems.KITCHEN_ACCESS_POINT.get());
                            output.accept(RefinedCookingItems.KITCHEN_NETWORK_CARD.get());
                        }));
    }
}
