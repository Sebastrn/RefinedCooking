package sebastrn.refinedcooking.blockentity;

import com.google.common.util.concurrent.RateLimiter;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.node.GraphNetworkComponent;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.network.ConnectionSink;
import com.refinedmods.refinedstorage.common.api.support.network.InWorldNetworkNodeContainer;
import com.refinedmods.refinedstorage.common.support.BlockEntityWithDrops;
import com.refinedmods.refinedstorage.common.support.containermenu.NetworkNodeExtendedMenuProvider;
import com.refinedmods.refinedstorage.common.support.network.AbstractBaseNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.support.network.SimpleConnectionStrategy;
import com.refinedmods.refinedstorage.common.util.ContainerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import sebastrn.refinedcooking.RefinedCooking;
import sebastrn.refinedcooking.RefinedCookingBlockEntities;
import sebastrn.refinedcooking.block.KitchenAccessPointBlock;
import sebastrn.refinedcooking.block.KitchenStationBlock;
import sebastrn.refinedcooking.container.KitchenAccessPointContainerMenu;
import sebastrn.refinedcooking.inventory.KitchenNetworkCardInventory;
import sebastrn.refinedcooking.network.KitchenAccessPointNetworkNode;
import sebastrn.refinedcooking.network.KitchenStationKey;

import javax.annotation.Nullable;

/**
 * The near end of the link: a node inside a Refined Storage network that projects it to a remote Kitchen Station.
 * Modelled on RS's own Network Transmitter, which is the same concept — a node with a network-card slot and a GUI
 * that reaches out to a remote receiver.
 * <p>
 * The remote link is made in {@link #createMainContainer}: the connection strategy adds an outgoing connection to
 * the station the card points at, so RS's graph builder walks across to it. Inserting or removing a card rebuilds
 * the graph via the inventory listener.
 * <p>
 * The GUI needs no packet of its own. The card lives in a real menu slot, so its position data component is synced
 * to the client for free; the only other thing the screen needs is our own position, which never changes and so is
 * sent once as the menu's extended data ({@link #getMenuData()}). See {@code KitchenAccessPointScreen}.
 */
