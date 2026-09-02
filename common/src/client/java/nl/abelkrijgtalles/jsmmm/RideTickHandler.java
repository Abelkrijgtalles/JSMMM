package nl.abelkrijgtalles.jsmmm;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;

public class RideTickHandler {
    public static void handleRideTick(LocalPlayer player, HandsBusySetter handsBusySetter) {
        Entity vehicle = player.getVehicle();

        //noinspection ConditionCoveredByFurtherCondition Can cause a NullPointerException if this isn't used
        if (vehicle == null || !(vehicle instanceof Boat)) return;

        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack itemStack = player.getItemInHand(hand);

            //noinspection ConstantValue - In earlier versions, this wasn't constant
            if (itemStack == null) continue;

            if (itemStack.getItem() instanceof MapItem) {
                handsBusySetter.setHandsBusy(false);
                return;
            }
        }
    }
}
