package Method.Client.utils.proxy.renderers;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModLayerBipedArmor extends ModLayerArmorBase<ModelBiped> {
  public ModLayerBipedArmor(RenderLivingBase<?> rendererIn) {
    super(rendererIn);
  }
  
  protected void initArmor() {
    this.modelLeggings = new ModelBiped(0.5F);
    this.modelArmor = new ModelBiped(1.0F);
  }
  
  protected void setModelSlotVisible(ModelBiped modelIn, EntityEquipmentSlot slotIn) {
    setModelVisible(modelIn);
    switch (slotIn) {
      case HEAD:
        modelIn.bipedHead.showModel = true;
        modelIn.bipedHeadwear.showModel = true;
        break;
      case CHEST:
        modelIn.bipedBody.showModel = true;
        modelIn.bipedRightArm.showModel = true;
        modelIn.bipedLeftArm.showModel = true;
        break;
      case LEGS:
        modelIn.bipedBody.showModel = true;
        modelIn.bipedRightLeg.showModel = true;
        modelIn.bipedLeftLeg.showModel = true;
        break;
      case FEET:
        modelIn.bipedRightLeg.showModel = true;
        modelIn.bipedLeftLeg.showModel = true;
        break;
    } 
  }
  
  protected void setModelVisible(ModelBiped model) {
    model.setVisible(false);
  }
  
  protected ModelBiped getArmorModelHook(EntityLivingBase entity, ItemStack itemStack, EntityEquipmentSlot slot, ModelBiped model) {
    return ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
  }
}
