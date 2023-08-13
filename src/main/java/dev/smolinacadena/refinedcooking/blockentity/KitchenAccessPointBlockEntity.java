package dev.smolinacadena.refinedcooking.blockentity;

import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.refinedmods.refinedstorage.blockentity.data.RSSerializers;
import dev.smolinacadena.refinedcooking.RefinedCooking;
import dev.smolinacadena.refinedcooking.RefinedCookingBlockEntities;
import dev.smolinacadena.refinedcooking.RefinedCookingBlocks;
import dev.smolinacadena.refinedcooking.block.KitchenAccessPointBlock;
import dev.smolinacadena.refinedcooking.network.KitchenAccessPointNetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;


public class KitchenAccessPointBlockEntity extends NetworkNodeBlockEntity<KitchenAccessPointNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, KitchenAccessPointBlockEntity> DISTANCE = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RefinedCooking.ID, "distance"), EntityDataSerializers.INT, 0, t -> t.getNode().getDistance());
    public static final BlockEntitySynchronizationParameter<Optional<ResourceLocation>, KitchenAccessPointBlockEntity> RECEIVER_DIMENSION = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RefinedCooking.ID, "receiver_dimension"), RSSerializers.OPTIONAL_RESOURCE_LOCATION_SERIALIZER, Optional.empty(), t -> {
        if (t.getNode().getReceiverDimension() != null) {
            return Optional.of(t.getNode().getReceiverDimension().location());
        }

        return Optional.empty();
    });

    private final LazyOptional<IItemHandler> networkCardCapability = LazyOptional.of(() -> getNode().getNetworkCard());

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
            .addWatchedParameter(REDSTONE_MODE)
            .addWatchedParameter(DISTANCE)
            .addWatchedParameter(RECEIVER_DIMENSION)
            .build();

    public KitchenAccessPointBlockEntity(BlockPos pos, BlockState state) {
        super(RefinedCookingBlockEntities.KITCHEN_ACCESS_POINT.get(), pos, state, SPEC);
    }

    @Override
    @Nonnull
    public KitchenAccessPointNetworkNode createNode(Level level, BlockPos pos) {
        return new KitchenAccessPointNetworkNode(level, pos);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return networkCardCapability.cast();
        }

        return super.getCapability(cap, direction);
    }

    public void setHasCard(boolean hasCard) {
        level.blockEvent(worldPosition, RefinedCookingBlocks.KITCHEN_ACCESS_POINT.get(), 0, 0);

        BlockState state = level.getBlockState(worldPosition);
        level.setBlockAndUpdate(worldPosition, state.setValue(KitchenAccessPointBlock.HAS_CARD, hasCard));

        setChanged();
    }
}
