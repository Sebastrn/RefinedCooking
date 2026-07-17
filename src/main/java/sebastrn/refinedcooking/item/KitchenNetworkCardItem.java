package sebastrn.refinedcooking.item;

import com.refinedmods.refinedstorage.common.content.DataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import sebastrn.refinedcooking.block.KitchenStationBlock;

import java.util.List;
import java.util.Optional;

/**
 * Binds an Access Point to a Kitchen Station: right-click a placed Station to record its position, then insert the
 * card into an Access Point on the network.
 * <p>
 * The position lives in a data component rather than item NBT, which no longer exists for this. We reuse Refined
 * Storage's own {@code networkLocation} component instead of registering one: it is public, typed exactly
 * {@code DataComponentType<GlobalPos>}, and is what RS's own Network Card stores. (Applied Cooking does the same
 * with AE2's {@code WIRELESS_LINK_TARGET}.) Only ever read inside a method, never a static initialiser — the
 * component is supplied by RS at mod init, so touching it earlier would throw.
 */
public class KitchenNetworkCardItem extends Item {

    public KitchenNetworkCardItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Block block = ctx.getLevel().getBlockState(ctx.getClickedPos()).getBlock();
        if (!(block instanceof KitchenStationBlock)) {
            return InteractionResult.PASS;
        }
        if (ctx.getLevel().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ctx.getItemInHand().set(
                DataComponents.INSTANCE.getNetworkLocation(),
                GlobalPos.of(ctx.getLevel().dimension(), ctx.getClickedPos())
        );
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        getLocation(stack).ifPresent(location -> {
            BlockPos pos = location.pos();
            tooltip.add(Component.translatable(
                    "misc.refinedcooking.kitchen_network_card.tooltip",
                    pos.getX(),
                    pos.getY(),
                    pos.getZ(),
                    location.dimension().location().toString()
            ).withStyle(ChatFormatting.GRAY));
        });
    }

    public Optional<GlobalPos> getLocation(ItemStack stack) {
        return Optional.ofNullable(stack.get(DataComponents.INSTANCE.getNetworkLocation()));
    }

    public boolean isBound(ItemStack stack) {
        return stack.has(DataComponents.INSTANCE.getNetworkLocation());
    }
}
