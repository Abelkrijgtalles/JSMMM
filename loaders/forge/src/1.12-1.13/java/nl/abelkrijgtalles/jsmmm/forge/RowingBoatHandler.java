package nl.abelkrijgtalles.jsmmm.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemMap;
#if MC_1_12
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
#else
import net.minecraftforge.eventbus.api.SubscribeEvent;
#endif
import net.minecraftforge.fml.common.gameevent.TickEvent;

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

        if (player == null) return;
        //noinspection ConstantValue
        if (player.getHeldItemMainhand() == null) return;

        if (player.getHeldItemMainhand().getItem() instanceof ItemMap) {
            player.rowingBoat = false;
        }
    }
}
