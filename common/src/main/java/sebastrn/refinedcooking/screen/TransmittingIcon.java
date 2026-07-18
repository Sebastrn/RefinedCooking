package sebastrn.refinedcooking.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED;

/**
 * The animated "transmitting" indicator between the card slot and the status text, mirroring RS's own Network
 * Transmitter: a static dot when not transmitting, an expanding wave animation when it is. RS's {@code TransmittingIcon}
 * is package-private, so this is a faithful copy — but it reuses RS's own GUI sprites ({@code refinedstorage:transmitting/*}),
 * which are always present because Refined Storage is a required dependency, so no textures of our own are needed.
 */
class TransmittingIcon {
    private static final int WIDTH_0 = 11;
    private static final int WIDTH_3 = 20;
    private static final int TRANSMITTING_FRAMES = 20;
    private static final Identifier NOT_TRANSMITTING = rs("transmitting/0");
    private static final Identifier TRANSMITTING_1 = rs("transmitting/1");
    private static final Identifier TRANSMITTING_2 = rs("transmitting/2");
    private static final Identifier TRANSMITTING_3 = rs("transmitting/3");

    private int frames;
    private int cycle;
    private boolean active;

    TransmittingIcon(final boolean active) {
        this.active = active;
    }

    private static Identifier rs(final String path) {
        return Identifier.fromNamespaceAndPath("refinedstorage", path);
    }

    void tick(final boolean newActive) {
        this.active = newActive;
        doTick();
    }

    private void doTick() {
        if (!active) {
            frames = 0;
            cycle = 0;
            return;
        }
        ++frames;
        if (frames == TRANSMITTING_FRAMES) {
            frames = 0;
            cycle++;
        }
    }

    void render(final GuiGraphicsExtractor graphics, final int x3, final int y3) {
        if (!active) {
            graphics.blitSprite(GUI_TEXTURED, NOT_TRANSMITTING, x3, y3 + 4, WIDTH_0, 4);
            return;
        }
        final int frame = cycle % 3;
        switch (frame) {
            case 1:
                graphics.blitSprite(GUI_TEXTURED, TRANSMITTING_2, x3, y3 + 1, 17, 10);
                break;
            case 2:
                graphics.blitSprite(GUI_TEXTURED, TRANSMITTING_3, x3, y3, WIDTH_3, 12);
                break;
            case 0:
            default:
                graphics.blitSprite(GUI_TEXTURED, TRANSMITTING_1, x3, y3 + 3, 14, 6);
                break;
        }
    }

    int getWidth() {
        return active ? WIDTH_3 : WIDTH_0;
    }
}
