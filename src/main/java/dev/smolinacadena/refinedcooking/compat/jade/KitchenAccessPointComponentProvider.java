package dev.smolinacadena.refinedcooking.compat.jade;

import dev.smolinacadena.refinedcooking.RefinedCooking;
import dev.smolinacadena.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
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

public class KitchenAccessPointComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockEntity> {

    public static final ResourceLocation KITCHEN_ACCESS_POINT_UID = new ResourceLocation(RefinedCooking.ID, "kitchen_access_point");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getServerData().contains("isConnectedToNetwork") && accessor.getServerData().contains("isTransmitting")) {
            tooltip.add(1, Component.translatable("jade.refinedcooking:online_transmitting"));
        } else if (accessor.getServerData().contains("isConnectedToNetwork") && !accessor.getServerData().contains("isTransmitting")) {
            tooltip.add(1, Component.translatable("jade.refinedcooking:online_no_transmission"));
        } else {
            tooltip.add(1, Component.translatable("jade.refinedcooking:offline"));
        }
    }

    @Override
    public void appendServerData(CompoundTag data, ServerPlayer player, Level world, BlockEntity blockEntity, boolean showDetails) {
        KitchenAccessPointBlockEntity kitchenAccessPoint = (KitchenAccessPointBlockEntity) blockEntity;
        if (kitchenAccessPoint.getNode().getNetwork() != null) {
            data.putBoolean("isConnectedToNetwork", true);
            if (kitchenAccessPoint.getNode().getDistance() > -1) {
                data.putBoolean("isTransmitting", true);
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return KITCHEN_ACCESS_POINT_UID;
    }

}