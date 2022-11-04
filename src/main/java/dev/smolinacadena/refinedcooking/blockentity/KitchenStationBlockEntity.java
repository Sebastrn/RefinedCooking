package dev.smolinacadena.refinedcooking.blockentity;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.mojang.datafixers.util.Pair;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import dev.smolinacadena.refinedcooking.RefinedCookingBlockEntities;
import dev.smolinacadena.refinedcooking.RefinedCookingBlocks;
import dev.smolinacadena.refinedcooking.api.cookingforblockheads.capability.KitchenItemProvider;
import dev.smolinacadena.refinedcooking.block.KitchenStationBlock;
import dev.smolinacadena.refinedcooking.network.KitchenStationNetworkNode;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.block.BalmBlockEntityContract;
import net.blay09.mods.balm.api.energy.EnergyStorage;
import net.blay09.mods.balm.api.fluid.FluidTank;
import net.blay09.mods.balm.api.provider.BalmProvider;
import net.blay09.mods.balm.api.provider.BalmProviderHolder;
import net.blay09.mods.balm.forge.container.BalmInvWrapper;
import net.blay09.mods.balm.forge.energy.ForgeEnergyStorage;
import net.blay09.mods.balm.forge.fluid.ForgeFluidTank;
import net.blay09.mods.balm.forge.provider.ForgeBalmProviders;
import net.blay09.mods.cookingforblockheads.api.capability.IKitchenItemProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitchenStationBlockEntity extends NetworkNodeBlockEntity<KitchenStationNetworkNode> implements BalmBlockEntityContract {
    private final Map<Capability<?>, LazyOptional<?>> capabilities = new HashMap<>();
    private final Table<Capability<?>, Direction, LazyOptional<?>> sidedCapabilities = HashBasedTable.create();
    private boolean capabilitiesInitialized;
    private final KitchenItemProvider itemProvider = new KitchenItemProvider(this);

    public KitchenStationBlockEntity(BlockPos pos, BlockState state) {
        super(RefinedCookingBlockEntities.KITCHEN_STATION, pos, state);
    }

    private void addCapabilities(BalmProvider<?> provider, Map<Capability<?>, LazyOptional<?>> capabilities) {
        ForgeBalmProviders forgeProviders = (ForgeBalmProviders) Balm.getProviders();
        Capability<?> capability = forgeProviders.getCapability(provider.getProviderClass());
        capabilities.put(capability, LazyOptional.of(provider::getInstance));

        if (provider.getProviderClass() == Container.class) {
            capabilities.put(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, LazyOptional.of(() -> new BalmInvWrapper((Container) provider.getInstance())));
        } else if (provider.getProviderClass() == FluidTank.class) {
            capabilities.put(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, LazyOptional.of(() -> new ForgeFluidTank((FluidTank) provider.getInstance())));
        } else if (provider.getProviderClass() == EnergyStorage.class) {
            capabilities.put(CapabilityEnergy.ENERGY, LazyOptional.of(() -> new ForgeEnergyStorage((EnergyStorage) provider.getInstance())));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProvider(Class<T> clazz) {
        ForgeBalmProviders forgeProviders = (ForgeBalmProviders) Balm.getProviders();
        Capability<?> capability = forgeProviders.getCapability(clazz);
        return (T) getCapability(capability).resolve().orElse(null);
    }

    @Override
    public List<BalmProvider<?>> getProviders() {
        return Lists.newArrayList(new BalmProvider<>(IKitchenItemProvider.class, itemProvider));
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @org.jetbrains.annotations.Nullable Direction side) {
        if (!capabilitiesInitialized) {
            List<BalmProviderHolder> providers = new ArrayList<>();
            buildProviders(providers);

            for (BalmProviderHolder providerHolder : providers) {
                for (BalmProvider<?> provider : providerHolder.getProviders()) {
                    addCapabilities(provider, capabilities);
                }

                for (Pair<Direction, BalmProvider<?>> pair : providerHolder.getSidedProviders()) {
                    Direction direction = pair.getFirst();
                    BalmProvider<?> provider = pair.getSecond();
                    Map<Capability<?>, LazyOptional<?>> sidedCapabilities = this.sidedCapabilities.column(direction);
                    addCapabilities(provider, sidedCapabilities);
                }
            }
            capabilitiesInitialized = true;
        }

        LazyOptional<?> result = null;
        if (side != null) {
            result = sidedCapabilities.get(cap, side);
        }
        if (result == null) {
            result = capabilities.get(cap);
        }

        return result != null ? result.cast() : super.getCapability(cap, side);
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
