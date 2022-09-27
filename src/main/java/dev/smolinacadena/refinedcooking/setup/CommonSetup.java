package dev.smolinacadena.refinedcooking.setup;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerFactory;
import com.refinedmods.refinedstorage.tile.BaseTile;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import dev.smolinacadena.refinedcooking.RefinedCooking;
import dev.smolinacadena.refinedcooking.RefinedCookingBlocks;
import dev.smolinacadena.refinedcooking.container.KitchenAccessPointContainer;
import dev.smolinacadena.refinedcooking.network.KitchenStationNetworkNode;
import dev.smolinacadena.refinedcooking.network.KitchenAccessPointNetworkNode;
import dev.smolinacadena.refinedcooking.tile.KitchenAccessPointTile;
import dev.smolinacadena.refinedcooking.tile.KitchenStationTile;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static dev.smolinacadena.refinedcooking.RefinedCooking.RSAPI;

public class CommonSetup {

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent e) {
        RSAPI.getNetworkNodeRegistry().add(KitchenStationNetworkNode.ID,(compoundNBT, world, blockPos) -> readAndReturn(compoundNBT, new KitchenStationNetworkNode(world, blockPos)));
        RSAPI.getNetworkNodeRegistry().add(KitchenAccessPointNetworkNode.ID,(compoundNBT, world, blockPos) -> readAndReturn(compoundNBT, new KitchenAccessPointNetworkNode(world, blockPos)));
    }

    @SubscribeEvent
    public void onRegisterTiles(RegistryEvent.Register<TileEntityType<?>> e) {
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(KitchenStationTile::new, RefinedCookingBlocks.KITCHEN_STATION.get()).build(null).setRegistryName(RefinedCooking.ID, "kitchen_station")));
        e.getRegistry().register(registerTileDataParameters(TileEntityType.Builder.of(KitchenAccessPointTile::new, RefinedCookingBlocks.KITCHEN_ACCESS_POINT.get()).build(null).setRegistryName(RefinedCooking.ID, "kitchen_access_point")));
    }

    @SubscribeEvent
    public void onRegisterContainers(RegistryEvent.Register<ContainerType<?>> e) {
        e.getRegistry().register(IForgeContainerType.create(new PositionalTileContainerFactory<KitchenAccessPointContainer, KitchenAccessPointTile>((windowId, inv, tile) -> new KitchenAccessPointContainer(tile, inv.player, windowId))).setRegistryName(RefinedCooking.ID, "kitchen_access_point"));
    }

    private INetworkNode readAndReturn(CompoundNBT tag, NetworkNode node) {
        node.read(tag);
        return node;
    }

    private <T extends TileEntity> TileEntityType<T> registerTileDataParameters(TileEntityType<T> t) {
        BaseTile tile = (BaseTile) t.create();
        tile.getDataManager().getParameters().forEach(TileDataManager::registerParameter);
        return t;
    }
}
