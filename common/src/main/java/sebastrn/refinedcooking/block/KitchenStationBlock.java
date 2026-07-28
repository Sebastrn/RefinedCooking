package sebastrn.refinedcooking.block;

import com.mojang.serialization.MapCodec;
import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage.common.support.AbstractBlockEntityTicker;
import com.refinedmods.refinedstorage.common.support.network.NetworkNodeBlockEntityTicker;
import net.blay09.mods.cookingforblockheads.block.BaseKitchenBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sebastrn.refinedcooking.RefinedCookingBlockEntities;
import sebastrn.refinedcooking.blockentity.KitchenStationBlockEntity;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class KitchenStationBlock extends BaseKitchenBlock {

    public static final MapCodec<KitchenStationBlock> CODEC = simpleCodec(KitchenStationBlock::new);

    // Single-box hitbox sized to the model's overall extent. SHAPE_NORTH is the raw model (facing=north, y=0 in the
    // blockstate); the other three are it rotated about the block centre to match the blockstate's y-rotation, so the
    // hitbox and model always turn together. The top (y=12) is the peak of the -40-degree screen panel on 26.1.2; the
    // 1.20.4 / 1.21.1 backports tilt the panel to -45 (their rotation set stops at 45) and peak at 12.5 instead.
    private static final VoxelShape SHAPE_NORTH = Block.box(1, 0, 1.5, 15, 12, 14);
    private static final VoxelShape SHAPE_EAST = Block.box(2, 0, 1, 14.5, 12, 15);
    private static final VoxelShape SHAPE_SOUTH = Block.box(1, 0, 2, 15, 12, 14.5);
    private static final VoxelShape SHAPE_WEST = Block.box(1.5, 0, 1, 14, 12, 15);

    /**
     * The Station's link state, set by the block entity each tick (see {@link KitchenStationBlockEntity#getLinkState()})
     * and read by the blockstate models and the Jade/TOP tooltips. Three values so the screen can show every case
     * distinctly, replacing the old {@code connected} boolean which folded the linked-but-unpowered case into "off":
     * <ul>
     *   <li>{@link LinkState#UNLINKED} - not on any network (alone), dark screen.</li>
     *   <li>{@link LinkState#LINKED_OFFLINE} - on a network but not powered/active, red screen.</li>
     *   <li>{@link LinkState#ONLINE} - active on a live network, green screen with full glow.</li>
     * </ul>
     */
    public enum LinkState implements StringRepresentable {
        UNLINKED("unlinked"),
        LINKED_OFFLINE("linked_offline"),
        ONLINE("online");

        private final String name;

        LinkState(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public static final EnumProperty<LinkState> LINK_STATE = EnumProperty.create("link_state", LinkState.class);

    /**
     * Refined Storage 2 ticks its nodes from the block entity, so this block has to supply a ticker or the node would
     * never go active and the Station would never join a network. We keep RS's {@link NetworkNodeBlockEntityTicker} for
     * the node lifecycle ({@code doWork} + activeness), but pass it a {@code null} activeness property - the old
     * {@code connected} boolean is gone - and instead drive {@link #LINK_STATE} ourselves in
     * {@link KitchenStationBlockEntity#updateLinkState()}, run right after the node has updated on the same tick.
     */
    private static final class LinkStateTicker
            extends NetworkNodeBlockEntityTicker<SimpleNetworkNode, KitchenStationBlockEntity> {
        private LinkStateTicker(Supplier<BlockEntityType<KitchenStationBlockEntity>> allowedTypeSupplier) {
            super(allowedTypeSupplier); // null activeness property: we own the block model state, not RS
        }

        @Override
        public void tick(Level level, BlockPos pos, BlockState state, KitchenStationBlockEntity blockEntity) {
            super.tick(level, pos, state, blockEntity);
            blockEntity.updateLinkState();
        }
    }

    // Lazy: on Fabric, Balm resolves block suppliers during blocks.initialize() (before block entities register), so a
    // `KITCHEN_STATION::value` method-ref would bind a still-null field. Reading it inside the lambda defers to tick
    // time, by when the type is registered.
    private static final AbstractBlockEntityTicker<KitchenStationBlockEntity> TICKER =
            new LinkStateTicker(() -> RefinedCookingBlockEntities.KITCHEN_STATION.value());

    public KitchenStationBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(LINK_STATE, LinkState.UNLINKED));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LINK_STATE);
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

    @Nullable
    @Override
    public <O extends BlockEntity> BlockEntityTicker<O> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<O> type) {
        return TICKER.get(level, type);
    }
}
