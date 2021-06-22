package Method.Client.utils.proxy.renderers;

import Method.Client.module.render.ArmorRender;
import Method.Client.utils.proxy.Overrides.ColorMix;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class ModLayerArmorBase<T extends ModelBase> implements LayerRenderer<EntityLivingBase> {
  protected static final ResourceLocation RES_ITEM_GLINT_RUNE = new ResourceLocation("futurex", "enchanted_item_glint_rune.png");
  
  protected static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("futurex", "enchanted_item_glint.png");
  
  protected T modelLeggings;
  
  protected T modelArmor;
  
  private final RenderLivingBase<?> renderer;
  
  private boolean skipRenderGlint;
  
  private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();
  
  public ModLayerArmorBase(RenderLivingBase<?> rendererIn) {
    this.renderer = rendererIn;
    initArmor();
  }
  
  public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
    renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.CHEST);
    renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.LEGS);
    renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.FEET);
    renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.HEAD);
  }
  
  public boolean shouldCombineTextures() {
    return false;
  }
  
  private void renderArmorLayer(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn) {
    ItemStack itemstack = entityLivingBaseIn.getItemStackFromSlot(slotIn);
    if (ArmorRender.RenderArmor.getValBoolean() && 
      itemstack.getItem() instanceof ItemArmor) {
      ItemArmor itemarmor = (ItemArmor)itemstack.getItem();
      if (itemarmor.getEquipmentSlot() == slotIn) {
        T model = getModelFromSlot(slotIn);
        model = getArmorModelHook(entityLivingBaseIn, itemstack, slotIn, model);
        model.setModelAttributes(this.renderer.getMainModel());
        model.setLivingAnimations(entityLivingBaseIn, limbSwing, limbSwingAmount, partialTicks);
        setModelSlotVisible(model, slotIn);
        this.renderer.bindTexture(getArmorResource((Entity)entityLivingBaseIn, itemstack, slotIn, null));
        float alpha = 1.0F;
        float colorR = 1.0F;
        float colorG = 1.0F;
        float colorB = 1.0F;
        if (itemarmor.hasOverlay(itemstack)) {
          int itemColor = itemarmor.getColor(itemstack);
          float itemRed = (itemColor >> 16 & 0xFF) / 255.0F;
          float itemGreen = (itemColor >> 8 & 0xFF) / 255.0F;
          float itemBlue = (itemColor & 0xFF) / 255.0F;
          GlStateManager.color(colorR * itemRed, colorG * itemGreen, colorB * itemBlue, alpha);
          model.render((Entity)entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
          this.renderer.bindTexture(getArmorResource((Entity)entityLivingBaseIn, itemstack, slotIn, "overlay"));
        } 
        GlStateManager.color(colorR, colorG, colorB, alpha);
        model.render((Entity)entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        if (!this.skipRenderGlint && itemstack.hasEffect())
          renderEnchantedGlint(this.renderer, entityLivingBaseIn, (ModelBase)model, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, ColorMix.getColorForEnchantment(EnchantmentHelper.getEnchantments(itemstack))); 
      } 
    } 
  }
  
  public T getModelFromSlot(EntityEquipmentSlot slotIn) {
    return isLegSlot(slotIn) ? this.modelLeggings : this.modelArmor;
  }
  
  private boolean isLegSlot(EntityEquipmentSlot slotIn) {
    return (slotIn == EntityEquipmentSlot.LEGS);
  }
  
  public static void renderEnchantedGlint(RenderLivingBase<?> parRenderLivingBase, EntityLivingBase parEntityLivingBase, ModelBase model, float parLimbSwing, float parLimbSwingAmount, float parPartialTicks, float parAgeInTicks, float parHeadYaw, float parHeadPitch, float parScale, int parColor) {
    float f = parEntityLivingBase.ticksExisted + parPartialTicks;
    if (ArmorRender.useRuneTexture.getValBoolean()) {
      parRenderLivingBase.bindTexture(RES_ITEM_GLINT_RUNE);
    } else {
      parRenderLivingBase.bindTexture(RES_ITEM_GLINT);
    } 
    (Minecraft.getMinecraft()).entityRenderer.setupFogColor(true);
    GlStateManager.enableBlend();
    GlStateManager.depthFunc(514);
    GlStateManager.depthMask(false);
    GlStateManager.color(ColorMix.redFromColor(parColor), ColorMix.greenFromColor(parColor), ColorMix.blueFromColor(parColor), ColorMix.alphaFromColor());
    for (int i = 0; i < 2; i++) {
      GlStateManager.disableLighting();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
      GlStateManager.color(ColorMix.redFromColor(parColor), ColorMix.greenFromColor(parColor), ColorMix.blueFromColor(parColor), ColorMix.alphaFromColor());
      GlStateManager.matrixMode(5890);
      GlStateManager.loadIdentity();
      GlStateManager.scale(3.0F, 3.0F, 3.0F);
      GlStateManager.rotate(30.0F - i * 60.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(0.0F, f * (0.001F + i * 0.003F) * 5.0F, 0.0F);
      GlStateManager.matrixMode(5888);
      model.render((Entity)parEntityLivingBase, parLimbSwing, parLimbSwingAmount, parAgeInTicks, parHeadYaw, parHeadPitch, parScale);
      GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    } 
    GlStateManager.matrixMode(5890);
    GlStateManager.loadIdentity();
    GlStateManager.matrixMode(5888);
    GlStateManager.enableLighting();
    GlStateManager.depthMask(true);
    GlStateManager.depthFunc(515);
    GlStateManager.disableBlend();
    (Minecraft.getMinecraft()).entityRenderer.setupFogColor(false);
  }
  
  protected abstract void initArmor();
  
  protected abstract void setModelSlotVisible(T paramT, EntityEquipmentSlot paramEntityEquipmentSlot);
  
  protected T getArmorModelHook(EntityLivingBase entity, ItemStack itemStack, EntityEquipmentSlot slot, T model) {
    return model;
  }
  
  public ResourceLocation getArmorResource(Entity entity, ItemStack stack, EntityEquipmentSlot slot, String type) {
    ItemArmor item = (ItemArmor)stack.getItem();
    String texture = item.getArmorMaterial().getName();
    String domain = "minecraft";
    int idx = texture.indexOf(':');
    if (idx != -1) {
      domain = texture.substring(0, idx);
      texture = texture.substring(idx + 1);
    } 
    String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", new Object[] { domain, texture, Integer.valueOf(isLegSlot(slot) ? 2 : 1), (type == null) ? "" : String.format("_%s", new Object[] { type }) });
    s1 = ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
    ResourceLocation resourcelocation = ARMOR_TEXTURE_RES_MAP.get(s1);
    if (resourcelocation == null) {
      resourcelocation = new ResourceLocation(s1);
      ARMOR_TEXTURE_RES_MAP.put(s1, resourcelocation);
    } 
    return resourcelocation;
  }
}
