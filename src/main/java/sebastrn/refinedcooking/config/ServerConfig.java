package sebastrn.refinedcooking.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ServerConfig {
    private final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
    private final ModConfigSpec spec;

    private final KitchenStation kitchenStation;
    private final KitchenAccessPoint kitchenAccessPoint;

    public ServerConfig() {
        kitchenStation = new KitchenStation();
        kitchenAccessPoint = new KitchenAccessPoint();

        spec = builder.build();
    }

    public ModConfigSpec getSpec() {
        return spec;
    }

    public KitchenStation getKitchenStation() {
        return kitchenStation;
    }

    public KitchenAccessPoint getKitchenAccessPoint() {
        return kitchenAccessPoint;
    }

    public class KitchenStation {
        private final ModConfigSpec.IntValue usage;

        public KitchenStation() {
            builder.push("kitchenStation");

            usage = builder.comment("The energy used by the Kitchen Station (0 by default, matching RS's Network Receiver — the passive remote end of the link)").defineInRange("usage", 0, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class KitchenAccessPoint {
        private final ModConfigSpec.IntValue usage;

        public KitchenAccessPoint() {
            builder.push("kitchenAccessPoint");

            usage = builder.comment("The energy used by the Kitchen Access Point (64 by default, matching RS's Network Transmitter — the device that projects the network to a remote station)").defineInRange("usage", 64, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }
}
