#if !NO_MIXIN
package nl.abelkrijgtalles.jsmmm.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import nl.abelkrijgtalles.jsmmm.RideTickHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Redirect(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isHandsBusy()Z"))
    private boolean overrideStartAttackHandsBusyCheck(LocalPlayer instance) {
        return RideTickHandler.isActuallyBusy();
    }

    @Redirect(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isHandsBusy()Z"))
    private boolean overrideStartUseItemHandsBusyCheck(LocalPlayer instance) {
        return RideTickHandler.isActuallyBusy();
    }
}
#endif