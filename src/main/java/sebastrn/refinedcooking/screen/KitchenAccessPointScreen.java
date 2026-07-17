package sebastrn.refinedcooking.screen;

import com.refinedmods.refinedstorage.common.support.AbstractBaseScreen;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.widget.RedstoneModeSideButtonWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import sebastrn.refinedcooking.RefinedCooking;
import sebastrn.refinedcooking.container.KitchenAccessPointContainerMenu;
import sebastrn.refinedcooking.item.KitchenNetworkCardItem;

import java.util.Optional;

/**
 * Derives everything it shows from state the client already has: the card in slot 0 (whose bound position rides along
 * as a data component on the synced stack) and the Access Point's own position from the menu's extended data. That
 * keeps the label live as a card is inserted or taken out, with no status packet — where RS1 needed two watched
 * block-entity parameters to do the same job.
 */
public class KitchenAccessPointScreen extends AbstractBaseScreen<KitchenAccessPointContainerMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(RefinedCooking.ID, "textures/gui/kitchen_access_point.png");

    public KitchenAccessPointScreen(KitchenAccessPointContainerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.inventoryLabelY = 42;
        this.imageWidth = 176;
        this.imageHeight = 137;
    }

    @Override
    protected void init() {
        super.init();
        addSideButton(new RedstoneModeSideButtonWidget(getMenu().getProperty(PropertyTypes.REDSTONE_MODE)));
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        graphics.drawString(font, getStatusText(), 51, 24, 4210752, false);
    }

    private String getStatusText() {
        Optional<GlobalPos> station = getBoundStation();
        if (station.isEmpty()) {
            return I18n.get("gui.refinedcooking.kitchen_access_point.missing_card");
        }
        GlobalPos stationPos = station.get();
        GlobalPos self = getMenu().getAccessPointPos();
        if (!self.dimension().equals(stationPos.dimension())) {
            return stationPos.dimension().location().toString();
        }
        int distance = (int) Math.sqrt(self.pos().distSqr(stationPos.pos()));
        return I18n.get("gui.refinedcooking.kitchen_access_point.distance", distance);
    }

    private Optional<GlobalPos> getBoundStation() {
        ItemStack card = getMenu().getNetworkCard();
        if (card.getItem() instanceof KitchenNetworkCardItem cardItem) {
            return cardItem.getLocation(card);
        }
        return Optional.empty();
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}
