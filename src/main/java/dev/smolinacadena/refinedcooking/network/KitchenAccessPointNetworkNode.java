package dev.smolinacadena.refinedcooking.network;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.INetworkNodeVisitor;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.validator.ItemValidator;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.util.StackUtils;
import dev.smolinacadena.refinedcooking.RefinedCooking;
import dev.smolinacadena.refinedcooking.RefinedCookingItems;
import dev.smolinacadena.refinedcooking.item.KitchenNetworkCardItem;
import dev.smolinacadena.refinedcooking.tile.KitchenAccessPointTile;
import dev.smolinacadena.refinedcooking.tile.KitchenStationTile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

public class KitchenAccessPointNetworkNode extends NetworkNode {
    public static final ResourceLocation ID = new ResourceLocation(RefinedCooking.ID, "kitchen_access_point");

    private final BaseItemHandler networkCard = new BaseItemHandler(1)
            .addValidator(new ItemValidator(RefinedCookingItems.KITCHEN_NETWORK_CARD.get()))
            .addListener(new NetworkNodeInventoryListener(this))
            .addListener((handler, slot, reading) -> {
                ItemStack card = handler.getStackInSlot(slot);

                if (card.isEmpty()) {
                    receiver = null;
                    receiverDimension = null;
                    if(!reading) {
                        TileEntity tile = world.getBlockEntity(pos);
                        if (tile instanceof KitchenAccessPointTile) {
                            ((KitchenAccessPointTile) tile).setConnectedToStation(false);
                        }
                    }
                } else {
                    receiver = KitchenNetworkCardItem.getReceiver(card);
                    receiverDimension = KitchenNetworkCardItem.getDimension(card);
                    if(!reading) {
                        TileEntity tile = world.getBlockEntity(pos);
                        if (tile instanceof KitchenAccessPointTile) {
                            ((KitchenAccessPointTile) tile).setConnectedToStation(true);
                        }
                    }
                }

                if (network != null) {
                    network.getNodeGraph().invalidate(Action.PERFORM, network.getWorld(), network.getPosition());
                }
            });

    private BlockPos receiver;
    private RegistryKey<World> receiverDimension;

    public KitchenAccessPointNetworkNode(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public void onConnected(INetwork network) {
        onConnectedStateChange(network, true, ConnectivityStateChangeCause.GRAPH_CHANGE);
        TileEntity tile = world.getBlockEntity(pos);

        if(tile instanceof KitchenAccessPointTile){
            ((KitchenAccessPointTile)tile).setConnectedToStation(!networkCard.isEmpty());
        }
        this.network = network;
    }

    @Override
    public void onDisconnected(INetwork network){
        super.onDisconnected(network);

        TileEntity tile = world.getBlockEntity(pos);

        if(tile instanceof KitchenAccessPointTile){
            ((KitchenAccessPointTile)tile).setConnectedToStation(false);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        StackUtils.writeItems(networkCard, 0, tag);

        return tag;
    }

    @Override
    public void read(CompoundNBT tag) {
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

    @Override
    public IItemHandler getDrops() {
        return getNetworkCard();
    }

    @Nullable
    public RegistryKey<World> getReceiverDimension() {
        return receiverDimension;
    }

    public int getDistance() {
        if (receiver == null || receiverDimension == null || !isSameDimension()) {
            return -1;
        }

        return (int) Math.sqrt(Math.pow(pos.getX() - receiver.getX(), 2) + Math.pow(pos.getY() - receiver.getY(), 2) + Math.pow(pos.getZ() - receiver.getZ(), 2));
    }

    public boolean isSameDimension() {
        return world.dimension() == receiverDimension;
    }

    private boolean canTransmit() {
        return canUpdate() && receiver != null && receiverDimension != null;
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
                World dimensionWorld = world.getServer().getLevel(receiverDimension);

                if (dimensionWorld != null && dimensionWorld.getBlockEntity(receiver) instanceof KitchenStationTile) {
                    operator.apply(dimensionWorld, receiver, null);
                }
            } else {
                if (world.getBlockEntity(receiver) instanceof KitchenStationTile) {
                    operator.apply(world, receiver, null);
                }
            }
        }
    }
}
