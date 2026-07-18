package sebastrn.refinedcooking;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.item.BalmItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class RefinedCookingCreativeTab {

    // Balm titles a tab via "itemGroup." + id.toString().replace(':','.'), i.e. itemGroup.refinedcooking.refinedcooking
    // (the lang files carry that key). Registered before the blocks/items so they can populate it by id.
    public static final ResourceLocation TAB_ID =
            ResourceLocation.fromNamespaceAndPath(RefinedCooking.ID, RefinedCooking.ID);

    public static DeferredObject<CreativeModeTab> TAB;

    private RefinedCookingCreativeTab() {
    }

    public static void initialize(BalmItems items) {
        TAB = items.registerCreativeModeTab(
                () -> new ItemStack(RefinedCookingItems.KITCHEN_NETWORK_CARD.get()), TAB_ID);
    }
}
