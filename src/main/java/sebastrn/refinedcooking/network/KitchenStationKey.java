package sebastrn.refinedcooking.network;

import net.minecraft.core.GlobalPos;

/**
 * Indexes a Kitchen Station's node in the network graph, so the Access Point can ask whether the station its card
 * points at actually ended up in the network — see {@code GraphNetworkComponent#getContainer(Object)}.
 * <p>
 * Refined Storage's own {@code NetworkReceiverKey} is package-private, so we cannot reuse it. That is no loss: the
 * key is only ever compared for equality, and having our own type keeps our stations in a separate key space from
 * RS's receivers rather than colliding with one at the same position.
 */
public record KitchenStationKey(GlobalPos pos) {
}
