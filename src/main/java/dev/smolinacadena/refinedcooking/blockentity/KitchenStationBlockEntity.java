package dev.smolinacadena.refinedcooking.blockentity;

import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import dev.smolinacadena.refinedcooking.RefinedCookingBlockEntities;
import dev.smolinacadena.refinedcooking.RefinedCookingBlocks;
import dev.smolinacadena.refinedcooking.api.cookingforblockheads.capability.KitchenItemProvider;
import dev.smolinacadena.refinedcooking.block.KitchenStationBlock;
import dev.smolinacadena.refinedcooking.network.KitchenStationNetworkNode;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.forge.provider.ForgeBalmProviders;
import net.blay09.mods.cookingforblockheads.api.capability.IKitchenItemProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class KitchenStationBlockEntity extends NetworkNodeBlockEntity<KitchenStationNetworkNode> {
    private Capability<IKitchenItemProvider> kitchenCapability;
    private final LazyOptional<KitchenItemProvider> itemProvider = LazyOptional.of(() -> new KitchenItemProvider(this));

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
            .addWatchedParameter(REDSTONE_MODE)
            .build();

    public KitchenStationBlockEntity(BlockPos pos, BlockState state) {
        super(RefinedCookingBlockEntities.KITCHEN_STATION.get(), pos, state, SPEC);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @org.jetbrains.annotations.Nullable Direction side) {
        if (kitchenCapability == null) {
            ForgeBalmProviders forgeProviders = (ForgeBalmProviders) Balm.getProviders();
            kitchenCapability = forgeProviders.getCapability(IKitchenItemProvider.class);
        }

        return cap == kitchenCapability ? itemProvider.cast() : super.getCapability(cap, side);
    }

    @Override
    @Nonnull
    public KitchenStationNetworkNode createNode(Level level, BlockPos pos) {
        return new KitchenStationNetworkNode(level, pos);
    }

    public void setConnected(boolean connected) {
        level.blockEvent(worldPosition, RefinedCookingBlocks.KITCHEN_STATION.get(), 0, 0);

        BlockState state = level.getBlockState(worldPosition);
        level.setBlockAndUpdate(worldPosition, state.setValue(KitchenStationBlock.CONNECTED, connected));
        setChanged();
    }
}
