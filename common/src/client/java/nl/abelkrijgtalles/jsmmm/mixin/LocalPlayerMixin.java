#if !NO_MIXIN
package nl.abelkrijgtalles.jsmmm.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

	@Accessor("handsBusy")
    abstract void jsmmm$setHandsBusy(boolean handsBusy);

	// I don't want to change something if it isn't needed
	@Invoker("getUsedItemHand")
	abstract InteractionHand invokeGetUsedItemHand();

	@Inject(method = "rideTick", at = @At("TAIL"))
	private void overrideHandsBusy(CallbackInfo ci) {
		InteractionHand hand = invokeGetUsedItemHand();

		if (hand == null) {
			hand = InteractionHand.MAIN_HAND;
		}

		ItemStack itemStack = ((LivingEntity) (Object) this).getItemInHand(hand);
		if (itemStack == null) return;
		if (itemStack.getItem() instanceof MapItem) {
			jsmmm$setHandsBusy(false);
		}
	}

}
#endif