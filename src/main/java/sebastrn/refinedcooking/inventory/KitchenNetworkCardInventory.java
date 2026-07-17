package sebastrn.refinedcooking.inventory;

import com.refinedmods.refinedstorage.common.support.FilteredContainer;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.item.ItemStack;
import sebastrn.refinedcooking.item.KitchenNetworkCardItem;

import java.util.Optional;

/**
 * The Access Point's single card slot. Mirrors RS's own {@code NetworkCardInventory}, which is package-private and so
 * cannot be reused — but {@code FilteredContainer} is public, which is all it was built on.
 * <p>
 * Only <em>bound</em> cards are accepted, matching RS: an unbound card names no station, so it would do nothing but
 * make the block claim it had a card.
 */
public class KitchenNetworkCardInventory extends FilteredContainer {

    public KitchenNetworkCardInventory() {
        super(1, stack -> stack.getItem() instanceof KitchenNetworkCardItem card && card.isBound(stack));
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
