package sebastrn.refinedcooking.block;

import com.mojang.serialization.MapCodec;
import net.blay09.mods.cookingforblockheads.block.BaseKitchenBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sebastrn.refinedcooking.blockentity.KitchenStationBlockEntity;

import javax.annotation.Nullable;

public class KitchenStationBlock extends BaseKitchenBlock {

    public static final MapCodec<KitchenStationBlock> CODEC = simpleCodec(KitchenStationBlock::new);

    private static final VoxelShape SHAPE_NORTH = Block.box(3, -1, 6, 13, 7.5, 13);
    private static final VoxelShape SHAPE_SOUTH = Block.box(3, -1, 3, 13, 7.5, 10);
    private static final VoxelShape SHAPE_EAST = Block.box(3, -1, 3, 10, 7.5, 13);
    private static final VoxelShape SHAPE_WEST = Block.box(6, -1, 3, 13, 7.5, 13);
    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    public KitchenStationBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(CONNECTED, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        switch (state.getValue(FACING)) {
            case NORTH:
                return SHAPE_NORTH;
            case WEST:
                return SHAPE_WEST;
            case EAST:
                return SHAPE_EAST;
            case SOUTH:
            default:
                return SHAPE_SOUTH;
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new KitchenStationBlockEntity(pos, state);
    }
}
