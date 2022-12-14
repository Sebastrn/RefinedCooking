package dev.smolinacadena.refinedcooking;

import dev.smolinacadena.refinedcooking.block.KitchenAccessPointBlock;
import dev.smolinacadena.refinedcooking.block.KitchenStationBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class RefinedCookingBlocks {

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RefinedCooking.ID);

    public static final RegistryObject<KitchenStationBlock> KITCHEN_STATION;
    public static final RegistryObject<KitchenAccessPointBlock> KITCHEN_ACCESS_POINT;

    static {
        KITCHEN_STATION = BLOCKS.register("kitchen_station", KitchenStationBlock::new);
        KITCHEN_ACCESS_POINT = BLOCKS.register("kitchen_access_point", KitchenAccessPointBlock::new);
    }

    private RefinedCookingBlocks() {
    }

    public static void register() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
