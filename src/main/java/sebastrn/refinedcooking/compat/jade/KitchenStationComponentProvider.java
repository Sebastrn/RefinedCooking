package sebastrn.refinedcooking.compat.jade;

import com.refinedmods.refinedstorage.api.network.INetwork;
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

    public static final ResourceLocation KITCHEN_STATION_UID = new ResourceLocation(RefinedCooking.ID, "kitchen_station");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        String linkState = data.getString("linkState");
        String networkPos = data.getString("RSNetworkPosition");
        switch (linkState) {
            case "online" -> {
                if (networkPos.isEmpty()) {
                    tooltip.add(Component.translatable("jade.refinedcooking:kitchen_station_connected"));
                } else {
                    tooltip.add(Component.translatable("jade.refinedcooking:kitchen_station", networkPos));
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
        INetwork network = kitchenStation.getNode().getNetwork();
        if (network != null) {
            data.putString("RSNetworkPosition", "%d, %d, %d".formatted(
                    network.getPosition().getX(),
                    network.getPosition().getY(),
                    network.getPosition().getZ()));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return KITCHEN_STATION_UID;
    }

}