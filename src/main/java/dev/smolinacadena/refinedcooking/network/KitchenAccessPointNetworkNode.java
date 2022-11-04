package dev.smolinacadena.refinedcooking.network;

import com.refinedmods.refinedstorage.api.network.INetworkNodeVisitor;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.validator.ItemValidator;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.util.StackUtils;
import dev.smolinacadena.refinedcooking.RefinedCooking;
import dev.smolinacadena.refinedcooking.RefinedCookingItems;
import dev.smolinacadena.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
import dev.smolinacadena.refinedcooking.blockentity.KitchenStationBlockEntity;
import dev.smolinacadena.refinedcooking.item.KitchenNetworkCardItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;

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
