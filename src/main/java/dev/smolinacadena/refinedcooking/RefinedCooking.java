package dev.smolinacadena.refinedcooking;

import com.refinedmods.refinedstorage.api.IRSAPI;
import com.refinedmods.refinedstorage.api.RSAPIInject;
import dev.smolinacadena.refinedcooking.config.ServerConfig;
import dev.smolinacadena.refinedcooking.item.group.MainItemGroup;
import dev.smolinacadena.refinedcooking.setup.ClientSetup;
import dev.smolinacadena.refinedcooking.setup.CommonSetup;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
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
    public static final ItemGroup MAIN_GROUP = new MainItemGroup(RefinedCooking.ID);
    public static final ServerConfig SERVER_CONFIG = new ServerConfig();

    public RefinedCooking() {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientSetup::new);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG.getSpec());

        CommonSetup commonSetup = new CommonSetup();
        RefinedCookingBlocks.register();
        RefinedCookingItems.register();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(commonSetup::onCommonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(TileEntityType.class, commonSetup::onRegisterTiles);
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ContainerType.class, commonSetup::onRegisterContainers);
    }
}
