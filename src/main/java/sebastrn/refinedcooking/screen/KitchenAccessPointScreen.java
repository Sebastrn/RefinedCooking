package sebastrn.refinedcooking.screen;

import com.refinedmods.refinedstorage.common.support.AbstractBaseScreen;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.widget.RedstoneModeSideButtonWidget;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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

    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(RefinedCooking.ID, "textures/gui/kitchen_access_point.png");

    public KitchenAccessPointScreen(KitchenAccessPointContainerMenu menu, Inventory inventory, Component title) {
        // 26.1's imageWidth/imageHeight are final ctor params now, not assignable fields.
        super(menu, inventory, title, 176, 137);
        this.inventoryLabelY = 42;
    }

    @Override
    protected void init() {
        super.init();
        addSideButton(new RedstoneModeSideButtonWidget(getMenu().getProperty(PropertyTypes.REDSTONE_MODE)));
    }

    // RS2 3.2.1 routes screen drawing through its own GuiGraphicsExtractor and renamed the label hook to extractLabels;
    // graphics.text replaces drawString.
    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        graphics.text(font, getStatusText(), 51, 24, 4210752, false);
    }

    /**
     * The status itself is synced (only the server can tell "unreachable" from "transmitting"), but the distance and
     * dimension are read straight off the card in the slot — so they stay live as the card is inserted or removed
     * without anything extra crossing the wire.
     */
    private String getStatusText() {
        return switch (getMenu().getStatus()) {
            case INACTIVE -> I18n.get("gui.refinedcooking.kitchen_access_point.inactive");
            case MISSING_CARD -> I18n.get("gui.refinedcooking.kitchen_access_point.missing_card");
            case UNBOUND_CARD -> I18n.get("gui.refinedcooking.kitchen_access_point.unbound_card");
            case UNREACHABLE -> I18n.get("gui.refinedcooking.kitchen_access_point.unreachable");
            case TRANSMITTING -> getTransmittingText();
        };
    }

    private String getTransmittingText() {
        Optional<GlobalPos> station = getBoundStation();
        if (station.isEmpty()) {
            // The card was pulled in the same tick the status arrived; the next sync corrects it.
            return I18n.get("gui.refinedcooking.kitchen_access_point.missing_card");
        }
        GlobalPos stationPos = station.get();
        GlobalPos self = getMenu().getAccessPointPos();
        if (!self.dimension().equals(stationPos.dimension())) {
            return stationPos.dimension().identifier().toString();
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
    protected Identifier getTexture() {
        return TEXTURE;
    }
}