public class KitchenAccessPointBlockEntity
        extends AbstractBaseNetworkNodeContainerBlockEntity<KitchenAccessPointNetworkNode>
        implements NetworkNodeExtendedMenuProvider<GlobalPos>, BlockEntityWithDrops {

    private static final String TAG_NETWORK_CARD_INVENTORY = "nc";

    private final KitchenNetworkCardInventory networkCardInventory = new KitchenNetworkCardInventory();
    private final RateLimiter stateChangeRateLimiter = RateLimiter.create(1);
    private final RateLimiter networkRebuildRetryRateLimiter = RateLimiter.create(1 / 5D);

    public KitchenAccessPointBlockEntity(BlockPos pos, BlockState state) {
        super(RefinedCookingBlockEntities.KITCHEN_ACCESS_POINT.get(), pos, state,
                new KitchenAccessPointNetworkNode(RefinedCooking.SERVER_CONFIG.getKitchenAccessPoint().getUsage()));
        networkCardInventory.addListener(container -> {
            updateStationLocation();
            if (level != null) {
                // The card decides where we connect, so the graph has to be rebuilt when it changes.
                setChanged();
                containers.update(level);
            }
        });
    }

    @Override
    protected InWorldNetworkNodeContainer createMainContainer(KitchenAccessPointNetworkNode networkNode) {
        return RefinedStorageApi.INSTANCE.createNetworkNodeContainer(this, networkNode)
                // All six sides, as every RS1 node did (see KitchenStationBlockEntity), plus the remote link.
                .connectionStrategy(new SimpleConnectionStrategy(getBlockPos()) {
                    @Override
                    public void addOutgoingConnections(ConnectionSink sink) {
                        super.addOutgoingConnections(sink);
                        // Connect the bound station whenever a card names it, powered or not. This is what lets the
                        // station tell "linked but the network is offline" (LINKED_OFFLINE / red screen) from "not
                        // linked at all" (UNLINKED / dark): gating on isActive() dropped the station off the graph the
                        // instant the network lost power, so it went straight to dark and the offline state was
                        // unreachable through the normal Access-Point link. The station still only serves items and
                        // lights up green while its own node is active (see KitchenStationBlockEntity#calculateActive),
                        // so an unpowered link stays dark-for-cooking, just visibly red instead of vanishing.
                        GlobalPos stationPos = mainNetworkNode.getStationPos();
                        if (stationPos != null) {
                            sink.tryConnect(stationPos, KitchenStationBlock.class);
                        }
                    }
                })
                .build();
    }

    /**
     * Drives both blockstate properties from one place. RS's transmitter does the same for its state enum; going
     * through the ticker (rather than writing the blockstate the moment a card changes) keeps the write rate-limited
     * and off the network-graph callback. The ticker is registered without an activeness property for this reason —
     * otherwise it and this method would both write {@code connected} and fight.
     */
    public void updateStateInLevel(BlockState state) {
        boolean newConnected = mainNetworkNode.isActive();
        boolean newHasCard = hasCard();
        boolean changed = state.getValue(KitchenAccessPointBlock.CONNECTED) != newConnected
                || state.getValue(KitchenAccessPointBlock.HAS_CARD) != newHasCard;
        if (changed && level != null && stateChangeRateLimiter.tryAcquire()) {
            level.setBlockAndUpdate(worldPosition, state
                    .setValue(KitchenAccessPointBlock.CONNECTED, newConnected)
                    .setValue(KitchenAccessPointBlock.HAS_CARD, newHasCard));
        }
    }

    /**
     * Retries the link when the station is bound but absent from the graph, most often because its chunk had not
     * loaded when the graph was last built. Runs regardless of power now that the link is maintained whether or not
     * the network is active, so a station whose chunk loads under an unpowered Access Point still links (as red).
     * Rate-limited to once every five seconds, as RS's transmitter does.
     */
    @Override
    public void doWork() {
        super.doWork();
        Network network = mainNetworkNode.getNetwork();
        GlobalPos stationPos = mainNetworkNode.getStationPos();
        if (network == null || stationPos == null) {
            return;
        }
        if (!isStationInNetwork(network, stationPos) && networkRebuildRetryRateLimiter.tryAcquire()
                && isStationLoadedInLevel(stationPos)) {
            containers.update(level);
        }
    }

    private boolean isStationLoadedInLevel(GlobalPos stationPos) {
        if (level == null) {
            return false;
        }
        MinecraftServer server = level.getServer();
        if (server == null) {
            return false;
        }
        Level stationLevel = server.getLevel(stationPos.dimension());
        if (stationLevel == null || !stationLevel.isLoaded(stationPos.pos())) {
            return false;
        }
        return stationLevel.getBlockState(stationPos.pos()).getBlock() instanceof KitchenStationBlock;
    }

    private static boolean isStationInNetwork(Network network, GlobalPos pos) {
        return network.getComponent(GraphNetworkComponent.class).getContainer(new KitchenStationKey(pos)) != null;
    }

    private void updateStationLocation() {
        mainNetworkNode.setStationPos(networkCardInventory.getStationLocation().orElse(null));
    }

    // ---- state, as the Jade / The One Probe tooltips report it ----

    /** True when the node is on a network, powered and redstone-enabled — i.e. when the block reads as lit. */
    public boolean isConnected() {
        return mainNetworkNode.isActive();
    }

    /** True when a bound card is inserted, whether or not the station it names is reachable. */
    public boolean hasCard() {
        return !networkCardInventory.getNetworkCard().isEmpty();
    }

    /** Distance to the bound station, or -1 when there is no card, or it is in another dimension. */
    public int getDistance() {
        GlobalPos stationPos = mainNetworkNode.getStationPos();
        if (stationPos == null || level == null || !level.dimension().equals(stationPos.dimension())) {
            return -1;
        }
        return (int) Math.sqrt(worldPosition.distSqr(stationPos.pos()));
    }

    /**
     * What the GUI shows, following the same order RS's Network Transmitter uses. Server-side only: telling
     * {@code UNREACHABLE} from {@code TRANSMITTING} needs the network graph, which is why this is synced to the
     * client as a menu property rather than derived there.
     */
    public KitchenAccessPointStatus getStatus() {
        Network network = mainNetworkNode.getNetwork();
        if (!mainNetworkNode.isActive() || network == null) {
            return KitchenAccessPointStatus.INACTIVE;
        }
        GlobalPos stationPos = mainNetworkNode.getStationPos();
        if (stationPos == null) {
            return hasCard() ? KitchenAccessPointStatus.UNBOUND_CARD : KitchenAccessPointStatus.MISSING_CARD;
        }
        return isStationInNetwork(network, stationPos)
                ? KitchenAccessPointStatus.TRANSMITTING
                : KitchenAccessPointStatus.UNREACHABLE;
    }

    /** True when a bound card names a station that is actually present in our network. */
    public boolean isTransmitting() {
        return getStatus() == KitchenAccessPointStatus.TRANSMITTING;
    }

    public ItemStack getNetworkCard() {
        return networkCardInventory.getNetworkCard();
    }

    public Container getNetworkCardInventory() {
        return networkCardInventory;
    }

    // ---- persistence ----

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put(TAG_NETWORK_CARD_INVENTORY, ContainerUtil.write(networkCardInventory, provider));
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains(TAG_NETWORK_CARD_INVENTORY)) {
            ContainerUtil.read(tag.getCompound(TAG_NETWORK_CARD_INVENTORY), networkCardInventory, provider);
        }
        updateStationLocation();
    }

    // ---- menu ----

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
        return new KitchenAccessPointContainerMenu(syncId, inventory, this);
    }

    /** Our own position: all the screen needs beyond the card itself, and it never changes. */
    @Override
    public GlobalPos getMenuData() {
        return GlobalPos.of(level != null ? level.dimension() : Level.OVERWORLD, worldPosition);
    }

    @Override
    public StreamEncoder<RegistryFriendlyByteBuf, GlobalPos> getMenuCodec() {
        return GlobalPos.STREAM_CODEC::encode;
    }

    @Override
    public Component getName() {
        return overrideName(Component.translatable("gui.refinedcooking.kitchen_access_point"));
    }

    @Override
    public NonNullList<ItemStack> getDrops() {
        return NonNullList.of(ItemStack.EMPTY, networkCardInventory.getNetworkCard());
    }
}
