package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.FriendManager;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Utils;
import Method.Client.utils.visual.ColorUtils;
import Method.Client.utils.visual.RenderUtils;
import java.util.ArrayList;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class Tracers extends Module {
  Setting Player;
  
  Setting invis;
  
  Setting Box;
  
  Setting BoxMode;
  
  Setting BoxWidth;
  
  Setting FriendColor;
  
  Setting PlayerColor;
  
  Setting inviscolor;
  
  Setting LineWidth;
  
  Setting Distance;
  
  Setting Mode;
  
  Setting Glow;
  
  Setting GlowWidth;
  
  public Tracers() {
    super("Tracers", 0, Category.RENDER, "Tracers");
  }
  
  public void setup() {
    ArrayList<String> options = new ArrayList<>();
    options.add("Head");
    options.add("Body");
    options.add("Feet");
    Main.setmgr.add(this.Mode = new Setting("Player mode", this, "Head", options));
    Main.setmgr.add(this.Player = new Setting("Player", this, true));
    Main.setmgr.add(this.invis = new Setting("Invis", this, false));
    Main.setmgr.add(this.Glow = new Setting("Glow", this, true));
    Main.setmgr.add(this.GlowWidth = new Setting("GlowWidth", this, 0.0D, 0.0D, 1.0D, false));
    Main.setmgr.add(this.Box = new Setting("Box", this, false));
    Main.setmgr.add(this.BoxMode = new Setting("Box", this, "Outline", BlockEspOptions()));
    Main.setmgr.add(this.BoxWidth = new Setting("BoxWidth", this, 1.0D, 0.0D, 3.0D, false));
    Main.setmgr.add(this.LineWidth = new Setting("LineWidth", this, 1.0D, 0.0D, 3.0D, false));
    Main.setmgr.add(this.Distance = new Setting("Distance", this, 500.0D, 0.0D, 500.0D, true));
    Main.setmgr.add(this.FriendColor = new Setting("FriendColor", this, 0.0D, 1.0D, 1.0D, 1.0D, this.Player, 7));
    Main.setmgr.add(this.PlayerColor = new Setting("PlayerColor", this, 0.44D, 1.0D, 1.0D, 1.0D, this.Player, 8));
    Main.setmgr.add(this.inviscolor = new Setting("Inviscolor", this, 0.88D, 1.0D, 1.0D, 1.0D, this.invis, 9));
  }
  
  public void onDisable() {
    for (Object object : mc.world.loadedEntityList) {
      if (object instanceof EntityPlayer) {
        EntityPlayer entity = (EntityPlayer)object;
        if (entity.isGlowing())
          entity.setGlowing(false); 
      } 
    } 
    super.onDisable();
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    for (Object object : mc.world.loadedEntityList) {
      if (object instanceof EntityLivingBase && !(object instanceof net.minecraft.entity.item.EntityArmorStand)) {
        EntityLivingBase entity = (EntityLivingBase)object;
        if (mc.player.getDistance((Entity)entity) < this.Distance.getValDouble())
          render(entity); 
      } 
    } 
    super.onRenderWorldLast(event);
  }
  
  void render(EntityLivingBase entity) {
    if (entity == mc.player)
      return; 
    if (entity instanceof EntityPlayer && this.Player.getValBoolean()) {
      int y = 0;
      if (this.Mode.getValString().equalsIgnoreCase("Head"))
        y++; 
      if (this.Mode.getValString().equalsIgnoreCase("Feet"))
        y--; 
      if (entity.hurtTime > 0) {
        RenderUtils.RenderBlock("Tracer", RenderUtils.Boundingbb((Entity)entity, 0.0D, y, 0.0D, 0.0D, y, 0.0D), ColorUtils.rainbow().getRGB(), Double.valueOf(this.LineWidth.getValDouble()));
        if (this.Box.getValBoolean())
          RenderUtils.RenderBlock(this.BoxMode.getValString(), RenderUtils.Boundingbb((Entity)entity, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D), ColorUtils.rainbow().getRGB(), Double.valueOf(this.LineWidth.getValDouble())); 
        return;
      } 
      EntityPlayer player = (EntityPlayer)entity;
      String ID = Utils.getPlayerName(player);
      if (this.Glow.getValBoolean()) {
        player.setGlowing(true);
        mc.renderGlobal.entityOutlineShader.listShaders.forEach(shader -> {
              ShaderUniform outlineRadius = shader.getShaderManager().getShaderUniform("Radius");
              if (outlineRadius != null)
                outlineRadius.set((float)this.GlowWidth.getValDouble()); 
            });
      } 
      if (FriendManager.friendsList.contains(ID)) {
        if (this.Box.getValBoolean())
          RenderUtils.RenderBlock(this.BoxMode.getValString(), RenderUtils.Boundingbb((Entity)entity, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D), this.FriendColor.getcolor(), Double.valueOf(this.LineWidth.getValDouble())); 
        RenderUtils.RenderBlock("Tracer", RenderUtils.Boundingbb((Entity)entity, 0.0D, y, 0.0D, 0.0D, y, 0.0D), this.FriendColor.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
        return;
      } 
      RenderUtils.RenderBlock("Tracer", RenderUtils.Boundingbb((Entity)entity, 0.0D, y, 0.0D, 0.0D, y, 0.0D), this.PlayerColor.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
      if (this.Box.getValBoolean())
        RenderUtils.RenderBlock(this.BoxMode.getValString(), RenderUtils.Boundingbb((Entity)entity, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D), this.PlayerColor.getcolor(), Double.valueOf(this.LineWidth.getValDouble())); 
      if (entity.isInvisible() && this.invis.getValBoolean()) {
        RenderUtils.RenderBlock("Tracer", RenderUtils.Boundingbb((Entity)entity, 0.0D, y, 0.0D, 0.0D, y, 0.0D), this.inviscolor.getcolor(), Double.valueOf(this.LineWidth.getValDouble()));
        if (this.Box.getValBoolean())
          RenderUtils.RenderBlock(this.BoxMode.getValString(), RenderUtils.Boundingbb((Entity)entity, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D), this.inviscolor.getcolor(), Double.valueOf(this.LineWidth.getValDouble())); 
      } 
    } 
  }
}
