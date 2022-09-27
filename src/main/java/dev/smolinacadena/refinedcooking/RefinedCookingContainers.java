package dev.smolinacadena.refinedcooking;

import dev.smolinacadena.refinedcooking.container.KitchenAccessPointContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(RefinedCooking.ID)
public class RefinedCookingContainers {

    @ObjectHolder("kitchen_access_point")
    public static final ContainerType<KitchenAccessPointContainer> KITCHEN_ACCESS_POINT = null;

    private RefinedCookingContainers() {
    }
}
