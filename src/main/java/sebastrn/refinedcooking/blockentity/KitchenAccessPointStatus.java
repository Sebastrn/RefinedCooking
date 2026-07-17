package sebastrn.refinedcooking.blockentity;

/**
 * What the Access Point's GUI reports, mirroring the states RS's own Network Transmitter shows.
 * <p>
 * Only the server can tell {@link #UNREACHABLE} from {@link #TRANSMITTING} — that needs the network graph — so this
 * is synced to the client as a menu property (an int over a vanilla {@code DataSlot}, the same mechanism the
 * redstone mode already uses). The distance and dimension shown for {@link #TRANSMITTING} are *not* synced: the
 * client reads those off the card in the slot.
 */
public enum KitchenAccessPointStatus {
    /** Off the network, unpowered, or disabled by redstone. */
    INACTIVE,
    /** The slot is empty. */
    MISSING_CARD,
    /**
     * A card is in the slot, but it has not been right-clicked on a station yet. RS avoids needing this state by
     * refusing unbound cards outright; ours accepts them, so it has to say so — reporting "missing card" with a card
     * plainly sitting in the slot reads as a bug.
     */
    UNBOUND_CARD,
    /** Bound to a station that is not in our network — broken, unloaded, or on a different network. */
    UNREACHABLE,
    /** Bound to a station that really is in our network. */
    TRANSMITTING;

    /** True when actively projecting to a station — the state that animates the transmitting icon. */
    public boolean transmitting() {
        return this == TRANSMITTING;
    }

    /** True for the states that warrant the warning marker (a card problem), matching RS's Transmitter. INACTIVE is not
     * an error — it renders as a plain static icon, exactly as RS shows it. */
    public boolean error() {
        return this == MISSING_CARD || this == UNBOUND_CARD || this == UNREACHABLE;
    }

    private static final KitchenAccessPointStatus[] VALUES = values();

    public static int toId(KitchenAccessPointStatus status) {
        return status.ordinal();
    }

    /** Falls back to {@link #INACTIVE} rather than throwing: the id arrives over the network. */
    public static KitchenAccessPointStatus fromId(int id) {
        return id >= 0 && id < VALUES.length ? VALUES[id] : INACTIVE;
    }
}
