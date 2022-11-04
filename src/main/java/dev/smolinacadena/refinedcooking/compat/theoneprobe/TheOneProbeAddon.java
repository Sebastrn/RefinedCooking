package dev.smolinacadena.refinedcooking.compat.theoneprobe;

import dev.smolinacadena.refinedcooking.RefinedCooking;
import dev.smolinacadena.refinedcooking.RefinedCookingItems;
import dev.smolinacadena.refinedcooking.block.KitchenAccessPointBlock;
import dev.smolinacadena.refinedcooking.block.KitchenStationBlock;
import dev.smolinacadena.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
import dev.smolinacadena.refinedcooking.blockentity.KitchenStationBlockEntity;
import dev.smolinacadena.refinedcooking.compat.Compat;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.styles.ItemStyle;
import mcjty.theoneprobe.apiimpl.styles.LayoutStyle;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.InterModComms;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class TheOneProbeAddon {

    public static void register() {
        InterModComms.sendTo(Compat.THEONEPROBE, "getTheOneProbe", TopInitializer::new);
    }

    public static class TopInitializer implements Function<ITheOneProbe, Void> {
        @Nullable
        @Override
        public Void apply(@Nullable ITheOneProbe top) {
            if (top != null) {
                top.registerProvider(new ProbeInfoProvider());
            }
            return null;
        }
    }

    public static class ProbeInfoProvider implements IProbeInfoProvider {
        @Override
        public ResourceLocation getID() {
            return new ResourceLocation(RefinedCooking.ID, RefinedCooking.ID);
        }

        @Override
        public void addProbeInfo(ProbeMode mode, IProbeInfo info, Player playerEntity, Level level, BlockState state, IProbeHitData data) {
            if (state.getBlock() instanceof KitchenStationBlock) {
                var kitchenStationBlockEntity = tryGetTileEntity(level, data.getPos(), KitchenStationBlockEntity.class);
                if (kitchenStationBlockEntity != null) {
                    if (kitchenStationBlockEntity.getNode().getNetwork() != null) {
                        info.mcText(new TranslatableComponent("jade.refinedcooking:kitchen_station", "%d, %d, %d".formatted(
                                kitchenStationBlockEntity.getNode().getNetwork().getPosition().getX(),
                                kitchenStationBlockEntity.getNode().getNetwork().getPosition().getY(),
                                kitchenStationBlockEntity.getNode().getNetwork().getPosition().getZ()))
                                .withStyle(ChatFormatting.GRAY));
                    } else {
                        info.mcText(new TranslatableComponent("jade.refinedcooking:offline").withStyle(ChatFormatting.GRAY));
                    }
                }
            } else if (state.getBlock() instanceof KitchenAccessPointBlock) {
                var kitchenAccessPointBlockEntity = tryGetTileEntity(level, data.getPos(), KitchenAccessPointBlockEntity.class);
                if (kitchenAccessPointBlockEntity != null) {
                    if (kitchenAccessPointBlockEntity.getNode().getNetwork() != null && kitchenAccessPointBlockEntity.getNode().getDistance() > -1) {
                        info.mcText(new TranslatableComponent("jade.refinedcooking:online_transmitting").withStyle(ChatFormatting.GRAY));
                    } else if (kitchenAccessPointBlockEntity.getNode().getNetwork() != null && kitchenAccessPointBlockEntity.getNode().getDistance() <= -1) {
                        info.mcText(new TranslatableComponent("jade.refinedcooking:online_no_transmission").withStyle(ChatFormatting.GRAY));
                    } else {
                        info.mcText(new TranslatableComponent("jade.refinedcooking:offline").withStyle(ChatFormatting.GRAY));
                    }

                    var networkCardItem = kitchenAccessPointBlockEntity.getNode().getNetworkCard().getStackInSlot(0);
                    if (networkCardItem.is(RefinedCookingItems.KITCHEN_NETWORK_CARD.get())) {
                        info.horizontal(new LayoutStyle().alignment(ElementAlignment.ALIGN_CENTER))
                                .item(networkCardItem, new ItemStyle().width(16).height(16))
                                .mcText(new TextComponent(networkCardItem.getCount() + "x ").withStyle(ChatFormatting.GRAY))
                                .mcText(new TranslatableComponent("item.refinedcooking.kitchen_network_card").withStyle(ChatFormatting.GRAY));
                    }
                }
            }
        }

        @Nullable
        @SuppressWarnings("unchecked")
        private static <T extends BlockEntity> T tryGetTileEntity(Level level, BlockPos pos, Class<T> tileClass) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity != null && tileClass.isAssignableFrom(blockEntity.getClass())) {
                return (T) blockEntity;
            }
            return null;
        }
    }
}
