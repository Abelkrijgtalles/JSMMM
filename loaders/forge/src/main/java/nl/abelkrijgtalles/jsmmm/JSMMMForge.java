package nl.abelkrijgtalles.jsmmm;

import net.minecraftforge.fml.common.Mod;
#if NO_MIXIN
import net.minecraftforge.common.MinecraftForge;
import nl.abelkrijgtalles.jsmmm.forge.HandsBusyHandler;
#endif

@Mod(#if MCMOD modid = #endif "jsmmm")
public class JSMMMForge {
    public JSMMMForge() {
        #if NO_MIXIN
        MinecraftForge.EVENT_BUS.register(HandsBusyHandler.class);
        #endif
    }
}
