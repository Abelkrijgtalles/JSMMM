package nl.abelkrijgtalles.jsmmm;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;

public class RideTickHandler {
    private static boolean actuallyBusy = false;

    public static void handleRideTick(LocalPlayer player, HandsBusySetter handsBusySetter) {
        if (player == null) return;
        actuallyBusy = player.isHandsBusy();

        // Vanilla Minecraft actually does most of the checks, and we can just do this. Didn't know why I didn't just check handsBusy earlier
        if (!player.isHandsBusy()) return;

        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack itemStack = player.getItemInHand(hand);

            //noinspection ConstantValue - In earlier versions, this wasn't constant
            if (itemStack == null) continue;

            if (itemStack.getItem() instanceof MapItem) {
                handsBusySetter.setHandsBusy(false);
                actuallyBusy = true;
                return;
            }
        }
    }

    public static boolean isActuallyBusy() {
        return actuallyBusy;
    }
}
