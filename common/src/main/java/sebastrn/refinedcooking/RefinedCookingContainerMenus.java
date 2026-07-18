package sebastrn.refinedcooking;

import net.blay09.mods.balm.world.BalmMenuFactory;
import net.blay09.mods.balm.world.inventory.BalmMenuTypeRegistrar;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import sebastrn.refinedcooking.container.KitchenAccessPointContainerMenu;

public final class RefinedCookingContainerMenus {

    public static Holder<MenuType<KitchenAccessPointContainerMenu>> KITCHEN_ACCESS_POINT;

    private RefinedCookingContainerMenus() {
    }

    public static void initialize(BalmMenuTypeRegistrar menuTypes) {
        // The Access Point menu is opened by RS's MenuOpener with the block's GlobalPos as the extended payload; the
        // factory decodes it with the matching GlobalPos codec. Balm carries the payload for us across both loaders.
        KITCHEN_ACCESS_POINT = menuTypes.register("kitchen_access_point",
                new BalmMenuFactory<KitchenAccessPointContainerMenu, GlobalPos>() {
                    @Override
                    public KitchenAccessPointContainerMenu create(int syncId, Inventory inventory, GlobalPos pos) {
                        return new KitchenAccessPointContainerMenu(syncId, inventory, pos);
                    }

                    @Override
                    public StreamCodec<RegistryFriendlyByteBuf, GlobalPos> getStreamCodec() {
                        return GlobalPos.STREAM_CODEC.cast();
                    }
                }).asHolder();
    }
}
