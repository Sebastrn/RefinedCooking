package sebastrn.refinedcooking.blockentity;

import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
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
import sebastrn.refinedcooking.block.KitchenStationBlock;
import sebastrn.refinedcooking.block.KitchenStationBlock.LinkState;
import sebastrn.refinedcooking.network.KitchenAccessPointNetworkNode;
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
 * The {@link KitchenStationBlock#LINK_STATE} blockstate is driven from here via {@link #updateLinkState()}, which the
 * Station's ticker calls each tick right after RS has updated the node's activeness.
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

    /**
     * Adds "and something else is actually on my network" to RS's own check, which is what drives the lit screen.
     * <p>
     * RS's {@code calculateActive} only distinguishes a real network from a lone one by testing
     * {@code stored >= energyUsage} — every RS2 node always <em>has</em> a network, forming one containing just
     * itself when nothing links it. That test is useless the moment the energy usage is zero: {@code 0 >= 0} passes,
     * so an unlinked Station would report itself active and sit there lit from the moment it was placed. Our default
     * is no longer zero, but a player may set it to zero to opt out of energy costs, and the screen must not start
     * lying when they do.
     */
    @Override
    protected boolean calculateActive() {
        return super.calculateActive() && isAttachedToNetwork();
    }

    /** Whether our network holds anything besides us — an Access Point's link, a cable, anything. */
    private boolean isAttachedToNetwork() {
        Network network = mainNetworkNode.getNetwork();
        return network != null
                && network.getComponent(GraphNetworkComponent.class).getContainers().size() > 1;
    }

    public RSKitchenItemProvider getItemProvider() {
        return itemProvider;
    }

    @Nullable
    public Network getNetwork() {
        return mainNetworkNode.getNetwork();
    }

    /** On a network with something else on it, and powered — i.e. exactly when the screen is lit. */
    public boolean isActive() {
        return mainNetworkNode.isActive();
    }

    /**
     * The three-way link state read by the blockstate model and the Jade/TOP tooltips. Derived, not stored: an active
     * node means ONLINE (screen lit), on a network but not active means LINKED_OFFLINE (linked, no power), neither
     * means UNLINKED (alone). This is the split the old {@code connected} boolean could not make.
     */
    public LinkState getLinkState() {
        if (isActive()) {
            return LinkState.ONLINE;
        }
        return isAttachedToNetwork() ? LinkState.LINKED_OFFLINE : LinkState.UNLINKED;
    }

    /**
     * Push {@link #getLinkState()} into the blockstate, but only when it changes so we do not spam block updates.
     * Server-only; called from the Station's ticker after RS has refreshed the node's activeness for the tick.
     */
    public void updateLinkState() {
        if (level == null) {
            return;
        }
        BlockState state = level.getBlockState(worldPosition);
        if (!(state.getBlock() instanceof KitchenStationBlock)) {
            return;
        }
        LinkState current = getLinkState();
        if (state.getValue(KitchenStationBlock.LINK_STATE) != current) {
            level.setBlockAndUpdate(worldPosition, state.setValue(KitchenStationBlock.LINK_STATE, current));
            setChanged();
        }
    }

    /** For {@code RSKitchenItemProvider}, which attributes its storage operations to this node like RS's own do. */
    public SimpleNetworkNode getMainNetworkNode() {
        return mainNetworkNode;
    }

    /**
     * The Access Point that linked us, for the Jade / The One Probe tooltips — i.e. the point the network reaches
     * this station from, which is also where the card sits.
     * <p>
     * This replaces RS1's {@code INetwork.getPosition()}, which reported the controller and is gone: an RS2 network
     * is an unanchored graph. Reporting a controller instead would be a poor substitute even where one can be
     * found — RS2 treats controllers as an energy pool you add to, so a network routinely has several and which one
     * you got would be arbitrary and liable to change. The Access Point is the thing actually bound to this station.
     * <p>
     * Nothing stops several Access Points pointing at one station, so this reports the first; unlike the controller
     * case that is a genuinely odd setup rather than the norm.
     */
    public Optional<BlockPos> getLinkedAccessPointPos() {
        Network network = getNetwork();
        if (network == null || level == null) {
            return Optional.empty();
        }
        GlobalPos self = GlobalPos.of(level.dimension(), getBlockPos());
        return network.getComponent(GraphNetworkComponent.class).getContainers().stream()
                .filter(container -> container.getNode() instanceof KitchenAccessPointNetworkNode accessPoint
                        && self.equals(accessPoint.getStationPos()))
                .filter(InWorldNetworkNodeContainer.class::isInstance)
                .map(container -> ((InWorldNetworkNodeContainer) container).getPosition().pos())
                .findFirst();
    }

    @Override
    public Component getName() {
        return overrideName(Component.translatable("block.refinedcooking.kitchen_station"));
    }
}
