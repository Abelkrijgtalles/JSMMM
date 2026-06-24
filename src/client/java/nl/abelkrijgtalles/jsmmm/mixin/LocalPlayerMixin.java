package nl.abelkrijgtalles.jsmmm.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.MapItem;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

	@Shadow private boolean handsBusy;

	@Shadow
	public abstract InteractionHand getUsedItemHand();

	@Inject(method = "rideTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/LocalPlayer;handsBusy:Z", opcode = Opcodes.PUTFIELD, ordinal = 1, shift = At.Shift.AFTER))
	private void overrideHandsBusy(CallbackInfo ci) {
		if (((LivingEntity) (Object) this).getItemInHand(getUsedItemHand()).getItem() instanceof MapItem) {
			this.handsBusy = false;
		}
	}

}