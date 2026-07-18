package sebastrn.refinedcooking;

import net.blay09.mods.balm.world.level.block.BalmBlockRegistrar;
import net.blay09.mods.balm.world.level.block.DeferredBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import sebastrn.refinedcooking.block.KitchenAccessPointBlock;
import sebastrn.refinedcooking.block.KitchenStationBlock;

public final class RefinedCookingBlocks {

    public static DeferredBlock KITCHEN_STATION;
    public static DeferredBlock KITCHEN_ACCESS_POINT;

    private RefinedCookingBlocks() {
    }

    public static void initialize(BalmBlockRegistrar blocks) {
        // 26.1 gives block items the block's translation key only when this is enabled (it becomes the default in
        // 26.2). Matches the single-module registerSimpleBlockItem behaviour the NeoForge build shipped with.
        blocks.enableBlockDescriptionPrefixForItems();

        KITCHEN_STATION = blocks.register("kitchen_station", KitchenStationBlock::new,
                        it -> it.mapColor(MapColor.METAL).sound(SoundType.METAL).strength(2.5f))
                .withDefaultItem(it -> it.stacksTo(1))
                .asDeferredBlock();

        // A plain BlockItem is enough: RS2's AbstractDirectionalBlock faces the Access Point to the player itself in
        // getStateForPlacement (RS1 needed RS's own BaseBlockItem to do that).
        KITCHEN_ACCESS_POINT = blocks.register("kitchen_access_point", KitchenAccessPointBlock::new,
                        it -> it.mapColor(MapColor.METAL).sound(SoundType.METAL).strength(2.5f))
                .withDefaultItem(it -> it.stacksTo(1))
                .asDeferredBlock();
    }
}
