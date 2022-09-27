package dev.smolinacadena.refinedcooking;

import dev.smolinacadena.refinedcooking.container.KitchenAccessPointContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(RefinedCooking.ID)
public class RefinedCookingContainerMenus {

    @ObjectHolder("kitchen_access_point")
    public static final MenuType<KitchenAccessPointContainerMenu> KITCHEN_ACCESS_POINT = null;

    private RefinedCookingContainerMenus() {
    }
}
