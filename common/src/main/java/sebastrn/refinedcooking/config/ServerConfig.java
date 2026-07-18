package sebastrn.refinedcooking.config;

import net.blay09.mods.balm.api.Balm;

/**
 * Thin facade over the Balm-backed {@link ServerConfigData}, keeping the nested {@code getKitchenStation().getUsage()}
 * shape the block entities already use so their call sites are unchanged.
 */
public class ServerConfig {

    public void initialize() {
        Balm.getConfig().registerConfig(ServerConfigData.class, null);
    }

    private static ServerConfigData data() {
        return Balm.getConfig().getActive(ServerConfigData.class);
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
