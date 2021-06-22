package Method.Client.utils.proxy.renderers;

import Method.Client.module.movement.BoatFly;
import Method.Client.utils.visual.ColorUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBoat;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;

public class ModRenderBoat extends RenderBoat {
  public ModRenderBoat(RenderManager renderManager) {
    super(renderManager);
  }
  
  public void doRender(EntityBoat entity, double x, double y, double z, float entityYaw, float partialTicks) {
    if (BoatFly.Boatblend.getValBoolean())
      GlStateManager.enableBlend(); 
    if (BoatFly.BoatRender.getValString().equalsIgnoreCase("Vanish"))
      return; 
    if (BoatFly.BoatRender.getValString().equalsIgnoreCase("Rainbow"))
      ColorUtils.glColor(ColorUtils.rainbow().getRGB()); 
    super.doRender(entity, x, y, z, entityYaw, partialTicks);
    if (BoatFly.Boatblend.getValBoolean())
      GlStateManager.disableBlend(); 
  }
}
