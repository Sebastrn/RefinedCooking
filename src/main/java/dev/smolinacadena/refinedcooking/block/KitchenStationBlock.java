package dev.smolinacadena.refinedcooking.block;

import com.refinedmods.refinedstorage.util.BlockUtils;
import dev.smolinacadena.refinedcooking.RefinedCookingBlocks;
import dev.smolinacadena.refinedcooking.tile.KitchenStationTile;
import net.blay09.mods.cookingforblockheads.block.BlockKitchen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class KitchenStationBlock extends BlockKitchen {

    private static final VoxelShape SHAPE_NORTH = Block.box(3, -1, 6, 13, 7.5, 13);
    private static final VoxelShape SHAPE_SOUTH = Block.box(3, -1, 3, 13, 7.5, 10);
    private static final VoxelShape SHAPE_EAST = Block.box(3, -1, 3, 10, 7.5, 13);
    private static final VoxelShape SHAPE_WEST = Block.box(6, -1, 3, 13, 7.5, 13);
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    public KitchenStationBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES, RefinedCookingBlocks.KITCHEN_STATION.getId());
        registerDefaultState(getStateDefinition().any().setValue(CONNECTED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING, CONNECTED);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        switch (state.getValue(FACING)) {
            case UP :
            case DOWN :
            case SOUTH :
            default :
                return SHAPE_SOUTH;
            case NORTH :
                return SHAPE_NORTH;
            case WEST :
                return SHAPE_WEST;
            case EAST :
                return SHAPE_EAST;
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new KitchenStationTile();
    }
}
