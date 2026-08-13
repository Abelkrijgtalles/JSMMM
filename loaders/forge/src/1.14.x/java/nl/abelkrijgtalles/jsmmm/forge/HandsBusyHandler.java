package nl.abelkrijgtalles.jsmmm.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
#if MC_1_14_2
import net.minecraftforge.fml.common.gameevent.TickEvent;
#else
import net.minecraftforge.event.TickEvent;
#endif
import net.minecraftforge.eventbus.api.SubscribeEvent;
import nl.abelkrijgtalles.jsmmm.HandsBusySetter;
import nl.abelkrijgtalles.jsmmm.RideTickHandler;

public class HandsBusyHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        ClientPlayerEntity player = Minecraft.getInstance().player;

        RideTickHandler.handleRideTick(player, (HandsBusySetter) handsBusy -> {
            #if MC_1_14_2
            player.rowingBoat = handsBusy;
            #else
            player.handsBusy = handsBusy;
            #endif
        });
    }
}
