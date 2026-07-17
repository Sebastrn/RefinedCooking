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

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getServerData().contains("isConnectedToNetwork")) {
            tooltip.add(Component.translatable("jade.refinedcooking:kitchen_station", accessor.getServerData().getString("RSNetworkPosition")));
        } else {
            tooltip.add(Component.translatable("jade.refinedcooking:offline"));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        KitchenStationBlockEntity kitchenStation = (KitchenStationBlockEntity) accessor.getBlockEntity();
        kitchenStation.getLinkedAccessPointPos().ifPresent(pos -> {
            data.putBoolean("isConnectedToNetwork", true);
            data.putString("RSNetworkPosition", "%d, %d, %d".formatted(pos.getX(), pos.getY(), pos.getZ()));
        });
    }

    @Override
    public ResourceLocation getUid() {
        return KITCHEN_STATION_UID;
    }

}