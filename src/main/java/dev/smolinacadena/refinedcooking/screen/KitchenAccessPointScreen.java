package dev.smolinacadena.refinedcooking.screen;

import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import dev.smolinacadena.refinedcooking.RefinedCooking;
import dev.smolinacadena.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
import dev.smolinacadena.refinedcooking.container.KitchenAccessPointContainerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class KitchenAccessPointScreen extends BaseScreen<KitchenAccessPointContainerMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(RefinedCooking.ID, "textures/gui/kitchen_access_point.png");

    public KitchenAccessPointScreen(KitchenAccessPointContainerMenu container, Inventory inventory, Component title) {
        super(container, 176, 137, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeBlockEntity.REDSTONE_MODE));
    }

    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int x, int y, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        renderString(guiGraphics, 7, 7, title.getString());

        String text;

        Optional<ResourceLocation> receiverDim = KitchenAccessPointBlockEntity.RECEIVER_DIMENSION.getValue();
        int distance = KitchenAccessPointBlockEntity.DISTANCE.getValue();

        if (!receiverDim.isPresent()) {
            text = I18n.get("gui.refinedcooking.kitchen_access_point.missing_card");
        } else if (distance != -1) {
            text = I18n.get("gui.refinedcooking.kitchen_access_point.distance", distance);
        } else {
            text = receiverDim.get().toString();
        }

        renderString(guiGraphics, 51, 24, text);
        renderString(guiGraphics, 7, 42, I18n.get("container.inventory"));
    }
}
