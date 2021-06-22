package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.module.ModuleManager;
import Method.Client.module.combat.TotemPop;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.awt.Color;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

public class NameTags extends Module {
  public static boolean toggled = false;
  
  public Setting Scale;
  
  public Setting armor;
  
  public Setting Background;
  
  public Setting TextColor;
  
  public Setting ScaleMode;
  
  public Setting Gamemode;
  
  public Setting Player;
  
  public Setting Ping;
  
  public Setting Mob;
  
  public Setting Hostile;
  
  public Setting Totem;
  
  public Setting Healthmode;
  
  public NameTags() {
    super("NameTags", 0, Category.RENDER, "NameTags");
    this.Scale = Main.setmgr.add(new Setting("Scale", this, 2.0D, 0.0D, 4.0D, false));
    this.armor = Main.setmgr.add(new Setting("armor", this, true));
    this.Background = Main.setmgr.add(new Setting("Background", this, 0.0D, 1.0D, 0.01D, 0.22D));
    this.TextColor = Main.setmgr.add(new Setting("Name", this, 0.0D, 1.0D, 1.0D, 1.0D));
    this.ScaleMode = Main.setmgr.add(new Setting("Armor Mode", this, "H", new String[] { "H", "V" }));
    this.Gamemode = Main.setmgr.add(new Setting("Gamemode", this, true));
    this.Player = Main.setmgr.add(new Setting("Player", this, true));
    this.Ping = Main.setmgr.add(new Setting("Ping", this, true));
    this.Mob = Main.setmgr.add(new Setting("Mob", this, false));
    this.Hostile = Main.setmgr.add(new Setting("Hostile", this, false));
    this.Totem = Main.setmgr.add(new Setting("Totem Pops", this, false));
    this.Healthmode = Main.setmgr.add(new Setting("Health Mode", this, "Number", new String[] { "Number", "Bar" }));
  }
  
  public void onDisable() {
    toggled = false;
  }
  
  public void onEnable() {
    toggled = true;
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    for (Entity object : mc.world.loadedEntityList) {
      if ((this.Player.getValBoolean() && object instanceof EntityPlayer) || (this.Mob
        .getValBoolean() && object instanceof net.minecraft.entity.passive.IAnimals) || (this.Hostile
        .getValBoolean() && object instanceof net.minecraft.entity.monster.IMob)) {
        assert object instanceof EntityLivingBase;
        if (object != mc.player)
          Runnametag((EntityLivingBase)object); 
      } 
    } 
  }
  
  private void Runnametag(EntityLivingBase e) {
    double scale = Math.max(this.Scale.getValDouble() * (mc.player.getDistance((Entity)e) / 20.0F), 2.0D);
    StringBuilder healthBuilder = new StringBuilder();
    for (int i = 0; i < e.getHealth(); ) {
      healthBuilder.append(ChatFormatting.GREEN + "|");
      i++;
    } 
    StringBuilder health = new StringBuilder(healthBuilder.toString());
    int j;
    for (j = 0; j < MathHelper.clamp(e.getAbsorptionAmount(), 0.0F, e.getMaxHealth() - e.getHealth()); j++)
      health.append(ChatFormatting.RED + "|"); 
    for (j = 0; j < e.getMaxHealth() - e.getHealth() + e.getAbsorptionAmount(); j++)
      health.append(ChatFormatting.YELLOW + "|"); 
    if (e.getAbsorptionAmount() - e.getMaxHealth() - e.getHealth() > 0.0F)
      health.append(ChatFormatting.BLUE + " +").append((int)(e.getAbsorptionAmount() - e.getMaxHealth() - e.getHealth())); 
    String tag = "";
    if (this.Totem.getValBoolean() && 
      e instanceof EntityPlayer && ModuleManager.getModuleByName("TotemPop").isToggled())
      tag = tag + " T:" + TotemPop.getpops((Entity)e) + " "; 
    if (this.Ping.getValBoolean() && 
      e instanceof EntityPlayer)
      try {
        tag = tag + " " + (int)MathHelper.clamp(((NetHandlerPlayClient)Objects.<NetHandlerPlayClient>requireNonNull(mc.getConnection())).getPlayerInfo(e.getUniqueID()).getResponseTime(), 1.0F, 300.0F) + " P ";
      } catch (NullPointerException nullPointerException) {} 
    if (this.Gamemode.getValBoolean() && 
      e instanceof EntityPlayer) {
      EntityPlayer entityPlayer = (EntityPlayer)e;
      if (entityPlayer.isCreative())
        tag = tag + "[C]"; 
      if (entityPlayer.isSpectator())
        tag = tag + " [I]"; 
      if (!entityPlayer.isAllowEdit() && !entityPlayer.isSpectator())
        tag = tag + " [A]"; 
      if (!entityPlayer.isCreative() && !entityPlayer.isSpectator() && entityPlayer.isAllowEdit())
        tag = tag + " [S]"; 
    } 
    if (this.Healthmode.getValString().equalsIgnoreCase("Number")) {
      Processtext(e.getName() + " [" + (int)(e.getHealth() + e.getAbsorptionAmount()) + "/" + (int)e.getMaxHealth() + "]" + tag, mc.fontRenderer
          .getStringWidth(e.getName() + tag + "[]") / 2, this.TextColor, (Entity)e, e.height + 0.5D * scale, scale);
    } else if (this.Healthmode.getValString().equalsIgnoreCase("Bar")) {
      Processtext(e.getName() + tag, mc.fontRenderer.getStringWidth(e.getName() + tag) / 2, this.TextColor, (Entity)e, e.height + 0.5D * scale, scale);
      Processtext(health.toString(), mc.fontRenderer.getStringWidth(health.toString()) / 2, this.TextColor, (Entity)e, e.height + 0.75D * scale, scale);
    } 
    if (this.armor.getValBoolean()) {
      double c = 0.0D;
      double higher = this.Healthmode.getValString().equalsIgnoreCase("Bar") ? 0.25D : 0.0D;
      if (this.ScaleMode.getValString().equalsIgnoreCase("H")) {
        drawItem(e.posX, e.posY + e.height + (0.75D + higher) * scale, e.posZ, -2.5D, 0.0D, scale, e.getHeldItemMainhand());
        drawItem(e.posX, e.posY + e.height + (0.75D + higher) * scale, e.posZ, 2.5D, 0.0D, scale, e.getHeldItemOffhand());
        for (ItemStack itemStack : e.getArmorInventoryList()) {
          drawItem(e.posX, e.posY + e.height + (0.75D + higher) * scale, e.posZ, c + 1.5D, 0.0D, scale, itemStack);
          c--;
        } 
      } else if (this.ScaleMode.getValString().equalsIgnoreCase("V")) {
        drawItem(e.posX, e.posY + e.height + (0.75D + higher) * scale, e.posZ, -1.25D, 0.0D, scale, e.getHeldItemMainhand());
        drawItem(e.posX, e.posY + e.height + (0.75D + higher) * scale, e.posZ, 1.25D, 0.0D, scale, e.getHeldItemOffhand());
        for (ItemStack itemStack : e.getArmorInventoryList()) {
          if (itemStack.getCount() < 1)
            continue; 
          drawItem(e.posX, e.posY + e.height + (0.75D + higher) * scale, e.posZ, 0.0D, c, scale, itemStack);
          c++;
        } 
      } 
    } 
  }
  
