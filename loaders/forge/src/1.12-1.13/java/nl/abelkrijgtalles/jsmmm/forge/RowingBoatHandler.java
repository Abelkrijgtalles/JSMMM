package nl.abelkrijgtalles.jsmmm.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
#if MC_1_12
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
#else
import net.minecraftforge.eventbus.api.SubscribeEvent;
#endif
import net.minecraftforge.fml.common.gameevent.TickEvent;
import nl.abelkrijgtalles.jsmmm.HandsBusySetter;
import nl.abelkrijgtalles.jsmmm.RideTickHandler;

public class RowingBoatHandler {
    @SubscribeEvent
    public void onPlayerTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        EntityPlayerSP player = Minecraft
                #if MC_1_12
                .getMinecraft()
                #else
                .getInstance()
                #endif.player;

        RideTickHandler.handleRideTick(player, (HandsBusySetter) handsBusy -> player.rowingBoat = handsBusy);
    }
}
