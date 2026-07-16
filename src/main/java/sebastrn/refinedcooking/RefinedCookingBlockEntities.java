package sebastrn.refinedcooking;

import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import sebastrn.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
import sebastrn.refinedcooking.blockentity.KitchenStationBlockEntity;

public final class RefinedCookingBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, RefinedCooking.ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KitchenAccessPointBlockEntity>> KITCHEN_ACCESS_POINT =
            REGISTRY.register("kitchen_access_point", () -> registerSynchronizationParameters(KitchenAccessPointBlockEntity.SPEC, BlockEntityType.Builder.of(KitchenAccessPointBlockEntity::new, RefinedCookingBlocks.KITCHEN_ACCESS_POINT.get()).build(null)));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KitchenStationBlockEntity>> KITCHEN_STATION =
            REGISTRY.register("kitchen_station", () -> registerSynchronizationParameters(KitchenStationBlockEntity.SPEC, BlockEntityType.Builder.of(KitchenStationBlockEntity::new, RefinedCookingBlocks.KITCHEN_STATION.get()).build(null)));

    private static <T extends BlockEntity> BlockEntityType<T> registerSynchronizationParameters(BlockEntitySynchronizationSpec spec, BlockEntityType<T> t) {
        spec.getParameters().forEach(BlockEntitySynchronizationManager::registerParameter);
        return t;
    }

    private RefinedCookingBlockEntities() {
    }

    public static void register(IEventBus modEventBus) {
        REGISTRY.register(modEventBus);
    }
}
