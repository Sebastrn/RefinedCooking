package dev.smolinacadena.refinedcooking.compat.jade;

import dev.smolinacadena.refinedcooking.RefinedCooking;
import dev.smolinacadena.refinedcooking.block.KitchenAccessPointBlock;
import dev.smolinacadena.refinedcooking.block.KitchenStationBlock;
import dev.smolinacadena.refinedcooking.blockentity.KitchenAccessPointBlockEntity;
import dev.smolinacadena.refinedcooking.blockentity.KitchenStationBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin(RefinedCooking.ID)
public class JadePlugin implements IWailaPlugin {
    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(new KitchenStationComponentProvider(), KitchenStationBlockEntity.class);
        registration.registerBlockDataProvider(new KitchenAccessPointComponentProvider(), KitchenAccessPointBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new KitchenStationComponentProvider(), KitchenStationBlock.class);
        registration.registerBlockComponent(new KitchenAccessPointComponentProvider(), KitchenAccessPointBlock.class);
    }
}
