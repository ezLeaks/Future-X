package Method.Client.utils.proxy.renderers;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelSkeleton;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSkeleton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModRenderSkeleton extends RenderSkeleton {
  public ModRenderSkeleton(RenderManager renderManagerIn) {
    super(renderManagerIn);
    this.layerRenderers.remove(4);
    addLayer(new ModLayerBipedArmor((RenderLivingBase)this) {
          protected void initArmor() {
            this.modelLeggings = (ModelBiped)new ModelSkeleton(0.5F, true);
            this.modelArmor = (ModelBiped)new ModelSkeleton(1.0F, true);
          }
        });
  }
}
