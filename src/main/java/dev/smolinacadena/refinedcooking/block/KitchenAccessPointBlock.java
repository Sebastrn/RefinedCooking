package dev.smolinacadena.refinedcooking.block;

import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.container.factory.BlockEntityMenuProvider;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import dev.smolinacadena.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
import dev.smolinacadena.refinedcooking.container.KitchenAccessPointContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class KitchenAccessPointBlock extends NetworkNodeBlock {

    private static final VoxelShape SHAPE = Block.box(3, 0, 4, 13, 6.4, 12);
    public static final BooleanProperty HAS_CARD = BooleanProperty.create("has_card");

    public KitchenAccessPointBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL).sound(SoundType.METAL).strength(2.5f));
        registerDefaultState(getStateDefinition().any().setValue(HAS_CARD, false));
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
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            return NetworkUtils.attemptModify(level, pos, player, () -> NetworkHooks.openGui(
                    (ServerPlayer) player,
                    new BlockEntityMenuProvider<KitchenAccessPointBlockEntity>(
                            new TranslatableComponent("gui.refinedcooking.kitchen_access_point"),
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
