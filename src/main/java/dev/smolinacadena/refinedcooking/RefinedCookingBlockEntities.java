package dev.smolinacadena.refinedcooking;

import dev.smolinacadena.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
import dev.smolinacadena.refinedcooking.blockentity.KitchenStationBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(RefinedCooking.ID)
public class RefinedCookingBlockEntities {

    @ObjectHolder("kitchen_station")
    public static final BlockEntityType<KitchenStationBlockEntity> KITCHEN_STATION = null;

    @ObjectHolder("kitchen_access_point")
    public static final BlockEntityType<KitchenAccessPointBlockEntity> KITCHEN_ACCESS_POINT = null;
}
