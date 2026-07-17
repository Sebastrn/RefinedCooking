package sebastrn.refinedcooking.block;

import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage.common.support.AbstractBlockEntityTicker;
import com.refinedmods.refinedstorage.common.support.AbstractDirectionalBlock;
import com.refinedmods.refinedstorage.common.support.direction.DirectionType;
import com.refinedmods.refinedstorage.common.support.direction.HorizontalDirection;
import com.refinedmods.refinedstorage.common.support.direction.HorizontalDirectionType;
import com.refinedmods.refinedstorage.common.support.network.NetworkNodeBlockEntityTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sebastrn.refinedcooking.RefinedCookingBlockEntities;
import sebastrn.refinedcooking.blockentity.KitchenAccessPointBlockEntity;

import javax.annotation.Nullable;

/**
 * Extends RS's {@link AbstractDirectionalBlock}, which brings the rotatable {@code direction} property — the same
 * property name and north/east/south/west values RS1's {@code BlockDirection.HORIZONTAL} used, so the blockstate JSON
 * and models carry over unchanged — places the block facing the player, and (through {@code AbstractBaseBlock}) opens
 * the block entity's menu on right-click and drops the card on break.
 */
public class KitchenAccessPointBlock extends AbstractDirectionalBlock<HorizontalDirection> implements EntityBlock {

    /** The block is 10 wide by 8 deep, so the hitbox swaps axes when it faces east/west. */
    private static final VoxelShape SHAPE_NORTH_SOUTH = Block.box(3, 0, 4, 13, 6.4, 12);
    private static final VoxelShape SHAPE_EAST_WEST = Block.box(4, 0, 3, 12, 6.4, 13);

    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");
    public static final BooleanProperty HAS_CARD = BooleanProperty.create("has_card");

    /**
     * Registered <em>without</em> an activeness property: {@code KitchenAccessPointBlockEntity#updateStateInLevel}
     * writes {@code connected} together with {@code has_card}, so letting the ticker write it too would have the two
     * fighting over the blockstate.
     */
    private static final AbstractBlockEntityTicker<KitchenAccessPointBlockEntity> TICKER =
            new NetworkNodeBlockEntityTicker<SimpleNetworkNode, KitchenAccessPointBlockEntity>(
                    RefinedCookingBlockEntities.KITCHEN_ACCESS_POINT::get) {
                @Override
                public void tick(Level level, BlockPos pos, BlockState state, KitchenAccessPointBlockEntity be) {
                    super.tick(level, pos, state, be);
                    be.updateStateInLevel(state);
                }
            };

    public KitchenAccessPointBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(2.5f));
    }

    @Override
    protected DirectionType<HorizontalDirection> getDirectionType() {
        return HorizontalDirectionType.INSTANCE;
    }

    /**
     * Faces the block towards whoever placed it, which is what it did on RS1 and what its models are drawn for.
     * <p>
     * RS quietly flipped this convention between versions: RS1's {@code BlockDirection.HORIZONTAL} stored
     * {@code player.getDirection().getOpposite()}, whereas RS2's {@code HorizontalDirectionType} stores the player's
     * facing as-is — while both map {@code direction} to the same model rotations. Inheriting RS2's behaviour would
     * therefore turn this block 180° from where it has always sat, with nothing to show for it in a compile.
     */
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(getDirectionType().getProperty(), toHorizontal(
                ctx.getHorizontalDirection().getOpposite()
        ));
    }

    private static HorizontalDirection toHorizontal(Direction direction) {
        return switch (direction) {
            case EAST -> HorizontalDirection.EAST;
            case SOUTH -> HorizontalDirection.SOUTH;
            case WEST -> HorizontalDirection.WEST;
            default -> HorizontalDirection.NORTH;
        };
    }

    @Override
    protected BlockState getDefaultState() {
        return super.getDefaultState().setValue(CONNECTED, false).setValue(HAS_CARD, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CONNECTED, HAS_CARD);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new KitchenAccessPointBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <O extends BlockEntity> BlockEntityTicker<O> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<O> type) {
        return TICKER.get(level, type);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = getDirectionType().extractDirection(state.getValue(getDirectionType().getProperty()));
        return direction.getAxis() == Direction.Axis.X ? SHAPE_EAST_WEST : SHAPE_NORTH_SOUTH;
    }
}
