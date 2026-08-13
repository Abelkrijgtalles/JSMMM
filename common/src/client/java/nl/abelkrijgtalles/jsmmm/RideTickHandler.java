package nl.abelkrijgtalles.jsmmm;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;

public class RideTickHandler {
    public static void handleRideTick(LocalPlayer player, HandsBusySetter handsBusySetter) {
        if (!(player.getVehicle() instanceof Boat)) return;

        InteractionHand hand = player.getUsedItemHand();

        //noinspection ConstantValue - In earlier versions, this wasn't constant
        if (hand == null) {
            hand = InteractionHand.MAIN_HAND;
        }

        ItemStack itemStack = player.getItemInHand(hand);
        //noinspection ConstantValue - In earlier versions, this wasn't constant
        if (itemStack == null) return;
        if (itemStack.getItem() instanceof MapItem) {
            handsBusySetter.setHandsBusy(false);
        }
    }
}
