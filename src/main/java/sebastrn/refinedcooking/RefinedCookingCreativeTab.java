package sebastrn.refinedcooking;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class RefinedCookingCreativeTab {

    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RefinedCooking.ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_MODE_TAB = CREATIVE_TABS.register(RefinedCooking.ID,
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(RefinedCookingItems.KITCHEN_NETWORK_CARD.get()))
                    .title(Component.translatable("itemGroup." + RefinedCooking.ID))
                    .displayItems((features, output) -> {
                        output.accept(RefinedCookingItems.KITCHEN_STATION.get());
                        output.accept(RefinedCookingItems.KITCHEN_ACCESS_POINT.get());
                        output.accept(RefinedCookingItems.KITCHEN_NETWORK_CARD.get());
                    })
                    .build());

    private RefinedCookingCreativeTab() {
    }

    public static void register(IEventBus modEventBus) {
        CREATIVE_TABS.register(modEventBus);
    }
}
