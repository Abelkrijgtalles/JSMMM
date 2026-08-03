#if MC_1_15_2
package nl.abelkrijgtalles.jsmmm.mixin;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.util.Hand;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Accessor("handsBusy")
    abstract void jsmmm$setHandsBusy(boolean handsBusy);

    @Invoker("getUsedItemHand")
    abstract Hand invokeGetUsedItemHand();

    @Inject(method = "rideTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/entity/player/ClientPlayerEntity;handsBusy:Z", opcode = Opcodes.PUTFIELD, ordinal = 1, shift = At.Shift.AFTER))
    private void overrideHandsBusy(CallbackInfo ci) {
        Hand hand = invokeGetUsedItemHand();

        if (hand == null) {
            hand = Hand.MAIN_HAND;
        }

        if (((LivingEntity) (Object) this).getItemInHand(hand).getItem() instanceof FilledMapItem) {
            jsmmm$setHandsBusy(false);
        }
    }

}
#endif