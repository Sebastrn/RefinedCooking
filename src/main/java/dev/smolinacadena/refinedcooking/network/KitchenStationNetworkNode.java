package dev.smolinacadena.refinedcooking.network;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import dev.smolinacadena.refinedcooking.RefinedCooking;
import dev.smolinacadena.refinedcooking.tile.KitchenStationTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KitchenStationNetworkNode extends NetworkNode {

    public static final ResourceLocation ID = new ResourceLocation(RefinedCooking.ID, "kitchen_station");

    public KitchenStationNetworkNode(World world, BlockPos pos) {
        super(world, pos);
    }

    @Override
    public void onConnected(INetwork network) {
        onConnectedStateChange(network, true, ConnectivityStateChangeCause.GRAPH_CHANGE);
        TileEntity tile = world.getBlockEntity(pos);

        if(tile instanceof KitchenStationTile){
            ((KitchenStationTile)tile).setConnected(true);
        }
        this.network = network;
    }

    @Override
    public void onDisconnected(INetwork network){
        super.onDisconnected(network);

        TileEntity tile = world.getBlockEntity(pos);

        if(tile instanceof KitchenStationTile){
            ((KitchenStationTile)tile).setConnected(false);
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
