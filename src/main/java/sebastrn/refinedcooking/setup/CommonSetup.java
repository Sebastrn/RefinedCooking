package sebastrn.refinedcooking.setup;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import sebastrn.refinedcooking.network.KitchenAccessPointNetworkNode;
import sebastrn.refinedcooking.network.KitchenStationNetworkNode;

import static sebastrn.refinedcooking.RefinedCooking.RSAPI;

public final class CommonSetup {

    private CommonSetup() {
    }

    public static void onCommonSetup(FMLCommonSetupEvent e) {
        e.enqueueWork(() -> {
            RSAPI.getNetworkNodeRegistry().add(KitchenStationNetworkNode.ID, (compoundNBT, world, blockPos) -> readAndReturn(compoundNBT, new KitchenStationNetworkNode(world, blockPos)));
            RSAPI.getNetworkNodeRegistry().add(KitchenAccessPointNetworkNode.ID, (compoundNBT, world, blockPos) -> readAndReturn(compoundNBT, new KitchenAccessPointNetworkNode(world, blockPos)));
        });
    }

    private static INetworkNode readAndReturn(CompoundTag tag, NetworkNode node) {
        node.read(tag);
        return node;
    }
}
