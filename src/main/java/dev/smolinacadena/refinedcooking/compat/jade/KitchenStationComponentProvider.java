package dev.smolinacadena.refinedcooking.compat.jade;

import dev.smolinacadena.refinedcooking.RefinedCooking;
import dev.smolinacadena.refinedcooking.blockentity.KitchenStationBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class KitchenStationComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockEntity> {

    public static final ResourceLocation KITCHEN_STATION_UID = new ResourceLocation(RefinedCooking.ID, "kitchen_station");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getServerData().contains("isConnectedToNetwork")) {
            tooltip.add(Component.translatable("jade.refinedcooking:kitchen_station", accessor.getServerData().getString("RSNetworkPosition")));
        } else {
            tooltip.add(Component.translatable("jade.refinedcooking:offline"));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayer player, Level world, BlockEntity blockEntity, boolean showDetails) {
        KitchenStationBlockEntity kitchenStation = (KitchenStationBlockEntity) blockEntity;
        if (kitchenStation.getNode().getNetwork() != null) {
            data.putBoolean("isConnectedToNetwork", true);
            data.putString("RSNetworkPosition", "%d, %d, %d".formatted(
                    kitchenStation.getNode().getNetwork().getPosition().getX(),
                    kitchenStation.getNode().getNetwork().getPosition().getY(),
                    kitchenStation.getNode().getNetwork().getPosition().getZ()));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return KITCHEN_STATION_UID;
    }

}