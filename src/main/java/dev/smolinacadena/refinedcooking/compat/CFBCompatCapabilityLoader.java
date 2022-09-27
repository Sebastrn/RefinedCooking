package dev.smolinacadena.refinedcooking.compat;

import dev.smolinacadena.refinedcooking.RefinedCooking;
import net.blay09.mods.cookingforblockheads.compat.CompatCapabilityLoader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RefinedCooking.ID)
public class CFBCompatCapabilityLoader {

    @SubscribeEvent
    public static void attachTileEntityCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        CompatCapabilityLoader.attachTileEntityCapabilities(event);
    }
}
