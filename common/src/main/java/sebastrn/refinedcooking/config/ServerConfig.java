package sebastrn.refinedcooking.config;

import net.blay09.mods.balm.Balm;

/**
 * Thin facade over the Balm-backed {@link ServerConfigData}, keeping the nested {@code getKitchenStation().getUsage()}
 * shape the block entities already use so their call sites are unchanged. Replaces the NeoForge {@code ModConfigSpec}
 * the single-module build used, so the values are readable from loader-neutral common code.
 */
public class ServerConfig {

    public void initialize() {
        Balm.config().registerConfig(ServerConfigData.class);
    }

    private static ServerConfigData data() {
        return Balm.config().getActiveConfig(ServerConfigData.class);
    }

    public KitchenStation getKitchenStation() {
        return new KitchenStation();
    }

    public KitchenAccessPoint getKitchenAccessPoint() {
        return new KitchenAccessPoint();
    }

    public static final class KitchenStation {
        public int getUsage() {
            return data().kitchenStationUsage;
        }
    }

    public static final class KitchenAccessPoint {
        public int getUsage() {
            return data().kitchenAccessPointUsage;
        }
    }
}
