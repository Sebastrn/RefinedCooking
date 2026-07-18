package sebastrn.refinedcooking.compat.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import sebastrn.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

/**
 * Server-side data half, split from {@link KitchenAccessPointComponentProvider} because Jade 1.21.6+ forbids one class
 * being both a data provider and a component provider. Shares the component provider's UID.
 */
public class KitchenAccessPointServerDataProvider implements IServerDataProvider<BlockAccessor> {

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        KitchenAccessPointBlockEntity kitchenAccessPoint = (KitchenAccessPointBlockEntity) accessor.getBlockEntity();
        // Key "connected" off the node's active state (network + power + redstone) so the tooltip matches the block's
        // lit antennas, and report card / transmission separately to cover all four states.
        data.putBoolean("connected", kitchenAccessPoint.isConnected());
        data.putBoolean("hasCard", kitchenAccessPoint.hasCard());
        data.putBoolean("transmitting", kitchenAccessPoint.isTransmitting());
    }

    @Override
    public Identifier getUid() {
        return KitchenAccessPointComponentProvider.KITCHEN_ACCESS_POINT_UID;
    }
}
