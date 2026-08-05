package nl.abelkrijgtalles.jsmmm.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.util.Hand;
#if MC_1_14_2
import net.minecraftforge.fml.common.gameevent.TickEvent;
#else
import net.minecraftforge.event.TickEvent;
#endif
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HandsBusyHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        ClientPlayerEntity player = Minecraft.getInstance().player;

        if (player == null) return;
        #if MC_1_14_2
        Hand hand = player.getActiveHand();
        #else
        Hand hand = player.getUsedItemHand();
        #endif

        if (hand == null) {
            hand = Hand.MAIN_HAND;
        }

        if (#if MC_1_14_2
                player.getHeldItem(hand)
            #else
                player.getItemInHand(hand)
            #endif
        .getItem() instanceof FilledMapItem) {
            #if MC_1_14_2
            player.rowingBoat = false;
            #else
            player.handsBusy = false;
            #endif
        }
    }
}
