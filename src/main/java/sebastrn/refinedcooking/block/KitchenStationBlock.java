package sebastrn.refinedcooking.block;

import com.mojang.serialization.MapCodec;
import net.blay09.mods.cookingforblockheads.block.BaseKitchenBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import sebastrn.refinedcooking.blockentity.KitchenStationBlockEntity;

import javax.annotation.Nullable;

public class KitchenStationBlock extends BaseKitchenBlock {

    public static final MapCodec<KitchenStationBlock> CODEC = simpleCodec(KitchenStationBlock::new);

    // Single-box hitbox sized to the model's overall extent. SHAPE_NORTH is the raw model (facing=north, y=0); the
    // other three are it rotated about the block centre to match the blockstate's y-rotation. The top (y=12.5) is the
    // peak of the -45-degree screen panel used on 1.20.4 / 1.21.1 (the 26.1.2 build tilts it to -40 and peaks at 12.0).
    private static final VoxelShape SHAPE_NORTH = Block.box(1, 0, 1.5, 15, 12.5, 14);
    private static final VoxelShape SHAPE_EAST = Block.box(2, 0, 1, 14.5, 12.5, 15);
    private static final VoxelShape SHAPE_SOUTH = Block.box(1, 0, 2, 15, 12.5, 14.5);
    private static final VoxelShape SHAPE_WEST = Block.box(1.5, 0, 1, 14, 12.5, 15);

    /**
     * The Station's link state, driven by the block entity each tick (see
     * {@link KitchenStationBlockEntity#updateLinkState()}) and read by the blockstate models and the Jade/TOP tooltips.
     * Three values, replacing the old {@code connected} boolean which folded the linked-but-unpowered case into "off":
     * <ul>
     *   <li>{@link LinkState#UNLINKED} - not on any network, dark screen.</li>
     *   <li>{@link LinkState#LINKED_OFFLINE} - on a network but not powered/redstone-enabled, red screen.</li>
     *   <li>{@link LinkState#ONLINE} - active on a live, powered network, green screen with full glow.</li>
     * </ul>
     * Refined Storage 1 ticks its nodes itself (see {@code KitchenStationNetworkNode#update()}), so unlike the RS2
     * builds on 1.21.1 / 26.1.2 there is no block ticker: the node pokes the block entity, which writes this property.
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
}
