package dev.smolinacadena.refinedcooking.item.group;

import dev.smolinacadena.refinedcooking.RefinedCookingItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class MainCreativeModeTab extends CreativeModeTab {
    public MainCreativeModeTab(String label) {
        super(label);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(RefinedCookingItems.KITCHEN_NETWORK_CARD.get());
    }
}
