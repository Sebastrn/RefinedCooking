package dev.smolinacadena.refinedcooking.compat.jade;

import dev.smolinacadena.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
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

public class KitchenAccessPointComponentProvider implements IComponentProvider, IServerDataProvider<BlockEntity> {

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getServerData().contains("isConnectedToNetwork") && accessor.getServerData().contains("isTransmitting")) {
            tooltip.add(0, new TranslatableComponent("jade.refinedcooking:online_transmitting"));
        } else if (accessor.getServerData().contains("isConnectedToNetwork") && !accessor.getServerData().contains("isTransmitting")) {
            tooltip.add(0, new TranslatableComponent("jade.refinedcooking:online_no_transmission"));
        } else {
            tooltip.add(0, new TranslatableComponent("jade.refinedcooking:offline"));
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

}