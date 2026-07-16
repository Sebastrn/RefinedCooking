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

    public void setConnected(boolean connected) {
        BlockState state = level.getBlockState(worldPosition);
        level.setBlockAndUpdate(worldPosition, state.setValue(KitchenStationBlock.CONNECTED, connected));
        setChanged();
    }
}
