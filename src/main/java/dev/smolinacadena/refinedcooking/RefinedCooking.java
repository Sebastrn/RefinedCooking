package dev.smolinacadena.refinedcooking;

import com.refinedmods.refinedstorage.api.IRSAPI;
import com.refinedmods.refinedstorage.api.RSAPIInject;
import dev.smolinacadena.refinedcooking.config.ServerConfig;
import dev.smolinacadena.refinedcooking.item.group.MainCreativeModeTab;
import dev.smolinacadena.refinedcooking.setup.ClientSetup;
import dev.smolinacadena.refinedcooking.setup.CommonSetup;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RefinedCooking.ID)
public final class RefinedCooking {
    @RSAPIInject
    public static IRSAPI RSAPI;

    public static final String ID = "refinedcooking";
    public static final ServerConfig SERVER_CONFIG = new ServerConfig();
    public static final CreativeModeTab CREATIVE_MODE_TAB = new MainCreativeModeTab(RefinedCooking.ID);

    public RefinedCooking() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::onClientSetup);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::onModelBake);
        });

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG.getSpec());

        RefinedCookingBlocks.register();
        RefinedCookingItems.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(CommonSetup::onCommonSetup);
        RefinedCookingContainerMenus.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
        RefinedCookingBlockEntities.REGISTRY.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
