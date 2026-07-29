package sebastrn.refinedcooking.blockentity;

import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import sebastrn.refinedcooking.RefinedCookingBlockEntities;
import sebastrn.refinedcooking.api.cookingforblockheads.capability.RSKitchenItemProvider;
import sebastrn.refinedcooking.block.KitchenStationBlock;
import sebastrn.refinedcooking.network.KitchenStationNetworkNode;

import javax.annotation.Nonnull;

public class KitchenStationBlockEntity extends NetworkNodeBlockEntity<KitchenStationNetworkNode> {

    /**
     * Supplies the Refined Storage network's items to Cooking for Blockheads. Exposed to CFB through
     * {@code RegisterCapabilitiesEvent} (see {@code RefinedCooking#registerCapabilities}) rather than the old
     * Forge {@code getCapability} override, which no longer exists on NeoForge.
     */
    private final RSKitchenItemProvider itemProvider = new RSKitchenItemProvider(this);

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
            .addWatchedParameter(REDSTONE_MODE)
            .build();

    public KitchenStationBlockEntity(BlockPos pos, BlockState state) {
        super(RefinedCookingBlockEntities.KITCHEN_STATION.get(), pos, state, SPEC, KitchenStationNetworkNode.class);
    }

    public RSKitchenItemProvider getItemProvider() {
        return itemProvider;
    }

    @Override
    @Nonnull
    public KitchenStationNetworkNode createNode(Level level, BlockPos pos) {
        return new KitchenStationNetworkNode(level, pos);
    }

    /**
     * The Station's three-way link state, derived from its Refined Storage node. RS1 has no "isActive means online"
     * shortcut like RS2: the node's own {@code isActive()} covers only redstone, so "online" is the node being able to
     * run (on a network that is present and powered), exposed as {@link KitchenStationNetworkNode#isOnline()}.
     */
    public KitchenStationBlock.LinkState getLinkState() {
        KitchenStationNetworkNode node = getNode();
        if (node.isOnline()) {
            return KitchenStationBlock.LinkState.ONLINE;
        }
        if (node.getNetwork() != null) {
            return KitchenStationBlock.LinkState.LINKED_OFFLINE;
        }
        return KitchenStationBlock.LinkState.UNLINKED;
    }

    /**
     * Write {@link KitchenStationBlock#LINK_STATE} to match {@link #getLinkState()} when it has changed. Called from the
     * node's tick (RS1 ticks nodes itself, so there is no block ticker), so it only writes the blockstate on a real
     * change rather than every tick.
     */
    public void updateLinkState() {
        if (level == null || level.isClientSide) {
            return;
        }
        KitchenStationBlock.LinkState linkState = getLinkState();
        BlockState state = level.getBlockState(worldPosition);
        if (state.getBlock() instanceof KitchenStationBlock
                && state.getValue(KitchenStationBlock.LINK_STATE) != linkState) {
            level.setBlockAndUpdate(worldPosition, state.setValue(KitchenStationBlock.LINK_STATE, linkState));
            setChanged();
        }
    }
}
