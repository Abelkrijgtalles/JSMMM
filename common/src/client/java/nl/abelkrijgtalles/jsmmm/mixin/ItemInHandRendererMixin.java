package nl.abelkrijgtalles.jsmmm.mixin;

import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import nl.abelkrijgtalles.jsmmm.RideTickHandler;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Shadow
    private ItemStack mainHandItem;

    @Shadow
    private float mainHandHeight;

    @Redirect(method = "tick", at = @At(value = "FIELD", ordinal = 1, target = "Lnet/minecraft/client/renderer/ItemInHandRenderer;mainHandHeight:F", opcode = Opcodes.PUTFIELD))
    private void overrideMainHandHeight(ItemInHandRenderer instance, float mainHandHeight) {
        if (RideTickHandler.isActuallyBusy() && !(this.mainHandItem.getItem() instanceof MapItem)) {
            this.mainHandHeight = Mth.clamp(this.mainHandHeight - 0.4F, 0.0F, 1.0F);
        } else {
            this.mainHandHeight = mainHandHeight;
        }
    }

}
