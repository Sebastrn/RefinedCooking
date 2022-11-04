package dev.smolinacadena.refinedcooking.compat.jade;

import dev.smolinacadena.refinedcooking.blockentity.KitchenStationBlockEntity;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IServerDataProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class KitchenStationComponentProvider implements IComponentProvider, IServerDataProvider<BlockEntity> {

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getServerData().contains("isConnectedToNetwork")) {
            tooltip.add(new TranslatableComponent("jade.refinedcooking:kitchen_station", accessor.getServerData().getString("RSNetworkPosition")));
        } else {
            tooltip.add(new TranslatableComponent("jade.refinedcooking:offline"));
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
}