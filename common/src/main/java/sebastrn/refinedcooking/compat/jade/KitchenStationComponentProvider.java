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
 * {@code IServerDataProvider}, so the server half lives in {@link KitchenStationServerDataProvider}; both share the UID.
 * <p>
 * Three readings, because being on the network and being linked by an Access Point are not the same thing: the Station
 * is a network node itself, so cabling it straight to the network connects it with no card involved. Naming the Access
 * Point is the useful case, but claiming "not connected" for a cabled Station would contradict its own lit screen.
 */
public class KitchenStationComponentProvider implements IBlockComponentProvider {

    public static final Identifier KITCHEN_STATION_UID =
            Identifier.fromNamespaceAndPath(RefinedCooking.ID, "kitchen_station");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        // 26.1's CompoundTag getters return Optional; use the *Or accessors to keep a plain value.
        CompoundTag data = accessor.getServerData();
        String linkState = data.getStringOr("linkState", "unlinked");
        String accessPointPos = data.getStringOr("accessPointPos", "");
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
    public Identifier getUid() {
        return KITCHEN_STATION_UID;
    }
}
