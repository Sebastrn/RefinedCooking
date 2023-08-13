package dev.smolinacadena.refinedcooking;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class RefinedCookingCreativeTab {

    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RefinedCooking.ID);

    public static final RegistryObject<CreativeModeTab> CREATIVE_MODE_TAB = CREATIVE_TABS.register(RefinedCooking.ID,
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(RefinedCookingItems.KITCHEN_NETWORK_CARD.get()))
                    .title(Component.translatable("itemGroup." + RefinedCooking.ID))
                    .displayItems((features, output) -> {
                        output.accept(RefinedCookingItems.KITCHEN_STATION.get());
                        output.accept(RefinedCookingItems.KITCHEN_ACCESS_POINT.get());
                        output.accept(RefinedCookingItems.KITCHEN_NETWORK_CARD.get());
                    })
                    .build());

    public static void register() {
        CREATIVE_TABS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
