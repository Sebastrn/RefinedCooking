package sebastrn.refinedcooking;

import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import sebastrn.refinedcooking.container.KitchenAccessPointContainerMenu;

public final class RefinedCookingContainerMenus {

    public static final DeferredRegister<MenuType<?>> REGISTRY =
            DeferredRegister.create(Registries.MENU, RefinedCooking.ID);

    /**
     * Reads back the extended data RS's {@code MenuOpener} writes for an {@code ExtendedMenuProvider} — it encodes
     * with the provider's own codec, so this must decode with the matching one. See
     * {@code KitchenAccessPointBlockEntity#getMenuCodec}.
     */
    public static final DeferredHolder<MenuType<?>, MenuType<KitchenAccessPointContainerMenu>> KITCHEN_ACCESS_POINT =
            REGISTRY.register("kitchen_access_point", () -> IMenuTypeExtension.create(
                    (windowId, inventory, buf) -> new KitchenAccessPointContainerMenu(
                            windowId, inventory, GlobalPos.STREAM_CODEC.decode(buf))));

    private RefinedCookingContainerMenus() {
    }

    public static void register(IEventBus modEventBus) {
        REGISTRY.register(modEventBus);
    }
}
