package sebastrn.refinedcooking.compat.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import sebastrn.refinedcooking.blockentity.KitchenStationBlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;

/**
 * Server-side data half, split from {@link KitchenStationComponentProvider} because Jade 1.21.6+ forbids one class
 * being both a data provider and a component provider. Shares the component provider's UID.
 */
public class KitchenStationServerDataProvider implements IServerDataProvider<BlockAccessor> {

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        KitchenStationBlockEntity kitchenStation = (KitchenStationBlockEntity) accessor.getBlockEntity();
        data.putString("linkState", kitchenStation.getLinkState().getSerializedName());
        kitchenStation.getLinkedAccessPointPos().ifPresent(pos ->
                data.putString("accessPointPos", "%d, %d, %d".formatted(pos.getX(), pos.getY(), pos.getZ())));
    }

    @Override
    public Identifier getUid() {
        return KitchenStationComponentProvider.KITCHEN_STATION_UID;
    }
}
