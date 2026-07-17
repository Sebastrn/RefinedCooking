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

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KitchenAccessPointBlockEntity>> KITCHEN_ACCESS_POINT =
            REGISTRY.register("kitchen_access_point", () -> BlockEntityType.Builder
                    .of(KitchenAccessPointBlockEntity::new, RefinedCookingBlocks.KITCHEN_ACCESS_POINT.get())
                    .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<KitchenStationBlockEntity>> KITCHEN_STATION =
            REGISTRY.register("kitchen_station", () -> BlockEntityType.Builder
                    .of(KitchenStationBlockEntity::new, RefinedCookingBlocks.KITCHEN_STATION.get())
                    .build(null));

    private RefinedCookingBlockEntities() {
    }

    public static void register(IEventBus modEventBus) {
        REGISTRY.register(modEventBus);
    }
}
