package sebastrn.refinedcooking.screen;

import com.refinedmods.refinedstorage.common.support.AbstractBaseScreen;
import com.refinedmods.refinedstorage.common.support.Sprites;
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
 * keeps the label live as a card is inserted or taken out, with no status packet, where RS1 needed two watched
 * block-entity parameters to do the same job.
 */
public class KitchenAccessPointScreen extends AbstractBaseScreen<KitchenAccessPointContainerMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(RefinedCooking.ID, "textures/gui/kitchen_access_point.png");

    private final TransmittingIcon icon;

    public KitchenAccessPointScreen(KitchenAccessPointContainerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.inventoryLabelY = 42;
        this.imageWidth = 176;
        this.imageHeight = 137;
        this.icon = new TransmittingIcon(isIconActive());
    }

    @Override
    protected void init() {
        super.init();
        addSideButton(new RedstoneModeSideButtonWidget(getMenu().getProperty(PropertyTypes.REDSTONE_MODE)));
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        icon.tick(isIconActive());
    }

    private boolean isIconActive() {
        return !getMenu().getStatus().error() && getMenu().getStatus().transmitting();
    }

    // The transmitting indicator (static dot / animated wave) is drawn in the background layer between the card slot
    // and the status text, the same place and the same way RS's Network Transmitter draws it.
    @Override
    protected void renderBg(GuiGraphics graphics, float delta, int mouseX, int mouseY) {
        super.renderBg(graphics, delta, mouseX, mouseY);
        icon.render(graphics, leftPos + 29, topPos + 22);
    }

    // Warning marker + status text, positioned after the icon exactly as RS's Transmitter does (a warning sprite for
    // the error states, then the message).
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        var status = getMenu().getStatus();
        int x = 25 + 4 + icon.getWidth() + 4;
        if (status.error()) {
            graphics.blitSprite(Sprites.WARNING, x, 23, Sprites.WARNING_SIZE, Sprites.WARNING_SIZE);
        }
        graphics.drawString(font, getStatusText(), x + (status.error() ? (Sprites.WARNING_SIZE + 4) : 0), 25, 4210752, false);
    }

    /**
     * The status itself is synced (only the server can tell "unreachable" from "transmitting"), but the distance and
     * dimension are read straight off the card in the slot, so they stay live as the card is inserted or removed
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
