package dev.smolinacadena.refinedcooking;

import dev.smolinacadena.refinedcooking.tile.KitchenAccessPointTile;
import dev.smolinacadena.refinedcooking.tile.KitchenStationTile;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(RefinedCooking.ID)
public class RefinedCookingTiles {

    @ObjectHolder("kitchen_station")
    public static final TileEntityType<KitchenStationTile> KITCHEN_STATION = null;

    @ObjectHolder("kitchen_access_point")
    public static final TileEntityType<KitchenAccessPointTile> KITCHEN_ACCESS_POINT = null;
}
