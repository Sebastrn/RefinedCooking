package sebastrn.refinedcooking.item;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import sebastrn.refinedcooking.RefinedCooking;

/**
 * A client-side item-model condition — true when the network card is bound to a station — so the card can render a
 * distinct bound/unbound texture, the same way RS's own Network Card does. Registered in {@code ClientSetup}; the
 * {@code items/kitchen_network_card.json} model selects on {@link #NAME}.
 */
public class KitchenNetworkCardBoundProperty implements ConditionalItemModelProperty {

    public static final Identifier NAME = Identifier.fromNamespaceAndPath(RefinedCooking.ID, "bound_network_card");
    public static final MapCodec<KitchenNetworkCardBoundProperty> MAP_CODEC =
            MapCodec.unit(new KitchenNetworkCardBoundProperty());

    @Override
    public MapCodec<? extends ConditionalItemModelProperty> type() {
        return MAP_CODEC;
    }

    @Override
    public boolean get(ItemStack stack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity,
                       int seed, ItemDisplayContext displayContext) {
        return stack.getItem() instanceof KitchenNetworkCardItem card && card.isBound(stack);
    }
}
