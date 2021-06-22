package Method.Client.utils.proxy.Overrides;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;

public class EntityRenderMixin extends EntityRenderer {
  public static boolean Camswitch = true;
  
  public EntityRenderMixin(Minecraft mcIn, IResourceManager resourceManagerIn) {
    super(mcIn, resourceManagerIn);
  }
  
  public void hurtCameraEffect(float partialTicks) {
    if (Camswitch)
      super.hurtCameraEffect(partialTicks); 
  }
}
