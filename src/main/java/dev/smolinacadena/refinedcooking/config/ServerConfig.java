package dev.smolinacadena.refinedcooking.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    private final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    private final ForgeConfigSpec spec;

    private final KitchenStation kitchenStation;
    private final KitchenAccessPoint kitchenAccessPoint;

    public ServerConfig(){
        kitchenStation = new KitchenStation();
        kitchenAccessPoint = new KitchenAccessPoint();

        spec = builder.build();
    }

    public ForgeConfigSpec getSpec() {
        return spec;
    }

    public KitchenStation getKitchenStation() {
        return kitchenStation;
    }

    public KitchenAccessPoint getKitchenAccessPoint() {
        return kitchenAccessPoint;
    }

    public class KitchenStation {
        private final ForgeConfigSpec.IntValue usage;

        public KitchenStation() {
            builder.push("kitchenStation");

            usage = builder.comment("The energy used by the Kitchen Station").defineInRange("usage", 1, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }

    public class KitchenAccessPoint {
        private final ForgeConfigSpec.IntValue usage;

        public KitchenAccessPoint() {
            builder.push("kitchenAccessPoint");

            usage = builder.comment("The energy used by the Kitchen Access Point").defineInRange("usage", 1, 0, Integer.MAX_VALUE);

            builder.pop();
        }

        public int getUsage() {
            return usage.get();
        }
    }
}
