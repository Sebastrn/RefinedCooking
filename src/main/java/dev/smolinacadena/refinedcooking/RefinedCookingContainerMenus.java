package dev.smolinacadena.refinedcooking;

import com.refinedmods.refinedstorage.container.factory.BlockEntityContainerFactory;
import dev.smolinacadena.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
import dev.smolinacadena.refinedcooking.container.KitchenAccessPointContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RefinedCookingContainerMenus {

    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, RefinedCooking.ID);

    public static final RegistryObject<MenuType<KitchenAccessPointContainerMenu>> KITCHEN_ACCESS_POINT = REGISTRY.register("kitchen_access_point", () -> IForgeMenuType.create(new BlockEntityContainerFactory<KitchenAccessPointContainerMenu, KitchenAccessPointBlockEntity>((windowId, inv, blockEntity) -> new KitchenAccessPointContainerMenu(blockEntity, inv.player, windowId))));

    private RefinedCookingContainerMenus() {
    }
}
