package Method.Client.utils.proxy.renderers;

import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerStrayClothing;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.util.ResourceLocation;

public class ModRenderStray extends ModRenderSkeleton {
  private static final ResourceLocation STRAY_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/stray.png");
  
  public ModRenderStray(RenderManager manager) {
    super(manager);
    addLayer((LayerRenderer)new LayerStrayClothing((RenderLivingBase)this));
  }
  
  protected ResourceLocation getEntityTexture(AbstractSkeleton entity) {
    return STRAY_SKELETON_TEXTURES;
  }
}
