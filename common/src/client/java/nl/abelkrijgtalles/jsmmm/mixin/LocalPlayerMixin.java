#if !NO_MIXIN
package nl.abelkrijgtalles.jsmmm.mixin;

import net.minecraft.client.player.LocalPlayer;
import nl.abelkrijgtalles.jsmmm.RideTickHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {

	@Accessor("handsBusy")
    abstract void jsmmm$setHandsBusy(boolean handsBusy);

	@Inject(method = "rideTick", at = @At("TAIL"))
	private void overrideHandsBusy(CallbackInfo ci) {
		RideTickHandler.handleRideTick((LocalPlayer) ((Object) this), this::jsmmm$setHandsBusy);
	}

}
#endif