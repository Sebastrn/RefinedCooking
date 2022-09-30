package dev.smolinacadena.refinedcooking.setup;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import dev.smolinacadena.refinedcooking.network.KitchenAccessPointNetworkNode;
import dev.smolinacadena.refinedcooking.network.KitchenStationNetworkNode;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static dev.smolinacadena.refinedcooking.RefinedCooking.RSAPI;

public final class CommonSetup {

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent e) {
        RSAPI.getNetworkNodeRegistry().add(KitchenStationNetworkNode.ID,(compoundNBT, world, blockPos) -> readAndReturn(compoundNBT, new KitchenStationNetworkNode(world, blockPos)));
        RSAPI.getNetworkNodeRegistry().add(KitchenAccessPointNetworkNode.ID,(compoundNBT, world, blockPos) -> readAndReturn(compoundNBT, new KitchenAccessPointNetworkNode(world, blockPos)));
    }

    private static INetworkNode readAndReturn(CompoundTag tag, NetworkNode node) {
        node.read(tag);
        return node;
    }
}
