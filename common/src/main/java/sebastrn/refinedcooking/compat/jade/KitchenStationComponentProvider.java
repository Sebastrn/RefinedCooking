package sebastrn.refinedcooking.compat.jade;

import sebastrn.refinedcooking.RefinedCooking;
import sebastrn.refinedcooking.blockentity.KitchenStationBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class KitchenStationComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    public static final ResourceLocation KITCHEN_STATION_UID =
            ResourceLocation.fromNamespaceAndPath(RefinedCooking.ID, "kitchen_station");

    /**
     * Three readings, because being on the network and being linked by an Access Point are not the same thing: the
     * Station is a network node itself, so cabling it straight to the network connects it with no card involved.
     * Naming the Access Point is the useful case, but claiming "not connected" for a cabled Station would contradict
     * its own lit screen.
     */
    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        if (data.contains("RSNetworkPosition")) {
            tooltip.add(Component.translatable("jade.refinedcooking:kitchen_station",
                    data.getString("RSNetworkPosition")));
        } else if (data.getBoolean("isConnectedToNetwork")) {
            tooltip.add(Component.translatable("jade.refinedcooking:kitchen_station_connected"));
        } else {
            tooltip.add(Component.translatable("jade.refinedcooking:offline"));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        KitchenStationBlockEntity kitchenStation = (KitchenStationBlockEntity) accessor.getBlockEntity();
        data.putBoolean("isConnectedToNetwork", kitchenStation.isActive());
        kitchenStation.getLinkedAccessPointPos().ifPresent(pos ->
                data.putString("RSNetworkPosition", "%d, %d, %d".formatted(pos.getX(), pos.getY(), pos.getZ())));
    }

    @Override
    public ResourceLocation getUid() {
        return KITCHEN_STATION_UID;
    }

}