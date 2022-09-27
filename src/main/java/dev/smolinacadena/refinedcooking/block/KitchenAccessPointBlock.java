package dev.smolinacadena.refinedcooking.block;

import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import dev.smolinacadena.refinedcooking.container.KitchenAccessPointContainer;
import dev.smolinacadena.refinedcooking.tile.KitchenAccessPointTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class KitchenAccessPointBlock extends NetworkNodeBlock {

    private static final VoxelShape SHAPE = Block.box(3, 0, 4, 13, 6.4, 12);
    public static final BooleanProperty CONNECTED_TO_STATION = BooleanProperty.create("has_card");

    public KitchenAccessPointBlock() {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);
        registerDefaultState(getStateDefinition().any().setValue(CONNECTED_TO_STATION, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        builder.add(CONNECTED_TO_STATION);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new KitchenAccessPointTile();
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            return NetworkUtils.attemptModify(world, pos, player, () -> NetworkHooks.openGui(
                    (ServerPlayerEntity) player,
                    new PositionalTileContainerProvider<KitchenAccessPointTile>(
                            new TranslationTextComponent("gui.refinedcooking.kitchen_access_point"),
                            (tile, windowId, inventory, p) -> new KitchenAccessPointContainer(tile, player, windowId),
                            pos
                    ),
                    pos
            ));
        }

        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }

    @Override
    public boolean triggerEvent(BlockState p_189539_1_, World world, BlockPos pos, int id, int param) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity != null) {
            return tileEntity.triggerEvent(id, param);
        }

        return false;
    }
}
