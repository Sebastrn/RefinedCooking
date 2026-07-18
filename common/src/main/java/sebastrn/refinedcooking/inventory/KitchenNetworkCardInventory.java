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
 * Bound-only, exactly like RS's slot: an unbound card cannot be inserted. This is discoverable rather than silent
 * because the card carries a distinct bound/unbound item texture, so a card that won't go in shows why in the hand.
 */
public class KitchenNetworkCardInventory extends FilteredContainer {

    public static final Predicate<ItemStack> IS_CARD =
            stack -> stack.getItem() instanceof KitchenNetworkCardItem card && card.isBound(stack);

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
