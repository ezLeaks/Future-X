package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Screens.Custom.Esp.EspGui;
import Method.Client.utils.visual.RenderUtils;
import java.util.Objects;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class ESP extends Module {
  Setting Box = Main.setmgr.add(new Setting("Box", this, true));
  
  Setting Nametag = Main.setmgr.add(new Setting("Nametag", this, false));
  
  Setting MobColor = Main.setmgr.add(new Setting("MobColor", this, 0.88D, 1.0D, 1.0D, 1.0D));
  
  Setting Mode = Main.setmgr.add(new Setting("Hole Mode", this, "Outline", BlockEspOptions()));
  
  Setting LineWidth = Main.setmgr.add(new Setting("LineWidth", this, 1.0D, 0.0D, 3.0D, false));
  
  Setting Glow = Main.setmgr.add(new Setting("Glow", this, true));
  
  Setting GlowWidth = Main.setmgr.add(new Setting("GlowWidth", this, 0.0D, 0.0D, 1.0D, false));
  
  Setting MobSelect = Main.setmgr.add(new Setting("Gui", this, (GuiScreen)Main.GuiEsp));
  
  public ESP() {
    super("ESP", 0, Category.RENDER, "ESP");
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    for (Entity object : mc.world.loadedEntityList) {
      for (String mob : EspGui.Getmobs()) {
        if (((Class)Objects.<Class<?>>requireNonNull(EntityList.getClassFromName(mob))).getName().equalsIgnoreCase(object.getClass().getName()))
          render(object); 
      } 
    } 
    super.onRenderWorldLast(event);
  }
  
  void render(Entity ent) {
    if (ent == mc.player)
      return; 
    if (this.Nametag.getValBoolean())
      ent.setAlwaysRenderNameTag(true); 
    if (this.Glow.getValBoolean())
      mc.renderGlobal.entityOutlineShader.listShaders.forEach(shader -> {
            ShaderUniform outlineRadius = shader.getShaderManager().getShaderUniform("Radius");
            if (outlineRadius != null)
              outlineRadius.set((float)this.GlowWidth.getValDouble()); 
          }); 
    if (ent instanceof EntityLivingBase) {
      EntityLivingBase entity = (EntityLivingBase)ent;
      entity.setGlowing(this.Glow.getValBoolean());
      if (this.Nametag.getValBoolean())
        entity.setCustomNameTag(ent.getName()); 
      if (this.Box.getValBoolean())
        RenderUtils.RenderBlock(this.Mode.getValString(), RenderUtils.Boundingbb((Entity)entity, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D), this.MobColor.getcolor(), Double.valueOf(this.LineWidth.getValDouble())); 
    } 
  }
  
  public void onDisable() {
    for (Object object : mc.world.loadedEntityList) {
      Entity entity = (Entity)object;
      if (entity.isGlowing())
        entity.setGlowing(false); 
    } 
    super.onDisable();
  }
}
