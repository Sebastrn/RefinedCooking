package dev.smolinacadena.refinedcooking.container;

import com.refinedmods.refinedstorage.container.BaseContainer;
import dev.smolinacadena.refinedcooking.RefinedCookingContainers;
import dev.smolinacadena.refinedcooking.tile.KitchenAccessPointTile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class KitchenAccessPointContainer extends BaseContainer {
    public KitchenAccessPointContainer(KitchenAccessPointTile kitchenAccessPoint, PlayerEntity player, int windowId) {
        super(RefinedCookingContainers.KITCHEN_ACCESS_POINT, kitchenAccessPoint, player, windowId);

        addSlot(new SlotItemHandler(kitchenAccessPoint.getNode().getNetworkCard(), 0, 8, 20));

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(player.inventory, kitchenAccessPoint.getNode().getNetworkCard());
    }
}
