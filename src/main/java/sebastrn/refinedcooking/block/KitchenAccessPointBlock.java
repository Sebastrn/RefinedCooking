package sebastrn.refinedcooking.block;

import com.refinedmods.refinedstorage.block.BlockDirection;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.container.factory.BlockEntityMenuProvider;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import sebastrn.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
import sebastrn.refinedcooking.container.KitchenAccessPointContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class KitchenAccessPointBlock extends NetworkNodeBlock {

    /** The block is 10 wide by 8 deep, so the hitbox swaps axes when it faces east/west. */
    private static final VoxelShape SHAPE_NORTH_SOUTH = Block.box(3, 0, 4, 13, 6.4, 12);
    private static final VoxelShape SHAPE_EAST_WEST = Block.box(4, 0, 3, 12, 6.4, 13);
    public static final BooleanProperty HAS_CARD = BooleanProperty.create("has_card");

    public KitchenAccessPointBlock() {
        super(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).sound(SoundType.METAL).strength(2.5f));
        // Pin CONNECTED false too: any() defaults booleans to true, and NetworkNodeBlock's own default (which does
        // set CONNECTED false) is clobbered by this call, so without this a freshly placed block reads "connected".
        registerDefaultState(getStateDefinition().any()
                .setValue(getDirection().getProperty(), Direction.NORTH)
                .setValue(NetworkNodeBlock.CONNECTED, false)
                .setValue(HAS_CARD, false));
    }

    /**
     * Makes the block rotatable. RS's {@link com.refinedmods.refinedstorage.block.BaseBlock} adds the {@code direction}
     * property (and handles rotation) for anything that isn't {@link BlockDirection#NONE}, and its
     * {@code BaseBlockItem} turns the block to face the player on placement, so the item must be a
     * {@code BaseBlockItem} for placement to set this (see RefinedCookingItems).
     */
    @Override
    public BlockDirection getDirection() {
        return BlockDirection.HORIZONTAL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        builder.add(HAS_CARD);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new KitchenAccessPointBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return state.getValue(getDirection().getProperty()).getAxis() == Direction.Axis.X ? SHAPE_EAST_WEST : SHAPE_NORTH_SOUTH;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            return NetworkUtils.attemptModify(level, pos, player, () -> ((ServerPlayer) player).openMenu(
                    new BlockEntityMenuProvider<KitchenAccessPointBlockEntity>(
                            Component.translatable("gui.refinedcooking.kitchen_access_point"),
                            (blockEntity, windowId, inventory, p) -> new KitchenAccessPointContainerMenu(blockEntity, player, windowId),
                            pos
                    ),
                    pos
            ));
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
