package dev.smolinacadena.refinedcooking.setup;

import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.blockentity.BaseBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.container.factory.BlockEntityContainerFactory;
import dev.smolinacadena.refinedcooking.RefinedCooking;
import dev.smolinacadena.refinedcooking.RefinedCookingBlocks;
import dev.smolinacadena.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
import dev.smolinacadena.refinedcooking.blockentity.KitchenStationBlockEntity;
import dev.smolinacadena.refinedcooking.container.KitchenAccessPointContainerMenu;
import dev.smolinacadena.refinedcooking.network.KitchenAccessPointNetworkNode;
import dev.smolinacadena.refinedcooking.network.KitchenStationNetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static dev.smolinacadena.refinedcooking.RefinedCooking.RSAPI;

public final class CommonSetup {

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent e) {
        RSAPI.getNetworkNodeRegistry().add(KitchenStationNetworkNode.ID, (compoundNBT, world, blockPos) -> readAndReturn(compoundNBT, new KitchenStationNetworkNode(world, blockPos)));
        RSAPI.getNetworkNodeRegistry().add(KitchenAccessPointNetworkNode.ID, (compoundNBT, world, blockPos) -> readAndReturn(compoundNBT, new KitchenAccessPointNetworkNode(world, blockPos)));
    }

    private static INetworkNode readAndReturn(CompoundTag tag, NetworkNode node) {
        node.read(tag);
        return node;
    }

    @SubscribeEvent
    public static void onRegisterBlockEntities(RegistryEvent.Register<BlockEntityType<?>> e) {
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of(KitchenStationBlockEntity::new, RefinedCookingBlocks.KITCHEN_STATION.get()).build(null).setRegistryName(RefinedCooking.ID, "kitchen_station")));
        e.getRegistry().register(registerSynchronizationParameters(BlockEntityType.Builder.of(KitchenAccessPointBlockEntity::new, RefinedCookingBlocks.KITCHEN_ACCESS_POINT.get()).build(null).setRegistryName(RefinedCooking.ID, "kitchen_access_point")));
    }

    @SubscribeEvent
    public static void onRegisterContainerMenus(RegistryEvent.Register<MenuType<?>> e) {
        e.getRegistry().register(IForgeMenuType.create(new BlockEntityContainerFactory<KitchenAccessPointContainerMenu, KitchenAccessPointBlockEntity>((windowId, inv, tile) -> new KitchenAccessPointContainerMenu(tile, inv.player, windowId))).setRegistryName(RefinedCooking.ID, "kitchen_access_point"));
    }


    private static <T extends BlockEntity> BlockEntityType<T> registerSynchronizationParameters(BlockEntityType<T> t) {
        BaseBlockEntity blockEntity = (BaseBlockEntity) t.create(BlockPos.ZERO, null);

        blockEntity.getDataManager().getParameters().forEach(BlockEntitySynchronizationManager::registerParameter);

        return t;
    }
}
