package sebastrn.refinedcooking;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.menu.BalmMenuFactory;
import net.blay09.mods.balm.api.menu.BalmMenus;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import sebastrn.refinedcooking.container.KitchenAccessPointContainerMenu;

public final class RefinedCookingContainerMenus {

    public static DeferredObject<MenuType<KitchenAccessPointContainerMenu>> KITCHEN_ACCESS_POINT;

    private RefinedCookingContainerMenus() {
    }

    public static void initialize(BalmMenus menus) {
        // The Access Point menu is opened by RS's MenuOpener with the block's GlobalPos as the extended payload; the
        // factory decodes it with the matching GlobalPos codec. Balm carries the payload for us across both loaders.
        KITCHEN_ACCESS_POINT = menus.registerMenu(id("kitchen_access_point"),
                new BalmMenuFactory<KitchenAccessPointContainerMenu, GlobalPos>() {
                    @Override
                    public KitchenAccessPointContainerMenu create(int syncId, Inventory inventory, GlobalPos pos) {
                        return new KitchenAccessPointContainerMenu(syncId, inventory, pos);
                    }

                    @Override
                    public StreamCodec<RegistryFriendlyByteBuf, GlobalPos> getStreamCodec() {
                        return GlobalPos.STREAM_CODEC.cast();
                    }
                });
    }

    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(RefinedCooking.ID, name);
    }
}
