package Method.Client.utils.proxy.renderers;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelZombieVillager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderZombieVillager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModRenderZombieVillager extends RenderZombieVillager {
  public ModRenderZombieVillager(RenderManager manager) {
    super(manager);
    ModLayerBipedArmor layerbipedarmor = new ModLayerBipedArmor((RenderLivingBase)this) {
        protected void initArmor() {
          this.modelLeggings = (ModelBiped)new ModelZombieVillager(0.5F, 0.0F, true);
          this.modelArmor = (ModelBiped)new ModelZombieVillager(1.0F, 0.0F, true);
        }
      };
    this.layerRenderers.remove(3);
    addLayer(layerbipedarmor);
  }
}
