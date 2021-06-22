package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.visual.RenderUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class Breadcrumb extends Module {
  Setting tickdelay = Main.setmgr.add(new Setting("tickdelay", this, 2.0D, 1.0D, 20.0D, true));
  
  Setting Color = Main.setmgr.add(new Setting("Color", this, 0.4D, 0.8D, 0.9D, 1.0D));
  
  Setting OtherColor = Main.setmgr.add(new Setting("OtherColor", this, 0.8D, 0.8D, 0.9D, 1.0D));
  
  Setting Width = Main.setmgr.add(new Setting("Width", this, 2.5D, 1.0D, 5.0D, false));
  
  Setting BlockSnap = Main.setmgr.add(new Setting("BlockSnap", this, false));
  
  Setting OtherPlayers = Main.setmgr.add(new Setting("OtherPlayers", this, false));
  
  List<Vec3d> doubles = new ArrayList<>();
  
  List<OtherPos> OtherPos = new ArrayList<>();
  
  public Breadcrumb() {
    super("Breadcrumb", 0, Category.RENDER, "Breadcrumbs");
  }
  
  public void onEnable() {
    this.doubles.clear();
    if (mc.player.getDistance(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ) < 200.0D) {
      this.doubles.add(new Vec3d(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ));
      this.doubles.add(new Vec3d((Vec3i)mc.player.getPosition()));
    } 
  }
  
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    if (mc.player.ticksExisted % (int)this.tickdelay.getValDouble() == 0) {
      this.doubles.add(new Vec3d(mc.player.posX, mc.player.posY, mc.player.posZ));
      if (this.OtherPlayers.getValBoolean())
        for (Entity entity : mc.world.loadedEntityList) {
          if (entity instanceof EntityOtherPlayerMP) {
            EntityOtherPlayerMP otherPlayerMP = (EntityOtherPlayerMP)entity;
            boolean newplayer = true;
            for (OtherPos otherPo : this.OtherPos) {
              if (otherPo.getName().equals(otherPlayerMP.getName())) {
                otherPo.doubles.add(new Vec3d(otherPlayerMP.posX, otherPlayerMP.posY, otherPlayerMP.posZ));
                newplayer = false;
              } 
            } 
            if (newplayer) {
              OtherPos NewPla = new OtherPos(otherPlayerMP.getName());
              this.OtherPos.add(NewPla);
              NewPla.doubles.add(new Vec3d(otherPlayerMP.posX, otherPlayerMP.posY, otherPlayerMP.posZ));
            } 
          } 
        }  
    } 
    RenderUtils.RenderLine(this.doubles, this.Color.getcolor(), this.Width.getValDouble(), this.BlockSnap.getValBoolean());
    if (this.OtherPlayers.getValBoolean())
      for (OtherPos otherPo : this.OtherPos)
        RenderUtils.RenderLine(otherPo.doubles, this.OtherColor.getcolor(), this.Width.getValDouble(), this.BlockSnap.getValBoolean());  
  }
  
  static class OtherPos {
    private final String name;
    
    public String getName() {
      return this.name;
    }
    
    List<Vec3d> doubles = new ArrayList<>();
    
    public OtherPos(String name) {
      this.name = name;
    }
  }
}
