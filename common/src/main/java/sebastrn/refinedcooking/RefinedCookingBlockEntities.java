package sebastrn.refinedcooking;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.block.BalmBlockEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import sebastrn.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
import sebastrn.refinedcooking.blockentity.KitchenStationBlockEntity;

public final class RefinedCookingBlockEntities {

    public static DeferredObject<BlockEntityType<KitchenAccessPointBlockEntity>> KITCHEN_ACCESS_POINT;
    public static DeferredObject<BlockEntityType<KitchenStationBlockEntity>> KITCHEN_STATION;

    private RefinedCookingBlockEntities() {
    }

    public static void initialize(BalmBlockEntities blockEntities) {
        KITCHEN_ACCESS_POINT = blockEntities.registerBlockEntity(id("kitchen_access_point"),
                KitchenAccessPointBlockEntity::new, RefinedCookingBlocks.KITCHEN_ACCESS_POINT);
        KITCHEN_STATION = blockEntities.registerBlockEntity(id("kitchen_station"),
                KitchenStationBlockEntity::new, RefinedCookingBlocks.KITCHEN_STATION);
    }

    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(RefinedCooking.ID, name);
    }
}
