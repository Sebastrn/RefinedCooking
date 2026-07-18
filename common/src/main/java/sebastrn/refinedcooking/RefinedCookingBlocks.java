package sebastrn.refinedcooking;

import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.block.BalmBlocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import sebastrn.refinedcooking.block.KitchenAccessPointBlock;
import sebastrn.refinedcooking.block.KitchenStationBlock;

public final class RefinedCookingBlocks {

    public static DeferredObject<Block> KITCHEN_STATION;
    public static DeferredObject<Block> KITCHEN_ACCESS_POINT;

    private RefinedCookingBlocks() {
    }

    public static void initialize(BalmBlocks blocks) {
        KITCHEN_STATION = blocks.registerBlock(id -> new KitchenStationBlock(
                BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(2.5f)),
                id("kitchen_station"));
        blocks.registerBlockItem(id -> new BlockItem(KITCHEN_STATION.get(), new Item.Properties().stacksTo(1)),
                id("kitchen_station"), RefinedCookingCreativeTab.TAB_ID);

        // A plain BlockItem is enough: RS2's AbstractDirectionalBlock faces the Access Point to the player itself in
        // getStateForPlacement (RS1 needed RS's own BaseBlockItem to do that).
        KITCHEN_ACCESS_POINT = blocks.registerBlock(id -> new KitchenAccessPointBlock(), id("kitchen_access_point"));
        blocks.registerBlockItem(id -> new BlockItem(KITCHEN_ACCESS_POINT.get(), new Item.Properties().stacksTo(1)),
                id("kitchen_access_point"), RefinedCookingCreativeTab.TAB_ID);
    }

    private static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath(RefinedCooking.ID, name);
    }
}
