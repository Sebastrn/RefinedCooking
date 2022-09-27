package dev.smolinacadena.refinedcooking.tile;

import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import dev.smolinacadena.refinedcooking.RefinedCookingBlocks;
import dev.smolinacadena.refinedcooking.RefinedCookingTiles;
import dev.smolinacadena.refinedcooking.api.cookingforblockheads.capability.KitchenItemProvider;
import dev.smolinacadena.refinedcooking.block.KitchenStationBlock;
import dev.smolinacadena.refinedcooking.network.KitchenStationNetworkNode;
import net.blay09.mods.cookingforblockheads.api.capability.CapabilityKitchenItemProvider;
import net.blay09.mods.cookingforblockheads.api.capability.IKitchenItemProvider;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class KitchenStationTile extends NetworkNodeTile<KitchenStationNetworkNode> {
    private final KitchenItemProvider itemProvider = new KitchenItemProvider(this);
    private final LazyOptional<IKitchenItemProvider> itemProviderCap = LazyOptional.of(() -> itemProvider);

    private boolean connected;

    public KitchenStationTile() {
        super(RefinedCookingTiles.KITCHEN_STATION);
    }

    @Override
    @Nonnull
    public KitchenStationNetworkNode createNode(World world, BlockPos pos) {
        return new KitchenStationNetworkNode(world, pos);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        LazyOptional<T> result = CapabilityKitchenItemProvider.CAPABILITY.orEmpty(cap, itemProviderCap);

        if (result.isPresent()) {
            return result;
        } else {
            return super.getCapability(cap, side);
        }
    }

    public void setConnected(boolean connected) {
        this.connected = connected;

        level.blockEvent(worldPosition, RefinedCookingBlocks.KITCHEN_STATION.get(), 0, 0);

        BlockState state = level.getBlockState(worldPosition);
        level.setBlockAndUpdate(worldPosition, state.setValue(KitchenStationBlock.CONNECTED, connected));
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

    public boolean isConnected(){
        return connected;
    }

    @Override
    public void load(BlockState state, CompoundNBT tagCompound) {
        super.load(state, tagCompound);
        connected = tagCompound.getBoolean("Connected");
    }

    @Override
    public CompoundNBT save(CompoundNBT tagCompound) {
        super.save(tagCompound);
        tagCompound.putBoolean("Connected", connected);
        return tagCompound;
    }
}
