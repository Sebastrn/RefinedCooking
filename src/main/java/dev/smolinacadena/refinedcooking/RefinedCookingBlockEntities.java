package dev.smolinacadena.refinedcooking;

import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import dev.smolinacadena.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
import dev.smolinacadena.refinedcooking.blockentity.KitchenStationBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RefinedCookingBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RefinedCooking.ID);

    public static final RegistryObject<BlockEntityType<KitchenAccessPointBlockEntity>> KITCHEN_ACCESS_POINT =
            REGISTRY.register("kitchen_access_point", () -> registerSynchronizationParameters(KitchenAccessPointBlockEntity.SPEC, BlockEntityType.Builder.of(KitchenAccessPointBlockEntity::new, RefinedCookingBlocks.KITCHEN_ACCESS_POINT.get()).build(null)));
    public static final RegistryObject<BlockEntityType<KitchenStationBlockEntity>> KITCHEN_STATION =
            REGISTRY.register("kitchen_station", () -> registerSynchronizationParameters(KitchenStationBlockEntity.SPEC, BlockEntityType.Builder.of(KitchenStationBlockEntity::new, RefinedCookingBlocks.KITCHEN_STATION.get()).build(null)));

    private static <T extends BlockEntity> BlockEntityType<T> registerSynchronizationParameters(BlockEntitySynchronizationSpec spec, BlockEntityType<T> t) {
        spec.getParameters().forEach(BlockEntitySynchronizationManager::registerParameter);
        return t;
    }

    private RefinedCookingBlockEntities() {

    }
}
