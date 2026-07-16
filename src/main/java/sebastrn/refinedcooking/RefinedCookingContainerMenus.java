package sebastrn.refinedcooking;

import com.refinedmods.refinedstorage.container.factory.BlockEntityContainerFactory;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import sebastrn.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
import sebastrn.refinedcooking.container.KitchenAccessPointContainerMenu;

public final class RefinedCookingContainerMenus {

    public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(Registries.MENU, RefinedCooking.ID);

    public static final DeferredHolder<MenuType<?>, MenuType<KitchenAccessPointContainerMenu>> KITCHEN_ACCESS_POINT =
            REGISTRY.register("kitchen_access_point", () -> IMenuTypeExtension.create(new BlockEntityContainerFactory<KitchenAccessPointContainerMenu, KitchenAccessPointBlockEntity>((windowId, inv, blockEntity) -> new KitchenAccessPointContainerMenu(blockEntity, inv.player, windowId))));

    private RefinedCookingContainerMenus() {
    }

    public static void register(IEventBus modEventBus) {
        REGISTRY.register(modEventBus);
    }
}
