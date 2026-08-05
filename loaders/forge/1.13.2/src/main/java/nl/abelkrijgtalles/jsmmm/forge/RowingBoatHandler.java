package nl.abelkrijgtalles.jsmmm.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemMap;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class RowingBoatHandler {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        EntityPlayerSP player = Minecraft.getInstance().player;

        if (player == null) return;

        if (player.getHeldItemMainhand().getItem() instanceof ItemMap) {
            player.rowingBoat = false;
        }
    }
}
