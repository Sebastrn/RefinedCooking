package dev.smolinacadena.refinedcooking.container;

import com.refinedmods.refinedstorage.container.BaseContainerMenu;
import dev.smolinacadena.refinedcooking.RefinedCookingContainerMenus;
import dev.smolinacadena.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.SlotItemHandler;

public class KitchenAccessPointContainerMenu extends BaseContainerMenu {
    public KitchenAccessPointContainerMenu(KitchenAccessPointBlockEntity kitchenAccessPoint, Player player, int windowId) {
        super(RefinedCookingContainerMenus.KITCHEN_ACCESS_POINT, kitchenAccessPoint, player, windowId);

        addSlot(new SlotItemHandler(kitchenAccessPoint.getNode().getNetworkCard(), 0, 8, 20));

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(player.getInventory(), kitchenAccessPoint.getNode().getNetworkCard());
    }
}
