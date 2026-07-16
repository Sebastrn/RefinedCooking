package sebastrn.refinedcooking.compat.jade;

import sebastrn.refinedcooking.RefinedCooking;
import sebastrn.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class KitchenAccessPointComponentProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    public static final ResourceLocation KITCHEN_ACCESS_POINT_UID = new ResourceLocation(RefinedCooking.ID, "kitchen_access_point");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag data = accessor.getServerData();
        boolean connected = data.getBoolean("connected");
        boolean hasCard = data.getBoolean("hasCard");
        boolean transmitting = data.getBoolean("transmitting");

        String key;
        if (connected) {
            key = transmitting ? "online_transmitting" : hasCard ? "online_no_transmission" : "online_no_card";
        } else {
            key = hasCard ? "offline_with_card" : "offline";
        }
        tooltip.add(1, Component.translatable("jade.refinedcooking:" + key));
    }

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        KitchenAccessPointBlockEntity kitchenAccessPoint = (KitchenAccessPointBlockEntity) accessor.getBlockEntity();
        // Key "connected" off the node's active state (network + power + redstone) so the tooltip matches the block's
        // lit antennas, and report card / transmission separately to cover all four states.
        data.putBoolean("connected", kitchenAccessPoint.getNode().isConnected());
        data.putBoolean("hasCard", kitchenAccessPoint.getNode().hasCard());
        data.putBoolean("transmitting", kitchenAccessPoint.getNode().getDistance() > -1);
    }

    @Override
    public ResourceLocation getUid() {
        return KITCHEN_ACCESS_POINT_UID;
    }
}
