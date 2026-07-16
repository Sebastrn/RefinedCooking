package sebastrn.refinedcooking.blockentity;

import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.refinedmods.refinedstorage.blockentity.data.RSSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import sebastrn.refinedcooking.RefinedCooking;
import sebastrn.refinedcooking.RefinedCookingBlockEntities;
import sebastrn.refinedcooking.block.KitchenAccessPointBlock;
import sebastrn.refinedcooking.network.KitchenAccessPointNetworkNode;

import javax.annotation.Nonnull;
import java.util.Optional;

public class KitchenAccessPointBlockEntity extends NetworkNodeBlockEntity<KitchenAccessPointNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, KitchenAccessPointBlockEntity> DISTANCE = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RefinedCooking.ID, "distance"), EntityDataSerializers.INT, 0, t -> t.getNode().getDistance());
    public static final BlockEntitySynchronizationParameter<Optional<ResourceLocation>, KitchenAccessPointBlockEntity> RECEIVER_DIMENSION = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RefinedCooking.ID, "receiver_dimension"), RSSerializers.OPTIONAL_RESOURCE_LOCATION_SERIALIZER, Optional.empty(), t -> {
        if (t.getNode().getReceiverDimension() != null) {
            return Optional.of(t.getNode().getReceiverDimension().location());
        }

        return Optional.empty();
    });

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
            .addWatchedParameter(REDSTONE_MODE)
            .addWatchedParameter(DISTANCE)
            .addWatchedParameter(RECEIVER_DIMENSION)
            .build();

    public KitchenAccessPointBlockEntity(BlockPos pos, BlockState state) {
        super(RefinedCookingBlockEntities.KITCHEN_ACCESS_POINT.get(), pos, state, SPEC, KitchenAccessPointNetworkNode.class);
    }

    @Override
    @Nonnull
    public KitchenAccessPointNetworkNode createNode(Level level, BlockPos pos) {
        return new KitchenAccessPointNetworkNode(level, pos);
    }

    public void setHasCard(boolean hasCard) {
        BlockState state = level.getBlockState(worldPosition);
        level.setBlockAndUpdate(worldPosition, state.setValue(KitchenAccessPointBlock.HAS_CARD, hasCard));

        setChanged();
    }
}
