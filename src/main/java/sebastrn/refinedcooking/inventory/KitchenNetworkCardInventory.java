package sebastrn.refinedcooking.inventory;

import com.refinedmods.refinedstorage.common.support.FilteredContainer;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.item.ItemStack;
import sebastrn.refinedcooking.item.KitchenNetworkCardItem;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * The Access Point's single card slot. Mirrors RS's own {@code NetworkCardInventory}, which is package-private and so
 * cannot be reused — but {@code FilteredContainer} is public, which is all it was built on.
 * <p>
 * Unbound cards are accepted, which is where this parts company with RS: RS's card slot takes bound cards only, but
 * ours has always taken any card, and the block has a state for exactly that ("card inserted, not transmitting").
 * Rejecting them outright would also be silent — RS's card tooltip says "Unbound", ours says nothing at all, so a
 * card that simply refused to go in would leave nothing to explain why.
 */
public class KitchenNetworkCardInventory extends FilteredContainer {

    public static final Predicate<ItemStack> IS_CARD = stack -> stack.getItem() instanceof KitchenNetworkCardItem;

    public KitchenNetworkCardInventory() {
        super(1, IS_CARD);
    }

    public ItemStack getNetworkCard() {
        return getItem(0);
    }

    public Optional<GlobalPos> getStationLocation() {
        ItemStack stack = getNetworkCard();
        if (stack.getItem() instanceof KitchenNetworkCardItem card) {
            return card.getLocation(stack);
        }
        return Optional.empty();
    }
}
