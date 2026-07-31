package sebastrn.refinedcooking.network;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.INetworkNodeVisitor;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.validator.ItemValidator;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.util.StackUtils;
import sebastrn.refinedcooking.RefinedCooking;
import sebastrn.refinedcooking.RefinedCookingItems;
import sebastrn.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
import sebastrn.refinedcooking.blockentity.KitchenStationBlockEntity;
import sebastrn.refinedcooking.item.KitchenNetworkCardItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;

public class KitchenAccessPointNetworkNode extends NetworkNode {
    public static final ResourceLocation ID = new ResourceLocation(RefinedCooking.ID, "kitchen_access_point");
    private BlockPos receiver;
    private ResourceKey<Level> receiverDimension;
    private final BaseItemHandler networkCard = new BaseItemHandler(1)
            .addValidator(new ItemValidator(RefinedCookingItems.KITCHEN_NETWORK_CARD.get()))
            .addListener(new NetworkNodeInventoryListener(this))
            .addListener((handler, slot, reading) -> {
                ItemStack card = handler.getStackInSlot(slot);

                if (card.isEmpty()) {
                    receiver = null;
                    receiverDimension = null;
                    if (!reading) {
                        BlockEntity tile = level.getBlockEntity(pos);
                        if (tile instanceof KitchenAccessPointBlockEntity) {
                            ((KitchenAccessPointBlockEntity) tile).setHasCard(false);
                        }
                    }
                } else {
                    receiver = KitchenNetworkCardItem.getReceiver(card);
                    receiverDimension = KitchenNetworkCardItem.getDimension(card);
                    if (!reading) {
                        BlockEntity tile = level.getBlockEntity(pos);
                        if (tile instanceof KitchenAccessPointBlockEntity) {
                            ((KitchenAccessPointBlockEntity) tile).setHasCard(true);
                        }
                    }
                }

                if (network != null) {
                    network.getNodeGraph().invalidate(Action.PERFORM, network.getLevel(), network.getPosition());
                }
            });


    public KitchenAccessPointNetworkNode(Level level, BlockPos pos) {
        super(level, pos);
    }

    /**
     * Keep the block's {@code connected} state in sync with whether the node is actually active. RS only writes
     * that blockstate from {@link #update()}, which stops running once the node leaves the network, so on
     * disconnect the antennas would stay lit. This hook fires on every connectivity change (connect, disconnect,
     * power/redstone flip), so we re-derive the real active state from {@link #canUpdate()} and darken the block
     * when it drops off the network.
     */
    @Override
    protected void onConnectedStateChange(INetwork network, boolean state, ConnectivityStateChangeCause cause) {
        super.onConnectedStateChange(network, state, cause);

        boolean active = canUpdate();
        BlockState blockState = level.getBlockState(pos);
        if (blockState.getBlock() instanceof NetworkNodeBlock networkNodeBlock
                && networkNodeBlock.hasConnectedState()
                && blockState.getValue(NetworkNodeBlock.CONNECTED) != active) {
            level.setBlockAndUpdate(pos, blockState.setValue(NetworkNodeBlock.CONNECTED, active));
        }
    }

    @Override
    public CompoundTag write(CompoundTag tag) {
        super.write(tag);

        StackUtils.writeItems(networkCard, 0, tag);

        return tag;
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);

        StackUtils.readItems(networkCard, 0, tag);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public int getEnergyUsage() {
        return RefinedCooking.SERVER_CONFIG.getKitchenAccessPoint().getUsage();
    }

    public BaseItemHandler getNetworkCard() {
        return networkCard;
    }

    /** True when the node is actually active, on a network, powered, and redstone-enabled (mirrors the block's lit state). */
    public boolean isConnected() {
        return canUpdate();
    }

    /** True when a network card is inserted (independent of whether it points at a reachable station). */
    public boolean hasCard() {
        return !networkCard.getStackInSlot(0).isEmpty();
    }

    @Override
    public IItemHandler getDrops() {
        return getNetworkCard();
    }

    @Nullable
    public ResourceKey<Level> getReceiverDimension() {
        return receiverDimension;
    }

    public int getDistance() {
        if (receiver == null || receiverDimension == null || !isSameDimension()) {
            return -1;
        }

        return (int) Math.sqrt(Math.pow(pos.getX() - receiver.getX(), 2) + Math.pow(pos.getY() - receiver.getY(), 2) + Math.pow(pos.getZ() - receiver.getZ(), 2));
    }

    public boolean isSameDimension() {
        return level.dimension() == receiverDimension;
    }

    /**
     * Whether the Access Point relays its bound Station into the network graph. It does so whenever a card names a
     * Station, powered or not: gating this on {@link #canUpdate()} dropped the Station off the graph the instant the
     * network lost power, so its screen went straight to dark (UNLINKED) and the "linked but the network is offline"
     * state (LINKED_OFFLINE / red) was unreachable through an Access-Point link. The Station still only serves items and
     * lights up green while its own node is online (see {@code KitchenStationBlockEntity#getLinkState}), so an unpowered
     * link stays dark-for-cooking, just visibly red instead of vanishing. The Access Point's own lit state still tracks
     * {@link #canUpdate()} (see {@link #isConnected()} and {@link #onConnectedStateChange}).
     */
    private boolean canTransmit() {
        return receiver != null && receiverDimension != null;
    }

    @Override
    public boolean shouldRebuildGraphOnChange() {
        return true;
    }

    @Override
    public void visit(INetworkNodeVisitor.Operator operator) {
        super.visit(operator);

        if (canTransmit()) {
            if (!isSameDimension()) {
                Level dimensionWorld = level.getServer().getLevel(receiverDimension);

                if (dimensionWorld != null && dimensionWorld.getBlockEntity(receiver) instanceof KitchenStationBlockEntity) {
                    operator.apply(dimensionWorld, receiver, null);
                }
            } else {
                if (level.getBlockEntity(receiver) instanceof KitchenStationBlockEntity) {
                    operator.apply(level, receiver, null);
                }
            }
        }
    }
}
