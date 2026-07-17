package sebastrn.refinedcooking.blockentity;

import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage.api.network.impl.node.controller.ControllerNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.GraphNetworkComponent;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.network.InWorldNetworkNodeContainer;
import com.refinedmods.refinedstorage.common.support.network.AbstractBaseNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.support.network.SimpleConnectionStrategy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.BlockState;
import sebastrn.refinedcooking.RefinedCooking;
import sebastrn.refinedcooking.RefinedCookingBlockEntities;
import sebastrn.refinedcooking.api.cookingforblockheads.capability.RSKitchenItemProvider;
import sebastrn.refinedcooking.network.KitchenStationKey;

import javax.annotation.Nullable;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * The remote end of the link: a network node that an Access Point pulls into a Refined Storage network, and that
 * exposes that network's contents to Cooking for Blockheads. Modelled on RS's own Network Receiver, which is the
 * same concept — a passive node a Network Transmitter reaches out to.
 * <p>
 * It joins the network purely by being reachable: the Access Point's connection strategy calls
 * {@code ConnectionSink#tryConnect} on our position. We index ourselves under a {@link KitchenStationKey} so the
 * Access Point can confirm we really landed in its graph.
 * <p>
 * The {@code connected} blockstate is driven by the ticker, not from here — see {@code KitchenStationBlock#getTicker},
 * which hands the property to RS's {@code NetworkNodeBlockEntityTicker}.
 */
public class KitchenStationBlockEntity extends AbstractBaseNetworkNodeContainerBlockEntity<SimpleNetworkNode> {

    /**
     * Supplies the Refined Storage network's contents to Cooking for Blockheads. Exposed to CFB through
     * {@code RegisterCapabilitiesEvent} (see {@code RefinedCooking#registerCapabilities}) rather than the old
     * Forge {@code getCapability} override, which no longer exists on NeoForge.
     */
    private final RSKitchenItemProvider itemProvider = new RSKitchenItemProvider(this);

    public KitchenStationBlockEntity(BlockPos pos, BlockState state) {
        super(RefinedCookingBlockEntities.KITCHEN_STATION.get(), pos, state,
                new SimpleNetworkNode(RefinedCooking.SERVER_CONFIG.getKitchenStation().getUsage()));
    }

    @Override
    protected InWorldNetworkNodeContainer createMainContainer(SimpleNetworkNode networkNode) {
        return RefinedStorageApi.INSTANCE.createNetworkNodeContainer(this, networkNode)
                // Connect on all six sides, as every RS1 node did. RS's own blocks use ColoredConnectionStrategy,
                // which also refuses connections through a block's facing side — we have no colour, and the Station
                // is meant to be cabled up from any side.
                .connectionStrategy(new SimpleConnectionStrategy(getBlockPos()))
                .keyProvider(this::createKey)
                .build();
    }

    private KitchenStationKey createKey() {
        return new KitchenStationKey(GlobalPos.of(requireNonNull(level).dimension(), getBlockPos()));
    }

    /** The Station is the passive end of the link; redstone control belongs on the Access Point. */
    @Override
    protected boolean hasRedstoneMode() {
        return false;
    }

    public RSKitchenItemProvider getItemProvider() {
        return itemProvider;
    }

    @Nullable
    public Network getNetwork() {
        return mainNetworkNode.getNetwork();
    }

    /** For {@code RSKitchenItemProvider}, which attributes its storage operations to this node like RS's own do. */
    public SimpleNetworkNode getMainNetworkNode() {
        return mainNetworkNode;
    }

    /**
     * A controller in our network, for the Jade / The One Probe tooltips — the closest thing RS2 has to RS1's
     * {@code INetwork.getPosition()}, which is gone: an RS2 network is a graph with no single anchor, and may hold
     * more than one controller (RS1 allowed exactly one), so we report the first the graph gives us.
     */
    public Optional<BlockPos> getNetworkControllerPos() {
        Network network = getNetwork();
        if (network == null) {
            return Optional.empty();
        }
        return network.getComponent(GraphNetworkComponent.class).getContainers().stream()
                .filter(container -> container.getNode() instanceof ControllerNetworkNode)
                .filter(InWorldNetworkNodeContainer.class::isInstance)
                .map(container -> ((InWorldNetworkNodeContainer) container).getPosition().pos())
                .findFirst();
    }

    @Override
    public Component getName() {
        return overrideName(Component.translatable("block.refinedcooking.kitchen_station"));
    }
}
