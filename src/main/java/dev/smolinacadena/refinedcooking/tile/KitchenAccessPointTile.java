package dev.smolinacadena.refinedcooking.tile;

import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.data.RSSerializers;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import dev.smolinacadena.refinedcooking.RefinedCookingBlocks;
import dev.smolinacadena.refinedcooking.RefinedCookingTiles;
import dev.smolinacadena.refinedcooking.block.KitchenAccessPointBlock;
import dev.smolinacadena.refinedcooking.network.KitchenAccessPointNetworkNode;
import net.minecraft.block.BlockState;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class KitchenAccessPointTile extends NetworkNodeTile<KitchenAccessPointNetworkNode> {
    public static final TileDataParameter<Integer, KitchenAccessPointTile> DISTANCE = new TileDataParameter<>(DataSerializers.INT, 0, t -> t.getNode().getDistance());
    public static final TileDataParameter<Optional<ResourceLocation>, KitchenAccessPointTile> RECEIVER_DIMENSION = new TileDataParameter<>(RSSerializers.OPTIONAL_RESOURCE_LOCATION_SERIALIZER, Optional.empty(), t -> {
        if (t.getNode().getReceiverDimension() != null) {
            return Optional.of(t.getNode().getReceiverDimension().location());
        }

        return Optional.empty();
    });

    private final LazyOptional<IItemHandler> networkCardCapability = LazyOptional.of(() -> getNode().getNetworkCard());

    public KitchenAccessPointTile() {
        super(RefinedCookingTiles.KITCHEN_ACCESS_POINT);

        dataManager.addWatchedParameter(DISTANCE);
        dataManager.addWatchedParameter(RECEIVER_DIMENSION);
    }

    @Override
    @Nonnull
    public KitchenAccessPointNetworkNode createNode(World world, BlockPos pos) {
        return new KitchenAccessPointNetworkNode(world, pos);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return networkCardCapability.cast();
        }

        return super.getCapability(cap, direction);
    }

    public void setConnectedToStation(boolean connectedToStation) {

        level.blockEvent(worldPosition, RefinedCookingBlocks.KITCHEN_ACCESS_POINT.get(), 0, 0);

        BlockState state = level.getBlockState(worldPosition);
        level.setBlockAndUpdate(worldPosition, state.setValue(KitchenAccessPointBlock.CONNECTED_TO_STATION, connectedToStation));

        setChanged();
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 0) {
            BlockState state = level.getBlockState(worldPosition);
            level.markAndNotifyBlock(worldPosition, level.getChunkAt(worldPosition), state, state, 3, 512);
            return true;
        }
        return super.triggerEvent(id, type);
    }
}
