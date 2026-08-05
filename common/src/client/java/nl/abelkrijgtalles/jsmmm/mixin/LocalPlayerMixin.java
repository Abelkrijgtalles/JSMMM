#if !NO_MM
package nl.abelkrijgtalles.jsmmm.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.MapItem;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
#if !FORGE_OBF
import org.spongepowered.asm.mixin.Shadow;
#else
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
#endif
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

	#if !FORGE_OBF
	@Shadow private boolean handsBusy;
	#else
	@Accessor("handsBusy")
    abstract void jsmmm$setHandsBusy(boolean handsBusy);
	#endif

	// I don't want to change something if it isn't needed
	#if !FORGE_OBF
	@Shadow
	public abstract InteractionHand getUsedItemHand();
	#else
	@Invoker("getUsedItemHand")
	abstract InteractionHand invokeGetUsedItemHand();
	#endif

	@Inject(method = "rideTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/LocalPlayer;handsBusy:Z", opcode = Opcodes.PUTFIELD, ordinal = 1, shift = At.Shift.AFTER))
	private void overrideHandsBusy(CallbackInfo ci) {
		InteractionHand hand =
				#if !FORGE_OBF getUsedItemHand();
				#else invokeGetUsedItemHand();
				#endif

		#if MC_1_14_4 || MC_1_16_5 || MC_1_17 || MC_1_17_1 || MC_1_18_PRE
			if (hand == null) {
				hand = InteractionHand.MAIN_HAND;
			}
		#endif
		if (((LivingEntity) (Object) this).getItemInHand(hand).getItem() instanceof MapItem) {
			#if !FORGE_OBF
			this.handsBusy = false;
			#else
			jsmmm$setHandsBusy(false);
			#endif
		}
	}

}
#endif