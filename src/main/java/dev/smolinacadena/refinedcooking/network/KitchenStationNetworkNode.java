package dev.smolinacadena.refinedcooking.network;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import dev.smolinacadena.refinedcooking.RefinedCooking;
import dev.smolinacadena.refinedcooking.blockentity.KitchenStationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class KitchenStationNetworkNode extends NetworkNode {

    public static final ResourceLocation ID = new ResourceLocation(RefinedCooking.ID, "kitchen_station");

    public KitchenStationNetworkNode(Level level, BlockPos pos) {
        super(level, pos);
    }

    @Override
    public void onConnected(INetwork network) {
        onConnectedStateChange(network, true, ConnectivityStateChangeCause.GRAPH_CHANGE);
        BlockEntity tile = level.getBlockEntity(pos);

        if (tile instanceof KitchenStationBlockEntity) {
            ((KitchenStationBlockEntity) tile).setConnected(true);
        }
        this.network = network;
    }

    @Override
    public void onDisconnected(INetwork network) {
        super.onDisconnected(network);

        BlockEntity tile = level.getBlockEntity(pos);

        if (tile instanceof KitchenStationBlockEntity) {
            ((KitchenStationBlockEntity) tile).setConnected(false);
        }
    }

    @Override
    public int getEnergyUsage() {
        return RefinedCooking.SERVER_CONFIG.getKitchenStation().getUsage();
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
