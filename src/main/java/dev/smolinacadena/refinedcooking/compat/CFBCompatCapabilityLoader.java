package dev.smolinacadena.refinedcooking.compat;

import net.blay09.mods.cookingforblockheads.compat.CompatCapabilityLoader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CFBCompatCapabilityLoader {

    @SubscribeEvent
    public static void attachTileEntityCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        CompatCapabilityLoader.attachTileEntityCapabilities(event);
    }
}
