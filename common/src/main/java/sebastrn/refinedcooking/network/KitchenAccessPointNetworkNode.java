package sebastrn.refinedcooking.network;

import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import net.minecraft.core.GlobalPos;

import javax.annotation.Nullable;

/**
 * A plain {@link SimpleNetworkNode} that also remembers which station its card points at.
 * <p>
 * This is not a revival of the Refined Storage 1 node class that used to live here — that one existed to carry an
 * RS1 node id, connect/disconnect callbacks and an energy usage, all of which RS2 handles for us. This one exists
 * for one reason: the network graph hands out {@code NetworkNodeContainer#getNode()}, so making the node a distinct
 * type is what lets a Kitchen Station find the Access Point that linked it. A bare {@code SimpleNetworkNode} is
 * indistinguishable from every other node in the network, including the Station's own.
 * <p>
 * The station position lives here rather than on the block entity so there is exactly one copy of it.
 */
public class KitchenAccessPointNetworkNode extends SimpleNetworkNode {

    @Nullable
    private GlobalPos stationPos;

    public KitchenAccessPointNetworkNode(long energyUsage) {
        super(energyUsage);
    }

    /** Where the inserted card points, or null when there is no card, or it is unbound. */
    @Nullable
    public GlobalPos getStationPos() {
        return stationPos;
    }

    public void setStationPos(@Nullable GlobalPos stationPos) {
        this.stationPos = stationPos;
    }
}
