package dev.smolinacadena.refinedcooking.compat.jade;

import dev.smolinacadena.refinedcooking.RefinedCooking;
import dev.smolinacadena.refinedcooking.block.KitchenAccessPointBlock;
import dev.smolinacadena.refinedcooking.block.KitchenStationBlock;
import dev.smolinacadena.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
import dev.smolinacadena.refinedcooking.blockentity.KitchenStationBlockEntity;
import mcp.mobius.waila.api.*;

@WailaPlugin(RefinedCooking.ID)
public class JadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(new KitchenStationComponentProvider(), KitchenStationBlockEntity.class);
        registration.registerBlockDataProvider(new KitchenAccessPointComponentProvider(), KitchenAccessPointBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerComponentProvider(new KitchenStationComponentProvider(), TooltipPosition.BODY, KitchenStationBlock.class);
        registration.registerComponentProvider(new KitchenAccessPointComponentProvider(), TooltipPosition.BODY, KitchenAccessPointBlock.class);
    }
}
