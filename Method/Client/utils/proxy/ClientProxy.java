package Method.Client.utils.proxy;

import Method.Client.module.Module;
import Method.Client.utils.proxy.Overrides.ColorMix;
import Method.Client.utils.proxy.Overrides.EntityRenderMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(value = {Side.CLIENT}, modid = "futurex")
public class ClientProxy implements IProxy {
  public static Module Gl;
  
  protected static Minecraft mc = Minecraft.getMinecraft();
  
  public void init(FMLInitializationEvent event) {
    ColorMix.replaceRenderers();
    ViewBobOverride();
  }
  
  public static void ViewBobOverride() {
    mc.entityRenderer = (EntityRenderer)new EntityRenderMixin(mc, (IResourceManager)mc.resourceManager);
  }
}
