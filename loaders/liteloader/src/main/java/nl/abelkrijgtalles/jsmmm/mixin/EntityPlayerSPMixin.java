package nl.abelkrijgtalles.jsmmm.mixin;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemMap;
import net.minecraft.util.EnumHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayerSP.class)
public abstract class EntityPlayerSPMixin {

    @Shadow private boolean rowingBoat;

    @Shadow
    public abstract EnumHand getActiveHand();

    @Inject(method = "updateRidden", at = @At("TAIL"))
    private void overrideRowingBoat(CallbackInfo ci) {
        EnumHand hand = getActiveHand();

        if (hand == null) {
            hand = EnumHand.MAIN_HAND;
        }

        if (((EntityLivingBase) (Object) this).getHeldItem(hand) == null) return;

        if (((EntityLivingBase) (Object) this).getHeldItem(hand).getItem() instanceof ItemMap) {
            this.rowingBoat = false;
        }
    }

}
