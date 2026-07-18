package sebastrn.refinedcooking.compat.jade;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import sebastrn.refinedcooking.RefinedCooking;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

/**
 * Client-side tooltip half. Jade 1.21.6+ forbids one class implementing both {@link IBlockComponentProvider} and
 * {@code IServerDataProvider}, so the server half lives in {@link KitchenAccessPointServerDataProvider}; they share
 * the UID.
 */
public class KitchenAccessPointComponentProvider implements IBlockComponentProvider {

    public static final Identifier KITCHEN_ACCESS_POINT_UID =
            Identifier.fromNamespaceAndPath(RefinedCooking.ID, "kitchen_access_point");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        // 26.1's CompoundTag getters return Optional; use the *Or accessors to keep a plain boolean.
        CompoundTag data = accessor.getServerData();
        boolean connected = data.getBooleanOr("connected", false);
        boolean hasCard = data.getBooleanOr("hasCard", false);
        boolean transmitting = data.getBooleanOr("transmitting", false);

        String key;
        if (connected) {
            key = transmitting ? "online_transmitting" : hasCard ? "online_no_transmission" : "online_no_card";
        } else {
            key = hasCard ? "offline_with_card" : "offline";
        }
        tooltip.add(1, Component.translatable("jade.refinedcooking:" + key));
    }

    @Override
    public Identifier getUid() {
        return KITCHEN_ACCESS_POINT_UID;
    }
}
