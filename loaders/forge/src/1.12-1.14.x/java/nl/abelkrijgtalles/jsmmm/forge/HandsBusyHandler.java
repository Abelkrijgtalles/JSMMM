package nl.abelkrijgtalles.jsmmm.forge;

import net.minecraft.client.Minecraft;
#if MC_1_14
import net.minecraft.client.entity.player.ClientPlayerEntity;
#else
import net.minecraft.client.entity.EntityPlayerSP;
#endif
#if LEGACY_EVENTS
import net.minecraftforge.fml.common.gameevent.TickEvent;
#else
import net.minecraftforge.event.TickEvent;
#endif
#if MC_1_12
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
#else
import net.minecraftforge.eventbus.api.SubscribeEvent;
#endif
import nl.abelkrijgtalles.jsmmm.HandsBusySetter;
import nl.abelkrijgtalles.jsmmm.RideTickHandler;

public class HandsBusyHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        #if MC_1_14
        ClientPlayerEntity player = Minecraft.getInstance().player;
        #else
        EntityPlayerSP player = Minecraft
                #if MC_1_12
                .getMinecraft()
                #else
                .getInstance()
                #endif.player;
        #endif

        RideTickHandler.handleRideTick(player, (HandsBusySetter) handsBusy -> {
            #if LEGACY_EVENTS
            player.rowingBoat = handsBusy;
            #else
            player.handsBusy = handsBusy;
            #endif
        });
    }
}
