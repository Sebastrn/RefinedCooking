package sebastrn.refinedcooking;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import sebastrn.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
import sebastrn.refinedcooking.blockentity.KitchenStationBlockEntity;

public final class RefinedCookingBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> REGISTRY =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, RefinedCooking.ID);

    // 26.1 removed BlockEntityType.Builder; construct the type directly (factory + valid blocks vararg).
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KitchenAccessPointBlockEntity>> KITCHEN_ACCESS_POINT =
            REGISTRY.register("kitchen_access_point",
                    () -> new BlockEntityType<>(KitchenAccessPointBlockEntity::new, RefinedCookingBlocks.KITCHEN_ACCESS_POINT.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KitchenStationBlockEntity>> KITCHEN_STATION =
            REGISTRY.register("kitchen_station",
                    () -> new BlockEntityType<>(KitchenStationBlockEntity::new, RefinedCookingBlocks.KITCHEN_STATION.get()));

    private RefinedCookingBlockEntities() {
    }

    public static void register(IEventBus modEventBus) {
        REGISTRY.register(modEventBus);
    }
}