  private void Processtext(String s, int stringWidth, Setting getcolor, Entity entity, double rofl, double scale) {
    try {
      glSetup(entity.posX, entity.posY + rofl, entity.posZ);
      GlStateManager.scale(-0.025D * scale, -0.025D * scale, 0.025D * scale);
      GlStateManager.disableTexture2D();
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
      bufferbuilder.pos((-stringWidth - 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
      bufferbuilder.pos((-stringWidth - 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
      bufferbuilder.pos((stringWidth + 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
      bufferbuilder.pos((stringWidth + 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      mc.fontRenderer.drawStringWithShadow(s, -stringWidth, 0.0F, getcolor.getcolor());
      glCleanup();
    } catch (Exception exception) {}
  }
  
  public static void drawItem(double x, double y, double z, double offX, double offY, double scale, ItemStack item) {
    glSetup(x, y, z);
    GlStateManager.scale(0.4D * scale, 0.4D * scale, 0.0D);
    GlStateManager.translate(offX, offY, 0.0D);
    mc.itemRenderer.renderItemSide((EntityLivingBase)mc.player, item, ItemCameraTransforms.TransformType.NONE, false);
    GlStateManager.enableTexture2D();
    GlStateManager.disableLighting();
    GlStateManager.scale(-0.05F, -0.05F, 0.0F);
    try {
      if (item.getCount() > 0) {
        int w = mc.fontRenderer.getStringWidth("x" + item.getCount()) / 2;
        mc.fontRenderer.drawStringWithShadow("x" + item.getCount(), (7 - w), 5.0F, 16777215);
      } 
      GlStateManager.scale(0.85F, 0.85F, 0.85F);
      int c = 0;
      for (Map.Entry<Enchantment, Integer> m : (Iterable<Map.Entry<Enchantment, Integer>>)EnchantmentHelper.getEnchantments(item).entrySet()) {
        int w1 = mc.fontRenderer.getStringWidth(I18n.format(((Enchantment)m.getKey()).getName().substring(0, 2), new Object[0]) + (((Integer)m.getValue()).intValue() / 2));
        mc.fontRenderer.drawStringWithShadow(
            I18n.format(((Enchantment)m.getKey()).getName(), new Object[0]).substring(0, 2) + m.getValue(), (-4 - w1 + 3), (c * 10 - 1), (m
            .getKey() == Enchantments.VANISHING_CURSE || m.getKey() == Enchantments.BINDING_CURSE) ? 16732240 : 16756960);
        c--;
      } 
      GlStateManager.scale(0.6F, 0.6F, 0.6F);
      String dur = (item.getMaxDamage() - item.getItemDamage()) + "";
      int color = MathHelper.hsvToRGB((item.getMaxDamage() - item.getItemDamage()) / item.getMaxDamage() / 3.0F, 1.0F, 1.0F);
      if (item.isItemStackDamageable())
        mc.fontRenderer.drawStringWithShadow(dur, (-8 - dur.length() * 3), 15.0F, (new Color(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF)).getRGB()); 
    } catch (Exception exception) {}
    glCleanup();
  }
  
  public static void glSetup(double x, double y, double z) {
    GlStateManager.pushMatrix();
    RenderManager renderManager = mc.getRenderManager();
    GlStateManager.translate(x - (mc.getRenderManager()).viewerPosX, y - (mc.getRenderManager()).viewerPosY, z - (mc.getRenderManager()).viewerPosZ);
    GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
    GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotate((mc.gameSettings.thirdPersonView == 2) ? -renderManager.playerViewX : renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
    GlStateManager.disableLighting();
    GL11.glDisable(2929);
    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
  }
  
  public static void glCleanup() {
    GlStateManager.enableLighting();
    GlStateManager.disableBlend();
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glEnable(2929);
    GlStateManager.enableDepth();
    GL11.glTranslatef(-0.5F, 0.0F, 0.0F);
    GlStateManager.popMatrix();
  }
}
