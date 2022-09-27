package dev.smolinacadena.refinedcooking.item.group;

import dev.smolinacadena.refinedcooking.RefinedCookingItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class MainItemGroup extends ItemGroup {
    public MainItemGroup(String label) {
        super(label);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(RefinedCookingItems.KITCHEN_NETWORK_CARD.get());
    }
}
