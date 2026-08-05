package nl.abelkrijgtalles.jsmmm;

import net.minecraftforge.fml.common.Mod;
#if MC_1_12 || MC_1_13_2 || MC_1_14_2 || MC_1_14_4
import net.minecraftforge.common.MinecraftForge;
#endif
#if MC_1_12 || MC_1_13_2
import nl.abelkrijgtalles.jsmmm.forge.RowingBoatHandler;
#endif
#if MC_1_14_2 || MC_1_14_4
import nl.abelkrijgtalles.jsmmm.forge.HandsBusyHandler;
#endif

@Mod(#if MC_1_12 modid = #endif "jsmmm")
public class JSMMMForge {
    public JSMMMForge() {
        #if MC_1_14_2 || MC_1_14_4
        MinecraftForge.EVENT_BUS.register(HandsBusyHandler.class);
        #endif

        #if MC_1_12 || MC_1_13_2
        MinecraftForge.EVENT_BUS.register(new RowingBoatHandler());
        #endif
    }
}
