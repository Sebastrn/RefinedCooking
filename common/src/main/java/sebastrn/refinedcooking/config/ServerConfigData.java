package sebastrn.refinedcooking.config;

import net.blay09.mods.balm.api.config.BalmConfigData;
import net.blay09.mods.balm.api.config.Comment;
import net.blay09.mods.balm.api.config.Config;
import sebastrn.refinedcooking.RefinedCooking;

/**
 * Balm-backed config (replaces the NeoForge ModConfigSpec so the values are readable from loader-neutral code). Same
 * defaults as before, Kitchen Station 8 (RS Network Receiver), Access Point 32 (RS Network Transmitter).
 */
@Config(RefinedCooking.ID)
public class ServerConfigData implements BalmConfigData {

    @Comment("The energy used by the Kitchen Station (8 by default, matching RS's Network Receiver, the passive remote end of the link)")
    public int kitchenStationUsage = 8;

    @Comment("The energy used by the Kitchen Access Point (32 by default, matching RS's Network Transmitter, the device that projects the network to a remote station)")
    public int kitchenAccessPointUsage = 32;
}
