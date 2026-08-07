package nl.abelkrijgtalles.jsmmm.mixin;

import net.minecraft.client.entity.living.player.LocalClientPlayerEntity;
import net.minecraft.entity.living.LivingEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalClientPlayerEntity.class)
public abstract class LocalClientPlayerEntityMixin {

    @Shadow private boolean rowing;

    @Shadow
    public abstract InteractionHand getHandInUse();

    @Inject(method = "rideTick", at = @At("TAIL"))
    private void overrideRowing(CallbackInfo ci) {
        InteractionHand hand = getHandInUse();

        if (hand == null) {
            hand = InteractionHand.MAIN_HAND;
        }

        if (((LivingEntity) (Object) this).getItemInHand(hand) == null) return;

        if (((LivingEntity) (Object) this).getItemInHand(hand).getItem() instanceof FilledMapItem) {
            this.rowing = false;
        }
    }

}
