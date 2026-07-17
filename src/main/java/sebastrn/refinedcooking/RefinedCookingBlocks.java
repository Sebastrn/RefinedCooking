package sebastrn.refinedcooking;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import sebastrn.refinedcooking.block.KitchenAccessPointBlock;
import sebastrn.refinedcooking.block.KitchenStationBlock;

public final class RefinedCookingBlocks {

    // 26.1 requires every block to carry its registry id on the Properties (Properties.setId). The
    // DeferredRegister.Blocks helper's registerBlock sets it; a plain DeferredRegister<Block> leaves it unset and
    // registration fails with "Block id not set".
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(RefinedCooking.ID);

    public static final DeferredBlock<KitchenStationBlock> KITCHEN_STATION =
            BLOCKS.registerBlock("kitchen_station",
                    KitchenStationBlock::new,
                    () -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(2.5f));
    public static final DeferredBlock<KitchenAccessPointBlock> KITCHEN_ACCESS_POINT =
            BLOCKS.registerBlock("kitchen_access_point",
                    KitchenAccessPointBlock::new,
                    () -> BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(2.5f));

    private RefinedCookingBlocks() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
