package sebastrn.refinedcooking;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import sebastrn.refinedcooking.block.KitchenAccessPointBlock;
import sebastrn.refinedcooking.block.KitchenStationBlock;

public final class RefinedCookingBlocks {

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, RefinedCooking.ID);

    public static final DeferredHolder<Block, KitchenStationBlock> KITCHEN_STATION =
            BLOCKS.register("kitchen_station", () -> new KitchenStationBlock(
                    BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(2.5f)));
    public static final DeferredHolder<Block, KitchenAccessPointBlock> KITCHEN_ACCESS_POINT =
            BLOCKS.register("kitchen_access_point", KitchenAccessPointBlock::new);

    private RefinedCookingBlocks() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
