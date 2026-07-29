package sebastrn.refinedcooking.item;

import javax.annotation.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import sebastrn.refinedcooking.RefinedCooking;

/**
 * A client-side item-model predicate — 1 when the network card is bound to a station, 0 otherwise — so the card can
 * render a distinct bound/unbound texture, the same way RS's own Network Card does. RS's
 * {@code NetworkCardItemPropertyFunction} is the model; this is the RC equivalent. Registered per loader (the NeoForge and
 * Fabric client shims); the {@code models/item/kitchen_network_card.json} overrides select on {@link #NAME}.
 */
public class KitchenNetworkCardBoundProperty implements ClampedItemPropertyFunction {
    public static final ResourceLocation NAME =
            ResourceLocation.fromNamespaceAndPath(RefinedCooking.ID, "network_card_bound");

    @Override
    public float unclampedCall(final ItemStack itemStack,
                               @Nullable final ClientLevel clientLevel,
                               @Nullable final LivingEntity livingEntity,
                               final int i) {
        if (itemStack.getItem() instanceof KitchenNetworkCardItem card) {
            return card.isBound(itemStack) ? 1 : 0;
        }
        return 0;
    }
}
