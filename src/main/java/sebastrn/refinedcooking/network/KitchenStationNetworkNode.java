package sebastrn.refinedcooking.network;

import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import sebastrn.refinedcooking.RefinedCooking;
import sebastrn.refinedcooking.blockentity.KitchenStationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class KitchenStationNetworkNode extends NetworkNode {

    public static final ResourceLocation ID = new ResourceLocation(RefinedCooking.ID, "kitchen_station");

    public KitchenStationNetworkNode(Level level, BlockPos pos) {
        super(level, pos);
    }

    /**
     * True when the Station can actually run: on a network that is present, powered, and redstone-enabled. RS1's own
     * {@link #isActive()} only covers redstone, so this exposes the protected {@link #canUpdate()} (which also checks
     * the network is present and can run) as the "online" test the block entity's link state is built on.
     */
    public boolean isOnline() {
        return canUpdate();
    }

    /**
     * RS1 ticks every network node here (via its NetworkListener), powered or not, so this is where the Station keeps
     * its blockstate in step with the link state, replacing the old {@code setConnected} calls on graph connect and
     * disconnect. The block entity only writes the blockstate when the state actually changes.
     */
    @Override
    public void update() {
        super.update();

        if (level == null || level.isClientSide) {
            return;
        }

        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof KitchenStationBlockEntity) {
            ((KitchenStationBlockEntity) tile).updateLinkState();
        }
    }

    @Override
    public int getEnergyUsage() {
        return RefinedCooking.SERVER_CONFIG.getKitchenStation().getUsage();
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }
}
