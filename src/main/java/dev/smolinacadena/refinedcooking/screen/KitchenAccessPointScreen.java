package dev.smolinacadena.refinedcooking.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import dev.smolinacadena.refinedcooking.RefinedCooking;
import dev.smolinacadena.refinedcooking.container.KitchenAccessPointContainer;
import dev.smolinacadena.refinedcooking.tile.KitchenAccessPointTile;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.Optional;

public class KitchenAccessPointScreen extends BaseScreen<KitchenAccessPointContainer> {
    public KitchenAccessPointScreen(KitchenAccessPointContainer container, PlayerInventory inventory, ITextComponent title) {
        super(container, 176, 137, inventory, title);
    }

    @Override
    public void onPostInit(int x, int y) {
        addSideButton(new RedstoneModeSideButton(this, NetworkNodeTile.REDSTONE_MODE));
    }

    @Override
    public void tick(int x, int y) {
        // NO OP
    }

    @Override
    public void renderBackground(MatrixStack matrixStack, int x, int y, int mouseX, int mouseY) {
        bindTexture(RefinedCooking.ID, "gui/kitchen_access_point.png");

        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY) {
        renderString(matrixStack, 7, 7, title.getString());

        String text;

        Optional<ResourceLocation> receiverDim = KitchenAccessPointTile.RECEIVER_DIMENSION.getValue();
        int distance = KitchenAccessPointTile.DISTANCE.getValue();

        if (!receiverDim.isPresent()) {
            text = I18n.get("gui.refinedcooking.kitchen_access_point.missing_card");
        } else if (distance != -1) {
            text = I18n.get("gui.refinedcooking.kitchen_access_point.distance", distance);
        } else {
            text = receiverDim.get().toString();
        }

        renderString(matrixStack, 51, 24, text);
        renderString(matrixStack, 7, 42, I18n.get("container.inventory"));
    }
}
