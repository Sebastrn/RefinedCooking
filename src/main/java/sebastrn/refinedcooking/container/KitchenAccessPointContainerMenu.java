package sebastrn.refinedcooking.container;

import com.refinedmods.refinedstorage.common.support.AbstractBaseContainerMenu;
import com.refinedmods.refinedstorage.common.support.RedstoneMode;
import com.refinedmods.refinedstorage.common.support.containermenu.ClientProperty;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.containermenu.ServerProperty;
import com.refinedmods.refinedstorage.common.support.containermenu.ValidatedSlot;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import sebastrn.refinedcooking.RefinedCookingContainerMenus;
import sebastrn.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
import sebastrn.refinedcooking.inventory.KitchenNetworkCardInventory;

import javax.annotation.Nullable;

/**
 * Carries no status of its own. The card sits in a real slot, so its bound position — a data component — is synced to
 * the client with the stack; combined with {@link #getAccessPointPos()}, which arrives once as the menu's extended
 * data, the screen can work out everything it displays without a packet. See {@code KitchenAccessPointScreen}.
 */
public class KitchenAccessPointContainerMenu extends AbstractBaseContainerMenu {

    @Nullable
    private final KitchenAccessPointBlockEntity blockEntity;
    private final GlobalPos accessPointPos;
    private Slot cardSlot;

    /** Server side: bound to the real block entity. */
    public KitchenAccessPointContainerMenu(int syncId, Inventory playerInventory,
                                           KitchenAccessPointBlockEntity blockEntity) {
        super(RefinedCookingContainerMenus.KITCHEN_ACCESS_POINT.get(), syncId);
        registerProperty(new ServerProperty<>(
                PropertyTypes.REDSTONE_MODE,
                blockEntity::getRedstoneMode,
                blockEntity::setRedstoneMode
        ));
        this.blockEntity = blockEntity;
        this.accessPointPos = blockEntity.getMenuData();
        addSlots(playerInventory, blockEntity.getNetworkCardInventory());
    }

    /** Client side: built from the extended data written when the menu was opened. */
    public KitchenAccessPointContainerMenu(int syncId, Inventory playerInventory, GlobalPos accessPointPos) {
        super(RefinedCookingContainerMenus.KITCHEN_ACCESS_POINT.get(), syncId);
        registerProperty(new ClientProperty<>(PropertyTypes.REDSTONE_MODE, RedstoneMode.IGNORE));
        this.blockEntity = null;
        this.accessPointPos = accessPointPos;
        addSlots(playerInventory, new KitchenNetworkCardInventory());
    }

    private void addSlots(Inventory playerInventory, Container networkCardInventory) {
        addPlayerInventory(playerInventory, 8, 55);
        cardSlot = addSlot(new ValidatedSlot(
                networkCardInventory,
                0,
                8,
                20,
                KitchenNetworkCardInventory.IS_CARD
        ));
        transferManager.addBiTransfer(playerInventory, networkCardInventory);
    }

    public GlobalPos getAccessPointPos() {
        return accessPointPos;
    }

    /** The inserted card, on either side — the screen reads its bound position straight off the synced stack. */
    public ItemStack getNetworkCard() {
        return cardSlot.getItem();
    }

    @Override
    public boolean stillValid(Player player) {
        if (blockEntity == null) {
            return true;
        }
        return Container.stillValidBlockEntity(blockEntity, player);
    }
}
