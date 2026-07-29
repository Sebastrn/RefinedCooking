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
        String linkState = data.getString("linkState");
        String accessPointPos = data.getString("accessPointPos");
        switch (linkState) {
            case "online" -> {
                // Cabled straight to the network has no Access Point to name; only show the position when linked.
                if (accessPointPos.isEmpty()) {
                    tooltip.add(Component.translatable("jade.refinedcooking:kitchen_station_connected"));
                } else {
                    tooltip.add(Component.translatable("jade.refinedcooking:kitchen_station", accessPointPos));
                }
            }
            case "linked_offline" -> tooltip.add(Component.translatable("jade.refinedcooking:linked_offline"));
            default -> tooltip.add(Component.translatable("jade.refinedcooking:offline"));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        KitchenStationBlockEntity kitchenStation = (KitchenStationBlockEntity) accessor.getBlockEntity();
        data.putString("linkState", kitchenStation.getLinkState().getSerializedName());
        kitchenStation.getLinkedAccessPointPos().ifPresent(pos ->
                data.putString("accessPointPos", "%d, %d, %d".formatted(pos.getX(), pos.getY(), pos.getZ())));
    }

    @Override
    public ResourceLocation getUid() {
        return KITCHEN_STATION_UID;
    }

}