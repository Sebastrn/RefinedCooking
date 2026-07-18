package sebastrn.refinedcooking;

import net.blay09.mods.balm.world.level.block.entity.BalmBlockEntityTypeRegistrar;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import sebastrn.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
import sebastrn.refinedcooking.blockentity.KitchenStationBlockEntity;

public final class RefinedCookingBlockEntities {

    public static Holder<BlockEntityType<KitchenAccessPointBlockEntity>> KITCHEN_ACCESS_POINT;
    public static Holder<BlockEntityType<KitchenStationBlockEntity>> KITCHEN_STATION;

    private RefinedCookingBlockEntities() {
    }

    public static void initialize(BalmBlockEntityTypeRegistrar blockEntityTypes) {
        KITCHEN_ACCESS_POINT = blockEntityTypes.register("kitchen_access_point",
                KitchenAccessPointBlockEntity::new, RefinedCookingBlocks.KITCHEN_ACCESS_POINT).asHolder();
        KITCHEN_STATION = blockEntityTypes.register("kitchen_station",
                KitchenStationBlockEntity::new, RefinedCookingBlocks.KITCHEN_STATION).asHolder();
    }
}
